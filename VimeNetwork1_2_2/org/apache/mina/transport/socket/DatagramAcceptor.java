/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IoSessionRecycler;
import org.apache.mina.transport.socket.DatagramSessionConfig;

public interface DatagramAcceptor
extends IoAcceptor {
    @Override
    public InetSocketAddress getLocalAddress();

    @Override
    public InetSocketAddress getDefaultLocalAddress();

    public void setDefaultLocalAddress(InetSocketAddress var1);

    public IoSessionRecycler getSessionRecycler();

    public void setSessionRecycler(IoSessionRecycler var1);

    @Override
    public DatagramSessionConfig getSessionConfig();
}

