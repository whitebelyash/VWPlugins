/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet305ChatLog
extends Packet {
    public String username;
    public String message;

    public Packet305ChatLog() {
    }

    public Packet305ChatLog(String username, String message) {
        this.username = username;
        this.message = message;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
        buf.writeString(this.message);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.username = buf.readString();
        this.message = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle305ChatLog(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{username=" + this.username + ", message=" + this.message + "}";
    }
}

