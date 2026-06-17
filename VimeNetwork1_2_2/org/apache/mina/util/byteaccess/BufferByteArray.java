/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import java.util.Collections;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.AbstractByteArray;
import org.apache.mina.util.byteaccess.ByteArray;

public abstract class BufferByteArray
extends AbstractByteArray {
    protected IoBuffer bb;

    public BufferByteArray(IoBuffer bb) {
        this.bb = bb;
    }

    @Override
    public Iterable<IoBuffer> getIoBuffers() {
        return Collections.singletonList(this.bb);
    }

    @Override
    public IoBuffer getSingleIoBuffer() {
        return this.bb;
    }

    @Override
    public ByteArray slice(int index, int length) {
        int oldLimit = this.bb.limit();
        this.bb.position(index);
        this.bb.limit(index + length);
        IoBuffer slice = this.bb.slice();
        this.bb.limit(oldLimit);
        return new BufferByteArray(slice){

            @Override
            public void free() {
            }
        };
    }

    @Override
    public abstract void free();

    @Override
    public ByteArray.Cursor cursor() {
        return new CursorImpl();
    }

    @Override
    public ByteArray.Cursor cursor(int index) {
        return new CursorImpl(index);
    }

    @Override
    public int first() {
        return 0;
    }

    @Override
    public int last() {
        return this.bb.limit();
    }

    @Override
    public ByteOrder order() {
        return this.bb.order();
    }

    @Override
    public void order(ByteOrder order) {
        this.bb.order(order);
    }

    @Override
    public byte get(int index) {
        return this.bb.get(index);
    }

    @Override
    public void put(int index, byte b) {
        this.bb.put(index, b);
    }

    @Override
    public void get(int index, IoBuffer other) {
        this.bb.position(index);
        other.put(this.bb);
    }

    @Override
    public void put(int index, IoBuffer other) {
        this.bb.position(index);
        this.bb.put(other);
    }

    @Override
    public short getShort(int index) {
        return this.bb.getShort(index);
    }

    @Override
    public void putShort(int index, short s) {
        this.bb.putShort(index, s);
    }

    @Override
    public int getInt(int index) {
        return this.bb.getInt(index);
    }

    @Override
    public void putInt(int index, int i) {
        this.bb.putInt(index, i);
    }

    @Override
    public long getLong(int index) {
        return this.bb.getLong(index);
    }

    @Override
    public void putLong(int index, long l) {
        this.bb.putLong(index, l);
    }

    @Override
    public float getFloat(int index) {
        return this.bb.getFloat(index);
    }

    @Override
    public void putFloat(int index, float f) {
        this.bb.putFloat(index, f);
    }

    @Override
    public double getDouble(int index) {
        return this.bb.getDouble(index);
    }

    @Override
    public void putDouble(int index, double d) {
        this.bb.putDouble(index, d);
    }

    @Override
    public char getChar(int index) {
        return this.bb.getChar(index);
    }

    @Override
    public void putChar(int index, char c) {
        this.bb.putChar(index, c);
    }

    private class CursorImpl
    implements ByteArray.Cursor {
        private int index;

        public CursorImpl() {
        }

        public CursorImpl(int index) {
            this.setIndex(index);
        }

        @Override
        public int getRemaining() {
            return BufferByteArray.this.last() - this.index;
        }

        @Override
        public boolean hasRemaining() {
            return this.getRemaining() > 0;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public void setIndex(int index) {
            if (index < 0 || index > BufferByteArray.this.last()) {
                throw new IndexOutOfBoundsException();
            }
            this.index = index;
        }

        @Override
        public void skip(int length) {
            this.setIndex(this.index + length);
        }

        @Override
        public ByteArray slice(int length) {
            ByteArray slice = BufferByteArray.this.slice(this.index, length);
            this.index += length;
            return slice;
        }

        @Override
        public ByteOrder order() {
            return BufferByteArray.this.order();
        }

        @Override
        public byte get() {
            byte b = BufferByteArray.this.get(this.index);
            ++this.index;
            return b;
        }

        @Override
        public void put(byte b) {
            BufferByteArray.this.put(this.index, b);
            ++this.index;
        }

        @Override
        public void get(IoBuffer bb) {
            int size = Math.min(this.getRemaining(), bb.remaining());
            BufferByteArray.this.get(this.index, bb);
            this.index += size;
        }

        @Override
        public void put(IoBuffer bb) {
            int size = bb.remaining();
            BufferByteArray.this.put(this.index, bb);
            this.index += size;
        }

        @Override
        public short getShort() {
            short s = BufferByteArray.this.getShort(this.index);
            this.index += 2;
            return s;
        }

        @Override
        public void putShort(short s) {
            BufferByteArray.this.putShort(this.index, s);
            this.index += 2;
        }

        @Override
        public int getInt() {
            int i = BufferByteArray.this.getInt(this.index);
            this.index += 4;
            return i;
        }

        @Override
        public void putInt(int i) {
            BufferByteArray.this.putInt(this.index, i);
            this.index += 4;
        }

        @Override
        public long getLong() {
            long l = BufferByteArray.this.getLong(this.index);
            this.index += 8;
            return l;
        }

        @Override
        public void putLong(long l) {
            BufferByteArray.this.putLong(this.index, l);
            this.index += 8;
        }

        @Override
        public float getFloat() {
            float f = BufferByteArray.this.getFloat(this.index);
            this.index += 4;
            return f;
        }

        @Override
        public void putFloat(float f) {
            BufferByteArray.this.putFloat(this.index, f);
            this.index += 4;
        }

        @Override
        public double getDouble() {
            double d = BufferByteArray.this.getDouble(this.index);
            this.index += 8;
            return d;
        }

        @Override
        public void putDouble(double d) {
            BufferByteArray.this.putDouble(this.index, d);
            this.index += 8;
        }

        @Override
        public char getChar() {
            char c = BufferByteArray.this.getChar(this.index);
            this.index += 2;
            return c;
        }

        @Override
        public void putChar(char c) {
            BufferByteArray.this.putChar(this.index, c);
            this.index += 2;
        }
    }
}

