/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet63FriendRequest
extends Packet {
    public String username;
    public String requester;

    private Packet63FriendRequest() {
    }

    public Packet63FriendRequest(String username, String requester) {
        this.username = username;
        this.requester = requester;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
        buf.writeString(this.requester);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.username = buf.readString();
        this.requester = buf.readString();
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle63FriendRequest(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{requester=" + this.requester + ", target=" + this.username + "}";
    }
}

