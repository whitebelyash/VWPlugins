/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import org.apache.mina.core.polling.AbstractPollingIoAcceptor;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketAcceptor;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketSession;

public final class NioSocketAcceptor
extends AbstractPollingIoAcceptor<NioSession, ServerSocketChannel>
implements SocketAcceptor {
    private volatile Selector selector;
    private volatile SelectorProvider selectorProvider = null;

    public NioSocketAcceptor() {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), NioProcessor.class);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketAcceptor(int processorCount) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketAcceptor(IoProcessor<NioSession> processor) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), processor);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketAcceptor(Executor executor, IoProcessor<NioSession> processor) {
        super((IoSessionConfig)new DefaultSocketSessionConfig(), executor, processor);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
    }

    public NioSocketAcceptor(int processorCount, SelectorProvider selectorProvider) {
        super(new DefaultSocketSessionConfig(), NioProcessor.class, processorCount, selectorProvider);
        ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
        this.selectorProvider = selectorProvider;
    }

    @Override
    protected void init() throws Exception {
        this.selector = Selector.open();
    }

    @Override
    protected void init(SelectorProvider selectorProvider) throws Exception {
        this.selectorProvider = selectorProvider;
        this.selector = selectorProvider == null ? Selector.open() : selectorProvider.openSelector();
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
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)super.getLocalAddress();
    }

    @Override
    public InetSocketAddress getDefaultLocalAddress() {
        return (InetSocketAddress)super.getDefaultLocalAddress();
    }

    @Override
    public void setDefaultLocalAddress(InetSocketAddress localAddress) {
        this.setDefaultLocalAddress((SocketAddress)localAddress);
    }

    @Override
    protected NioSession accept(IoProcessor<NioSession> processor, ServerSocketChannel handle) throws Exception {
        SelectionKey key = null;
        if (handle != null) {
            key = handle.keyFor(this.selector);
        }
        if (key == null || !key.isValid() || !key.isAcceptable()) {
            return null;
        }
        SocketChannel ch = handle.accept();
        if (ch == null) {
            return null;
        }
        return new NioSocketSession(this, processor, ch);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected ServerSocketChannel open(SocketAddress localAddress) throws Exception {
        ServerSocketChannel channel = null;
        channel = this.selectorProvider != null ? this.selectorProvider.openServerSocketChannel() : ServerSocketChannel.open();
        boolean success = false;
        try {
            channel.configureBlocking(false);
            ServerSocket socket = channel.socket();
            socket.setReuseAddress(this.isReuseAddress());
            try {
                socket.bind(localAddress, this.getBacklog());
            }
            catch (IOException ioe) {
                String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
                IOException e = new IOException(newMessage);
                e.initCause(ioe.getCause());
                channel.close();
                throw e;
            }
            channel.register(this.selector, 16);
            success = true;
        }
        finally {
            if (!success) {
                this.close(channel);
            }
        }
        return channel;
    }

    @Override
    protected SocketAddress localAddress(ServerSocketChannel handle) throws Exception {
        return handle.socket().getLocalSocketAddress();
    }

    @Override
    protected int select() throws Exception {
        return this.selector.select();
    }

    @Override
    protected Iterator<ServerSocketChannel> selectedHandles() {
        return new ServerSocketChannelIterator(this.selector.selectedKeys());
    }

    @Override
    protected void close(ServerSocketChannel handle) throws Exception {
        SelectionKey key = handle.keyFor(this.selector);
        if (key != null) {
            key.cancel();
        }
        handle.close();
    }

    @Override
    protected void wakeup() {
        this.selector.wakeup();
    }

    private static class ServerSocketChannelIterator
    implements Iterator<ServerSocketChannel> {
        private final Iterator<SelectionKey> iterator;

        private ServerSocketChannelIterator(Collection<SelectionKey> selectedKeys) {
            this.iterator = selectedKeys.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.iterator.hasNext();
        }

        @Override
        public ServerSocketChannel next() {
            SelectionKey key = this.iterator.next();
            if (key.isValid() && key.isAcceptable()) {
                return (ServerSocketChannel)key.channel();
            }
            return null;
        }

        @Override
        public void remove() {
            this.iterator.remove();
        }
    }
}

