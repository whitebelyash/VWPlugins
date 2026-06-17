/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet102BungeeConnect
extends ResponsePacket {
    public String id;
    public String host;
    public int port;
    public int max;

    private Packet102BungeeConnect() {
    }

    public Packet102BungeeConnect(String id, String host, int port, int max) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.max = max;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeString(this.id);
        buf.writeString(this.host);
        buf.writeInt(this.port);
        buf.writeInt(this.max);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.id = buf.readString();
        this.host = buf.readString();
        this.port = buf.readInt();
        this.max = buf.readInt();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle102BungeeConnect(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{id=" + this.id + "}";
    }
}

