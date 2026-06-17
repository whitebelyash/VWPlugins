/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet53Answer
extends ResponsePacket {
    public String status;

    private Packet53Answer() {
    }

    public Packet53Answer(String status, ResponsePacket request) {
        this.pResponseId = request.pResponseId;
        this.status = status;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.status);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.status = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle53Answer(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{status=" + this.status + "}";
    }
}

