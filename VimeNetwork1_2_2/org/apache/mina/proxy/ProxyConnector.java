/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executor;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultConnectFuture;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.proxy.AbstractProxyIoHandler;
import org.apache.mina.proxy.filter.ProxyFilter;
import org.apache.mina.proxy.handlers.socks.SocksProxyRequest;
import org.apache.mina.proxy.session.ProxyIoSession;
import org.apache.mina.proxy.session.ProxyIoSessionInitializer;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;

public class ProxyConnector
extends AbstractIoConnector {
    private static final TransportMetadata METADATA = new DefaultTransportMetadata("proxy", "proxyconnector", false, true, InetSocketAddress.class, SocketSessionConfig.class, IoBuffer.class, FileRegion.class);
    private SocketConnector connector = null;
    private final ProxyFilter proxyFilter = new ProxyFilter();
    private ProxyIoSession proxyIoSession;
    private DefaultConnectFuture future;

    public ProxyConnector() {
        super(new DefaultSocketSessionConfig(), null);
    }

    public ProxyConnector(SocketConnector connector) {
        this(connector, new DefaultSocketSessionConfig(), null);
    }

    public ProxyConnector(SocketConnector connector, IoSessionConfig config, Executor executor) {
        super(config, executor);
        this.setConnector(connector);
    }

    @Override
    public IoSessionConfig getSessionConfig() {
        return this.connector.getSessionConfig();
    }

    public ProxyIoSession getProxyIoSession() {
        return this.proxyIoSession;
    }

    public void setProxyIoSession(ProxyIoSession proxyIoSession) {
        if (proxyIoSession == null) {
            throw new IllegalArgumentException("proxySession object cannot be null");
        }
        if (proxyIoSession.getProxyAddress() == null) {
            throw new IllegalArgumentException("proxySession.proxyAddress cannot be null");
        }
        proxyIoSession.setConnector(this);
        this.setDefaultRemoteAddress(proxyIoSession.getProxyAddress());
        this.proxyIoSession = proxyIoSession;
    }

    @Override
    protected ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        if (!this.proxyIoSession.isReconnectionNeeded()) {
            IoHandler handler = this.getHandler();
            if (!(handler instanceof AbstractProxyIoHandler)) {
                throw new IllegalArgumentException("IoHandler must be an instance of AbstractProxyIoHandler");
            }
            this.connector.setHandler(handler);
            this.future = new DefaultConnectFuture();
        }
        ConnectFuture conFuture = this.connector.connect((SocketAddress)this.proxyIoSession.getProxyAddress(), new ProxyIoSessionInitializer<ConnectFuture>(sessionInitializer, this.proxyIoSession));
        if (this.proxyIoSession.getRequest() instanceof SocksProxyRequest || this.proxyIoSession.isReconnectionNeeded()) {
            return conFuture;
        }
        return this.future;
    }

    public void cancelConnectFuture() {
        this.future.cancel();
    }

    protected ConnectFuture fireConnected(IoSession session) {
        this.future.setSession(session);
        return this.future;
    }

    public final SocketConnector getConnector() {
        return this.connector;
    }

    private void setConnector(SocketConnector connector) {
        if (connector == null) {
            throw new IllegalArgumentException("connector cannot be null");
        }
        this.connector = connector;
        String className = ProxyFilter.class.getName();
        if (connector.getFilterChain().contains(className)) {
            connector.getFilterChain().remove(className);
        }
        connector.getFilterChain().addFirst(className, this.proxyFilter);
    }

    @Override
    protected void dispose0() throws Exception {
        if (this.connector != null) {
            this.connector.dispose();
        }
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return METADATA;
    }
}

