/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.polling;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultConnectFuture;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.util.ExceptionMonitor;

public abstract class AbstractPollingIoConnector<T extends AbstractIoSession, H>
extends AbstractIoConnector {
    private final Queue<ConnectionRequest> connectQueue = new ConcurrentLinkedQueue<ConnectionRequest>();
    private final Queue<ConnectionRequest> cancelQueue = new ConcurrentLinkedQueue<ConnectionRequest>();
    private final IoProcessor<T> processor;
    private final boolean createdProcessor;
    private final AbstractIoService.ServiceOperationFuture disposalFuture = new AbstractIoService.ServiceOperationFuture();
    private volatile boolean selectable;
    private final AtomicReference<Connector> connectorRef = new AtomicReference();

    protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass), true);
    }

    protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class<? extends IoProcessor<T>> processorClass, int processorCount) {
        this(sessionConfig, null, new SimpleIoProcessorPool(processorClass, processorCount), true);
    }

    protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, IoProcessor<T> processor) {
        this(sessionConfig, null, processor, false);
    }

    protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor) {
        this(sessionConfig, executor, processor, false);
    }

    private AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor<T> processor, boolean createdProcessor) {
        super(sessionConfig, executor);
        if (processor == null) {
            throw new IllegalArgumentException("processor");
        }
        this.processor = processor;
        this.createdProcessor = createdProcessor;
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

    protected abstract void init() throws Exception;

    protected abstract void destroy() throws Exception;

    protected abstract H newHandle(SocketAddress var1) throws Exception;

    protected abstract boolean connect(H var1, SocketAddress var2) throws Exception;

    protected abstract boolean finishConnect(H var1) throws Exception;

    protected abstract T newSession(IoProcessor<T> var1, H var2) throws Exception;

    protected abstract void close(H var1) throws Exception;

    protected abstract void wakeup();

    protected abstract int select(int var1) throws Exception;

    protected abstract Iterator<H> selectedHandles();

    protected abstract Iterator<H> allHandles();

    protected abstract void register(H var1, ConnectionRequest var2) throws Exception;

    protected abstract ConnectionRequest getConnectionRequest(H var1);

    @Override
    protected final void dispose0() throws Exception {
        this.startupWorker();
        this.wakeup();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected final ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        H handle = null;
        boolean success = false;
        try {
            handle = this.newHandle(localAddress);
            if (this.connect(handle, remoteAddress)) {
                DefaultConnectFuture future = new DefaultConnectFuture();
                T session = this.newSession(this.processor, handle);
                this.initSession((IoSession)session, future, sessionInitializer);
                ((AbstractIoSession)session).getProcessor().add(session);
                success = true;
                DefaultConnectFuture defaultConnectFuture = future;
                return defaultConnectFuture;
            }
            success = true;
        }
        catch (Exception e) {
            ConnectFuture connectFuture = DefaultConnectFuture.newFailedFuture(e);
            return connectFuture;
        }
        finally {
            if (!success && handle != null) {
                try {
                    this.close(handle);
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
            }
        }
        ConnectionRequest request = new ConnectionRequest(handle, sessionInitializer);
        this.connectQueue.add(request);
        this.startupWorker();
        this.wakeup();
        return request;
    }

    private void startupWorker() {
        Connector connector;
        if (!this.selectable) {
            this.connectQueue.clear();
            this.cancelQueue.clear();
        }
        if ((connector = this.connectorRef.get()) == null && this.connectorRef.compareAndSet(null, connector = new Connector())) {
            this.executeWorker(connector);
        }
    }

    private int registerNew() {
        ConnectionRequest req;
        int nHandles = 0;
        while ((req = this.connectQueue.poll()) != null) {
            Object handle = req.handle;
            try {
                this.register(handle, req);
                ++nHandles;
            }
            catch (Exception e) {
                req.setException(e);
                try {
                    this.close(handle);
                }
                catch (Exception e2) {
                    ExceptionMonitor.getInstance().exceptionCaught(e2);
                }
            }
        }
        return nHandles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int cancelKeys() {
        ConnectionRequest req;
        int nHandles = 0;
        while ((req = this.cancelQueue.poll()) != null) {
            Object handle = req.handle;
            try {
                this.close(handle);
            }
            catch (Exception e) {
                ExceptionMonitor.getInstance().exceptionCaught(e);
            }
            finally {
                ++nHandles;
            }
        }
        if (nHandles > 0) {
            this.wakeup();
        }
        return nHandles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int processConnections(Iterator<H> handlers) {
        int nHandles = 0;
        while (handlers.hasNext()) {
            H handle = handlers.next();
            handlers.remove();
            ConnectionRequest connectionRequest = this.getConnectionRequest(handle);
            if (connectionRequest == null) continue;
            boolean success = false;
            try {
                if (this.finishConnect(handle)) {
                    T session = this.newSession(this.processor, handle);
                    this.initSession((IoSession)session, connectionRequest, connectionRequest.getSessionInitializer());
                    ((AbstractIoSession)session).getProcessor().add(session);
                    ++nHandles;
                }
                success = true;
            }
            catch (Exception e) {
                connectionRequest.setException(e);
            }
            finally {
                if (success) continue;
                this.cancelQueue.offer(connectionRequest);
            }
        }
        return nHandles;
    }

    private void processTimedOutSessions(Iterator<H> handles) {
        long currentTime = System.currentTimeMillis();
        while (handles.hasNext()) {
            H handle = handles.next();
            ConnectionRequest connectionRequest = this.getConnectionRequest(handle);
            if (connectionRequest == null || currentTime < connectionRequest.deadline) continue;
            connectionRequest.setException(new ConnectException("Connection timed out."));
            this.cancelQueue.offer(connectionRequest);
        }
    }

    public final class ConnectionRequest
    extends DefaultConnectFuture {
        private final H handle;
        private final long deadline;
        private final IoSessionInitializer<? extends ConnectFuture> sessionInitializer;

        public ConnectionRequest(H handle, IoSessionInitializer<? extends ConnectFuture> callback) {
            this.handle = handle;
            long timeout = AbstractPollingIoConnector.this.getConnectTimeoutMillis();
            this.deadline = timeout <= 0L ? Long.MAX_VALUE : System.currentTimeMillis() + timeout;
            this.sessionInitializer = callback;
        }

        public H getHandle() {
            return this.handle;
        }

        public long getDeadline() {
            return this.deadline;
        }

        public IoSessionInitializer<? extends ConnectFuture> getSessionInitializer() {
            return this.sessionInitializer;
        }

        @Override
        public boolean cancel() {
            boolean justCancelled;
            if (!this.isDone() && (justCancelled = super.cancel())) {
                AbstractPollingIoConnector.this.cancelQueue.add(this);
                AbstractPollingIoConnector.this.startupWorker();
                AbstractPollingIoConnector.this.wakeup();
            }
            return true;
        }
    }

    private class Connector
    implements Runnable {
        private Connector() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            assert (AbstractPollingIoConnector.this.connectorRef.get() == this);
            int nHandles = 0;
            while (AbstractPollingIoConnector.this.selectable) {
                try {
                    int timeout = (int)Math.min(AbstractPollingIoConnector.this.getConnectTimeoutMillis(), 1000L);
                    int selected = AbstractPollingIoConnector.this.select(timeout);
                    if ((nHandles += AbstractPollingIoConnector.this.registerNew()) == 0) {
                        AbstractPollingIoConnector.this.connectorRef.set(null);
                        if (AbstractPollingIoConnector.this.connectQueue.isEmpty()) {
                            assert (AbstractPollingIoConnector.this.connectorRef.get() != this);
                            break;
                        }
                        if (!AbstractPollingIoConnector.this.connectorRef.compareAndSet(null, this)) {
                            assert (AbstractPollingIoConnector.this.connectorRef.get() != this);
                            break;
                        }
                        assert (AbstractPollingIoConnector.this.connectorRef.get() == this);
                    }
                    if (selected > 0) {
                        nHandles -= AbstractPollingIoConnector.this.processConnections(AbstractPollingIoConnector.this.selectedHandles());
                    }
                    AbstractPollingIoConnector.this.processTimedOutSessions(AbstractPollingIoConnector.this.allHandles());
                    nHandles -= AbstractPollingIoConnector.this.cancelKeys();
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
            if (!AbstractPollingIoConnector.this.selectable) return;
            if (!AbstractPollingIoConnector.this.isDisposing()) return;
            AbstractPollingIoConnector.this.selectable = false;
            try {
                if (!AbstractPollingIoConnector.this.createdProcessor) return;
                AbstractPollingIoConnector.this.processor.dispose();
                return;
            }
            finally {
                try {
                    Object e = AbstractPollingIoConnector.this.disposalLock;
                    synchronized (e) {
                        if (AbstractPollingIoConnector.this.isDisposing()) {
                            AbstractPollingIoConnector.this.destroy();
                        }
                    }
                }
                catch (Exception e) {
                    ExceptionMonitor.getInstance().exceptionCaught(e);
                }
                finally {
                    AbstractPollingIoConnector.this.disposalFuture.setDone();
                }
            }
        }
    }
}

