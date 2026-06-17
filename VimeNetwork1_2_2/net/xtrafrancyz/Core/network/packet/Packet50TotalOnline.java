/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet50TotalOnline
extends Packet {
    public int online;

    private Packet50TotalOnline() {
    }

    public Packet50TotalOnline(int online) {
        this.online = online;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeInt(this.online);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.online = buf.readInt();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle50TotalOnline(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{online=" + this.online + "}";
    }
}

