/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.ByteArray;

public interface IoRelativeReader {
    public int getRemaining();

    public boolean hasRemaining();

    public void skip(int var1);

    public ByteArray slice(int var1);

    public ByteOrder order();

    public byte get();

    public void get(IoBuffer var1);

    public short getShort();

    public int getInt();

    public long getLong();

    public float getFloat();

    public double getDouble();

    public char getChar();
}

