/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

public interface DatagramSessionConfig
extends IoSessionConfig {
    public boolean isBroadcast();

    public void setBroadcast(boolean var1);

    public boolean isReuseAddress();

    public void setReuseAddress(boolean var1);

    public int getReceiveBufferSize();

    public void setReceiveBufferSize(int var1);

    public int getSendBufferSize();

    public void setSendBufferSize(int var1);

    public int getTrafficClass();

    public void setTrafficClass(int var1);

    public boolean isCloseOnPortUnreachable();

    public void setCloseOnPortUnreachable(boolean var1);
}

