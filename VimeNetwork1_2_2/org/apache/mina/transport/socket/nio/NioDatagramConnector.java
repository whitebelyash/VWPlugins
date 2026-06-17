/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Iterator;
import org.apache.mina.core.polling.AbstractPollingIoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.transport.socket.DatagramConnector;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramSession;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;

public final class NioDatagramConnector
extends AbstractPollingIoConnector<NioSession, DatagramChannel>
implements DatagramConnector {
    public NioDatagramConnector() {
        super((IoSessionConfig)new DefaultDatagramSessionConfig(), NioProcessor.class);
    }

    public NioDatagramConnector(int processorCount) {
        super((IoSessionConfig)new DefaultDatagramSessionConfig(), NioProcessor.class, processorCount);
    }

    public NioDatagramConnector(IoProcessor<NioSession> processor) {
        super((IoSessionConfig)new DefaultDatagramSessionConfig(), processor);
    }

    public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount) {
        super((IoSessionConfig)new DefaultDatagramSessionConfig(), processorClass, processorCount);
    }

    public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass) {
        super((IoSessionConfig)new DefaultDatagramSessionConfig(), processorClass);
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return NioDatagramSession.METADATA;
    }

    @Override
    public DatagramSessionConfig getSessionConfig() {
        return (DatagramSessionConfig)this.sessionConfig;
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
    protected void init() throws Exception {
    }

    @Override
    protected DatagramChannel newHandle(SocketAddress localAddress) throws Exception {
        DatagramChannel ch = DatagramChannel.open();
        try {
            if (localAddress != null) {
                try {
                    ch.socket().bind(localAddress);
                    this.setDefaultLocalAddress(localAddress);
                }
                catch (IOException ioe) {
                    String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
                    IOException e = new IOException(newMessage);
                    e.initCause(ioe.getCause());
                    ch.close();
                    throw e;
                }
            }
            return ch;
        }
        catch (Exception e) {
            ch.close();
            throw e;
        }
    }

    @Override
    protected boolean connect(DatagramChannel handle, SocketAddress remoteAddress) throws Exception {
        handle.connect(remoteAddress);
        return true;
    }

    @Override
    protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle) {
        NioDatagramSession session = new NioDatagramSession(this, handle, processor);
        ((AbstractIoSession)session).getConfig().setAll(this.getSessionConfig());
        return session;
    }

    @Override
    protected void close(DatagramChannel handle) throws Exception {
        handle.disconnect();
        handle.close();
    }

    @Override
    protected Iterator<DatagramChannel> allHandles() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    protected AbstractPollingIoConnector.ConnectionRequest getConnectionRequest(DatagramChannel handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void destroy() throws Exception {
    }

    @Override
    protected boolean finishConnect(DatagramChannel handle) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void register(DatagramChannel handle, AbstractPollingIoConnector.ConnectionRequest request) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int select(int timeout) throws Exception {
        return 0;
    }

    @Override
    protected Iterator<DatagramChannel> selectedHandles() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    protected void wakeup() {
    }
}

