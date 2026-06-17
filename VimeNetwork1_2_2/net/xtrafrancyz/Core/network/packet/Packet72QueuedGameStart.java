/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet72QueuedGameStart
extends Packet {
    public int id;
    public int[] players;
    public String path;

    public Packet72QueuedGameStart() {
    }

    public Packet72QueuedGameStart(int id, int[] players, String path) {
        this.id = id;
        this.players = players;
        this.path = path;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.id);
        buf.writeVarInt(this.players.length);
        for (int userid : this.players) {
            buf.writeInt(userid);
        }
        buf.writeString(this.path);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.id = buf.readInt();
        this.players = new int[buf.readVarInt()];
        for (int i = 0; i < this.players.length; ++i) {
            this.players[i] = buf.readInt();
        }
        this.path = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle72QueuedGameStart(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{id=" + this.id + ", path=" + this.path + ", players=" + Arrays.toString(this.players) + "}";
    }
}

