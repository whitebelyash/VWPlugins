/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.AbstractByteArray;
import org.apache.mina.util.byteaccess.BufferByteArray;
import org.apache.mina.util.byteaccess.ByteArray;
import org.apache.mina.util.byteaccess.ByteArrayFactory;
import org.apache.mina.util.byteaccess.ByteArrayList;

public final class CompositeByteArray
extends AbstractByteArray {
    private final ByteArrayList bas = new ByteArrayList();
    private ByteOrder order;
    private final ByteArrayFactory byteArrayFactory;

    public CompositeByteArray() {
        this(null);
    }

    public CompositeByteArray(ByteArrayFactory byteArrayFactory) {
        this.byteArrayFactory = byteArrayFactory;
    }

    public ByteArray getFirst() {
        if (this.bas.isEmpty()) {
            return null;
        }
        return this.bas.getFirst().getByteArray();
    }

    public void addFirst(ByteArray ba) {
        this.addHook(ba);
        this.bas.addFirst(ba);
    }

    public ByteArray removeFirst() {
        ByteArrayList.Node node = this.bas.removeFirst();
        return node == null ? null : node.getByteArray();
    }

    public ByteArray removeTo(int index) {
        if (index < this.first() || index > this.last()) {
            throw new IndexOutOfBoundsException();
        }
        CompositeByteArray prefix = new CompositeByteArray(this.byteArrayFactory);
        int remaining = index - this.first();
        while (remaining > 0) {
            ByteArray component = this.removeFirst();
            if (component.last() <= remaining) {
                prefix.addLast(component);
                remaining -= component.last();
                continue;
            }
            IoBuffer bb = component.getSingleIoBuffer();
            int originalLimit = bb.limit();
            bb.position(0);
            bb.limit(remaining);
            IoBuffer bb1 = bb.slice();
            bb.position(remaining);
            bb.limit(originalLimit);
            IoBuffer bb2 = bb.slice();
            BufferByteArray ba1 = new BufferByteArray(bb1){

                @Override
                public void free() {
                }
            };
            prefix.addLast(ba1);
            remaining -= ba1.last();
            final ByteArray componentFinal = component;
            BufferByteArray ba2 = new BufferByteArray(bb2){

                @Override
                public void free() {
                    componentFinal.free();
                }
            };
            this.addFirst(ba2);
        }
        return prefix;
    }

    public void addLast(ByteArray ba) {
        this.addHook(ba);
        this.bas.addLast(ba);
    }

    public ByteArray removeLast() {
        ByteArrayList.Node node = this.bas.removeLast();
        return node == null ? null : node.getByteArray();
    }

    @Override
    public void free() {
        while (!this.bas.isEmpty()) {
            ByteArrayList.Node node = this.bas.getLast();
            node.getByteArray().free();
            this.bas.removeLast();
        }
    }

    private void checkBounds(int index, int accessSize) {
        int lower = index;
        int upper = index + accessSize;
        if (lower < this.first()) {
            throw new IndexOutOfBoundsException("Index " + lower + " less than start " + this.first() + ".");
        }
        if (upper > this.last()) {
            throw new IndexOutOfBoundsException("Index " + upper + " greater than length " + this.last() + ".");
        }
    }

    @Override
    public Iterable<IoBuffer> getIoBuffers() {
        if (this.bas.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<IoBuffer> result = new ArrayList<IoBuffer>();
        ByteArrayList.Node node = this.bas.getFirst();
        for (IoBuffer bb : node.getByteArray().getIoBuffers()) {
            result.add(bb);
        }
        while (node.hasNextNode()) {
            node = node.getNextNode();
            for (IoBuffer bb : node.getByteArray().getIoBuffers()) {
                result.add(bb);
            }
        }
        return result;
    }

    @Override
    public IoBuffer getSingleIoBuffer() {
        if (this.byteArrayFactory == null) {
            throw new IllegalStateException("Can't get single buffer from CompositeByteArray unless it has a ByteArrayFactory.");
        }
        if (this.bas.isEmpty()) {
            ByteArray ba = this.byteArrayFactory.create(1);
            return ba.getSingleIoBuffer();
        }
        int actualLength = this.last() - this.first();
        ByteArrayList.Node node = this.bas.getFirst();
        ByteArray ba = node.getByteArray();
        if (ba.last() == actualLength) {
            return ba.getSingleIoBuffer();
        }
        ByteArray target = this.byteArrayFactory.create(actualLength);
        IoBuffer bb = target.getSingleIoBuffer();
        ByteArray.Cursor cursor = this.cursor();
        cursor.put(bb);
        while (!this.bas.isEmpty()) {
            ByteArrayList.Node node2 = this.bas.getLast();
            ByteArray component = node2.getByteArray();
            this.bas.removeLast();
            component.free();
        }
        this.bas.addLast(target);
        return bb;
    }

    @Override
    public ByteArray.Cursor cursor() {
        return new CursorImpl();
    }

    @Override
    public ByteArray.Cursor cursor(int index) {
        return new CursorImpl(index);
    }

    public ByteArray.Cursor cursor(CursorListener listener) {
        return new CursorImpl(listener);
    }

    public ByteArray.Cursor cursor(int index, CursorListener listener) {
        return new CursorImpl(index, listener);
    }

    @Override
    public ByteArray slice(int index, int length) {
        return this.cursor(index).slice(length);
    }

    @Override
    public byte get(int index) {
        return this.cursor(index).get();
    }

    @Override
    public void put(int index, byte b) {
        this.cursor(index).put(b);
    }

    @Override
    public void get(int index, IoBuffer bb) {
        this.cursor(index).get(bb);
    }

    @Override
    public void put(int index, IoBuffer bb) {
        this.cursor(index).put(bb);
    }

    @Override
    public int first() {
        return this.bas.firstByte();
    }

    @Override
    public int last() {
        return this.bas.lastByte();
    }

    private void addHook(ByteArray ba) {
        if (ba.first() != 0) {
            throw new IllegalArgumentException("Cannot add byte array that doesn't start from 0: " + ba.first());
        }
        if (this.order == null) {
            this.order = ba.order();
        } else if (!this.order.equals(ba.order())) {
            throw new IllegalArgumentException("Cannot add byte array with different byte order: " + ba.order());
        }
    }

    @Override
    public ByteOrder order() {
        if (this.order == null) {
            throw new IllegalStateException("Byte order not yet set.");
        }
        return this.order;
    }

    @Override
    public void order(ByteOrder order) {
        if (order == null || !order.equals(this.order)) {
            this.order = order;
            if (!this.bas.isEmpty()) {
                ByteArrayList.Node node = this.bas.getFirst();
                while (node.hasNextNode()) {
                    node.getByteArray().order(order);
                    node = node.getNextNode();
                }
            }
        }
    }

    @Override
    public short getShort(int index) {
        return this.cursor(index).getShort();
    }

    @Override
    public void putShort(int index, short s) {
        this.cursor(index).putShort(s);
    }

    @Override
    public int getInt(int index) {
        return this.cursor(index).getInt();
    }

    @Override
    public void putInt(int index, int i) {
        this.cursor(index).putInt(i);
    }

    @Override
    public long getLong(int index) {
        return this.cursor(index).getLong();
    }

    @Override
    public void putLong(int index, long l) {
        this.cursor(index).putLong(l);
    }

    @Override
    public float getFloat(int index) {
        return this.cursor(index).getFloat();
    }

    @Override
    public void putFloat(int index, float f) {
        this.cursor(index).putFloat(f);
    }

    @Override
    public double getDouble(int index) {
        return this.cursor(index).getDouble();
    }

    @Override
    public void putDouble(int index, double d) {
        this.cursor(index).putDouble(d);
    }

    @Override
    public char getChar(int index) {
        return this.cursor(index).getChar();
    }

    @Override
    public void putChar(int index, char c) {
        this.cursor(index).putChar(c);
    }

    private class CursorImpl
    implements ByteArray.Cursor {
        private int index;
        private final CursorListener listener;
        private ByteArrayList.Node componentNode;
        private int componentIndex;
        private ByteArray.Cursor componentCursor;

        public CursorImpl() {
            this(0, null);
        }

        public CursorImpl(int index) {
            this(index, null);
        }

        public CursorImpl(CursorListener listener) {
            this(0, listener);
        }

        public CursorImpl(int index, CursorListener listener) {
            this.index = index;
            this.listener = listener;
        }

        @Override
        public int getIndex() {
            return this.index;
        }

        @Override
        public void setIndex(int index) {
            CompositeByteArray.this.checkBounds(index, 0);
            this.index = index;
        }

        @Override
        public void skip(int length) {
            this.setIndex(this.index + length);
        }

        @Override
        public ByteArray slice(int length) {
            int componentSliceSize;
            CompositeByteArray slice = new CompositeByteArray(CompositeByteArray.this.byteArrayFactory);
            for (int remaining = length; remaining > 0; remaining -= componentSliceSize) {
                this.prepareForAccess(remaining);
                componentSliceSize = Math.min(remaining, this.componentCursor.getRemaining());
                ByteArray componentSlice = this.componentCursor.slice(componentSliceSize);
                slice.addLast(componentSlice);
                this.index += componentSliceSize;
            }
            return slice;
        }

        @Override
        public ByteOrder order() {
            return CompositeByteArray.this.order();
        }

        private void prepareForAccess(int accessSize) {
            if (this.componentNode != null && this.componentNode.isRemoved()) {
                this.componentNode = null;
                this.componentCursor = null;
            }
            CompositeByteArray.this.checkBounds(this.index, accessSize);
            ByteArrayList.Node oldComponentNode = this.componentNode;
            if (this.componentNode == null) {
                int basMidpoint = (CompositeByteArray.this.last() - CompositeByteArray.this.first()) / 2 + CompositeByteArray.this.first();
                if (this.index <= basMidpoint) {
                    this.componentNode = CompositeByteArray.this.bas.getFirst();
                    this.componentIndex = CompositeByteArray.this.first();
                    if (this.listener != null) {
                        this.listener.enteredFirstComponent(this.componentIndex, this.componentNode.getByteArray());
                    }
                } else {
                    this.componentNode = CompositeByteArray.this.bas.getLast();
                    this.componentIndex = CompositeByteArray.this.last() - this.componentNode.getByteArray().last();
                    if (this.listener != null) {
                        this.listener.enteredLastComponent(this.componentIndex, this.componentNode.getByteArray());
                    }
                }
            }
            while (this.index < this.componentIndex) {
                this.componentNode = this.componentNode.getPreviousNode();
                this.componentIndex -= this.componentNode.getByteArray().last();
                if (this.listener == null) continue;
                this.listener.enteredPreviousComponent(this.componentIndex, this.componentNode.getByteArray());
            }
            while (this.index >= this.componentIndex + this.componentNode.getByteArray().length()) {
                this.componentIndex += this.componentNode.getByteArray().last();
                this.componentNode = this.componentNode.getNextNode();
                if (this.listener == null) continue;
                this.listener.enteredNextComponent(this.componentIndex, this.componentNode.getByteArray());
            }
            int internalComponentIndex = this.index - this.componentIndex;
            if (this.componentNode == oldComponentNode) {
                this.componentCursor.setIndex(internalComponentIndex);
            } else {
                this.componentCursor = this.componentNode.getByteArray().cursor(internalComponentIndex);
            }
        }

        @Override
        public int getRemaining() {
            return CompositeByteArray.this.last() - this.index + 1;
        }

        @Override
        public boolean hasRemaining() {
            return this.getRemaining() > 0;
        }

        @Override
        public byte get() {
            this.prepareForAccess(1);
            byte b = this.componentCursor.get();
            ++this.index;
            return b;
        }

        @Override
        public void put(byte b) {
            this.prepareForAccess(1);
            this.componentCursor.put(b);
            ++this.index;
        }

        @Override
        public void get(IoBuffer bb) {
            while (bb.hasRemaining()) {
                int remainingBefore = bb.remaining();
                this.prepareForAccess(remainingBefore);
                this.componentCursor.get(bb);
                int remainingAfter = bb.remaining();
                int chunkSize = remainingBefore - remainingAfter;
                this.index += chunkSize;
            }
        }

        @Override
        public void put(IoBuffer bb) {
            while (bb.hasRemaining()) {
                int remainingBefore = bb.remaining();
                this.prepareForAccess(remainingBefore);
                this.componentCursor.put(bb);
                int remainingAfter = bb.remaining();
                int chunkSize = remainingBefore - remainingAfter;
                this.index += chunkSize;
            }
        }

        @Override
        public short getShort() {
            this.prepareForAccess(2);
            if (this.componentCursor.getRemaining() >= 4) {
                short s = this.componentCursor.getShort();
                this.index += 2;
                return s;
            }
            byte b0 = this.get();
            byte b1 = this.get();
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                return (short)(b0 << 8 | b1 & 0xFF);
            }
            return (short)(b1 << 8 | b0 & 0xFF);
        }

        @Override
        public void putShort(short s) {
            this.prepareForAccess(2);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putShort(s);
                this.index += 2;
            } else {
                byte b1;
                byte b0;
                if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                    b0 = (byte)(s >> 8 & 0xFF);
                    b1 = (byte)(s >> 0 & 0xFF);
                } else {
                    b0 = (byte)(s >> 0 & 0xFF);
                    b1 = (byte)(s >> 8 & 0xFF);
                }
                this.put(b0);
                this.put(b1);
            }
        }

        @Override
        public int getInt() {
            this.prepareForAccess(4);
            if (this.componentCursor.getRemaining() >= 4) {
                int i = this.componentCursor.getInt();
                this.index += 4;
                return i;
            }
            byte b0 = this.get();
            byte b1 = this.get();
            byte b2 = this.get();
            byte b3 = this.get();
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                return b0 << 24 | (b1 & 0xFF) << 16 | (b2 & 0xFF) << 8 | b3 & 0xFF;
            }
            return b3 << 24 | (b2 & 0xFF) << 16 | (b1 & 0xFF) << 8 | b0 & 0xFF;
        }

        @Override
        public void putInt(int i) {
            this.prepareForAccess(4);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putInt(i);
                this.index += 4;
            } else {
                byte b3;
                byte b2;
                byte b1;
                byte b0;
                if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                    b0 = (byte)(i >> 24 & 0xFF);
                    b1 = (byte)(i >> 16 & 0xFF);
                    b2 = (byte)(i >> 8 & 0xFF);
                    b3 = (byte)(i >> 0 & 0xFF);
                } else {
                    b0 = (byte)(i >> 0 & 0xFF);
                    b1 = (byte)(i >> 8 & 0xFF);
                    b2 = (byte)(i >> 16 & 0xFF);
                    b3 = (byte)(i >> 24 & 0xFF);
                }
                this.put(b0);
                this.put(b1);
                this.put(b2);
                this.put(b3);
            }
        }

        @Override
        public long getLong() {
            this.prepareForAccess(8);
            if (this.componentCursor.getRemaining() >= 4) {
                long l = this.componentCursor.getLong();
                this.index += 8;
                return l;
            }
            byte b0 = this.get();
            byte b1 = this.get();
            byte b2 = this.get();
            byte b3 = this.get();
            byte b4 = this.get();
            byte b5 = this.get();
            byte b6 = this.get();
            byte b7 = this.get();
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                return ((long)b0 & 0xFFL) << 56 | ((long)b1 & 0xFFL) << 48 | ((long)b2 & 0xFFL) << 40 | ((long)b3 & 0xFFL) << 32 | ((long)b4 & 0xFFL) << 24 | ((long)b5 & 0xFFL) << 16 | ((long)b6 & 0xFFL) << 8 | (long)b7 & 0xFFL;
            }
            return ((long)b7 & 0xFFL) << 56 | ((long)b6 & 0xFFL) << 48 | ((long)b5 & 0xFFL) << 40 | ((long)b4 & 0xFFL) << 32 | ((long)b3 & 0xFFL) << 24 | ((long)b2 & 0xFFL) << 16 | ((long)b1 & 0xFFL) << 8 | (long)b0 & 0xFFL;
        }

        @Override
        public void putLong(long l) {
            this.prepareForAccess(8);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putLong(l);
                this.index += 8;
            } else {
                byte b7;
                byte b6;
                byte b5;
                byte b4;
                byte b3;
                byte b2;
                byte b1;
                byte b0;
                if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                    b0 = (byte)(l >> 56 & 0xFFL);
                    b1 = (byte)(l >> 48 & 0xFFL);
                    b2 = (byte)(l >> 40 & 0xFFL);
                    b3 = (byte)(l >> 32 & 0xFFL);
                    b4 = (byte)(l >> 24 & 0xFFL);
                    b5 = (byte)(l >> 16 & 0xFFL);
                    b6 = (byte)(l >> 8 & 0xFFL);
                    b7 = (byte)(l >> 0 & 0xFFL);
                } else {
                    b0 = (byte)(l >> 0 & 0xFFL);
                    b1 = (byte)(l >> 8 & 0xFFL);
                    b2 = (byte)(l >> 16 & 0xFFL);
                    b3 = (byte)(l >> 24 & 0xFFL);
                    b4 = (byte)(l >> 32 & 0xFFL);
                    b5 = (byte)(l >> 40 & 0xFFL);
                    b6 = (byte)(l >> 48 & 0xFFL);
                    b7 = (byte)(l >> 56 & 0xFFL);
                }
                this.put(b0);
                this.put(b1);
                this.put(b2);
                this.put(b3);
                this.put(b4);
                this.put(b5);
                this.put(b6);
                this.put(b7);
            }
        }

        @Override
        public float getFloat() {
            this.prepareForAccess(4);
            if (this.componentCursor.getRemaining() >= 4) {
                float f = this.componentCursor.getFloat();
                this.index += 4;
                return f;
            }
            int i = this.getInt();
            return Float.intBitsToFloat(i);
        }

        @Override
        public void putFloat(float f) {
            this.prepareForAccess(4);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putFloat(f);
                this.index += 4;
            } else {
                int i = Float.floatToIntBits(f);
                this.putInt(i);
            }
        }

        @Override
        public double getDouble() {
            this.prepareForAccess(8);
            if (this.componentCursor.getRemaining() >= 4) {
                double d = this.componentCursor.getDouble();
                this.index += 8;
                return d;
            }
            long l = this.getLong();
            return Double.longBitsToDouble(l);
        }

        @Override
        public void putDouble(double d) {
            this.prepareForAccess(8);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putDouble(d);
                this.index += 8;
            } else {
                long l = Double.doubleToLongBits(d);
                this.putLong(l);
            }
        }

        @Override
        public char getChar() {
            this.prepareForAccess(2);
            if (this.componentCursor.getRemaining() >= 4) {
                char c = this.componentCursor.getChar();
                this.index += 2;
                return c;
            }
            byte b0 = this.get();
            byte b1 = this.get();
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                return (char)(b0 << 8 | b1 & 0xFF);
            }
            return (char)(b1 << 8 | b0 & 0xFF);
        }

        @Override
        public void putChar(char c) {
            this.prepareForAccess(2);
            if (this.componentCursor.getRemaining() >= 4) {
                this.componentCursor.putChar(c);
                this.index += 2;
            } else {
                byte b1;
                byte b0;
                if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
                    b0 = (byte)(c >> 8 & 0xFF);
                    b1 = (byte)(c >> 0 & 0xFF);
                } else {
                    b0 = (byte)(c >> 0 & 0xFF);
                    b1 = (byte)(c >> 8 & 0xFF);
                }
                this.put(b0);
                this.put(b1);
            }
        }
    }

    public static interface CursorListener {
        public void enteredFirstComponent(int var1, ByteArray var2);

        public void enteredNextComponent(int var1, ByteArray var2);

        public void enteredPreviousComponent(int var1, ByteArray var2);

        public void enteredLastComponent(int var1, ByteArray var2);
    }
}

