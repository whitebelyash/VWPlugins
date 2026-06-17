/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.net.SocketAddress;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IoSessionInitializer;

public interface IoConnector
extends IoService {
    public int getConnectTimeout();

    public long getConnectTimeoutMillis();

    public void setConnectTimeout(int var1);

    public void setConnectTimeoutMillis(long var1);

    public SocketAddress getDefaultRemoteAddress();

    public void setDefaultRemoteAddress(SocketAddress var1);

    public SocketAddress getDefaultLocalAddress();

    public void setDefaultLocalAddress(SocketAddress var1);

    public ConnectFuture connect();

    public ConnectFuture connect(IoSessionInitializer<? extends ConnectFuture> var1);

    public ConnectFuture connect(SocketAddress var1);

    public ConnectFuture connect(SocketAddress var1, IoSessionInitializer<? extends ConnectFuture> var2);

    public ConnectFuture connect(SocketAddress var1, SocketAddress var2);

    public ConnectFuture connect(SocketAddress var1, SocketAddress var2, IoSessionInitializer<? extends ConnectFuture> var3);
}

