/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet204BungeesInfo
extends ResponsePacket {
    public List<Data> servers;

    private Packet204BungeesInfo() {
    }

    public Packet204BungeesInfo(List<Data> servers) {
        this.servers = servers;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeVarInt((short)this.servers.size());
        for (Data data : this.servers) {
            buf.writeString(data.id);
            buf.writeVarInt(data.online);
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        int size = buf.readVarInt();
        this.servers = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            this.servers.add(new Data(buf.readString(), buf.readVarInt()));
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle204BungeesInfo(this);
    }

    public static class Data {
        String id;
        int online;

        public Data(String id, int online) {
            this.id = id;
            this.online = online;
        }
    }
}

