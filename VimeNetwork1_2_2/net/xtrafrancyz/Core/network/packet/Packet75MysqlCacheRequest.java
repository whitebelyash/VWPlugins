/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet76MysqlCacheResponse;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet75MysqlCacheRequest
extends ResponsePacket {
    public String table;
    public Object key;
    public List<String> columns;

    public Packet75MysqlCacheRequest() {
    }

    public Packet75MysqlCacheRequest(String table, Object key, List<String> columns) {
        this.table = table;
        this.key = key;
        this.columns = columns;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.table);
        Packet76MysqlCacheResponse.writeValue(buf, this.key);
        buf.writeVarInt(this.columns.size());
        this.columns.forEach(buf::writeString);
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        this.table = buf.readString();
        this.key = Packet76MysqlCacheResponse.readValue(buf);
        String[] temp = new String[buf.readVarInt()];
        for (int i = 0; i < temp.length; ++i) {
            temp[i] = buf.readString();
        }
        this.columns = Arrays.asList(temp);
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle75MysqlCacheRequest(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{table=" + this.table + ", key=" + this.key + ", columns=" + this.columns + "}";
    }
}

