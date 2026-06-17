/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet11PlayerGiveExpSimple
extends Packet {
    public int userid;
    public int exp;

    public Packet11PlayerGiveExpSimple() {
    }

    public Packet11PlayerGiveExpSimple(int userid, int exp) {
        this.userid = userid;
        this.exp = exp;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeVarInt(this.exp);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.exp = buf.readVarInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle11PlayerGiveExpSimple(this);
    }
}

