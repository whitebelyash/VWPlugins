/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet54PrivateMessage
extends ResponsePacket {
    public String receiver;
    public String sender;
    public String message;

    private Packet54PrivateMessage() {
    }

    public Packet54PrivateMessage(String receiver, String sender, String message) {
        this.receiver = receiver;
        this.sender = sender;
        this.message = message;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.receiver);
        buf.writeString(this.sender);
        buf.writeString(this.message);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.receiver = buf.readString();
        this.sender = buf.readString();
        this.message = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle54PrivateMessage(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{sender=" + this.sender + ", receiver=" + this.receiver + ", message=" + this.message + "}";
    }
}

