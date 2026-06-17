/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.stream;

import java.io.IOException;
import java.io.InputStream;
import org.apache.mina.core.buffer.IoBuffer;

class IoSessionInputStream
extends InputStream {
    private final Object mutex = new Object();
    private final IoBuffer buf = IoBuffer.allocate(16);
    private volatile boolean closed;
    private volatile boolean released;
    private IOException exception;

    public IoSessionInputStream() {
        this.buf.setAutoExpand(true);
        this.buf.limit(0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int available() {
        if (this.released) {
            return 0;
        }
        Object object = this.mutex;
        synchronized (object) {
            return this.buf.remaining();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        Object object = this.mutex;
        synchronized (object) {
            this.closed = true;
            this.releaseBuffer();
            this.mutex.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read() throws IOException {
        Object object = this.mutex;
        synchronized (object) {
            if (!this.waitForData()) {
                return -1;
            }
            return this.buf.get() & 0xFF;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        Object object = this.mutex;
        synchronized (object) {
            if (!this.waitForData()) {
                return -1;
            }
            int readBytes = len > this.buf.remaining() ? this.buf.remaining() : len;
            this.buf.get(b, off, readBytes);
            return readBytes;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean waitForData() throws IOException {
        if (this.released) {
            return false;
        }
        Object object = this.mutex;
        synchronized (object) {
            while (!this.released && this.buf.remaining() == 0 && this.exception == null) {
                try {
                    this.mutex.wait();
                }
                catch (InterruptedException e) {
                    IOException ioe = new IOException("Interrupted while waiting for more data");
                    ioe.initCause(e);
                    throw ioe;
                }
            }
        }
        if (this.exception != null) {
            this.releaseBuffer();
            throw this.exception;
        }
        if (this.closed && this.buf.remaining() == 0) {
            this.releaseBuffer();
            return false;
        }
        return true;
    }

    private void releaseBuffer() {
        if (this.released) {
            return;
        }
        this.released = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void write(IoBuffer src) {
        Object object = this.mutex;
        synchronized (object) {
            if (this.closed) {
                return;
            }
            if (this.buf.hasRemaining()) {
                this.buf.compact();
                this.buf.put(src);
                this.buf.flip();
            } else {
                this.buf.clear();
                this.buf.put(src);
                this.buf.flip();
                this.mutex.notifyAll();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void throwException(IOException e) {
        Object object = this.mutex;
        synchronized (object) {
            if (this.exception == null) {
                this.exception = e;
                this.mutex.notifyAll();
            }
        }
    }
}

