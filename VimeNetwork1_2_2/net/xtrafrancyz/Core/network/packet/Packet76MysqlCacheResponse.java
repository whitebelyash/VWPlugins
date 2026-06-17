/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;

public class Packet76MysqlCacheResponse
extends ResponsePacket {
    private static final byte INT_TYPE = 0;
    private static final byte UNSIGNED_VARINT_TYPE = 1;
    private static final byte SIGNED_VARINT_TYPE = 2;
    private static final byte STRING_TYPE = 3;
    private static final byte NULL_TYPE = 4;
    public Object[] values;

    public Packet76MysqlCacheResponse() {
    }

    public Packet76MysqlCacheResponse(Object[] values) {
        this.values = values;
    }

    @Override
    protected void write0(Buf buf) throws Exception {
        if (this.values == null) {
            buf.writeVarInt(0);
        } else {
            buf.writeVarInt(this.values.length);
            for (Object value : this.values) {
                Packet76MysqlCacheResponse.writeValue(buf, value);
            }
        }
    }

    @Override
    protected void read0(Buf buf) throws Exception {
        int size = buf.readVarInt();
        if (size == 0) {
            this.values = null;
        } else {
            this.values = new Object[size];
            for (int i = 0; i < this.values.length; ++i) {
                this.values[i] = Packet76MysqlCacheResponse.readValue(buf);
            }
        }
    }

    @Override
    protected void process0(PacketHandler handler) {
        handler.handle76MysqlCacheResponse(this);
    }

    @Override
    public String toString() {
        return super.toString() + "{values=" + Arrays.toString(this.values) + "}";
    }

    static Object readValue(Buf buf) {
        switch (buf.read()) {
            case 0: {
                return buf.readInt();
            }
            case 1: {
                return buf.readVarInt();
            }
            case 2: {
                return buf.readSignedVarInt();
            }
            case 3: {
                return buf.readString();
            }
            case 4: {
                return null;
            }
        }
        return null;
    }

    static void writeValue(Buf buf, Object value) {
        if (value == null) {
            buf.write((byte)4);
        } else {
            Class<?> clazz = value.getClass();
            if (clazz == Integer.class) {
                int num = (Integer)value;
                if (num >= 0 && num < 0x200000) {
                    buf.write((byte)1);
                    buf.writeVarInt(num);
                } else if (num < 0 && num > -1048576) {
                    buf.write((byte)2);
                    buf.writeSignedVarInt(num);
                } else {
                    buf.write((byte)0);
                    buf.writeInt(num);
                }
            } else if (clazz == String.class) {
                buf.write((byte)3);
                buf.writeString(value.toString());
            }
        }
    }
}

