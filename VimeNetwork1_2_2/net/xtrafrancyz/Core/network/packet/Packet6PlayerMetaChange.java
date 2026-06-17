/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet6PlayerMetaChange
extends Packet {
    public int userid;
    public String key;
    public String value;

    private Packet6PlayerMetaChange() {
    }

    public Packet6PlayerMetaChange(int userid, String key, String value) {
        this.userid = userid;
        this.key = key;
        this.value = value;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.writeString(this.key);
        buf.writeStringNullable(this.value);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.key = buf.readString();
        this.value = buf.readStringNullable();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle6PlayerMetaChange(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{userid=" + this.userid + ", key=" + this.key + ", value=" + this.value + "}";
    }
}

