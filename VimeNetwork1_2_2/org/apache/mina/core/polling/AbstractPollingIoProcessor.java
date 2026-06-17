/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.polling;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoServiceListenerSupport;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.SessionState;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.transport.socket.AbstractDatagramSessionConfig;
import org.apache.mina.util.ExceptionMonitor;
import org.apache.mina.util.NamePreservingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPollingIoProcessor<S extends AbstractIoSession>
implements IoProcessor<S> {
    private static final Logger LOG = LoggerFactory.getLogger(IoProcessor.class);
    private static final long SELECT_TIMEOUT = 1000L;
    private static final ConcurrentHashMap<Class<?>, AtomicInteger> threadIds = new ConcurrentHashMap();
    private final String threadName;
    private final Executor executor;
    private final Queue<S> newSessions = new ConcurrentLinkedQueue<S>();
    private final Queue<S> removingSessions = new ConcurrentLinkedQueue<S>();
    private final Queue<S> flushingSessions = new ConcurrentLinkedQueue<S>();
    private final Queue<S> trafficControllingSessions = new ConcurrentLinkedQueue<S>();
    private final AtomicReference<Processor> processorRef = new AtomicReference();
    private long lastIdleCheckTime;
    private final Object disposalLock = new Object();
    private volatile boolean disposing;
    private volatile boolean disposed;
    private final DefaultIoFuture disposalFuture = new DefaultIoFuture(null);
    protected AtomicBoolean wakeupCalled = new AtomicBoolean(false);

    protected AbstractPollingIoProcessor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor");
        }
        this.threadName = this.nextThreadName();
        this.executor = executor;
    }

    private String nextThreadName() {
        Class<?> cls = this.getClass();
        AtomicInteger threadId = threadIds.putIfAbsent(cls, new AtomicInteger(1));
        int newThreadId = threadId == null ? 1 : threadId.incrementAndGet();
        return cls.getSimpleName() + '-' + newThreadId;
    }

    @Override
    public final boolean isDisposing() {
        return this.disposing;
    }

    @Override
    public final boolean isDisposed() {
        return this.disposed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void dispose() {
        if (this.disposed || this.disposing) {
            return;
        }
        Object object = this.disposalLock;
        synchronized (object) {
            this.disposing = true;
            this.startupProcessor();
        }
        this.disposalFuture.awaitUninterruptibly();
        this.disposed = true;
    }

    protected abstract void doDispose() throws Exception;

    protected abstract int select(long var1) throws Exception;

    protected abstract int select() throws Exception;

    protected abstract boolean isSelectorEmpty();

    protected abstract void wakeup();

    protected abstract Iterator<S> allSessions();

    protected abstract Iterator<S> selectedSessions();

    protected abstract SessionState getState(S var1);

    protected abstract boolean isWritable(S var1);

    protected abstract boolean isReadable(S var1);

    protected abstract void setInterestedInWrite(S var1, boolean var2) throws Exception;

    protected abstract void setInterestedInRead(S var1, boolean var2) throws Exception;

    protected abstract boolean isInterestedInRead(S var1);

    protected abstract boolean isInterestedInWrite(S var1);

    protected abstract void init(S var1) throws Exception;

    protected abstract void destroy(S var1) throws Exception;

    protected abstract int read(S var1, IoBuffer var2) throws Exception;

    protected abstract int write(S var1, IoBuffer var2, int var3) throws IOException;

    protected abstract int transferFile(S var1, FileRegion var2, int var3) throws Exception;

    @Override
    public final void add(S session) {
        if (this.disposed || this.disposing) {
            throw new IllegalStateException("Already disposed.");
        }
        this.newSessions.add(session);
        this.startupProcessor();
    }

    @Override
    public final void remove(S session) {
        this.scheduleRemove(session);
        this.startupProcessor();
    }

    private void scheduleRemove(S session) {
        if (!this.removingSessions.contains(session)) {
            this.removingSessions.add(session);
        }
    }

    @Override
    public void write(S session, WriteRequest writeRequest) {
        WriteRequestQueue writeRequestQueue = ((AbstractIoSession)session).getWriteRequestQueue();
        writeRequestQueue.offer((IoSession)session, writeRequest);
        if (!((AbstractIoSession)session).isWriteSuspended()) {
            this.flush(session);
        }
    }

    @Override
    public final void flush(S session) {
        if (((AbstractIoSession)session).setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
            this.wakeup();
        }
    }

    private void scheduleFlush(S session) {
        if (((AbstractIoSession)session).setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
        }
    }

    public final void updateTrafficMask(S session) {
        this.trafficControllingSessions.add(session);
        this.wakeup();
    }

    private void startupProcessor() {
        Processor processor = this.processorRef.get();
        if (processor == null && this.processorRef.compareAndSet(null, processor = new Processor())) {
            this.executor.execute(new NamePreservingRunnable(processor, this.threadName));
        }
        this.wakeup();
    }

    protected abstract void registerNewSelector() throws IOException;

    protected abstract boolean isBrokenConnection() throws IOException;

    private int handleNewSessions() {
        int addedSessions = 0;
        AbstractIoSession session = (AbstractIoSession)this.newSessions.poll();
        while (session != null) {
            if (this.addNow(session)) {
                ++addedSessions;
            }
            session = (AbstractIoSession)this.newSessions.poll();
        }
        return addedSessions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean addNow(S session) {
        boolean registered = false;
        try {
            this.init(session);
            registered = true;
            IoFilterChainBuilder chainBuilder = ((AbstractIoSession)session).getService().getFilterChainBuilder();
            chainBuilder.buildFilterChain(session.getFilterChain());
            IoServiceListenerSupport listeners = ((AbstractIoService)((AbstractIoSession)session).getService()).getListeners();
            listeners.fireSessionCreated((IoSession)session);
        }
        catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
            try {
                this.destroy(session);
            }
            catch (Exception e1) {
                ExceptionMonitor.getInstance().exceptionCaught(e1);
            }
            finally {
                registered = false;
            }
        }
        return registered;
    }

    private int removeSessions() {
        int removedSessions = 0;
        AbstractIoSession session = (AbstractIoSession)this.removingSessions.poll();
        while (session != null) {
            SessionState state = this.getState(session);
            switch (state) {
                case OPENED: {
                    if (!this.removeNow(session)) break;
                    ++removedSessions;
                    break;
                }
                case CLOSING: {
                    ++removedSessions;
                    break;
                }
                case OPENING: {
                    this.newSessions.remove(session);
                    if (!this.removeNow(session)) break;
                    ++removedSessions;
                    break;
                }
                default: {
                    throw new IllegalStateException(String.valueOf((Object)state));
                }
            }
            session = (AbstractIoSession)this.removingSessions.poll();
        }
        return removedSessions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean removeNow(S session) {
        IoFilterChain filterChain;
        this.clearWriteRequestQueue(session);
        try {
            this.destroy(session);
            boolean bl = true;
            return bl;
        }
        catch (Exception e) {
            filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
        }
        finally {
            try {
                this.clearWriteRequestQueue(session);
                ((AbstractIoService)((AbstractIoSession)session).getService()).getListeners().fireSessionDestroyed((IoSession)session);
            }
            catch (Exception e) {
                filterChain = session.getFilterChain();
                filterChain.fireExceptionCaught(e);
            }
        }
        return false;
    }

    private void clearWriteRequestQueue(S session) {
        WriteRequestQueue writeRequestQueue = ((AbstractIoSession)session).getWriteRequestQueue();
        ArrayList<WriteRequest> failedRequests = new ArrayList<WriteRequest>();
        WriteRequest req = writeRequestQueue.poll((IoSession)session);
        if (req != null) {
            Object message = req.getMessage();
            if (message instanceof IoBuffer) {
                IoBuffer buf = (IoBuffer)message;
                if (buf.hasRemaining()) {
                    buf.reset();
                    failedRequests.add(req);
                } else {
                    IoFilterChain filterChain = session.getFilterChain();
                    filterChain.fireMessageSent(req);
                }
            } else {
                failedRequests.add(req);
            }
            while ((req = writeRequestQueue.poll((IoSession)session)) != null) {
                failedRequests.add(req);
            }
        }
        if (!failedRequests.isEmpty()) {
            WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);
            for (WriteRequest r : failedRequests) {
                ((AbstractIoSession)session).decreaseScheduledBytesAndMessages(r);
                r.getFuture().setException(cause);
            }
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(cause);
        }
    }

    private void process() throws Exception {
        Iterator<S> i = this.selectedSessions();
        while (i.hasNext()) {
            AbstractIoSession session = (AbstractIoSession)i.next();
            this.process(session);
            i.remove();
        }
    }

    private void process(S session) {
        if (this.isReadable(session) && !((AbstractIoSession)session).isReadSuspended()) {
            this.read(session);
        }
        if (this.isWritable(session) && !((AbstractIoSession)session).isWriteSuspended() && ((AbstractIoSession)session).setScheduledForFlush(true)) {
            this.flushingSessions.add(session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void read(S session) {
        IoSessionConfig config = ((AbstractIoSession)session).getConfig();
        int bufferSize = config.getReadBufferSize();
        IoBuffer buf = IoBuffer.allocate(bufferSize);
        boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
        try {
            IoFilterChain filterChain;
            int ret;
            int readBytes;
            block15: {
                readBytes = 0;
                try {
                    if (hasFragmentation) {
                        while ((ret = this.read(session, buf)) > 0) {
                            readBytes += ret;
                            if (buf.hasRemaining()) continue;
                            break block15;
                        }
                        break block15;
                    }
                    ret = this.read(session, buf);
                    if (ret > 0) {
                        readBytes = ret;
                    }
                }
                finally {
                    buf.flip();
                }
            }
            if (readBytes > 0) {
                filterChain = session.getFilterChain();
                filterChain.fireMessageReceived(buf);
                buf = null;
                if (hasFragmentation) {
                    if (readBytes << 1 < config.getReadBufferSize()) {
                        ((AbstractIoSession)session).decreaseReadBufferSize();
                    } else if (readBytes == config.getReadBufferSize()) {
                        ((AbstractIoSession)session).increaseReadBufferSize();
                    }
                }
            }
            if (ret < 0) {
                filterChain = session.getFilterChain();
                filterChain.fireInputClosed();
            }
        }
        catch (Exception e) {
            if (e instanceof IOException && (!(e instanceof PortUnreachableException) || !AbstractDatagramSessionConfig.class.isAssignableFrom(config.getClass()) || ((AbstractDatagramSessionConfig)config).isCloseOnPortUnreachable())) {
                this.scheduleRemove(session);
            }
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
        }
    }

    private void notifyIdleSessions(long currentTime) throws Exception {
        if (currentTime - this.lastIdleCheckTime >= 1000L) {
            this.lastIdleCheckTime = currentTime;
            AbstractIoSession.notifyIdleness(this.allSessions(), currentTime);
        }
    }

    @Override
    private void flush(long currentTime) {
        AbstractIoSession session;
        if (this.flushingSessions.isEmpty()) {
            return;
        }
        while ((session = (AbstractIoSession)this.flushingSessions.poll()) != null) {
            session.unscheduledForFlush();
            SessionState state = this.getState(session);
            switch (state) {
                case OPENED: {
                    try {
                        boolean flushedAll = this.flushNow(session, currentTime);
                        if (!flushedAll || session.getWriteRequestQueue().isEmpty(session) || session.isScheduledForFlush()) break;
                        this.scheduleFlush(session);
                    }
                    catch (Exception e) {
                        this.scheduleRemove(session);
                        session.closeNow();
                        IoFilterChain filterChain = session.getFilterChain();
                        filterChain.fireExceptionCaught(e);
                    }
                    break;
                }
                case CLOSING: {
                    break;
                }
                case OPENING: {
                    this.scheduleFlush(session);
                    return;
                }
                default: {
                    throw new IllegalStateException(String.valueOf((Object)state));
                }
            }
            if (!this.flushingSessions.isEmpty()) continue;
        }
    }

    private boolean flushNow(S session, long currentTime) {
        if (!((AbstractIoSession)session).isConnected()) {
            this.scheduleRemove(session);
            return false;
        }
        boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
        WriteRequestQueue writeRequestQueue = ((AbstractIoSession)session).getWriteRequestQueue();
        int maxWrittenBytes = ((AbstractIoSession)session).getConfig().getMaxReadBufferSize() + (((AbstractIoSession)session).getConfig().getMaxReadBufferSize() >>> 1);
        int writtenBytes = 0;
        WriteRequest req = null;
        try {
            this.setInterestedInWrite(session, false);
            do {
                int localWrittenBytes;
                Object message;
                if ((req = ((AbstractIoSession)session).getCurrentWriteRequest()) == null) {
                    req = writeRequestQueue.poll((IoSession)session);
                    if (req == null) break;
                    ((AbstractIoSession)session).setCurrentWriteRequest(req);
                }
                if ((message = req.getMessage()) instanceof IoBuffer) {
                    localWrittenBytes = this.writeBuffer(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
                    if (localWrittenBytes > 0 && ((IoBuffer)message).hasRemaining()) {
                        writtenBytes += localWrittenBytes;
                        this.setInterestedInWrite(session, true);
                        return false;
                    }
                } else if (message instanceof FileRegion) {
                    localWrittenBytes = this.writeFile(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
                    if (localWrittenBytes > 0 && ((FileRegion)message).getRemainingBytes() > 0L) {
                        writtenBytes += localWrittenBytes;
                        this.setInterestedInWrite(session, true);
                        return false;
                    }
                } else {
                    throw new IllegalStateException("Don't know how to handle message of type '" + message.getClass().getName() + "'.  Are you missing a protocol encoder?");
                }
                if (localWrittenBytes == 0) {
                    if (!req.equals(AbstractIoSession.MESSAGE_SENT_REQUEST)) {
                        this.setInterestedInWrite(session, true);
                        return false;
                    }
                } else if ((writtenBytes += localWrittenBytes) >= maxWrittenBytes) {
                    this.scheduleFlush(session);
                    return false;
                }
                if (!(message instanceof IoBuffer)) continue;
                ((IoBuffer)message).free();
            } while (writtenBytes < maxWrittenBytes);
        }
        catch (Exception e) {
            if (req != null) {
                req.getFuture().setException(e);
            }
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
            return false;
        }
        return true;
    }

    private int writeBuffer(S session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
        IoBuffer buf = (IoBuffer)req.getMessage();
        int localWrittenBytes = 0;
        if (buf.hasRemaining()) {
            int length = hasFragmentation ? Math.min(buf.remaining(), maxLength) : buf.remaining();
            try {
                localWrittenBytes = this.write(session, buf, length);
            }
            catch (IOException ioe) {
                buf.free();
                ((AbstractIoSession)session).closeNow();
                this.removeNow(session);
                return 0;
            }
        }
        ((AbstractIoSession)session).increaseWrittenBytes(localWrittenBytes, currentTime);
        if (!buf.hasRemaining() || !hasFragmentation && localWrittenBytes != 0) {
            Object originalMessage = req.getOriginalRequest().getMessage();
            if (originalMessage instanceof IoBuffer) {
                buf = (IoBuffer)req.getOriginalRequest().getMessage();
                int pos = buf.position();
                buf.reset();
                this.fireMessageSent(session, req);
                buf.position(pos);
            } else {
                this.fireMessageSent(session, req);
            }
        }
        return localWrittenBytes;
    }

    private int writeFile(S session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
        int localWrittenBytes;
        FileRegion region = (FileRegion)req.getMessage();
        if (region.getRemainingBytes() > 0L) {
            int length = hasFragmentation ? (int)Math.min(region.getRemainingBytes(), (long)maxLength) : (int)Math.min(Integer.MAX_VALUE, region.getRemainingBytes());
            localWrittenBytes = this.transferFile(session, region, length);
            region.update(localWrittenBytes);
        } else {
            localWrittenBytes = 0;
        }
        ((AbstractIoSession)session).increaseWrittenBytes(localWrittenBytes, currentTime);
        if (region.getRemainingBytes() <= 0L || !hasFragmentation && localWrittenBytes != 0) {
            this.fireMessageSent(session, req);
        }
        return localWrittenBytes;
    }

    private void fireMessageSent(S session, WriteRequest req) {
        ((AbstractIoSession)session).setCurrentWriteRequest(null);
        IoFilterChain filterChain = session.getFilterChain();
        filterChain.fireMessageSent(req);
    }

    private void updateTrafficMask() {
        block5: for (int queueSize = this.trafficControllingSessions.size(); queueSize > 0; --queueSize) {
            AbstractIoSession session = (AbstractIoSession)this.trafficControllingSessions.poll();
            if (session == null) {
                return;
            }
            SessionState state = this.getState(session);
            switch (state) {
                case OPENED: {
                    this.updateTrafficControl((S)session);
                    continue block5;
                }
                case CLOSING: {
                    continue block5;
                }
                case OPENING: {
                    this.trafficControllingSessions.add(session);
                    continue block5;
                }
                default: {
                    throw new IllegalStateException(String.valueOf((Object)state));
                }
            }
        }
    }

    @Override
    public void updateTrafficControl(S session) {
        IoFilterChain filterChain;
        try {
            this.setInterestedInRead(session, !((AbstractIoSession)session).isReadSuspended());
        }
        catch (Exception e) {
            filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
        }
        try {
            this.setInterestedInWrite(session, !((AbstractIoSession)session).getWriteRequestQueue().isEmpty((IoSession)session) && !((AbstractIoSession)session).isWriteSuspended());
        }
        catch (Exception e) {
            filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
        }
    }

    private class Processor
    implements Runnable {
        private Processor() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            assert (AbstractPollingIoProcessor.this.processorRef.get() == this);
            int nSessions = 0;
            AbstractPollingIoProcessor.this.lastIdleCheckTime = System.currentTimeMillis();
            int nbTries = 10;
            block13: while (true) {
                try {
                    while (true) {
                        long t0 = System.currentTimeMillis();
                        int selected = AbstractPollingIoProcessor.this.select(1000L);
                        long t1 = System.currentTimeMillis();
                        long delta = t1 - t0;
                        if (!AbstractPollingIoProcessor.this.wakeupCalled.getAndSet(false) && selected == 0 && delta < 100L) {
                            if (AbstractPollingIoProcessor.this.isBrokenConnection()) {
                                LOG.warn("Broken connection");
                            } else if (nbTries == 0) {
                                LOG.warn("Create a new selector. Selected is 0, delta = " + delta);
                                AbstractPollingIoProcessor.this.registerNewSelector();
                                nbTries = 10;
                            } else {
                                --nbTries;
                            }
                        } else {
                            nbTries = 10;
                        }
                        nSessions += AbstractPollingIoProcessor.this.handleNewSessions();
                        AbstractPollingIoProcessor.this.updateTrafficMask();
                        if (selected > 0) {
                            AbstractPollingIoProcessor.this.process();
                        }
                        long currentTime = System.currentTimeMillis();
                        AbstractPollingIoProcessor.this.flush(currentTime);
                        AbstractPollingIoProcessor.this.notifyIdleSessions(currentTime);
                        if ((nSessions -= AbstractPollingIoProcessor.this.removeSessions()) == 0) {
                            AbstractPollingIoProcessor.this.processorRef.set(null);
                            if (AbstractPollingIoProcessor.this.newSessions.isEmpty() && AbstractPollingIoProcessor.this.isSelectorEmpty()) {
                                assert (AbstractPollingIoProcessor.this.processorRef.get() != this);
                                break block13;
                            }
                            assert (AbstractPollingIoProcessor.this.processorRef.get() != this);
                            if (!AbstractPollingIoProcessor.this.processorRef.compareAndSet(null, this)) {
                                assert (AbstractPollingIoProcessor.this.processorRef.get() != this);
                                break block13;
                            }
                            assert (AbstractPollingIoProcessor.this.processorRef.get() == this);
                        }
                        if (!AbstractPollingIoProcessor.this.isDisposing()) continue;
                        boolean hasKeys = false;
                        Iterator i = AbstractPollingIoProcessor.this.allSessions();
                        while (i.hasNext()) {
                            IoSession session = (IoSession)i.next();
                            if (!session.isActive()) continue;
                            AbstractPollingIoProcessor.this.scheduleRemove((AbstractIoSession)session);
                            hasKeys = true;
                        }
                        if (!hasKeys) continue;
                        AbstractPollingIoProcessor.this.wakeup();
                    }
                }
                catch (ClosedSelectorException cse) {
                    ExceptionMonitor.getInstance().exceptionCaught(cse);
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                    try {
                        Thread.sleep(1000L);
                    }
                    catch (InterruptedException e1) {
                        ExceptionMonitor.getInstance().exceptionCaught(e1);
                    }
                    continue;
                }
                break;
            }
            try {
                Object e = AbstractPollingIoProcessor.this.disposalLock;
                synchronized (e) {
                    if (AbstractPollingIoProcessor.this.disposing) {
                        AbstractPollingIoProcessor.this.doDispose();
                    }
                }
            }
            catch (Exception e) {
                ExceptionMonitor.getInstance().exceptionCaught(e);
            }
            finally {
                AbstractPollingIoProcessor.this.disposalFuture.setValue(true);
            }
        }
    }
}

