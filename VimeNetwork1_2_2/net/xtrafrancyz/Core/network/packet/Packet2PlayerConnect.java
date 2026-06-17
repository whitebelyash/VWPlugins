/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet2PlayerConnect
extends ResponsePacket {
    public String username;

    private Packet2PlayerConnect() {
    }

    public Packet2PlayerConnect(String username) {
        this.username = username;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.username = buf.readString();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle2PlayerConnect(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{player=" + this.username + "}";
    }
}

