/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet71QueueRegisterPlayer
extends Packet {
    public int userid;
    public String path;
    public boolean party;

    public Packet71QueueRegisterPlayer() {
    }

    public Packet71QueueRegisterPlayer(int userid, String path, boolean party) {
        this.userid = userid;
        this.path = path;
        this.party = party;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeString(this.path);
        buf.writeBoolean(this.party);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.path = buf.readString();
        this.party = buf.readBoolean();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle71QueueRegisterPlayer(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{userid=" + this.userid + ", path=" + this.path + "}";
    }
}

