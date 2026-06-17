/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.xtrafrancyz.Core.network.Buf;
import org.apache.mina.core.buffer.IoBuffer;

public class CoreByteMap
extends HashMap<String, Object> {
    private static final long serialVersionUID = 4722366923612558988L;
    private static final byte TYPE_INT = 1;
    private static final byte TYPE_BYTE = 2;
    private static final byte TYPE_LONG = 3;
    private static final byte TYPE_STRING = 4;
    private static final byte TYPE_SHORT = 5;
    private static final byte TYPE_FLOAT = 6;
    private static final byte TYPE_DOUBLE = 7;
    private static final byte TYPE_BOOLEAN = 8;
    private static final byte TYPE_MAP = 9;
    private static final byte TYPE_BYTE_ARRAY = 10;
    private static final byte TYPE_STRING_ARRAY = 11;
    private static final byte TYPE_MAP_ARRAY = 12;

    public CoreByteMap() {
    }

    public CoreByteMap(HashMap<String, Object> map) {
        super(map);
    }

    public CoreByteMap(byte[] bytes) {
        try {
            Buf in = new Buf(IoBuffer.wrap(bytes));
            int size = in.readVarInt();
            block16: for (int i = 0; i < size; ++i) {
                String key = in.readString();
                switch (in.read()) {
                    case 1: {
                        this.put(key, in.readInt());
                        continue block16;
                    }
                    case 2: {
                        this.put(key, in.read());
                        continue block16;
                    }
                    case 3: {
                        this.put(key, in.readLong());
                        continue block16;
                    }
                    case 4: {
                        this.put(key, in.readString());
                        continue block16;
                    }
                    case 5: {
                        this.put(key, in.readShort());
                        continue block16;
                    }
                    case 6: {
                        this.put(key, Float.valueOf(in.readFloat()));
                        continue block16;
                    }
                    case 7: {
                        this.put(key, in.readDouble());
                        continue block16;
                    }
                    case 8: {
                        this.put(key, in.read() == 1);
                        continue block16;
                    }
                    case 9: {
                        this.put(key, new CoreByteMap(in.readByteArray()));
                        continue block16;
                    }
                    case 10: {
                        this.put(key, in.readByteArray());
                        continue block16;
                    }
                    case 11: {
                        int j;
                        Object[] arr = new String[in.readVarInt()];
                        for (j = 0; j < arr.length; ++j) {
                            arr[j] = in.readString();
                        }
                        this.put(key, arr);
                        continue block16;
                    }
                    case 12: {
                        int j;
                        Object[] arr = new CoreByteMap[in.readVarInt()];
                        for (j = 0; j < arr.length; ++j) {
                            arr[j] = new CoreByteMap(in.readByteArray());
                        }
                        this.put(key, arr);
                        continue block16;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] toByteArray() {
        if (this.isEmpty()) {
            return new byte[]{0};
        }
        try {
            IoBuffer buffer = IoBuffer.allocate(128);
            buffer.setAutoExpand(true);
            Buf out = new Buf(buffer);
            out.writeVarInt(this.size());
            for (Map.Entry entry : this.entrySet()) {
                Object[] arr;
                Object val = entry.getValue();
                if (val == null) {
                    throw new IllegalArgumentException("value in CoreByteMap cannot be null (" + (String)entry.getKey() + "=null)");
                }
                if (entry.getKey() == null) {
                    throw new IllegalArgumentException("key in CoreByteMap cannot be null (null=" + val + ")");
                }
                out.writeString((String)entry.getKey());
                Class<?> clazz = val.getClass();
                if (clazz == Integer.class) {
                    out.write((byte)1);
                    out.writeInt((Integer)val);
                    continue;
                }
                if (clazz == Float.class) {
                    out.write((byte)6);
                    out.writeFloat(((Float)val).floatValue());
                    continue;
                }
                if (clazz == Byte.class) {
                    out.write((byte)2);
                    out.write((Byte)val);
                    continue;
                }
                if (clazz == Short.class) {
                    out.write((byte)5);
                    out.writeShort((Short)val);
                    continue;
                }
                if (clazz == Long.class) {
                    out.write((byte)3);
                    out.writeLong((Long)val);
                    continue;
                }
                if (clazz == String.class) {
                    out.write((byte)4);
                    out.writeString((String)val);
                    continue;
                }
                if (clazz == Double.class) {
                    out.write((byte)7);
                    out.writeDouble((Double)val);
                    continue;
                }
                if (clazz == Boolean.class) {
                    out.write((byte)8);
                    out.write((Boolean)val != false ? (byte)1 : 0);
                    continue;
                }
                if (clazz == CoreByteMap.class) {
                    out.write((byte)9);
                    out.writeByteArray(((CoreByteMap)val).toByteArray());
                    continue;
                }
                if (clazz == byte[].class) {
                    out.write((byte)10);
                    out.writeByteArray((byte[])val);
                    continue;
                }
                if (clazz == String[].class) {
                    out.write((byte)11);
                    arr = (String[])val;
                    out.writeVarInt(arr.length);
                    for (Object str : arr) {
                        out.writeString((String)str);
                    }
                    continue;
                }
                if (clazz == CoreByteMap[].class) {
                    out.write((byte)12);
                    arr = (CoreByteMap[])val;
                    out.writeVarInt(arr.length);
                    for (Object map : arr) {
                        out.writeByteArray(((CoreByteMap)map).toByteArray());
                    }
                    continue;
                }
                throw new IllegalArgumentException("Unsupported type of data: " + clazz.getName());
            }
            byte[] bytes = new byte[buffer.position()];
            buffer.flip();
            buffer.get(bytes);
            buffer.free();
            return bytes;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return new byte[0];
        }
    }

    public String getString(String key) {
        return (String)this.get(key);
    }

    public byte getByte(String key) {
        return (Byte)this.get(key);
    }

    public short getShort(String key) {
        return (Short)this.get(key);
    }

    public float getFloat(String key) {
        return ((Float)this.get(key)).floatValue();
    }

    public double getDouble(String key) {
        return (Double)this.get(key);
    }

    public int getInt(String key) {
        return (Integer)this.get(key);
    }

    public long getLong(String key) {
        return (Long)this.get(key);
    }

    public boolean getBoolean(String key) {
        return (Boolean)this.get(key);
    }

    public CoreByteMap getMap(String key) {
        return (CoreByteMap)this.get(key);
    }

    public byte[] getByteArray(String key) {
        return (byte[])this.get(key);
    }

    public String[] getStringArray(String key) {
        return (String[])this.get(key);
    }

    public CoreByteMap[] getMapArray(String key) {
        return (CoreByteMap[])this.get(key);
    }

    public String getString(String key, String def) {
        Object o = this.get(key);
        return o == null ? def : (String)o;
    }

    public byte getByte(String key, byte def) {
        Object o = this.get(key);
        return o == null ? def : (Byte)o;
    }

    public short getShort(String key, short def) {
        Object o = this.get(key);
        return o == null ? def : (Short)o;
    }

    public float getFloat(String key, float def) {
        Object o = this.get(key);
        return o == null ? def : ((Float)o).floatValue();
    }

    public double getDouble(String key, double def) {
        Object o = this.get(key);
        return o == null ? def : (Double)o;
    }

    public int getInt(String key, int def) {
        Object o = this.get(key);
        return o == null ? def : (Integer)o;
    }

    public long getLong(String key, long def) {
        Object o = this.get(key);
        return o == null ? def : (Long)o;
    }

    public boolean getBoolean(String key, boolean def) {
        Object o = this.get(key);
        return o == null ? def : (Boolean)o;
    }

    public CoreByteMap getMap(String key, CoreByteMap def) {
        Object o = this.get(key);
        return o == null ? def : (CoreByteMap)o;
    }

    @Override
    public String toString() {
        Iterator i = this.entrySet().iterator();
        if (!i.hasNext()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        while (true) {
            Map.Entry e = i.next();
            String key = (String)e.getKey();
            Object value = e.getValue();
            sb.append(key);
            sb.append('=');
            String val = value == this ? "(this Map)" : (value instanceof byte[] ? Arrays.toString((byte[])value) : (value instanceof Object[] ? Arrays.toString((Object[])value) : String.valueOf(value)));
            sb.append(val);
            if (!i.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',').append(' ');
        }
    }
}

