/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.net.SocketAddress;
import java.util.concurrent.Executor;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;

public abstract class AbstractIoConnector
extends AbstractIoService
implements IoConnector {
    private long connectTimeoutCheckInterval = 50L;
    private long connectTimeoutInMillis = 60000L;
    private SocketAddress defaultRemoteAddress;
    private SocketAddress defaultLocalAddress;

    protected AbstractIoConnector(IoSessionConfig sessionConfig, Executor executor) {
        super(sessionConfig, executor);
    }

    public long getConnectTimeoutCheckInterval() {
        return this.connectTimeoutCheckInterval;
    }

    public void setConnectTimeoutCheckInterval(long minimumConnectTimeout) {
        if (this.getConnectTimeoutMillis() < minimumConnectTimeout) {
            this.connectTimeoutInMillis = minimumConnectTimeout;
        }
        this.connectTimeoutCheckInterval = minimumConnectTimeout;
    }

    @Override
    public final int getConnectTimeout() {
        return (int)this.connectTimeoutInMillis / 1000;
    }

    @Override
    public final long getConnectTimeoutMillis() {
        return this.connectTimeoutInMillis;
    }

    @Override
    public final void setConnectTimeout(int connectTimeout) {
        this.setConnectTimeoutMillis((long)connectTimeout * 1000L);
    }

    @Override
    public final void setConnectTimeoutMillis(long connectTimeoutInMillis) {
        if (connectTimeoutInMillis <= this.connectTimeoutCheckInterval) {
            this.connectTimeoutCheckInterval = connectTimeoutInMillis;
        }
        this.connectTimeoutInMillis = connectTimeoutInMillis;
    }

    @Override
    public SocketAddress getDefaultRemoteAddress() {
        return this.defaultRemoteAddress;
    }

    @Override
    public final void setDefaultLocalAddress(SocketAddress localAddress) {
        this.defaultLocalAddress = localAddress;
    }

    @Override
    public final SocketAddress getDefaultLocalAddress() {
        return this.defaultLocalAddress;
    }

    @Override
    public final void setDefaultRemoteAddress(SocketAddress defaultRemoteAddress) {
        if (defaultRemoteAddress == null) {
            throw new IllegalArgumentException("defaultRemoteAddress");
        }
        if (!this.getTransportMetadata().getAddressType().isAssignableFrom(defaultRemoteAddress.getClass())) {
            throw new IllegalArgumentException("defaultRemoteAddress type: " + defaultRemoteAddress.getClass() + " (expected: " + this.getTransportMetadata().getAddressType() + ")");
        }
        this.defaultRemoteAddress = defaultRemoteAddress;
    }

    @Override
    public final ConnectFuture connect() {
        SocketAddress defaultRemoteAddress = this.getDefaultRemoteAddress();
        if (defaultRemoteAddress == null) {
            throw new IllegalStateException("defaultRemoteAddress is not set.");
        }
        return this.connect(defaultRemoteAddress, null, null);
    }

    @Override
    public ConnectFuture connect(IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        SocketAddress defaultRemoteAddress = this.getDefaultRemoteAddress();
        if (defaultRemoteAddress == null) {
            throw new IllegalStateException("defaultRemoteAddress is not set.");
        }
        return this.connect(defaultRemoteAddress, null, sessionInitializer);
    }

    @Override
    public final ConnectFuture connect(SocketAddress remoteAddress) {
        return this.connect(remoteAddress, null, null);
    }

    @Override
    public ConnectFuture connect(SocketAddress remoteAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        return this.connect(remoteAddress, null, sessionInitializer);
    }

    @Override
    public ConnectFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
        return this.connect(remoteAddress, localAddress, null);
    }

    @Override
    public final ConnectFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        if (this.isDisposing()) {
            throw new IllegalStateException("The connector is being disposed.");
        }
        if (remoteAddress == null) {
            throw new IllegalArgumentException("remoteAddress");
        }
        if (!this.getTransportMetadata().getAddressType().isAssignableFrom(remoteAddress.getClass())) {
            throw new IllegalArgumentException("remoteAddress type: " + remoteAddress.getClass() + " (expected: " + this.getTransportMetadata().getAddressType() + ")");
        }
        if (localAddress != null && !this.getTransportMetadata().getAddressType().isAssignableFrom(localAddress.getClass())) {
            throw new IllegalArgumentException("localAddress type: " + localAddress.getClass() + " (expected: " + this.getTransportMetadata().getAddressType() + ")");
        }
        if (this.getHandler() == null) {
            if (this.getSessionConfig().isUseReadOperation()) {
                this.setHandler(new IoHandler(){

                    @Override
                    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    }

                    @Override
                    public void messageReceived(IoSession session, Object message) throws Exception {
                    }

                    @Override
                    public void messageSent(IoSession session, Object message) throws Exception {
                    }

                    @Override
                    public void sessionClosed(IoSession session) throws Exception {
                    }

                    @Override
                    public void sessionCreated(IoSession session) throws Exception {
                    }

                    @Override
                    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
                    }

                    @Override
                    public void sessionOpened(IoSession session) throws Exception {
                    }

                    @Override
                    public void inputClosed(IoSession session) throws Exception {
                    }
                });
            } else {
                throw new IllegalStateException("handler is not set.");
            }
        }
        return this.connect0(remoteAddress, localAddress, sessionInitializer);
    }

    protected abstract ConnectFuture connect0(SocketAddress var1, SocketAddress var2, IoSessionInitializer<? extends ConnectFuture> var3);

    @Override
    protected final void finishSessionInitialization0(final IoSession session, IoFuture future) {
        future.addListener(new IoFutureListener<ConnectFuture>(){

            @Override
            public void operationComplete(ConnectFuture future) {
                if (future.isCanceled()) {
                    session.closeNow();
                }
            }
        });
    }

    public String toString() {
        TransportMetadata m = this.getTransportMetadata();
        return '(' + m.getProviderName() + ' ' + m.getName() + " connector: " + "managedSessionCount: " + this.getManagedSessionCount() + ')';
    }
}

