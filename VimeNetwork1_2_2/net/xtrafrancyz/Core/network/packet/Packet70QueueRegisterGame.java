/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet70QueueRegisterGame
extends Packet {
    public List<String> paths;
    public int amount;
    public int slots;

    private Packet70QueueRegisterGame() {
    }

    public Packet70QueueRegisterGame(String path, int amount, int slots) {
        this(Collections.singletonList(path), amount, slots);
    }

    public Packet70QueueRegisterGame(List<String> paths, int amount, int slots) {
        this.paths = paths;
        this.amount = amount;
        this.slots = slots;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeVarInt(this.paths.size());
        for (String path : this.paths) {
            buf.writeString(path);
        }
        buf.writeVarInt(this.amount);
        buf.writeVarInt(this.slots);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        int size = buf.readVarInt();
        this.paths = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            this.paths.add(buf.readString());
        }
        this.amount = buf.readVarInt();
        this.slots = buf.readVarInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle70QueueRegisterGame(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{paths=" + this.paths + ", amount=" + this.amount + ", slots=" + this.slots + "}";
    }
}

