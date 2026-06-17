/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;

public class Packet56StreamAction
extends Packet {
    public String url;
    public String sender;
    public Action action;

    private Packet56StreamAction() {
    }

    public Packet56StreamAction(String sender, String url, Action action) {
        this.sender = sender;
        this.url = url;
        this.action = action;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.sender);
        buf.writeString(this.url);
        buf.write((byte)this.action.ordinal());
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.sender = buf.readString();
        this.url = buf.readString();
        this.action = Action.values()[buf.read()];
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle56StreamAction(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{action=" + (Object)((Object)this.action) + ", url=" + this.url + "}";
    }

    public static enum Action {
        ADD,
        REMOVE;

    }
}

