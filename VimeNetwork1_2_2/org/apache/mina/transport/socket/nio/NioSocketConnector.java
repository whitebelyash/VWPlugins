/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import org.apache.mina.core.polling.AbstractPollingIoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketSession;

public final class NioSocketConnector
extends AbstractPollingIoConnector<NioSession, SocketChannel>
implements SocketConnector {
    private volatile Selector selector;

    public NioSocketConnector() {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), NioProcessor.class);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketConnector(int processorCount) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketConnector(IoProcessor<NioSession> processor) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), processor);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketConnector(Executor executor, IoProcessor<NioSession> processor) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), executor, processor);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), processorClass, processorCount);
    }

    public NioSocketConnector(Class<? extends IoProcessor<NioSession>> processorClass) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), processorClass);
    }

    @Override
    protected void init() throws Exception {
        this.selector = Selector.open();
    }

    @Override
    protected void destroy() throws Exception {
        if (this.selector != null) {
            this.selector.close();
        }
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return NioSocketSession.METADATA;
    }

    @Override
    public SocketSessionConfig getSessionConfig() {
        return (SocketSessionConfig)this.sessionConfig;
    }

    @Override
    public InetSocketAddress getDefaultRemoteAddress() {
        return (InetSocketAddress)super.getDefaultRemoteAddress();
    }

    @Override
    public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
        super.setDefaultRemoteAddress(defaultRemoteAddress);
    }

    @Override
    protected Iterator<SocketChannel> allHandles() {
        return new SocketChannelIterator(this.selector.keys());
    }

    @Override
    protected boolean connect(SocketChannel handle, SocketAddress remoteAddress) throws Exception {
        return handle.connect(remoteAddress);
    }

    @Override
    protected AbstractPollingIoConnector.ConnectionRequest getConnectionRequest(SocketChannel handle) {
        SelectionKey key = handle.keyFor(this.selector);
        if (key == null || !key.isValid()) {
            return null;
        }
        return (AbstractPollingIoConnector.ConnectionRequest)key.attachment();
    }

    @Override
    protected void close(SocketChannel handle) throws Exception {
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        handle.close();
    }

    @Override
    protected boolean finishConnect(SocketChannel handle) throws Exception {
        if (handle.finishConnect()) {
            SelectionKey key = handle.keyFor(this.selector);
            if (key != null) {
                key.cancel();
            }
            return true;
        }
        return false;
    }

    @Override
    protected SocketChannel newHandle(SocketAddress localAddress) throws Exception {
        SocketChannel ch = SocketChannel.open();
        int receiveBufferSize = this.getSessionConfig().getReceiveBufferSize();
        if (receiveBufferSize > 65535) {
            ch.socket().setReceiveBufferSize(receiveBufferSize);
        }
        if (localAddress != null) {
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
        }
        ch.configureBlocking(false);
        return ch;
    }

    @Override
    protected NioSession newSession(IoProcessor<NioSession> processor, SocketChannel handle) {
        return new NioSocketSession(this, processor, handle);
    }

    @Override
    protected void register(SocketChannel handle, AbstractPollingIoConnector.ConnectionRequest request) throws Exception {
        handle.register(this.selector, 8, request);
    }

    @Override
    protected int select(int timeout) throws Exception {
        return this.selector.select(timeout);
    }

    @Override
    protected Iterator<SocketChannel> selectedHandles() {
        return new SocketChannelIterator(this.selector.selectedKeys());
    }

    @Override
    protected void wakeup() {
        this.selector.wakeup();
    }

    private static class SocketChannelIterator
    implements Iterator<SocketChannel> {
        private final Iterator<SelectionKey> i;

        private SocketChannelIterator(Collection<SelectionKey> selectedKeys) {
            this.i = selectedKeys.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public SocketChannel next() {
            SelectionKey key = this.i.next();
            return (SocketChannel)key.channel();
        }

        @Override
        public void remove() {
            this.i.remove();
        }
    }
}

