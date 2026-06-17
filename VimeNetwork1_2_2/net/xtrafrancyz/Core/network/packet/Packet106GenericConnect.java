/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet106GenericConnect
extends ResponsePacket {
    public String name;

    private Packet106GenericConnect() {
    }

    public Packet106GenericConnect(String name) {
        this.name = name;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.name);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.name = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle106GenericConnect(this);
    }
}

