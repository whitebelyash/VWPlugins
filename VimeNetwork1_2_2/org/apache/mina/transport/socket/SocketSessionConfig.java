/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

public interface SocketSessionConfig
extends IoSessionConfig {
    public boolean isReuseAddress();

    public void setReuseAddress(boolean var1);

    public int getReceiveBufferSize();

    public void setReceiveBufferSize(int var1);

    public int getSendBufferSize();

    public void setSendBufferSize(int var1);

    public int getTrafficClass();

    public void setTrafficClass(int var1);

    public boolean isKeepAlive();

    public void setKeepAlive(boolean var1);

    public boolean isOobInline();

    public void setOobInline(boolean var1);

    public int getSoLinger();

    public void setSoLinger(int var1);

    public boolean isTcpNoDelay();

    public void setTcpNoDelay(boolean var1);
}

