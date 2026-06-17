/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.polling;

import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

public abstract class AbstractPollingIoAcceptor<S extends AbstractIoSession, H>
extends AbstractIoAcceptor {
    private final Semaphore lock = new Semaphore(1);
    private final IoProcessor<S> processor;
    private final boolean createdProcessor;
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> registerQueue = new ConcurrentLinkedQueue<AbstractIoAcceptor.AcceptorOperationFuture>();
    private final Queue<AbstractIoAcceptor.AcceptorOperationFuture> cancelQueue = new ConcurrentLinkedQueue<AbstractIoAcceptor.AcceptorOperationFuture>();
    private final Map<SocketAddress, H> boundHandles = Collections.synchronizedMap(new HashMap());
    private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
    private volatile boolean selectable;
    private AtomicReference<Acceptor> acceptorRef = new AtomicReference();
    protected boolean reuseAddress = false;
    protected int backlog = 50;

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass), true, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass, int processorCount) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount), true, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class<? extends IoProcessor<S>> processorClass, int processorCount, SelectorProvider selectorProvider) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount, selectorProvider), true, selectorProvider);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, IoProcessor<S> processor) {
        this(sessionConfig, null, processor, false, null);
    }

    protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<S> processor) {
        this(sessionConfig, executor, processor, false, null);
    }

    private AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor<S> processor, boolean createdProcessor, SelectorProvider selectorProvider) {
        super(sessionConfig, executor);
        if (processor == null) {
            throw new IllegalArgumentException("processor");
        }
        this.processor = processor;
        this.createdProcessor = createdProcessor;
        try {
            this.init(selectorProvider);
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

    protected abstract void init() throws Exception;

    protected abstract void init(SelectorProvider var1) throws Exception;

    protected abstract void destroy() throws Exception;

    protected abstract int select() throws Exception;

    protected abstract void wakeup();

    protected abstract Iterator<H> selectedHandles();

    protected abstract H open(SocketAddress var1) throws Exception;

    protected abstract SocketAddress localAddress(H var1) throws Exception;

    protected abstract S accept(IoProcessor<S> var1, H var2) throws Exception;

    protected abstract void close(H var1) throws Exception;

    @Override
    protected void dispose0() throws Exception {
        this.unbind();
        this.startupAcceptor();
        this.wakeup();
    }

    @Override
    protected final Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.registerQueue.add(request);
        this.startupAcceptor();
        try {
            this.lock.acquire();
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
        for (H handle : this.boundHandles.values()) {
            newLocalAddresses.add(this.localAddress(handle));
        }
        return newLocalAddresses;
    }

    private void startupAcceptor() throws InterruptedException {
        Acceptor acceptor;
        if (!this.selectable) {
            this.registerQueue.clear();
            this.cancelQueue.clear();
        }
        if ((acceptor = this.acceptorRef.get()) == null) {
            this.lock.acquire();
            acceptor = new Acceptor();
            if (this.acceptorRef.compareAndSet(null, acceptor)) {
                this.executeWorker(acceptor);
            } else {
                this.lock.release();
            }
        }
    }

    @Override
    protected final void unbind0(List<? extends SocketAddress> localAddresses) throws Exception {
        AbstractIoAcceptor.AcceptorOperationFuture future = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
        this.cancelQueue.add(future);
        this.startupAcceptor();
        this.wakeup();
        future.awaitUninterruptibly();
        if (future.getException() != null) {
            throw future.getException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int registerHandles() {
        AbstractIoAcceptor.AcceptorOperationFuture future;
        while ((future = this.registerQueue.poll()) != null) {
            ConcurrentHashMap<SocketAddress, H> newHandles = new ConcurrentHashMap<SocketAddress, H>();
            List<SocketAddress> localAddresses = future.getLocalAddresses();
            try {
                for (SocketAddress socketAddress : localAddresses) {
                    Object handle = this.open(socketAddress);
                    newHandles.put(this.localAddress(handle), handle);
                }
                this.boundHandles.putAll(newHandles);
                future.setDone();
                int n = newHandles.size();
                return n;
            }
            catch (Exception e) {
                future.setException(e);
                continue;
            }
            finally {
                if (future.getException() == null) continue;
                for (SocketAddress socketAddress : newHandles.values()) {
                    try {
                        this.close(socketAddress);
                    }
                    catch (Exception e) {
                        ExceptionMonitor.getInstance().exceptionCaught(e);
                    }
                }
                this.wakeup();
                continue;
            }
            break;
        }
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int unregisterHandles() {
        AbstractIoAcceptor.AcceptorOperationFuture future;
        int cancelledHandles = 0;
        while ((future = this.cancelQueue.poll()) != null) {
            for (SocketAddress a : future.getLocalAddresses()) {
                H handle = this.boundHandles.remove(a);
                if (handle == null) continue;
                try {
                    this.close(handle);
                    this.wakeup();
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
                finally {
                    ++cancelledHandles;
                }
            }
            future.setDone();
        }
        return cancelledHandles;
    }

    @Override
    public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
        throw new UnsupportedOperationException();
    }

    public int getBacklog() {
        return this.backlog;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setBacklog(int backlog) {
        Object object = this.bindLock;
        synchronized (object) {
            if (this.isActive()) {
                throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
            }
            this.backlog = backlog;
        }
    }

    public boolean isReuseAddress() {
        return this.reuseAddress;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setReuseAddress(boolean reuseAddress) {
        Object object = this.bindLock;
        synchronized (object) {
            if (this.isActive()) {
                throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
            }
            this.reuseAddress = reuseAddress;
        }
    }

    @Override
    public SocketSessionConfig getSessionConfig() {
        return (SocketSessionConfig)this.sessionConfig;
    }

    private class Acceptor
    implements Runnable {
        private Acceptor() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            assert (AbstractPollingIoAcceptor.this.acceptorRef.get() == this);
            int nHandles = 0;
            AbstractPollingIoAcceptor.this.lock.release();
            while (AbstractPollingIoAcceptor.this.selectable) {
                try {
                    int selected = AbstractPollingIoAcceptor.this.select();
                    if ((nHandles += AbstractPollingIoAcceptor.this.registerHandles()) == 0) {
                        AbstractPollingIoAcceptor.this.acceptorRef.set(null);
                        if (AbstractPollingIoAcceptor.this.registerQueue.isEmpty() && AbstractPollingIoAcceptor.this.cancelQueue.isEmpty()) {
                            assert (AbstractPollingIoAcceptor.this.acceptorRef.get() != this);
                            break;
                        }
                        if (!AbstractPollingIoAcceptor.this.acceptorRef.compareAndSet(null, this)) {
                            assert (AbstractPollingIoAcceptor.this.acceptorRef.get() != this);
                            break;
                        }
                        assert (AbstractPollingIoAcceptor.this.acceptorRef.get() == this);
                    }
                    if (selected > 0) {
                        this.processHandles(AbstractPollingIoAcceptor.this.selectedHandles());
                    }
                    nHandles -= AbstractPollingIoAcceptor.this.unregisterHandles();
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
                    catch (InterruptedException e1) {
                        ExceptionMonitor.getInstance().exceptionCaught(e1);
                    }
                }
            }
            if (!AbstractPollingIoAcceptor.this.selectable) return;
            if (!AbstractPollingIoAcceptor.this.isDisposing()) return;
            AbstractPollingIoAcceptor.this.selectable = false;
            try {
                if (!AbstractPollingIoAcceptor.this.createdProcessor) return;
                AbstractPollingIoAcceptor.this.processor.dispose();
                return;
            }
            finally {
                try {
                    Object e = AbstractPollingIoAcceptor.this.disposalLock;
                    synchronized (e) {
                        if (AbstractPollingIoAcceptor.this.isDisposing()) {
                            AbstractPollingIoAcceptor.this.destroy();
                        }
                    }
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
                finally {
                    AbstractPollingIoAcceptor.this.disposalFuture.setDone();
                }
            }
        }

        private void processHandles(Iterator<H> handles) throws Exception {
            while (handles.hasNext()) {
                Object handle = handles.next();
                handles.remove();
                Object session = AbstractPollingIoAcceptor.this.accept(AbstractPollingIoAcceptor.this.processor, handle);
                if (session == null) continue;
                AbstractPollingIoAcceptor.this.initSession(session, null, null);
                ((AbstractIoSession)session).getProcessor().add(session);
            }
        }
    }
}

