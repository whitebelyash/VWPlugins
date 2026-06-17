/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet9PlayerStatChange
extends Packet {
    public int userid;
    public int stat;
    public int amount;

    private Packet9PlayerStatChange() {
    }

    public Packet9PlayerStatChange(int userid, int stat, int amount) {
        this.userid = userid;
        this.stat = stat;
        this.amount = amount;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeVarInt(this.stat);
        buf.writeSignedVarInt(this.amount);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.stat = buf.readVarInt();
        this.amount = buf.readSignedVarInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle9PlayerStatChange(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{userid=" + this.userid + ", stat=" + this.stat + ", amount=" + this.amount + "}";
    }
}

