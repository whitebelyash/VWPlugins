/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.SocketSessionConfig;

public interface SocketAcceptor
extends IoAcceptor {
    @Override
    public InetSocketAddress getLocalAddress();

    @Override
    public InetSocketAddress getDefaultLocalAddress();

    public void setDefaultLocalAddress(InetSocketAddress var1);

    public boolean isReuseAddress();

    public void setReuseAddress(boolean var1);

    public int getBacklog();

    public void setBacklog(int var1);

    @Override
    public SocketSessionConfig getSessionConfig();
}

