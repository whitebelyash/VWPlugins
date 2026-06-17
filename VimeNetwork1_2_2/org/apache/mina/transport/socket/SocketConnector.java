/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;

public interface SocketConnector
extends IoConnector {
    @Override
    public InetSocketAddress getDefaultRemoteAddress();

    @Override
    public SocketSessionConfig getSessionConfig();

    public void setDefaultRemoteAddress(InetSocketAddress var1);
}

