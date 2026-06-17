/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet61SendPlayerToServer
extends Packet {
    public String username;
    public String server;
    public CoreByteMap data;

    public Packet61SendPlayerToServer() {
    }

    public Packet61SendPlayerToServer(String username, String server) {
        this(username, server, null);
    }

    public Packet61SendPlayerToServer(String username, String server, CoreByteMap data) {
        this.username = username;
        this.server = server;
        this.data = data;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
        buf.writeString(this.server);
        if (this.data == null) {
            buf.write((byte)0);
        } else {
            buf.write((byte)1);
            buf.writeByteArray(this.data.toByteArray());
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.username = buf.readString();
        this.server = buf.readString();
        if (buf.read() == 1) {
            this.data = new CoreByteMap(buf.readByteArray());
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle61SendPlayerToServer(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{player=" + this.username + ", server=" + this.server + "}";
    }
}

