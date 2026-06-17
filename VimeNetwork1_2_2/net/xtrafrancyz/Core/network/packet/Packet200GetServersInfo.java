/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet200GetServersInfo
extends ResponsePacket {
    public static final byte FULL = 1;
    public static final byte ONLINE = 2;
    public static final byte ONLINE_AND_HOST = 3;
    public byte type;
    public List<String> servers;

    private Packet200GetServersInfo() {
    }

    public Packet200GetServersInfo(byte type) {
        this.type = type;
        this.servers = new LinkedList<String>();
    }

    public Packet200GetServersInfo(byte type, String ... servers) {
        this.type = type;
        this.servers = Arrays.asList(servers);
    }

    public Packet200GetServersInfo(byte type, List<String> servers) {
        this.type = type;
        this.servers = servers;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.write(this.type);
        buf.writeVarInt(this.servers.size());
        for (String str : this.servers) {
            buf.writeString(str);
        }
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.type = buf.read();
        int size = buf.readVarInt();
        this.servers = new ArrayList<String>(size);
        for (int i = 0; i < size; ++i) {
            this.servers.add(buf.readString());
        }
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle200GetServersInfo(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{type=" + this.type + ", servers=" + this.servers + "}";
    }
}

