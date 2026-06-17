/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet74QueueInfo
extends Packet {
    public int userid;
    public String path;
    public int estimatedTime;
    public int start;

    public Packet74QueueInfo() {
    }

    public Packet74QueueInfo(int userid, String path, int estimatedTime, int start) {
        this.userid = userid;
        this.path = path;
        this.estimatedTime = estimatedTime;
        this.start = start;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeStringNullable(this.path);
        buf.writeVarInt(this.estimatedTime);
        buf.writeInt(this.start);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.path = buf.readStringNullable();
        this.estimatedTime = buf.readVarInt();
        this.start = buf.readInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle74QueueInfo(this);
    }
}

