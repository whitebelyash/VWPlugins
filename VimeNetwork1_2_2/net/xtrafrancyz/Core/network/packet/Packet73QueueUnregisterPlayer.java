/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet73QueueUnregisterPlayer
extends Packet {
    public int userid;

    public Packet73QueueUnregisterPlayer() {
    }

    public Packet73QueueUnregisterPlayer(int userid) {
        this.userid = userid;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle73QueueUnregisterPlayer(this);
    }
}

