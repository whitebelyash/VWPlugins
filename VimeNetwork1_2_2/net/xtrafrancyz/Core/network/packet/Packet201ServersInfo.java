/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;
import org.apache.mina.core.buffer.IoBuffer;

public class Packet201ServersInfo
extends ResponsePacket {
    private static ThreadLocal<Deflater> localDeflater = ThreadLocal.withInitial(() -> new Deflater(1));
    public byte type;
    public List<Data> servers;

    private Packet201ServersInfo() {
    }

    public Packet201ServersInfo(byte type, List<Data> servers) {
        this.type = type;
        this.servers = servers;
    }

    @Override
    public void write0(Buf buf) throws Exception {
        buf.write(this.type);
        boolean text = this.type == 1;
        boolean host = text || this.type == 3;
        Buf bufOrig = new Buf(IoBuffer.allocate(1024));
        bufOrig.getBuffer().setAutoExpand(true);
        bufOrig.writeVarInt(this.servers.size());
        for (Data server : this.servers) {
            bufOrig.writeString(server.id);
            if (host) {
                bufOrig.writeString(server.host);
                bufOrig.writeInt(server.port);
            }
            if (text) {
                bufOrig.write((byte)server.menuText.length);
                for (String line : server.menuText) {
                    bufOrig.writeString(line);
                }
            }
            bufOrig.writeVarInt(server.online);
            bufOrig.writeVarInt(server.max);
            bufOrig.writeVarInt(server.state);
        }
        ByteBuffer byteBuffer = bufOrig.getBuffer().buf();
        int pos = byteBuffer.position();
        byte[] orig = new byte[pos];
        byteBuffer.flip();
        byteBuffer.get(orig);
        buf.writeInt(orig.length);
        Deflater deflater = localDeflater.get();
        deflater.reset();
        deflater.setInput(orig);
        deflater.finish();
        byte[] deflated = new byte[orig.length + 100];
        int size = deflater.deflate(deflated);
        COMPRESS_SAVED.addAndGet(orig.length - size);
        buf.writeByteArray(deflated, size);
    }

    @Override
    public void read0(Buf buf) throws Exception {
        this.type = buf.read();
        boolean text = this.type == 1;
        boolean host = text || this.type == 3;
        int origSize = buf.readInt();
        byte[] deflated = buf.readByteArray();
        byte[] orig = new byte[origSize];
        Inflater inflater = new Inflater();
        inflater.setInput(deflated);
        inflater.inflate(orig);
        inflater.end();
        Buf bufOrig = new Buf(IoBuffer.wrap(orig));
        int size = bufOrig.readVarInt();
        this.servers = new ArrayList<Data>(size);
        for (int i = 0; i < size; ++i) {
            Data server = new Data();
            server.id = bufOrig.readString();
            if (host) {
                server.host = bufOrig.readString();
                server.port = bufOrig.readInt();
            }
            if (text) {
                server.menuText = new String[bufOrig.read()];
                for (int k = 0; k < server.menuText.length; ++k) {
                    server.menuText[k] = bufOrig.readString();
                }
            }
            server.online = bufOrig.readVarInt();
            server.max = bufOrig.readVarInt();
            server.state = bufOrig.readVarInt();
            this.servers.add(server);
        }
    }

    @Override
    public void process0(PacketHandler handler) {
        handler.handle201ServersInfo(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{type=" + this.type + ", servers=" + this.servers.size() + "}";
    }

    public static class Data {
        public String id;
        public String host;
        public int port;
        public String[] menuText;
        public int online;
        public int max;
        public int state;
    }
}

