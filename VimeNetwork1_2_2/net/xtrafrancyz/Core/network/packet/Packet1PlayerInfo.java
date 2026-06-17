/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet1PlayerInfo
extends ResponsePacket {
    public int id;
    public String username;
    public int coins;
    public int exp;
    public String rank = null;
    public String bukkit = null;
    public String bungee = null;
    public Map<String, String> meta = null;
    public int[] minidotItems = null;
    public Map<String, Integer> minidotDressed = null;
    public int[][] stats = null;
    public int[][] achievements = null;
    public int queryFlags = 0;
    public CoreByteMap switchData = null;
    public int guildId = 0;

    private Packet1PlayerInfo() {
    }

    public Packet1PlayerInfo(int id, String username) {
        this.username = username;
        this.id = id;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.writeInt(this.id);
        buf.writeString(this.username);
        buf.writeVarInt(this.coins);
        buf.writeInt(this.exp);
        buf.writeShort((short)this.queryFlags);
        if ((this.queryFlags & 4) == 4) {
            buf.writeString(this.rank);
        }
        if ((this.queryFlags & 8) == 8) {
            buf.writeStringNullable(this.bukkit);
        }
        if ((this.queryFlags & 0x10) == 16) {
            buf.writeStringNullable(this.bungee);
        }
        if ((this.queryFlags & 1) == 1) {
            buf.writeVarInt(this.meta.size());
            for (Map.Entry<String, String> entry : this.meta.entrySet()) {
                buf.writeString(entry.getKey());
                buf.writeString(entry.getValue());
            }
        }
        if ((this.queryFlags & 2) == 2) {
            buf.writeVarInt(this.minidotItems.length);
            for (Object id : (Iterator<Map.Entry<String, Integer>>)this.minidotItems) {
                buf.writeInt((int)id);
            }
            buf.write((byte)this.minidotDressed.size());
            for (Map.Entry<String, Integer> entry : this.minidotDressed.entrySet()) {
                buf.writeString(entry.getKey());
                buf.writeInt(entry.getValue());
            }
        }
        if ((this.queryFlags & 0x20) == 32) {
            buf.writeVarInt(this.stats.length);
            for (Object stat : (Iterator<Map.Entry<String, Integer>>)this.stats) {
                buf.writeVarInt((int)stat[0]);
                buf.writeVarInt((int)stat[1]);
            }
        }
        if ((this.queryFlags & 0x40) == 64) {
            buf.writeVarInt(this.achievements.length);
            for (Iterator<Map.Entry<String, Integer>> a : (Iterator<Map.Entry<String, Integer>>)this.achievements) {
                buf.writeVarInt((int)a[0]);
                buf.writeInt((int)a[1]);
            }
        }
        if ((this.queryFlags & 0x80) == 128) {
            buf.writeByteArray(this.switchData.toByteArray());
        }
        if ((this.queryFlags & 0x100) == 256) {
            buf.writeVarInt(this.guildId);
        }
    }

    @Override
    public void read0(Buf buf) throws Exception {
        int i;
        int i2;
        int size;
        this.id = buf.readInt();
        this.username = buf.readString();
        this.coins = buf.readVarInt();
        this.exp = buf.readInt();
        this.queryFlags = buf.readShort();
        if ((this.queryFlags & 4) == 4) {
            this.rank = buf.readString();
        }
        if ((this.queryFlags & 8) == 8) {
            this.bukkit = buf.readStringNullable();
        }
        if ((this.queryFlags & 0x10) == 16) {
            this.bungee = buf.readStringNullable();
        }
        if ((this.queryFlags & 1) == 1) {
            size = buf.readVarInt();
            this.meta = new HashMap<String, String>(16);
            for (i2 = 0; i2 < size; ++i2) {
                this.meta.put(buf.readString(), buf.readString());
            }
        }
        if ((this.queryFlags & 2) == 2) {
            size = buf.readVarInt();
            this.minidotItems = new int[size];
            for (i2 = 0; i2 < size; ++i2) {
                this.minidotItems[i2] = buf.readInt();
            }
            size = buf.read();
            this.minidotDressed = new HashMap<String, Integer>(8);
            for (i2 = 0; i2 < size; ++i2) {
                this.minidotDressed.put(buf.readString(), buf.readInt());
            }
        }
        if ((this.queryFlags & 0x20) == 32) {
            this.stats = new int[buf.readVarInt()][];
            for (i = 0; i < this.stats.length; ++i) {
                this.stats[i] = new int[]{buf.readVarInt(), buf.readVarInt()};
            }
        }
        if ((this.queryFlags & 0x40) == 64) {
            this.achievements = new int[buf.readVarInt()][];
            for (i = 0; i < this.achievements.length; ++i) {
                this.achievements[i] = new int[]{buf.readVarInt(), buf.readInt()};
            }
        }
        if ((this.queryFlags & 0x80) == 128) {
            this.switchData = new CoreByteMap(buf.readByteArray());
        }
        if ((this.queryFlags & 0x100) == 256) {
            this.guildId = buf.readVarInt();
        }
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle1PlayerInfo(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{player=" + this.username + "}";
    }
}

