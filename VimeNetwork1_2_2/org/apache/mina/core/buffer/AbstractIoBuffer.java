/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.buffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.EnumSet;
import java.util.Set;
import org.apache.mina.core.buffer.BufferDataException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferAllocator;
import org.apache.mina.core.buffer.IoBufferHexDumper;

public abstract class AbstractIoBuffer
extends IoBuffer {
    private final boolean derived;
    private boolean autoExpand;
    private boolean autoShrink;
    private boolean recapacityAllowed = true;
    private int minimumCapacity;
    private static final long BYTE_MASK = 255L;
    private static final long SHORT_MASK = 65535L;
    private static final long INT_MASK = 0xFFFFFFFFL;
    private int mark = -1;

    protected AbstractIoBuffer(IoBufferAllocator allocator, int initialCapacity) {
        AbstractIoBuffer.setAllocator(allocator);
        this.recapacityAllowed = true;
        this.derived = false;
        this.minimumCapacity = initialCapacity;
    }

    protected AbstractIoBuffer(AbstractIoBuffer parent) {
        AbstractIoBuffer.setAllocator(IoBuffer.getAllocator());
        this.recapacityAllowed = false;
        this.derived = true;
        this.minimumCapacity = parent.minimumCapacity;
    }

    @Override
    public final boolean isDirect() {
        return this.buf().isDirect();
    }

    @Override
    public final boolean isReadOnly() {
        return this.buf().isReadOnly();
    }

    protected abstract void buf(ByteBuffer var1);

    @Override
    public final int minimumCapacity() {
        return this.minimumCapacity;
    }

    @Override
    public final IoBuffer minimumCapacity(int minimumCapacity) {
        if (minimumCapacity < 0) {
            throw new IllegalArgumentException("minimumCapacity: " + minimumCapacity);
        }
        this.minimumCapacity = minimumCapacity;
        return this;
    }

    @Override
    public final int capacity() {
        return this.buf().capacity();
    }

    @Override
    public final IoBuffer capacity(int newCapacity) {
        if (!this.recapacityAllowed) {
            throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
        }
        if (newCapacity > this.capacity()) {
            int pos = this.position();
            int limit = this.limit();
            ByteOrder bo = this.order();
            ByteBuffer oldBuf = this.buf();
            ByteBuffer newBuf = AbstractIoBuffer.getAllocator().allocateNioBuffer(newCapacity, this.isDirect());
            oldBuf.clear();
            newBuf.put(oldBuf);
            this.buf(newBuf);
            this.buf().limit(limit);
            if (this.mark >= 0) {
                this.buf().position(this.mark);
                this.buf().mark();
            }
            this.buf().position(pos);
            this.buf().order(bo);
        }
        return this;
    }

    @Override
    public final boolean isAutoExpand() {
        return this.autoExpand && this.recapacityAllowed;
    }

    @Override
    public final boolean isAutoShrink() {
        return this.autoShrink && this.recapacityAllowed;
    }

    @Override
    public final boolean isDerived() {
        return this.derived;
    }

    @Override
    public final IoBuffer setAutoExpand(boolean autoExpand) {
        if (!this.recapacityAllowed) {
            throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
        }
        this.autoExpand = autoExpand;
        return this;
    }

    @Override
    public final IoBuffer setAutoShrink(boolean autoShrink) {
        if (!this.recapacityAllowed) {
            throw new IllegalStateException("Derived buffers and their parent can't be shrinked.");
        }
        this.autoShrink = autoShrink;
        return this;
    }

    @Override
    public final IoBuffer expand(int expectedRemaining) {
        return this.expand(this.position(), expectedRemaining, false);
    }

    private IoBuffer expand(int expectedRemaining, boolean autoExpand) {
        return this.expand(this.position(), expectedRemaining, autoExpand);
    }

    @Override
    public final IoBuffer expand(int pos, int expectedRemaining) {
        return this.expand(pos, expectedRemaining, false);
    }

    private IoBuffer expand(int pos, int expectedRemaining, boolean autoExpand) {
        if (!this.recapacityAllowed) {
            throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
        }
        int end = pos + expectedRemaining;
        int newCapacity = autoExpand ? IoBuffer.normalizeCapacity(end) : end;
        if (newCapacity > this.capacity()) {
            this.capacity(newCapacity);
        }
        if (end > this.limit()) {
            this.buf().limit(end);
        }
        return this;
    }

    @Override
    public final IoBuffer shrink() {
        int limit;
        if (!this.recapacityAllowed) {
            throw new IllegalStateException("Derived buffers and their parent can't be expanded.");
        }
        int position = this.position();
        int capacity = this.capacity();
        if (capacity == (limit = this.limit())) {
            return this;
        }
        int newCapacity = capacity;
        int minCapacity = Math.max(this.minimumCapacity, limit);
        while (newCapacity >>> 1 >= minCapacity) {
            newCapacity >>>= 1;
            if (minCapacity != 0) continue;
        }
        newCapacity = Math.max(minCapacity, newCapacity);
        if (newCapacity == capacity) {
            return this;
        }
        ByteOrder bo = this.order();
        ByteBuffer oldBuf = this.buf();
        ByteBuffer newBuf = AbstractIoBuffer.getAllocator().allocateNioBuffer(newCapacity, this.isDirect());
        oldBuf.position(0);
        oldBuf.limit(limit);
        newBuf.put(oldBuf);
        this.buf(newBuf);
        this.buf().position(position);
        this.buf().limit(limit);
        this.buf().order(bo);
        this.mark = -1;
        return this;
    }

    @Override
    public final int position() {
        return this.buf().position();
    }

    @Override
    public final IoBuffer position(int newPosition) {
        this.autoExpand(newPosition, 0);
        this.buf().position(newPosition);
        if (this.mark > newPosition) {
            this.mark = -1;
        }
        return this;
    }

    @Override
    public final int limit() {
        return this.buf().limit();
    }

    @Override
    public final IoBuffer limit(int newLimit) {
        this.autoExpand(newLimit, 0);
        this.buf().limit(newLimit);
        if (this.mark > newLimit) {
            this.mark = -1;
        }
        return this;
    }

    @Override
    public final IoBuffer mark() {
        ByteBuffer byteBuffer = this.buf();
        byteBuffer.mark();
        this.mark = byteBuffer.position();
        return this;
    }

    @Override
    public final int markValue() {
        return this.mark;
    }

    @Override
    public final IoBuffer reset() {
        this.buf().reset();
        return this;
    }

    @Override
    public final IoBuffer clear() {
        this.buf().clear();
        this.mark = -1;
        return this;
    }

    @Override
    public final IoBuffer sweep() {
        this.clear();
        return this.fillAndReset(this.remaining());
    }

    @Override
    public final IoBuffer sweep(byte value) {
        this.clear();
        return this.fillAndReset(value, this.remaining());
    }

    @Override
    public final IoBuffer flip() {
        this.buf().flip();
        this.mark = -1;
        return this;
    }

    @Override
    public final IoBuffer rewind() {
        this.buf().rewind();
        this.mark = -1;
        return this;
    }

    @Override
    public final int remaining() {
        ByteBuffer byteBuffer = this.buf();
        return byteBuffer.limit() - byteBuffer.position();
    }

    @Override
    public final boolean hasRemaining() {
        ByteBuffer byteBuffer = this.buf();
        return byteBuffer.limit() > byteBuffer.position();
    }

    @Override
    public final byte get() {
        return this.buf().get();
    }

    @Override
    public final short getUnsigned() {
        return (short)(this.get() & 0xFF);
    }

    @Override
    public final IoBuffer put(byte b) {
        this.autoExpand(1);
        this.buf().put(b);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(byte value) {
        this.autoExpand(1);
        this.buf().put((byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, byte value) {
        this.autoExpand(index, 1);
        this.buf().put(index, (byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(short value) {
        this.autoExpand(1);
        this.buf().put((byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, short value) {
        this.autoExpand(index, 1);
        this.buf().put(index, (byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int value) {
        this.autoExpand(1);
        this.buf().put((byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, int value) {
        this.autoExpand(index, 1);
        this.buf().put(index, (byte)(value & 0xFF));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(long value) {
        this.autoExpand(1);
        this.buf().put((byte)(value & 0xFFL));
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, long value) {
        this.autoExpand(index, 1);
        this.buf().put(index, (byte)(value & 0xFFL));
        return this;
    }

    @Override
    public final byte get(int index) {
        return this.buf().get(index);
    }

    @Override
    public final short getUnsigned(int index) {
        return (short)(this.get(index) & 0xFF);
    }

    @Override
    public final IoBuffer put(int index, byte b) {
        this.autoExpand(index, 1);
        this.buf().put(index, b);
        return this;
    }

    @Override
    public final IoBuffer get(byte[] dst, int offset, int length) {
        this.buf().get(dst, offset, length);
        return this;
    }

    @Override
    public final IoBuffer put(ByteBuffer src) {
        this.autoExpand(src.remaining());
        this.buf().put(src);
        return this;
    }

    @Override
    public final IoBuffer put(byte[] src, int offset, int length) {
        this.autoExpand(length);
        this.buf().put(src, offset, length);
        return this;
    }

    @Override
    public final IoBuffer compact() {
        int remaining = this.remaining();
        int capacity = this.capacity();
        if (capacity == 0) {
            return this;
        }
        if (this.isAutoShrink() && remaining <= capacity >>> 2 && capacity > this.minimumCapacity) {
            int newCapacity = capacity;
            int minCapacity = Math.max(this.minimumCapacity, remaining << 1);
            while (newCapacity >>> 1 >= minCapacity) {
                newCapacity >>>= 1;
            }
            newCapacity = Math.max(minCapacity, newCapacity);
            if (newCapacity == capacity) {
                return this;
            }
            ByteOrder bo = this.order();
            if (remaining > newCapacity) {
                throw new IllegalStateException("The amount of the remaining bytes is greater than the new capacity.");
            }
            ByteBuffer oldBuf = this.buf();
            ByteBuffer newBuf = AbstractIoBuffer.getAllocator().allocateNioBuffer(newCapacity, this.isDirect());
            newBuf.put(oldBuf);
            this.buf(newBuf);
            this.buf().order(bo);
        } else {
            this.buf().compact();
        }
        this.mark = -1;
        return this;
    }

    @Override
    public final ByteOrder order() {
        return this.buf().order();
    }

    @Override
    public final IoBuffer order(ByteOrder bo) {
        this.buf().order(bo);
        return this;
    }

    @Override
    public final char getChar() {
        return this.buf().getChar();
    }

    @Override
    public final IoBuffer putChar(char value) {
        this.autoExpand(2);
        this.buf().putChar(value);
        return this;
    }

    @Override
    public final char getChar(int index) {
        return this.buf().getChar(index);
    }

    @Override
    public final IoBuffer putChar(int index, char value) {
        this.autoExpand(index, 2);
        this.buf().putChar(index, value);
        return this;
    }

    @Override
    public final CharBuffer asCharBuffer() {
        return this.buf().asCharBuffer();
    }

    @Override
    public final short getShort() {
        return this.buf().getShort();
    }

    @Override
    public final IoBuffer putShort(short value) {
        this.autoExpand(2);
        this.buf().putShort(value);
        return this;
    }

    @Override
    public final short getShort(int index) {
        return this.buf().getShort(index);
    }

    @Override
    public final IoBuffer putShort(int index, short value) {
        this.autoExpand(index, 2);
        this.buf().putShort(index, value);
        return this;
    }

    @Override
    public final ShortBuffer asShortBuffer() {
        return this.buf().asShortBuffer();
    }

    @Override
    public final int getInt() {
        return this.buf().getInt();
    }

    @Override
    public final IoBuffer putInt(int value) {
        this.autoExpand(4);
        this.buf().putInt(value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(byte value) {
        this.autoExpand(4);
        this.buf().putInt(value & 0xFF);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(int index, byte value) {
        this.autoExpand(index, 4);
        this.buf().putInt(index, value & 0xFF);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(short value) {
        this.autoExpand(4);
        this.buf().putInt(value & 0xFFFF);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(int index, short value) {
        this.autoExpand(index, 4);
        this.buf().putInt(index, value & 0xFFFF);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(int value) {
        this.autoExpand(4);
        this.buf().putInt(value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(int index, int value) {
        this.autoExpand(index, 4);
        this.buf().putInt(index, value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(long value) {
        this.autoExpand(4);
        this.buf().putInt((int)(value & 0xFFFFFFFFFFFFFFFFL));
        return this;
    }

    @Override
    public final IoBuffer putUnsignedInt(int index, long value) {
        this.autoExpand(index, 4);
        this.buf().putInt(index, (int)(value & 0xFFFFFFFFL));
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(byte value) {
        this.autoExpand(2);
        this.buf().putShort((short)(value & 0xFF));
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(int index, byte value) {
        this.autoExpand(index, 2);
        this.buf().putShort(index, (short)(value & 0xFF));
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(short value) {
        this.autoExpand(2);
        this.buf().putShort(value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(int index, short value) {
        this.autoExpand(index, 2);
        this.buf().putShort(index, value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(int value) {
        this.autoExpand(2);
        this.buf().putShort((short)value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(int index, int value) {
        this.autoExpand(index, 2);
        this.buf().putShort(index, (short)value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(long value) {
        this.autoExpand(2);
        this.buf().putShort((short)value);
        return this;
    }

    @Override
    public final IoBuffer putUnsignedShort(int index, long value) {
        this.autoExpand(index, 2);
        this.buf().putShort(index, (short)value);
        return this;
    }

    @Override
    public final int getInt(int index) {
        return this.buf().getInt(index);
    }

    @Override
    public final IoBuffer putInt(int index, int value) {
        this.autoExpand(index, 4);
        this.buf().putInt(index, value);
        return this;
    }

    @Override
    public final IntBuffer asIntBuffer() {
        return this.buf().asIntBuffer();
    }

    @Override
    public final long getLong() {
        return this.buf().getLong();
    }

    @Override
    public final IoBuffer putLong(long value) {
        this.autoExpand(8);
        this.buf().putLong(value);
        return this;
    }

    @Override
    public final long getLong(int index) {
        return this.buf().getLong(index);
    }

    @Override
    public final IoBuffer putLong(int index, long value) {
        this.autoExpand(index, 8);
        this.buf().putLong(index, value);
        return this;
    }

    @Override
    public final LongBuffer asLongBuffer() {
        return this.buf().asLongBuffer();
    }

    @Override
    public final float getFloat() {
        return this.buf().getFloat();
    }

    @Override
    public final IoBuffer putFloat(float value) {
        this.autoExpand(4);
        this.buf().putFloat(value);
        return this;
    }

    @Override
    public final float getFloat(int index) {
        return this.buf().getFloat(index);
    }

    @Override
    public final IoBuffer putFloat(int index, float value) {
        this.autoExpand(index, 4);
        this.buf().putFloat(index, value);
        return this;
    }

    @Override
    public final FloatBuffer asFloatBuffer() {
        return this.buf().asFloatBuffer();
    }

    @Override
    public final double getDouble() {
        return this.buf().getDouble();
    }

    @Override
    public final IoBuffer putDouble(double value) {
        this.autoExpand(8);
        this.buf().putDouble(value);
        return this;
    }

    @Override
    public final double getDouble(int index) {
        return this.buf().getDouble(index);
    }

    @Override
    public final IoBuffer putDouble(int index, double value) {
        this.autoExpand(index, 8);
        this.buf().putDouble(index, value);
        return this;
    }

    @Override
    public final DoubleBuffer asDoubleBuffer() {
        return this.buf().asDoubleBuffer();
    }

    @Override
    public final IoBuffer asReadOnlyBuffer() {
        this.recapacityAllowed = false;
        return this.asReadOnlyBuffer0();
    }

    protected abstract IoBuffer asReadOnlyBuffer0();

    @Override
    public final IoBuffer duplicate() {
        this.recapacityAllowed = false;
        return this.duplicate0();
    }

    protected abstract IoBuffer duplicate0();

    @Override
    public final IoBuffer slice() {
        this.recapacityAllowed = false;
        return this.slice0();
    }

    @Override
    public final IoBuffer getSlice(int index, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        int pos = this.position();
        int limit = this.limit();
        if (index > limit) {
            throw new IllegalArgumentException("index: " + index);
        }
        int endIndex = index + length;
        if (endIndex > limit) {
            throw new IndexOutOfBoundsException("index + length (" + endIndex + ") is greater " + "than limit (" + limit + ").");
        }
        this.clear();
        this.limit(endIndex);
        this.position(index);
        IoBuffer slice = this.slice();
        this.limit(limit);
        this.position(pos);
        return slice;
    }

    @Override
    public final IoBuffer getSlice(int length) {
        int nextPos;
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        int pos = this.position();
        int limit = this.limit();
        if (limit < (nextPos = pos + length)) {
            throw new IndexOutOfBoundsException("position + length (" + nextPos + ") is greater " + "than limit (" + limit + ").");
        }
        this.limit(pos + length);
        IoBuffer slice = this.slice();
        this.position(nextPos);
        this.limit(limit);
        return slice;
    }

    protected abstract IoBuffer slice0();

    public int hashCode() {
        int h = 1;
        int p = this.position();
        for (int i = this.limit() - 1; i >= p; --i) {
            h = 31 * h + this.get(i);
        }
        return h;
    }

    public boolean equals(Object o) {
        if (!(o instanceof IoBuffer)) {
            return false;
        }
        IoBuffer that = (IoBuffer)o;
        if (this.remaining() != that.remaining()) {
            return false;
        }
        int p = this.position();
        int i = this.limit() - 1;
        int j = that.limit() - 1;
        while (i >= p) {
            byte v2;
            byte v1 = this.get(i);
            if (v1 != (v2 = that.get(j))) {
                return false;
            }
            --i;
            --j;
        }
        return true;
    }

    @Override
    public int compareTo(IoBuffer that) {
        int n = this.position() + Math.min(this.remaining(), that.remaining());
        int i = this.position();
        int j = that.position();
        while (i < n) {
            byte v2;
            byte v1 = this.get(i);
            if (v1 != (v2 = that.get(j))) {
                if (v1 < v2) {
                    return -1;
                }
                return 1;
            }
            ++i;
            ++j;
        }
        return this.remaining() - that.remaining();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.isDirect()) {
            buf.append("DirectBuffer");
        } else {
            buf.append("HeapBuffer");
        }
        buf.append("[pos=");
        buf.append(this.position());
        buf.append(" lim=");
        buf.append(this.limit());
        buf.append(" cap=");
        buf.append(this.capacity());
        buf.append(": ");
        buf.append(this.getHexDump(16));
        buf.append(']');
        return buf.toString();
    }

    @Override
    public IoBuffer get(byte[] dst) {
        return this.get(dst, 0, dst.length);
    }

    @Override
    public IoBuffer put(IoBuffer src) {
        return this.put(src.buf());
    }

    @Override
    public IoBuffer put(byte[] src) {
        return this.put(src, 0, src.length);
    }

    @Override
    public int getUnsignedShort() {
        return this.getShort() & 0xFFFF;
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.getShort(index) & 0xFFFF;
    }

    @Override
    public long getUnsignedInt() {
        return (long)this.getInt() & 0xFFFFFFFFL;
    }

    @Override
    public int getMediumInt() {
        byte b1 = this.get();
        byte b2 = this.get();
        byte b3 = this.get();
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            return this.getMediumInt(b1, b2, b3);
        }
        return this.getMediumInt(b3, b2, b1);
    }

    @Override
    public int getUnsignedMediumInt() {
        short b1 = this.getUnsigned();
        short b2 = this.getUnsigned();
        short b3 = this.getUnsigned();
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            return b1 << 16 | b2 << 8 | b3;
        }
        return b3 << 16 | b2 << 8 | b1;
    }

    @Override
    public int getMediumInt(int index) {
        byte b1 = this.get(index);
        byte b2 = this.get(index + 1);
        byte b3 = this.get(index + 2);
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            return this.getMediumInt(b1, b2, b3);
        }
        return this.getMediumInt(b3, b2, b1);
    }

    @Override
    public int getUnsignedMediumInt(int index) {
        short b1 = this.getUnsigned(index);
        short b2 = this.getUnsigned(index + 1);
        short b3 = this.getUnsigned(index + 2);
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            return b1 << 16 | b2 << 8 | b3;
        }
        return b3 << 16 | b2 << 8 | b1;
    }

    private int getMediumInt(byte b1, byte b2, byte b3) {
        int ret = b1 << 16 & 0xFF0000 | b2 << 8 & 0xFF00 | b3 & 0xFF;
        if ((b1 & 0x80) == 128) {
            ret |= 0xFF000000;
        }
        return ret;
    }

    @Override
    public IoBuffer putMediumInt(int value) {
        byte b1 = (byte)(value >> 16);
        byte b2 = (byte)(value >> 8);
        byte b3 = (byte)value;
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            this.put(b1).put(b2).put(b3);
        } else {
            this.put(b3).put(b2).put(b1);
        }
        return this;
    }

    @Override
    public IoBuffer putMediumInt(int index, int value) {
        byte b1 = (byte)(value >> 16);
        byte b2 = (byte)(value >> 8);
        byte b3 = (byte)value;
        if (ByteOrder.BIG_ENDIAN.equals(this.order())) {
            this.put(index, b1).put(index + 1, b2).put(index + 2, b3);
        } else {
            this.put(index, b3).put(index + 1, b2).put(index + 2, b1);
        }
        return this;
    }

    @Override
    public long getUnsignedInt(int index) {
        return (long)this.getInt(index) & 0xFFFFFFFFL;
    }

    @Override
    public InputStream asInputStream() {
        return new InputStream(){

            @Override
            public int available() {
                return AbstractIoBuffer.this.remaining();
            }

            @Override
            public synchronized void mark(int readlimit) {
                AbstractIoBuffer.this.mark();
            }

            @Override
            public boolean markSupported() {
                return true;
            }

            @Override
            public int read() {
                if (AbstractIoBuffer.this.hasRemaining()) {
                    return AbstractIoBuffer.this.get() & 0xFF;
                }
                return -1;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                int remaining = AbstractIoBuffer.this.remaining();
                if (remaining > 0) {
                    int readBytes = Math.min(remaining, len);
                    AbstractIoBuffer.this.get(b, off, readBytes);
                    return readBytes;
                }
                return -1;
            }

            @Override
            public synchronized void reset() {
                AbstractIoBuffer.this.reset();
            }

            @Override
            public long skip(long n) {
                int bytes = n > Integer.MAX_VALUE ? AbstractIoBuffer.this.remaining() : Math.min(AbstractIoBuffer.this.remaining(), (int)n);
                AbstractIoBuffer.this.skip(bytes);
                return bytes;
            }
        };
    }

    @Override
    public OutputStream asOutputStream() {
        return new OutputStream(){

            @Override
            public void write(byte[] b, int off, int len) {
                AbstractIoBuffer.this.put(b, off, len);
            }

            @Override
            public void write(int b) {
                AbstractIoBuffer.this.put((byte)b);
            }
        };
    }

    @Override
    public String getHexDump() {
        return this.getHexDump(Integer.MAX_VALUE);
    }

    @Override
    public String getHexDump(int lengthLimit) {
        return IoBufferHexDumper.getHexdump(this, lengthLimit);
    }

    @Override
    public String getString(CharsetDecoder decoder) throws CharacterCodingException {
        CoderResult cr;
        int newPos;
        if (!this.hasRemaining()) {
            return "";
        }
        boolean utf16 = decoder.charset().name().startsWith("UTF-16");
        int oldPos = this.position();
        int oldLimit = this.limit();
        int end = -1;
        if (!utf16) {
            end = this.indexOf((byte)0);
            newPos = end < 0 ? (end = oldLimit) : end + 1;
        } else {
            block8: {
                int i = oldPos;
                while (true) {
                    boolean wasZero;
                    boolean bl = wasZero = this.get(i) == 0;
                    if (++i >= oldLimit) break block8;
                    if (this.get(i) != 0) {
                        if (++i < oldLimit) continue;
                        break block8;
                    }
                    if (wasZero) break;
                }
                end = i - 1;
            }
            newPos = end < 0 ? (end = oldPos + (oldLimit - oldPos & 0xFFFFFFFE)) : (end + 2 <= oldLimit ? end + 2 : end);
        }
        if (oldPos == end) {
            this.position(newPos);
            return "";
        }
        this.limit(end);
        decoder.reset();
        int expectedLength = (int)((float)this.remaining() * decoder.averageCharsPerByte()) + 1;
        CharBuffer out = CharBuffer.allocate(expectedLength);
        while (!(cr = this.hasRemaining() ? decoder.decode(this.buf(), out, true) : decoder.flush(out)).isUnderflow()) {
            if (cr.isOverflow()) {
                CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }
            if (!cr.isError()) continue;
            this.limit(oldLimit);
            this.position(oldPos);
            cr.throwException();
        }
        this.limit(oldLimit);
        this.position(newPos);
        return out.flip().toString();
    }

    @Override
    public String getString(int fieldSize, CharsetDecoder decoder) throws CharacterCodingException {
        CoderResult cr;
        int i;
        int end;
        AbstractIoBuffer.checkFieldSize(fieldSize);
        if (fieldSize == 0) {
            return "";
        }
        if (!this.hasRemaining()) {
            return "";
        }
        boolean utf16 = decoder.charset().name().startsWith("UTF-16");
        if (utf16 && (fieldSize & 1) != 0) {
            throw new IllegalArgumentException("fieldSize is not even.");
        }
        int oldPos = this.position();
        int oldLimit = this.limit();
        if (oldLimit < (end = oldPos + fieldSize)) {
            throw new BufferUnderflowException();
        }
        if (!utf16) {
            for (i = oldPos; i < end && this.get(i) != 0; ++i) {
            }
            if (i == end) {
                this.limit(end);
            } else {
                this.limit(i);
            }
        } else {
            for (i = oldPos; i < end && (this.get(i) != 0 || this.get(i + 1) != 0); i += 2) {
            }
            if (i == end) {
                this.limit(end);
            } else {
                this.limit(i);
            }
        }
        if (!this.hasRemaining()) {
            this.limit(oldLimit);
            this.position(end);
            return "";
        }
        decoder.reset();
        int expectedLength = (int)((float)this.remaining() * decoder.averageCharsPerByte()) + 1;
        CharBuffer out = CharBuffer.allocate(expectedLength);
        while (!(cr = this.hasRemaining() ? decoder.decode(this.buf(), out, true) : decoder.flush(out)).isUnderflow()) {
            if (cr.isOverflow()) {
                CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }
            if (!cr.isError()) continue;
            this.limit(oldLimit);
            this.position(oldPos);
            cr.throwException();
        }
        this.limit(oldLimit);
        this.position(end);
        return out.flip().toString();
    }

    @Override
    public IoBuffer putString(CharSequence val, CharsetEncoder encoder) throws CharacterCodingException {
        CoderResult cr;
        if (val.length() == 0) {
            return this;
        }
        CharBuffer in = CharBuffer.wrap(val);
        encoder.reset();
        int expandedState = 0;
        block4: while (!(cr = in.hasRemaining() ? encoder.encode(in, this.buf(), true) : encoder.flush(this.buf())).isUnderflow()) {
            if (cr.isOverflow()) {
                if (this.isAutoExpand()) {
                    switch (expandedState) {
                        case 0: {
                            this.autoExpand((int)Math.ceil((float)in.remaining() * encoder.averageBytesPerChar()));
                            ++expandedState;
                            continue block4;
                        }
                        case 1: {
                            this.autoExpand((int)Math.ceil((float)in.remaining() * encoder.maxBytesPerChar()));
                            ++expandedState;
                            continue block4;
                        }
                    }
                    throw new RuntimeException("Expanded by " + (int)Math.ceil((float)in.remaining() * encoder.maxBytesPerChar()) + " but that wasn't enough for '" + val + "'");
                }
            } else {
                expandedState = 0;
            }
            cr.throwException();
        }
        return this;
    }

    @Override
    public IoBuffer putString(CharSequence val, int fieldSize, CharsetEncoder encoder) throws CharacterCodingException {
        CoderResult cr;
        int end;
        AbstractIoBuffer.checkFieldSize(fieldSize);
        if (fieldSize == 0) {
            return this;
        }
        this.autoExpand(fieldSize);
        boolean utf16 = encoder.charset().name().startsWith("UTF-16");
        if (utf16 && (fieldSize & 1) != 0) {
            throw new IllegalArgumentException("fieldSize is not even.");
        }
        int oldLimit = this.limit();
        if (oldLimit < (end = this.position() + fieldSize)) {
            throw new BufferOverflowException();
        }
        if (val.length() == 0) {
            if (!utf16) {
                this.put((byte)0);
            } else {
                this.put((byte)0);
                this.put((byte)0);
            }
            this.position(end);
            return this;
        }
        CharBuffer in = CharBuffer.wrap(val);
        this.limit(end);
        encoder.reset();
        while (!(cr = in.hasRemaining() ? encoder.encode(in, this.buf(), true) : encoder.flush(this.buf())).isUnderflow() && !cr.isOverflow()) {
            cr.throwException();
        }
        this.limit(oldLimit);
        if (this.position() < end) {
            if (!utf16) {
                this.put((byte)0);
            } else {
                this.put((byte)0);
                this.put((byte)0);
            }
        }
        this.position(end);
        return this;
    }

    @Override
    public String getPrefixedString(CharsetDecoder decoder) throws CharacterCodingException {
        return this.getPrefixedString(2, decoder);
    }

    @Override
    public String getPrefixedString(int prefixLength, CharsetDecoder decoder) throws CharacterCodingException {
        CoderResult cr;
        int end;
        if (!this.prefixedDataAvailable(prefixLength)) {
            throw new BufferUnderflowException();
        }
        int fieldSize = 0;
        switch (prefixLength) {
            case 1: {
                fieldSize = this.getUnsigned();
                break;
            }
            case 2: {
                fieldSize = this.getUnsignedShort();
                break;
            }
            case 4: {
                fieldSize = this.getInt();
            }
        }
        if (fieldSize == 0) {
            return "";
        }
        boolean utf16 = decoder.charset().name().startsWith("UTF-16");
        if (utf16 && (fieldSize & 1) != 0) {
            throw new BufferDataException("fieldSize is not even for a UTF-16 string.");
        }
        int oldLimit = this.limit();
        if (oldLimit < (end = this.position() + fieldSize)) {
            throw new BufferUnderflowException();
        }
        this.limit(end);
        decoder.reset();
        int expectedLength = (int)((float)this.remaining() * decoder.averageCharsPerByte()) + 1;
        CharBuffer out = CharBuffer.allocate(expectedLength);
        while (!(cr = this.hasRemaining() ? decoder.decode(this.buf(), out, true) : decoder.flush(out)).isUnderflow()) {
            if (cr.isOverflow()) {
                CharBuffer o = CharBuffer.allocate(out.capacity() + expectedLength);
                out.flip();
                o.put(out);
                out = o;
                continue;
            }
            cr.throwException();
        }
        this.limit(oldLimit);
        this.position(end);
        return out.flip().toString();
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, CharsetEncoder encoder) throws CharacterCodingException {
        return this.putPrefixedString(in, 2, 0, encoder);
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, int prefixLength, CharsetEncoder encoder) throws CharacterCodingException {
        return this.putPrefixedString(in, prefixLength, 0, encoder);
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, CharsetEncoder encoder) throws CharacterCodingException {
        return this.putPrefixedString(in, prefixLength, padding, (byte)0, encoder);
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence val, int prefixLength, int padding, byte padValue, CharsetEncoder encoder) throws CharacterCodingException {
        int padMask;
        int maxLength;
        switch (prefixLength) {
            case 1: {
                maxLength = 255;
                break;
            }
            case 2: {
                maxLength = 65535;
                break;
            }
            case 4: {
                maxLength = Integer.MAX_VALUE;
                break;
            }
            default: {
                throw new IllegalArgumentException("prefixLength: " + prefixLength);
            }
        }
        if (val.length() > maxLength) {
            throw new IllegalArgumentException("The specified string is too long.");
        }
        if (val.length() == 0) {
            switch (prefixLength) {
                case 1: {
                    this.put((byte)0);
                    break;
                }
                case 2: {
                    this.putShort((short)0);
                    break;
                }
                case 4: {
                    this.putInt(0);
                }
            }
            return this;
        }
        switch (padding) {
            case 0: 
            case 1: {
                padMask = 0;
                break;
            }
            case 2: {
                padMask = 1;
                break;
            }
            case 4: {
                padMask = 3;
                break;
            }
            default: {
                throw new IllegalArgumentException("padding: " + padding);
            }
        }
        CharBuffer in = CharBuffer.wrap(val);
        this.skip(prefixLength);
        int oldPos = this.position();
        encoder.reset();
        int expandedState = 0;
        block24: while (true) {
            CoderResult cr = in.hasRemaining() ? encoder.encode(in, this.buf(), true) : encoder.flush(this.buf());
            if (this.position() - oldPos > maxLength) {
                throw new IllegalArgumentException("The specified string is too long.");
            }
            if (cr.isUnderflow()) break;
            if (cr.isOverflow()) {
                if (this.isAutoExpand()) {
                    switch (expandedState) {
                        case 0: {
                            this.autoExpand((int)Math.ceil((float)in.remaining() * encoder.averageBytesPerChar()));
                            ++expandedState;
                            continue block24;
                        }
                        case 1: {
                            this.autoExpand((int)Math.ceil((float)in.remaining() * encoder.maxBytesPerChar()));
                            ++expandedState;
                            continue block24;
                        }
                    }
                    throw new RuntimeException("Expanded by " + (int)Math.ceil((float)in.remaining() * encoder.maxBytesPerChar()) + " but that wasn't enough for '" + val + "'");
                }
            } else {
                expandedState = 0;
            }
            cr.throwException();
        }
        this.fill(padValue, padding - (this.position() - oldPos & padMask));
        int length = this.position() - oldPos;
        switch (prefixLength) {
            case 1: {
                this.put(oldPos - 1, (byte)length);
                break;
            }
            case 2: {
                this.putShort(oldPos - 2, (short)length);
                break;
            }
            case 4: {
                this.putInt(oldPos - 4, length);
            }
        }
        return this;
    }

    @Override
    public Object getObject() throws ClassNotFoundException {
        return this.getObject(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Object getObject(final ClassLoader classLoader) throws ClassNotFoundException {
        if (!this.prefixedDataAvailable(4)) {
            throw new BufferUnderflowException();
        }
        int length = this.getInt();
        if (length <= 4) {
            throw new BufferDataException("Object length should be greater than 4: " + length);
        }
        int oldLimit = this.limit();
        this.limit(this.position() + length);
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(this.asInputStream()){

                @Override
                protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
                    int type = this.read();
                    if (type < 0) {
                        throw new EOFException();
                    }
                    switch (type) {
                        case 0: {
                            return super.readClassDescriptor();
                        }
                        case 1: {
                            String className = this.readUTF();
                            Class<?> clazz = Class.forName(className, true, classLoader);
                            return ObjectStreamClass.lookup(clazz);
                        }
                    }
                    throw new StreamCorruptedException("Unexpected class descriptor type: " + type);
                }

                @Override
                protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    Class<?> clazz = desc.forClass();
                    if (clazz == null) {
                        String name = desc.getName();
                        try {
                            return Class.forName(name, false, classLoader);
                        }
                        catch (ClassNotFoundException ex) {
                            return super.resolveClass(desc);
                        }
                    }
                    return clazz;
                }
            };
            Object object = in.readObject();
            return object;
        }
        catch (IOException e) {
            throw new BufferDataException(e);
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (IOException iOException) {}
            this.limit(oldLimit);
        }
    }

    @Override
    public IoBuffer putObject(Object o) {
        int oldPos = this.position();
        this.skip(4);
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(this.asOutputStream()){

                @Override
                protected void writeClassDescriptor(ObjectStreamClass desc) throws IOException {
                    Class<?> clazz = desc.forClass();
                    if (clazz.isArray() || clazz.isPrimitive() || !Serializable.class.isAssignableFrom(clazz)) {
                        this.write(0);
                        super.writeClassDescriptor(desc);
                    } else {
                        this.write(1);
                        this.writeUTF(desc.getName());
                    }
                }
            };
            out.writeObject(o);
            out.flush();
        }
        catch (IOException e) {
            throw new BufferDataException(e);
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException iOException) {}
        }
        int newPos = this.position();
        this.position(oldPos);
        this.putInt(newPos - oldPos - 4);
        this.position(newPos);
        return this;
    }

    @Override
    public boolean prefixedDataAvailable(int prefixLength) {
        return this.prefixedDataAvailable(prefixLength, Integer.MAX_VALUE);
    }

    @Override
    public boolean prefixedDataAvailable(int prefixLength, int maxDataLength) {
        int dataLength;
        if (this.remaining() < prefixLength) {
            return false;
        }
        switch (prefixLength) {
            case 1: {
                dataLength = this.getUnsigned(this.position());
                break;
            }
            case 2: {
                dataLength = this.getUnsignedShort(this.position());
                break;
            }
            case 4: {
                dataLength = this.getInt(this.position());
                break;
            }
            default: {
                throw new IllegalArgumentException("prefixLength: " + prefixLength);
            }
        }
        if (dataLength < 0 || dataLength > maxDataLength) {
            throw new BufferDataException("dataLength: " + dataLength);
        }
        return this.remaining() - prefixLength >= dataLength;
    }

    @Override
    public int indexOf(byte b) {
        if (this.hasArray()) {
            int arrayOffset = this.arrayOffset();
            int beginPos = arrayOffset + this.position();
            int limit = arrayOffset + this.limit();
            byte[] array = this.array();
            for (int i = beginPos; i < limit; ++i) {
                if (array[i] != b) continue;
                return i - arrayOffset;
            }
        } else {
            int beginPos = this.position();
            int limit = this.limit();
            for (int i = beginPos; i < limit; ++i) {
                if (this.get(i) != b) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public IoBuffer skip(int size) {
        this.autoExpand(size);
        return this.position(this.position() + size);
    }

    @Override
    public IoBuffer fill(byte value, int size) {
        int intValue;
        this.autoExpand(size);
        int q = size >>> 3;
        int r = size & 7;
        if (q > 0) {
            intValue = value & 0xFF | value << 8 & 0xFF00 | value << 16 & 0xFF0000 | value << 24;
            long longValue = (long)intValue & 0xFFFFFFFFL | (long)intValue << 32;
            for (int i = q; i > 0; --i) {
                this.putLong(longValue);
            }
        }
        q = r >>> 2;
        r &= 3;
        if (q > 0) {
            intValue = value & 0xFF | value << 8 & 0xFF00 | value << 16 & 0xFF0000 | value << 24;
            this.putInt(intValue);
        }
        q = r >> 1;
        r &= 1;
        if (q > 0) {
            short shortValue = (short)(value & 0xFF | value << 8);
            this.putShort(shortValue);
        }
        if (r > 0) {
            this.put(value);
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IoBuffer fillAndReset(byte value, int size) {
        this.autoExpand(size);
        int pos = this.position();
        try {
            this.fill(value, size);
        }
        finally {
            this.position(pos);
        }
        return this;
    }

    @Override
    public IoBuffer fill(int size) {
        this.autoExpand(size);
        int q = size >>> 3;
        int r = size & 7;
        for (int i = q; i > 0; --i) {
            this.putLong(0L);
        }
        q = r >>> 2;
        r &= 3;
        if (q > 0) {
            this.putInt(0);
        }
        q = r >> 1;
        r &= 1;
        if (q > 0) {
            this.putShort((short)0);
        }
        if (r > 0) {
            this.put((byte)0);
        }
        return this;
    }

    @Override
    public IoBuffer fillAndReset(int size) {
        this.autoExpand(size);
        int pos = this.position();
        try {
            this.fill(size);
        }
        finally {
            this.position(pos);
        }
        return this;
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getUnsigned()));
    }

    @Override
    public <E extends Enum<E>> E getEnum(int index, Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getUnsigned(index)));
    }

    @Override
    public <E extends Enum<E>> E getEnumShort(Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getUnsignedShort()));
    }

    @Override
    public <E extends Enum<E>> E getEnumShort(int index, Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getUnsignedShort(index)));
    }

    @Override
    public <E extends Enum<E>> E getEnumInt(Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getInt()));
    }

    @Override
    public <E extends Enum<E>> E getEnumInt(int index, Class<E> enumClass) {
        return (E)((Enum)this.toEnum(enumClass, this.getInt(index)));
    }

    @Override
    public IoBuffer putEnum(Enum<?> e) {
        if ((long)e.ordinal() > 255L) {
            throw new IllegalArgumentException(this.enumConversionErrorMessage(e, "byte"));
        }
        return this.put((byte)e.ordinal());
    }

    @Override
    public IoBuffer putEnum(int index, Enum<?> e) {
        if ((long)e.ordinal() > 255L) {
            throw new IllegalArgumentException(this.enumConversionErrorMessage(e, "byte"));
        }
        return this.put(index, (byte)e.ordinal());
    }

    @Override
    public IoBuffer putEnumShort(Enum<?> e) {
        if ((long)e.ordinal() > 65535L) {
            throw new IllegalArgumentException(this.enumConversionErrorMessage(e, "short"));
        }
        return this.putShort((short)e.ordinal());
    }

    @Override
    public IoBuffer putEnumShort(int index, Enum<?> e) {
        if ((long)e.ordinal() > 65535L) {
            throw new IllegalArgumentException(this.enumConversionErrorMessage(e, "short"));
        }
        return this.putShort(index, (short)e.ordinal());
    }

    @Override
    public IoBuffer putEnumInt(Enum<?> e) {
        return this.putInt(e.ordinal());
    }

    @Override
    public IoBuffer putEnumInt(int index, Enum<?> e) {
        return this.putInt(index, e.ordinal());
    }

    private <E> E toEnum(Class<E> enumClass, int i) {
        E[] enumConstants = enumClass.getEnumConstants();
        if (i > enumConstants.length) {
            throw new IndexOutOfBoundsException(String.format("%d is too large of an ordinal to convert to the enum %s", i, enumClass.getName()));
        }
        return enumConstants[i];
    }

    private String enumConversionErrorMessage(Enum<?> e, String type) {
        return String.format("%s.%s has an ordinal value too large for a %s", e.getClass().getName(), e.name(), type);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.get() & 0xFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSet(int index, Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.get(index) & 0xFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetShort(Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.getShort() & 0xFFFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetShort(int index, Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.getShort(index) & 0xFFFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetInt(Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.getInt() & 0xFFFFFFFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetInt(int index, Class<E> enumClass) {
        return this.toEnumSet(enumClass, (long)this.getInt(index) & 0xFFFFFFFFL);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetLong(Class<E> enumClass) {
        return this.toEnumSet(enumClass, this.getLong());
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetLong(int index, Class<E> enumClass) {
        return this.toEnumSet(enumClass, this.getLong(index));
    }

    private <E extends Enum<E>> EnumSet<E> toEnumSet(Class<E> clazz, long vector) {
        EnumSet<Enum> set = EnumSet.noneOf(clazz);
        long mask = 1L;
        for (Enum e : (Enum[])clazz.getEnumConstants()) {
            if ((mask & vector) == mask) {
                set.add(e);
            }
            mask <<= 1;
        }
        return set;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSet(Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFFFFFFFF00L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in a byte: " + set);
        }
        return this.put((byte)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSet(int index, Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFFFFFFFF00L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in a byte: " + set);
        }
        return this.put(index, (byte)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetShort(Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFFFFFF0000L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in a short: " + set);
        }
        return this.putShort((short)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetShort(int index, Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFFFFFF0000L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in a short: " + set);
        }
        return this.putShort(index, (short)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetInt(Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFF00000000L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in an int: " + set);
        }
        return this.putInt((int)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetInt(int index, Set<E> set) {
        long vector = this.toLong(set);
        if ((vector & 0xFFFFFFFF00000000L) != 0L) {
            throw new IllegalArgumentException("The enum set is too large to fit in an int: " + set);
        }
        return this.putInt(index, (int)vector);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetLong(Set<E> set) {
        return this.putLong(this.toLong(set));
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetLong(int index, Set<E> set) {
        return this.putLong(index, this.toLong(set));
    }

    private <E extends Enum<E>> long toLong(Set<E> set) {
        long vector = 0L;
        for (Enum e : set) {
            if (e.ordinal() >= 64) {
                throw new IllegalArgumentException("The enum set is too large to fit in a bit vector: " + set);
            }
            vector |= 1L << e.ordinal();
        }
        return vector;
    }

    private IoBuffer autoExpand(int expectedRemaining) {
        if (this.isAutoExpand()) {
            this.expand(expectedRemaining, true);
        }
        return this;
    }

    private IoBuffer autoExpand(int pos, int expectedRemaining) {
        if (this.isAutoExpand()) {
            this.expand(pos, expectedRemaining, true);
        }
        return this;
    }

    private static void checkFieldSize(int fieldSize) {
        if (fieldSize < 0) {
            throw new IllegalArgumentException("fieldSize cannot be negative: " + fieldSize);
        }
    }
}

