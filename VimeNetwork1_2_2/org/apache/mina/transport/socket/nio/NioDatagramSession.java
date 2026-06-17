/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioSession;

class NioDatagramSession
extends NioSession {
    static final TransportMetadata METADATA = new DefaultTransportMetadata("nio", "datagram", true, false, InetSocketAddress.class, DatagramSessionConfig.class, IoBuffer.class);
    private final InetSocketAddress localAddress;
    private final InetSocketAddress remoteAddress;

    NioDatagramSession(IoService service, DatagramChannel channel, IoProcessor<NioSession> processor, SocketAddress remoteAddress) {
        super(processor, service, channel);
        this.config = new NioDatagramSessionConfig(channel);
        this.config.setAll(service.getSessionConfig());
        this.remoteAddress = (InetSocketAddress)remoteAddress;
        this.localAddress = (InetSocketAddress)channel.socket().getLocalSocketAddress();
    }

    NioDatagramSession(IoService service, DatagramChannel channel, IoProcessor<NioSession> processor) {
        this(service, channel, processor, channel.socket().getRemoteSocketAddress());
    }

    @Override
    public DatagramSessionConfig getConfig() {
        return (DatagramSessionConfig)this.config;
    }

    @Override
    DatagramChannel getChannel() {
        return (DatagramChannel)this.channel;
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return METADATA;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return this.localAddress;
    }

    @Override
    public InetSocketAddress getServiceAddress() {
        return (InetSocketAddress)super.getServiceAddress();
    }
}

