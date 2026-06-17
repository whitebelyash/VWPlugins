/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.DefaultIoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.AbstractIoSessionConfig;
import org.apache.mina.core.session.DefaultIoSessionDataStructureFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public class DummySession
extends AbstractIoSession {
    private static final TransportMetadata TRANSPORT_METADATA = new DefaultTransportMetadata("mina", "dummy", false, false, SocketAddress.class, IoSessionConfig.class, Object.class);
    private static final SocketAddress ANONYMOUS_ADDRESS = new SocketAddress(){
        private static final long serialVersionUID = -496112902353454179L;

        public String toString() {
            return "?";
        }
    };
    private volatile IoService service;
    private volatile IoSessionConfig config = new AbstractIoSessionConfig(){};
    private final IoFilterChain filterChain = new DefaultIoFilterChain(this);
    private final IoProcessor<IoSession> processor;
    private volatile IoHandler handler = new IoHandlerAdapter();
    private volatile SocketAddress localAddress = ANONYMOUS_ADDRESS;
    private volatile SocketAddress remoteAddress = ANONYMOUS_ADDRESS;
    private volatile TransportMetadata transportMetadata = TRANSPORT_METADATA;

    public DummySession() {
        super(new AbstractIoAcceptor(new AbstractIoSessionConfig(){}, new Executor(){

            @Override
            public void execute(Runnable command) {
            }
        }){

            @Override
            protected Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws Exception {
                throw new UnsupportedOperationException();
            }

            @Override
            protected void unbind0(List<? extends SocketAddress> localAddresses) throws Exception {
                throw new UnsupportedOperationException();
            }

            @Override
            public IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
                throw new UnsupportedOperationException();
            }

            @Override
            public TransportMetadata getTransportMetadata() {
                return TRANSPORT_METADATA;
            }

            @Override
            protected void dispose0() throws Exception {
            }

            @Override
            public IoSessionConfig getSessionConfig() {
                return this.sessionConfig;
            }
        });
        this.processor = new IoProcessor<IoSession>(){

            @Override
            public void add(IoSession session) {
            }

            @Override
            public void flush(IoSession session) {
                DummySession s = (DummySession)session;
                WriteRequest req = s.getWriteRequestQueue().poll(session);
                if (req != null) {
                    Object m = req.getMessage();
                    if (m instanceof FileRegion) {
                        FileRegion file = (FileRegion)m;
                        try {
                            file.getFileChannel().position(file.getPosition() + file.getRemainingBytes());
                            file.update(file.getRemainingBytes());
                        }
                        catch (IOException e) {
                            s.getFilterChain().fireExceptionCaught(e);
                        }
                    }
                    DummySession.this.getFilterChain().fireMessageSent(req);
                }
            }

            @Override
            public void write(IoSession session, WriteRequest writeRequest) {
                WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
                writeRequestQueue.offer(session, writeRequest);
                if (!session.isWriteSuspended()) {
                    this.flush(session);
                }
            }

            @Override
            public void remove(IoSession session) {
                if (!session.getCloseFuture().isClosed()) {
                    session.getFilterChain().fireSessionClosed();
                }
            }

            @Override
            public void updateTrafficControl(IoSession session) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean isDisposed() {
                return false;
            }

            @Override
            public boolean isDisposing() {
                return false;
            }
        };
        this.service = super.getService();
        try {
            DefaultIoSessionDataStructureFactory factory = new DefaultIoSessionDataStructureFactory();
            this.setAttributeMap(factory.getAttributeMap(this));
            this.setWriteRequestQueue(factory.getWriteRequestQueue(this));
        }
        catch (Exception e) {
            throw new InternalError();
        }
    }

    @Override
    public IoSessionConfig getConfig() {
        return this.config;
    }

    public void setConfig(IoSessionConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        this.config = config;
    }

    @Override
    public IoFilterChain getFilterChain() {
        return this.filterChain;
    }

    @Override
    public IoHandler getHandler() {
        return this.handler;
    }

    public void setHandler(IoHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler");
        }
        this.handler = handler;
    }

    @Override
    public SocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    public void setLocalAddress(SocketAddress localAddress) {
        if (localAddress == null) {
            throw new IllegalArgumentException("localAddress");
        }
        this.localAddress = localAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        if (remoteAddress == null) {
            throw new IllegalArgumentException("remoteAddress");
        }
        this.remoteAddress = remoteAddress;
    }

    @Override
    public IoService getService() {
        return this.service;
    }

    public void setService(IoService service) {
        if (service == null) {
            throw new IllegalArgumentException("service");
        }
        this.service = service;
    }

    @Override
    public final IoProcessor<IoSession> getProcessor() {
        return this.processor;
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return this.transportMetadata;
    }

    public void setTransportMetadata(TransportMetadata transportMetadata) {
        if (transportMetadata == null) {
            throw new IllegalArgumentException("transportMetadata");
        }
        this.transportMetadata = transportMetadata;
    }

    @Override
    public void setScheduledWriteBytes(int byteCount) {
        super.setScheduledWriteBytes(byteCount);
    }

    @Override
    public void setScheduledWriteMessages(int messages) {
        super.setScheduledWriteMessages(messages);
    }

    public void updateThroughput(boolean force) {
        super.updateThroughput(System.currentTimeMillis(), force);
    }
}

