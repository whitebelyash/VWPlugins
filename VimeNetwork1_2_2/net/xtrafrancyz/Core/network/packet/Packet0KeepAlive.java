/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet0KeepAlive
extends Packet {
    @Override
    public void write0(Buf buf) throws Exception {
    }

    @Override
    public void read0(Buf buf) throws Exception {
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle0KeepAlive(this);
    }
}

