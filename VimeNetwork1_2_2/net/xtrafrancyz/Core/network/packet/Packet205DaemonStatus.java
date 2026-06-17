/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet205DaemonStatus
extends Packet {
    private Packet205DaemonStatus() {
    }

    @Override
    protected void write0(Buf buf) throws Exception {
    }

    @Override
    protected void read0(Buf buf) throws Exception {
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle205DaemonStatus(this);
    }
}

