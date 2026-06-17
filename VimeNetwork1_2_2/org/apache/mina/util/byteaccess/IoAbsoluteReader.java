/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.ByteArray;

public interface IoAbsoluteReader {
    public int first();

    public int last();

    public int length();

    public ByteArray slice(int var1, int var2);

    public ByteOrder order();

    public byte get(int var1);

    public void get(int var1, IoBuffer var2);

    public short getShort(int var1);

    public int getInt(int var1);

    public long getLong(int var1);

    public float getFloat(int var1);

    public double getDouble(int var1);

    public char getChar(int var1);
}

