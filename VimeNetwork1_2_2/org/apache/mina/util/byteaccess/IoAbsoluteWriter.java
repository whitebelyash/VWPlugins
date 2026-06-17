/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoAbsoluteWriter {
    public int first();

    public int last();

    public ByteOrder order();

    public void put(int var1, byte var2);

    public void put(int var1, IoBuffer var2);

    public void putShort(int var1, short var2);

    public void putInt(int var1, int var2);

    public void putLong(int var1, long var2);

    public void putFloat(int var1, float var2);

    public void putDouble(int var1, double var2);

    public void putChar(int var1, char var2);
}

