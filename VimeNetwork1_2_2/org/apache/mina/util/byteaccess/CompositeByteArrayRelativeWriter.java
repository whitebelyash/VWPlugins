/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.ByteArray;
import org.apache.mina.util.byteaccess.ByteArrayFactory;
import org.apache.mina.util.byteaccess.CompositeByteArray;
import org.apache.mina.util.byteaccess.CompositeByteArrayRelativeBase;
import org.apache.mina.util.byteaccess.IoRelativeWriter;

public class CompositeByteArrayRelativeWriter
extends CompositeByteArrayRelativeBase
implements IoRelativeWriter {
    private final Expander expander;
    private final Flusher flusher;
    private final boolean autoFlush;

    public CompositeByteArrayRelativeWriter(CompositeByteArray cba, Expander expander, Flusher flusher, boolean autoFlush) {
        super(cba);
        this.expander = expander;
        this.flusher = flusher;
        this.autoFlush = autoFlush;
    }

    private void prepareForAccess(int size) {
        int underflow = this.cursor.getIndex() + size - this.last();
        if (underflow > 0) {
            this.expander.expand(this.cba, underflow);
        }
    }

    public void flush() {
        this.flushTo(this.cursor.getIndex());
    }

    public void flushTo(int index) {
        ByteArray removed = this.cba.removeTo(index);
        this.flusher.flush(removed);
    }

    @Override
    public void skip(int length) {
        this.cursor.skip(length);
    }

    @Override
    protected void cursorPassedFirstComponent() {
        if (this.autoFlush) {
            this.flushTo(this.cba.first() + this.cba.getFirst().length());
        }
    }

    @Override
    public void put(byte b) {
        this.prepareForAccess(1);
        this.cursor.put(b);
    }

    @Override
    public void put(IoBuffer bb) {
        this.prepareForAccess(bb.remaining());
        this.cursor.put(bb);
    }

    @Override
    public void putShort(short s) {
        this.prepareForAccess(2);
        this.cursor.putShort(s);
    }

    @Override
    public void putInt(int i) {
        this.prepareForAccess(4);
        this.cursor.putInt(i);
    }

    @Override
    public void putLong(long l) {
        this.prepareForAccess(8);
        this.cursor.putLong(l);
    }

    @Override
    public void putFloat(float f) {
        this.prepareForAccess(4);
        this.cursor.putFloat(f);
    }

    @Override
    public void putDouble(double d) {
        this.prepareForAccess(8);
        this.cursor.putDouble(d);
    }

    @Override
    public void putChar(char c) {
        this.prepareForAccess(2);
        this.cursor.putChar(c);
    }

    public static interface Flusher {
        public void flush(ByteArray var1);
    }

    public static class ChunkedExpander
    implements Expander {
        private final ByteArrayFactory baf;
        private final int newComponentSize;

        public ChunkedExpander(ByteArrayFactory baf, int newComponentSize) {
            this.baf = baf;
            this.newComponentSize = newComponentSize;
        }

        @Override
        public void expand(CompositeByteArray cba, int minSize) {
            for (int remaining = minSize; remaining > 0; remaining -= this.newComponentSize) {
                ByteArray component = this.baf.create(this.newComponentSize);
                cba.addLast(component);
            }
        }
    }

    public static class NopExpander
    implements Expander {
        @Override
        public void expand(CompositeByteArray cba, int minSize) {
        }
    }

    public static interface Expander {
        public void expand(CompositeByteArray var1, int var2);
    }
}

