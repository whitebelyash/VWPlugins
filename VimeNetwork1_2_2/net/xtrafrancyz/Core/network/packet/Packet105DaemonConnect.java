/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet105DaemonConnect
extends ResponsePacket {
    public String host;
    public int capacity;

    private Packet105DaemonConnect() {
    }

    public Packet105DaemonConnect(String host, int capacity) {
        this.host = host;
        this.capacity = capacity;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.host);
        buf.writeInt(this.capacity);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.host = buf.readString();
        this.capacity = buf.readInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle105DaemonConnect(this);
    }
}

