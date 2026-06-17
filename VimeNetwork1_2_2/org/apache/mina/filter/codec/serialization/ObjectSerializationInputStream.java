/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.StreamCorruptedException;
import org.apache.mina.core.buffer.IoBuffer;

public class ObjectSerializationInputStream
extends InputStream
implements ObjectInput {
    private final DataInputStream in;
    private final ClassLoader classLoader;
    private int maxObjectSize = 0x100000;

    public ObjectSerializationInputStream(InputStream in) {
        this(in, null);
    }

    public ObjectSerializationInputStream(InputStream in, ClassLoader classLoader) {
        if (in == null) {
            throw new IllegalArgumentException("in");
        }
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        this.in = in instanceof DataInputStream ? (DataInputStream)in : new DataInputStream(in);
        this.classLoader = classLoader;
    }

    public int getMaxObjectSize() {
        return this.maxObjectSize;
    }

    public void setMaxObjectSize(int maxObjectSize) {
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }
        this.maxObjectSize = maxObjectSize;
    }

    @Override
    public int read() throws IOException {
        return this.in.read();
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        int objectSize = this.in.readInt();
        if (objectSize <= 0) {
            throw new StreamCorruptedException("Invalid objectSize: " + objectSize);
        }
        if (objectSize > this.maxObjectSize) {
            throw new StreamCorruptedException("ObjectSize too big: " + objectSize + " (expected: <= " + this.maxObjectSize + ')');
        }
        IoBuffer buf = IoBuffer.allocate(objectSize + 4, false);
        buf.putInt(objectSize);
        this.in.readFully(buf.array(), 4, objectSize);
        buf.position(0);
        buf.limit(objectSize + 4);
        return buf.getObject(this.classLoader);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.in.readByte();
    }

    @Override
    public char readChar() throws IOException {
        return this.in.readChar();
    }

    @Override
    public double readDouble() throws IOException {
        return this.in.readDouble();
    }

    @Override
    public float readFloat() throws IOException {
        return this.in.readFloat();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.in.readFully(b, off, len);
    }

    @Override
    public int readInt() throws IOException {
        return this.in.readInt();
    }

    @Override
    @Deprecated
    public String readLine() throws IOException {
        return this.in.readLine();
    }

    @Override
    public long readLong() throws IOException {
        return this.in.readLong();
    }

    @Override
    public short readShort() throws IOException {
        return this.in.readShort();
    }

    @Override
    public String readUTF() throws IOException {
        return this.in.readUTF();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.in.readUnsignedByte();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.in.readUnsignedShort();
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.in.skipBytes(n);
    }
}

