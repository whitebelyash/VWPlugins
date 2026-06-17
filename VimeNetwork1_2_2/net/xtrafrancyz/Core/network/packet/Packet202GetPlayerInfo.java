/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet202GetPlayerInfo
extends ResponsePacket {
    public static final short FLAG_META = 1;
    public static final short FLAG_MINIDOT = 2;
    public static final short FLAG_RANK = 4;
    public static final short FLAG_LOC_BUKKIT = 8;
    public static final short FLAG_LOC_BUNGEE = 16;
    public static final short FLAG_STATS = 32;
    public static final short FLAG_ACHIEVEMENTS = 64;
    public static final short FLAG_SWITCH_DATA = 128;
    public static final short FLAG_GUILD = 256;
    public static final short FLAG_ALL = 511;
    public String username;
    public int queryFlags = 0;

    private Packet202GetPlayerInfo() {
    }

    public Packet202GetPlayerInfo(String username, int flags) {
        this.username = username;
        this.queryFlags = flags;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeString(this.username);
        buf.writeShort((short)this.queryFlags);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.username = buf.readString();
        this.queryFlags = buf.readShort();
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle202GetPlayerInfo(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{player=" + this.username + ", flags=" + Integer.toBinaryString(this.queryFlags) + "}";
    }
}

