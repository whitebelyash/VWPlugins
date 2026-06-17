/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.ByteArray;
import org.apache.mina.util.byteaccess.CompositeByteArray;
import org.apache.mina.util.byteaccess.CompositeByteArrayRelativeBase;
import org.apache.mina.util.byteaccess.IoRelativeReader;

public class CompositeByteArrayRelativeReader
extends CompositeByteArrayRelativeBase
implements IoRelativeReader {
    private final boolean autoFree;

    public CompositeByteArrayRelativeReader(CompositeByteArray cba, boolean autoFree) {
        super(cba);
        this.autoFree = autoFree;
    }

    @Override
    protected void cursorPassedFirstComponent() {
        if (this.autoFree) {
            this.cba.removeFirst().free();
        }
    }

    @Override
    public void skip(int length) {
        this.cursor.skip(length);
    }

    @Override
    public ByteArray slice(int length) {
        return this.cursor.slice(length);
    }

    @Override
    public byte get() {
        return this.cursor.get();
    }

    @Override
    public void get(IoBuffer bb) {
        this.cursor.get(bb);
    }

    @Override
    public short getShort() {
        return this.cursor.getShort();
    }

    @Override
    public int getInt() {
        return this.cursor.getInt();
    }

    @Override
    public long getLong() {
        return this.cursor.getLong();
    }

    @Override
    public float getFloat() {
        return this.cursor.getFloat();
    }

    @Override
    public double getDouble() {
        return this.cursor.getDouble();
    }

    @Override
    public char getChar() {
        return this.cursor.getChar();
    }
}

