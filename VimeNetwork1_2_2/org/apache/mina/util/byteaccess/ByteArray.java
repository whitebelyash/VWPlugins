/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.IoAbsoluteReader;
import org.apache.mina.util.byteaccess.IoAbsoluteWriter;
import org.apache.mina.util.byteaccess.IoRelativeReader;
import org.apache.mina.util.byteaccess.IoRelativeWriter;

public interface ByteArray
extends IoAbsoluteReader,
IoAbsoluteWriter {
    @Override
    public int first();

    @Override
    public int last();

    @Override
    public ByteOrder order();

    public void order(ByteOrder var1);

    public void free();

    public Iterable<IoBuffer> getIoBuffers();

    public IoBuffer getSingleIoBuffer();

    public boolean equals(Object var1);

    @Override
    public byte get(int var1);

    @Override
    public void get(int var1, IoBuffer var2);

    @Override
    public int getInt(int var1);

    public Cursor cursor();

    public Cursor cursor(int var1);

    public static interface Cursor
    extends IoRelativeReader,
    IoRelativeWriter {
        public int getIndex();

        public void setIndex(int var1);

        @Override
        public int getRemaining();

        @Override
        public boolean hasRemaining();

        @Override
        public byte get();

        @Override
        public void get(IoBuffer var1);

        @Override
        public int getInt();
    }
}

