/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet76MysqlCacheResponse;

public class Packet77MysqlCacheUpdate
extends Packet {
    public String table;
    public Object key;
    public String[] columns;
    public Object[] values;

    public Packet77MysqlCacheUpdate() {
    }

    public Packet77MysqlCacheUpdate(String table, Object key, String[] columns, Object[] values) {
        this.table = table;
        this.key = key;
        this.columns = columns;
        this.values = values;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        buf.writeString(this.table);
        Packet76MysqlCacheResponse.writeValue(buf, this.key);
        buf.writeVarInt(this.columns.length);
        for (String string : this.columns) {
            buf.writeString(string);
        }
        for (Object object : this.values) {
            Packet76MysqlCacheResponse.writeValue(buf, object);
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        int i;
        this.table = buf.readString();
        this.key = Packet76MysqlCacheResponse.readValue(buf);
        this.columns = new String[buf.readVarInt()];
        this.values = new Object[this.columns.length];
        for (i = 0; i < this.columns.length; ++i) {
            this.columns[i] = buf.readString();
        }
        for (i = 0; i < this.columns.length; ++i) {
            this.values[i] = Packet76MysqlCacheResponse.readValue(buf);
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle77MysqlCacheUpdate(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{table=" + this.table + ", key=" + this.key + ", columns=" + Arrays.toString(this.columns) + ", values=" + Arrays.toString(this.values) + "}";
    }
}

