/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet10PlayerGiveExp
extends Packet {
    public int[] users;
    public int exp;

    private Packet10PlayerGiveExp() {
    }

    public Packet10PlayerGiveExp(int user, int exp) {
        this(new int[]{user}, exp);
    }

    public Packet10PlayerGiveExp(int[] users, int exp) {
        this.users = users;
        this.exp = exp;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeVarInt(this.exp);
        buf.writeVarInt(this.users.length);
        for (int id : this.users) {
            buf.writeInt(id);
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.exp = buf.readVarInt();
        this.users = new int[buf.readVarInt()];
        for (int i = 0; i < this.users.length; ++i) {
            this.users[i] = buf.readInt();
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle10PlayerGiveExp(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{users=" + Arrays.toString(this.users) + ", exp=" + this.exp + "}";
    }
}

