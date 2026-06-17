/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet303ProtocolCheck
extends ResponsePacket {
    public int version;

    private Packet303ProtocolCheck() {
    }

    public Packet303ProtocolCheck(int version) {
        this.version = version;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.version);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.version = buf.readInt();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle303ProtocolCheck(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{version=" + this.version + "}";
    }
}

