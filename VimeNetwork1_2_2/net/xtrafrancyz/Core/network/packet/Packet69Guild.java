/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet69Guild
extends ResponsePacket {
    public int userid;
    public Action action;
    public CoreByteMap data;

    public Packet69Guild() {
    }

    public Packet69Guild(int userid, Action action) {
        this.userid = userid;
        this.action = action;
        this.data = new CoreByteMap();
    }

    public Packet69Guild put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeInt(this.userid);
        buf.write((byte)this.action.ordinal());
        buf.writeByteArray(this.data.toByteArray());
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.userid = buf.readInt();
        this.action = Action.values()[buf.read()];
        this.data = new CoreByteMap(buf.readByteArray());
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle69Guild(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{userid=" + this.userid + ", action=" + (Object)((Object)this.action) + ", dataSize=" + this.data.size() + "}";
    }

    public static enum Action {
        CREATE,
        DISBAND,
        INVITE,
        ACCEPT,
        KICK,
        PROMOTE,
        DEMOTE,
        TRANSFER,
        LEAVE,
        RENAME,
        MOTD,
        DEPOSIT,
        PARTY,
        TOP,
        LIST,
        SET_TAG,
        SET_COLOR,
        UPDATE,
        MESSAGE,
        MENU,
        OPEN_MENU,
        UPGRADE_PERK,
        ADM_RELOAD,
        ADM_ADD_EXP;

    }
}

