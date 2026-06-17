/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoRelativeWriter {
    public int getRemaining();

    public boolean hasRemaining();

    public void skip(int var1);

    public ByteOrder order();

    public void put(byte var1);

    public void put(IoBuffer var1);

    public void putShort(short var1);

    public void putInt(int var1);

    public void putLong(long var1);

    public void putFloat(float var1);

    public void putDouble(double var1);

    public void putChar(char var1);
}

