/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.util.function.Consumer;
import net.xtrafrancyz.Core.network.connector.CoreCallback;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public interface CoreBukkit {
    public boolean isEnabled();

    public boolean isConnected();

    public <T extends Packet> void addHandler(Class<T> var1, Consumer<T> var2);

    public <T extends Packet> void removeHandler(Class<T> var1, Consumer<T> var2);

    public void addConnectListener(Runnable var1);

    public void addDisconnectListener(Runnable var1);

    public void sendPacket(Packet var1);

    public void sendPacket(ResponsePacket var1, CoreCallback var2, long var3);

    public void sendPacket(ResponsePacket var1, CoreCallback var2, long var3, Runnable var5);
}

