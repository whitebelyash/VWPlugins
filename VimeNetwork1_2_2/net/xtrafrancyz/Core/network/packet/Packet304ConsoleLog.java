/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet304ConsoleLog
extends Packet {
    public String message;

    private Packet304ConsoleLog() {
    }

    public Packet304ConsoleLog(String message) {
        this.message = message;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.message);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.message = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle304ConsoleLog(this);
    }
}

