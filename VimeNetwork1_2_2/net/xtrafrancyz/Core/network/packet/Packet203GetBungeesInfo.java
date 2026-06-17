/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet203GetBungeesInfo
extends ResponsePacket {
    @Override
    protected void write0(Buf buf) throws Exception {
    }

    @Override
    protected void read0(Buf buf) throws Exception {
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle203GetBungeesInfo(this);
    }
}

