/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.transport.socket.DatagramSessionConfig;

public interface DatagramConnector
extends IoConnector {
    @Override
    public InetSocketAddress getDefaultRemoteAddress();

    @Override
    public DatagramSessionConfig getSessionConfig();

    public void setDefaultRemoteAddress(InetSocketAddress var1);
}

