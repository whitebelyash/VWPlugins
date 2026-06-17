/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet52CustomMessage
extends ResponsePacket {
    public String tag;
    public Scope scope = Scope.ALL;
    public String receiver = null;
    public CoreByteMap data = new CoreByteMap();

    private Packet52CustomMessage() {
    }

    public Packet52CustomMessage(String tag) {
        this.tag = tag;
    }

    public Packet52CustomMessage(String tag, Scope scope) {
        this.tag = tag;
        this.scope = scope;
    }

    public Packet52CustomMessage(String tag, Scope scope, String receiver) {
        this.tag = tag;
        this.scope = scope;
        this.receiver = receiver;
    }

    public Packet52CustomMessage put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.tag);
        buf.write((byte)this.scope.ordinal());
        if (this.scope.hasData) {
            buf.writeString(this.receiver);
        }
        buf.writeByteArray(this.data.toByteArray());
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.tag = buf.readString();
        this.scope = Scope.values()[buf.read()];
        if (this.scope.hasData) {
            this.receiver = buf.readString();
        }
        this.data = new CoreByteMap(buf.readByteArray());
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle52CustomMessage(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{tag=" + this.tag + ", scope=" + this.scope.name() + ", dataSize=" + this.data.size() + "}";
    }

    public static enum Scope {
        ALL(false),
        ALL_BUNGEE(false),
        ALL_BUKKIT(false),
        SELECTED_BUNGEE(true),
        SELECTED_BUKKIT(true),
        BUNGEE_OF_PLAYER(true),
        BUKKIT_OF_PLAYER(true),
        CORE(false);

        boolean hasData;

        private Scope(boolean hasData) {
            this.hasData = hasData;
        }
    }
}

