/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet8PlayerGetAchievement
extends Packet {
    public int userid;
    public int achievement;

    private Packet8PlayerGetAchievement() {
    }

    public Packet8PlayerGetAchievement(int userid, int achievement) {
        this.userid = userid;
        this.achievement = achievement;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeVarInt(this.achievement);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.achievement = buf.readVarInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle8PlayerGetAchievement(this);
    }
}

