/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet104OnBukkitDisconnect
extends Packet {
    public String id;

    private Packet104OnBukkitDisconnect() {
    }

    public Packet104OnBukkitDisconnect(String id) {
        this.id = id;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeString(this.id);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.id = buf.readString();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle104OnBukkitDisconnect(this);
    }
}

