/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.packet.Packet;

public abstract class ResponsePacket
extends Packet {
    public int pResponseId = -1;

    @Override
    public void write(Buf buf) throws Exception {
        super.write(buf);
        buf.writeInt(this.pResponseId);
    }

    @Override
    public void read(Buf buf) throws Exception {
        super.read(buf);
        this.pResponseId = buf.readInt();
    }
}

