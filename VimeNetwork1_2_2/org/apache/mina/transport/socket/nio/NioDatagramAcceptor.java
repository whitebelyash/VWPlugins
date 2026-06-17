/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.ExpiringSessionRecycler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionRecycler;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.transport.socket.DatagramAcceptor;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramSession;
import org.apache.mina.transport.socket.nio.NioDatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.util.ExceptionMonitor;

public final class NioDatagramAcceptor
extends AbstractIoAcceptor
implements DatagramAcceptor,
IoProcessor<NioSession> {
    private static final IoSessionRecycler DEFAULT_RECYCLER = new ExpiringSessionRecycler();
    private static final long SELECT_TIMEOUT = 1000L;
    private final Semaphore lock = new Semaphore(1);
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue = new ConcurrentLinkedQueue<AbstractIoAcceptor.AcceptorOperationFuture>();
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue = new ConcurrentLinkedQueue<AbstractIoAcceptor.AcceptorOperationFuture>();
    private final Queue<NioSession> flushingSessions = new ConcurrentLinkedQueue<NioSession>();
    private final Map<SocketAddress, DatagramChannel> boundHandles = Collections.synchronizedMap(new HashMap());
    private IoSessionRecycler sessionRecycler = DEFAULT_RECYCLER;
    private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
    private volatile boolean selectable;
    private Acceptor acceptor;
    private long lastIdleCheckTime;
    private volatile Selector selector;

    public NioDatagramAcceptor() {
        this(new DefaultDatagramSessionConfig(), null);
    }

    public NioDatagramAcceptor(Executor executor) {
        this(new DefaultDatagramSessionConfig(), executor);
    }

    private NioDatagramAcceptor(IoSessionConfig sessionConfig, Executor executor) {
        super(sessionConfig, executor);
        try {
            this.init();
            this.selectable = true;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeIoException("Failed to initialize.", e);
        }
        finally {
            if (!this.selectable) {
                try {
                    this.destroy();
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int registerHandles() {
        AbstractIoAcceptor.AcceptorOperationFuture req;
        while ((req = this.registerQueue.poll()) != null) {
            HashMap<SocketAddress, DatagramChannel> newHandles = new HashMap<SocketAddress, DatagramChannel>();
            List<SocketAddress> localAddresses = req.getLocalAddresses();
            try {
                for (SocketAddress socketAddress : localAddresses) {
                    DatagramChannel handle = this.open(socketAddress);
                    newHandles.put(this.localAddress(handle), handle);
                }
                this.boundHandles.putAll(newHandles);
                this.getListeners().fireServiceActivated();
                req.setDone();
                int n = newHandles.size();
                return n;
            }
            catch (Exception e) {
                req.setException(e);
            }
            finally {
                if (req.getException() == null) continue;
                for (DatagramChannel datagramChannel : newHandles.values()) {
                    try {
                        this.close(datagramChannel);
                    }
                    catch (Exception e) {
                        ExceptionMonitor.getInstance().exceptionCaught(e);
                    }
                }
                this.wakeup();
            }
        }
        return 0;
    }

    private void processReadySessions(Set<SelectionKey> handles) {
        Iterator<SelectionKey> iterator = handles.iterator();
        while (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            DatagramChannel handle = (DatagramChannel)key.channel();
            iterator.remove();
            try {
                if (key.isValid() && key.isReadable()) {
                    this.readHandle(handle);
                }
                if (!key.isValid() || !key.isWritable()) continue;
                for (IoSession session : this.getManagedSessions().values()) {
                    this.scheduleFlush((NioSession)session);
                }
            }
            catch (Exception e) {
                ExceptionMonitor.getInstance().exceptionCaught(e);
            }
        }
    }

    private boolean scheduleFlush(NioSession session) {
        if (session.setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
            return true;
        }
        return false;
    }

    private void readHandle(DatagramChannel handle) throws Exception {
        IoBuffer readBuf = IoBuffer.allocate(this.getSessionConfig().getReadBufferSize());
        SocketAddress remoteAddress = this.receive(handle, readBuf);
        if (remoteAddress != null) {
            IoSession session = this.newSessionWithoutLock(remoteAddress, this.localAddress(handle));
            readBuf.flip();
            session.getFilterChain().fireMessageReceived(readBuf);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private IoSession newSessionWithoutLock(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
        IoSession session;
        DatagramChannel handle = this.boundHandles.get(localAddress);
        if (handle == null) {
            throw new IllegalArgumentException("Unknown local address: " + localAddress);
        }
        IoSessionRecycler ioSessionRecycler = this.sessionRecycler;
        synchronized (ioSessionRecycler) {
            session = this.sessionRecycler.recycle(remoteAddress);
            if (session != null) {
                return session;
            }
            NioSession newSession = this.newSession(this, handle, remoteAddress);
            this.getSessionRecycler().put(newSession);
            session = newSession;
        }
        this.initSession(session, null, null);
        try {
            this.getFilterChainBuilder().buildFilterChain(session.getFilterChain());
            this.getListeners().fireSessionCreated(session);
        }
        catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
        }
        return session;
    }

    private void flushSessions(long currentTime) {
        NioSession session;
        while ((session = this.flushingSessions.poll()) != null) {
            session.unscheduledForFlush();
            try {
                boolean flushedAll = this.flush(session, currentTime);
                if (!flushedAll || session.getWriteRequestQueue().isEmpty(session) || session.isScheduledForFlush()) continue;
                this.scheduleFlush(session);
            }
            catch (Exception e) {
                session.getFilterChain().fireExceptionCaught(e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean flush(NioSession session, long currentTime) throws Exception {
        WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
        int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
        int writtenBytes = 0;
        try {
            while (true) {
                int localWrittenBytes;
                IoBuffer buf;
                WriteRequest req;
                if ((req = session.getCurrentWriteRequest()) == null) {
                    req = writeRequestQueue.poll(session);
                    if (req == null) {
                        this.setInterestedInWrite(session, false);
                        break;
                    }
                    session.setCurrentWriteRequest(req);
                }
                if ((buf = (IoBuffer)req.getMessage()).remaining() == 0) {
                    session.setCurrentWriteRequest(null);
                    buf.reset();
                    session.getFilterChain().fireMessageSent(req);
                    continue;
                }
                SocketAddress destination = req.getDestination();
                if (destination == null) {
                    destination = session.getRemoteAddress();
                }
                if ((localWrittenBytes = this.send(session, buf, destination)) == 0 || writtenBytes >= maxWrittenBytes) {
                    this.setInterestedInWrite(session, true);
                    boolean bl = false;
                    return bl;
                }
                this.setInterestedInWrite(session, false);
                session.setCurrentWriteRequest(null);
                writtenBytes += localWrittenBytes;
                buf.reset();
                session.getFilterChain().fireMessageSent(req);
            }
        }
        finally {
            session.increaseWrittenBytes(writtenBytes, currentTime);
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int unregisterHandles() {
        AbstractIoAcceptor.AcceptorOperationFuture request;
        int nHandles = 0;
        while ((request = this.cancelQueue.poll()) != null) {
            for (SocketAddress socketAddress : request.getLocalAddresses()) {
                DatagramChannel handle = this.boundHandles.remove(socketAddress);
                if (handle == null) continue;
                try {
                    this.close(handle);
                    this.wakeup();
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
                finally {
                    ++nHandles;
                }
            }
            request.setDone();
        }
        return nHandles;
    }

    private void notifyIdleSessions(long currentTime) {
        if (currentTime - this.lastIdleCheckTime >= 1000L) {
            this.lastIdleCheckTime = currentTime;
            AbstractIoSession.notifyIdleness(this.getListeners().getManagedSessions().values().iterator(), currentTime);
        }
    }

    private void startupAcceptor() throws InterruptedException {
        if (!this.selectable) {
            this.registerQueue.clear();
            this.cancelQueue.clear();
            this.flushingSessions.clear();
        }
        this.lock.acquire();
        if (this.acceptor == null) {
            this.acceptor = new Acceptor();
            this.executeWorker(this.acceptor);
        } else {
            this.lock.release();
        }
    }

    protected void init() throws Exception {
        this.selector = Selector.open();
    }

    @Override
    public void add(NioSession session) {
    }

    @Override
    protected final Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.registerQueue.add(request);
        this.startupAcceptor();
        try {
            this.lock.acquire();
            Thread.sleep(10L);
            this.wakeup();
        }
        finally {
            this.lock.release();
        }
        request.awaitUninterruptibly();
        if (request.getException() != null) {
            throw request.getException();
        }
        HashSet<SocketAddress> newLocalAddresses = new HashSet<SocketAddress>();
        for (DatagramChannel handle : this.boundHandles.values()) {
            newLocalAddresses.add(this.localAddress(handle));
        }
        return newLocalAddresses;
    }

    protected void close(DatagramChannel handle) throws Exception {
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        handle.disconnect();
        handle.close();
    }

    protected void destroy() throws Exception {
        if (this.selector != null) {
            this.selector.close();
        }
    }

    @Override
    protected void dispose0() throws Exception {
        this.unbind();
        this.startupAcceptor();
        this.wakeup();
    }

    @Override
    public void flush(NioSession session) {
        if (this.scheduleFlush(session)) {
            this.wakeup();
        }
    }

    @Override
    public InetSocketAddress getDefaultLocalAddress() {
        return (InetSocketAddress)super.getDefaultLocalAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)super.getLocalAddress();
    }

    @Override
    public DatagramSessionConfig getSessionConfig() {
        return (DatagramSessionConfig)this.sessionConfig;
    }

    @Override
    public final IoSessionRecycler getSessionRecycler() {
        return this.sessionRecycler;
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return NioDatagramSession.METADATA;
    }

    protected boolean isReadable(DatagramChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return false;
        }
        return key.isReadable();
    }

    protected boolean isWritable(DatagramChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return false;
        }
        return key.isWritable();
    }

    protected SocketAddress localAddress(DatagramChannel handle) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress)handle.socket().getLocalSocketAddress();
        InetAddress inetAddress = inetSocketAddress.getAddress();
        if (inetAddress instanceof Inet6Address && ((Inet6Address)inetAddress).isIPv4CompatibleAddress()) {
            byte[] ipV6Address = ((Inet6Address)inetAddress).getAddress();
            byte[] ipV4Address = new byte[4];
            System.arraycopy(ipV6Address, 12, ipV4Address, 0, 4);
            InetAddress inet4Adress = Inet4Address.getByAddress(ipV4Address);
            return new InetSocketAddress(inet4Adress, inetSocketAddress.getPort());
        }
        return inetSocketAddress;
    }

    protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle, SocketAddress remoteAddress) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return null;
        }
        NioDatagramSession newSession = new NioDatagramSession(this, handle, processor, remoteAddress);
        newSession.setSelectionKey(key);
        return newSession;
    }

    @Override
    public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
        if (this.isDisposing()) {
            throw new IllegalStateException("The Acceptor is being disposed.");
        }
        if (remoteAddress == null) {
            throw new IllegalArgumentException("remoteAddress");
        }
        Object object = this.bindLock;
        synchronized (object) {
            if (!this.isActive()) {
                throw new IllegalStateException("Can't create a session from a unbound service.");
            }
            try {
                return this.newSessionWithoutLock(remoteAddress, localAddress);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Error e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeIoException("Failed to create a session.", e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DatagramChannel open(SocketAddress localAddress) throws Exception {
        DatagramChannel ch = DatagramChannel.open();
        boolean success = false;
        try {
            new NioDatagramSessionConfig(ch).setAll(this.getSessionConfig());
            ch.configureBlocking(false);
            try {
                ch.socket().bind(localAddress);
            }
            catch (IOException ioe) {
                String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
                IOException e = new IOException(newMessage);
                e.initCause(ioe.getCause());
                ch.close();
                throw e;
            }
            ch.register(this.selector, 1);
            success = true;
        }
        finally {
            if (!success) {
                this.close(ch);
            }
        }
        return ch;
    }

    protected SocketAddress receive(DatagramChannel handle, IoBuffer buffer) throws Exception {
        return handle.receive(buffer.buf());
    }

    @Override
    public void remove(NioSession session) {
        this.getSessionRecycler().remove(session);
        this.getListeners().fireSessionDestroyed(session);
    }

    protected int select() throws Exception {
        return this.selector.select();
    }

    protected int select(long timeout) throws Exception {
        return this.selector.select(timeout);
    }

    protected Set<SelectionKey> selectedHandles() {
        return this.selector.selectedKeys();
    }

    protected int send(NioSession session, IoBuffer buffer, SocketAddress remoteAddress) throws Exception {
        return ((DatagramChannel)session.getChannel()).send(buffer.buf(), remoteAddress);
    }

    @Override
    public void setDefaultLocalAddress(InetSocketAddress localAddress) {
        this.setDefaultLocalAddress((SocketAddress)localAddress);
    }

    protected void setInterestedInWrite(NioSession session, boolean isInterested) throws Exception {
        SelectionKey key = session.getSelectionKey();
        if (key == null) {
            return;
        }
        int newInterestOps = key.interestOps();
        newInterestOps = isInterested ? (newInterestOps |= 4) : (newInterestOps &= 0xFFFFFFFB);
        key.interestOps(newInterestOps);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setSessionRecycler(IoSessionRecycler sessionRecycler) {
        Object object = this.bindLock;
        synchronized (object) {
            if (this.isActive()) {
                throw new IllegalStateException("sessionRecycler can't be set while the acceptor is bound.");
            }
            if (sessionRecycler == null) {
                sessionRecycler = DEFAULT_RECYCLER;
            }
            this.sessionRecycler = sessionRecycler;
        }
    }

    @Override
    protected final void unbind0(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.cancelQueue.add(request);
        this.startupAcceptor();
        this.wakeup();
        request.awaitUninterruptibly();
        if (request.getException() != null) {
            throw request.getException();
        }
    }

    @Override
    public void updateTrafficControl(NioSession session) {
        throw new UnsupportedOperationException();
    }

    protected void wakeup() {
        this.selector.wakeup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(NioSession session, WriteRequest writeRequest) {
        block11: {
            long currentTime = System.currentTimeMillis();
            WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
            int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
            int writtenBytes = 0;
            IoBuffer buf = (IoBuffer)writeRequest.getMessage();
            if (buf.remaining() == 0) {
                session.setCurrentWriteRequest(null);
                buf.reset();
                session.getFilterChain().fireMessageSent(writeRequest);
                return;
            }
            try {
                int localWrittenBytes;
                while (true) {
                    if (writeRequest == null) {
                        writeRequest = writeRequestQueue.poll(session);
                        if (writeRequest == null) {
                            this.setInterestedInWrite(session, false);
                            break block11;
                        }
                        session.setCurrentWriteRequest(writeRequest);
                    }
                    if ((buf = (IoBuffer)writeRequest.getMessage()).remaining() == 0) {
                        session.setCurrentWriteRequest(null);
                        buf.reset();
                        session.getFilterChain().fireMessageSent(writeRequest);
                        continue;
                    }
                    SocketAddress destination = writeRequest.getDestination();
                    if (destination == null) {
                        destination = session.getRemoteAddress();
                    }
                    if ((localWrittenBytes = this.send(session, buf, destination)) != 0 && writtenBytes < maxWrittenBytes) break;
                    this.setInterestedInWrite(session, true);
                    session.getWriteRequestQueue().offer(session, writeRequest);
                    this.scheduleFlush(session);
                }
                this.setInterestedInWrite(session, false);
                session.setCurrentWriteRequest(null);
                writtenBytes += localWrittenBytes;
                buf.reset();
                session.getFilterChain().fireMessageSent(writeRequest);
            }
            catch (Exception e) {
                session.getFilterChain().fireExceptionCaught(e);
            }
            finally {
                session.increaseWrittenBytes(writtenBytes, currentTime);
            }
        }
    }

    private class Acceptor
    implements Runnable {
        private Acceptor() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            int nHandles = 0;
            NioDatagramAcceptor.this.lastIdleCheckTime = System.currentTimeMillis();
            NioDatagramAcceptor.this.lock.release();
            while (NioDatagramAcceptor.this.selectable) {
                try {
                    int selected = NioDatagramAcceptor.this.select(1000L);
                    if ((nHandles += NioDatagramAcceptor.this.registerHandles()) == 0) {
                        try {
                            NioDatagramAcceptor.this.lock.acquire();
                            if (NioDatagramAcceptor.this.registerQueue.isEmpty() && NioDatagramAcceptor.this.cancelQueue.isEmpty()) {
                                NioDatagramAcceptor.this.acceptor = null;
                                break;
                            }
                        }
                        finally {
                            NioDatagramAcceptor.this.lock.release();
                        }
                    }
                    if (selected > 0) {
                        NioDatagramAcceptor.this.processReadySessions(NioDatagramAcceptor.this.selectedHandles());
                    }
                    long currentTime = System.currentTimeMillis();
                    NioDatagramAcceptor.this.flushSessions(currentTime);
                    nHandles -= NioDatagramAcceptor.this.unregisterHandles();
                    NioDatagramAcceptor.this.notifyIdleSessions(currentTime);
                }
                catch (ClosedSelectorException cse) {
                    ExceptionMonitor.getInstance().exceptionCaught(cse);
                    break;
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException interruptedException) {}
                }
            }
            if (NioDatagramAcceptor.this.selectable && NioDatagramAcceptor.this.isDisposing()) {
                NioDatagramAcceptor.this.selectable = false;
                try {
                    NioDatagramAcceptor.this.destroy();
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
                finally {
                    NioDatagramAcceptor.this.disposalFuture.setValue(true);
                }
            }
        }
    }
}

