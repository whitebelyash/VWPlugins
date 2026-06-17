/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.stream;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

class IoSessionOutputStream
extends OutputStream {
    private final IoSession session;
    private WriteFuture lastWriteFuture;

    public IoSessionOutputStream(IoSession session) {
        this.session = session;
    }

    @Override
    public void close() throws IOException {
        try {
            this.flush();
        }
        finally {
            this.session.closeNow().awaitUninterruptibly();
        }
    }

    private void checkClosed() throws IOException {
        if (!this.session.isConnected()) {
            throw new IOException("The session has been closed.");
        }
    }

    private synchronized void write(IoBuffer buf) throws IOException {
        WriteFuture future;
        this.checkClosed();
        this.lastWriteFuture = future = this.session.write(buf);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.write(IoBuffer.wrap((byte[])b.clone(), off, len));
    }

    @Override
    public void write(int b) throws IOException {
        IoBuffer buf = IoBuffer.allocate(1);
        buf.put((byte)b);
        buf.flip();
        this.write(buf);
    }

    @Override
    public synchronized void flush() throws IOException {
        if (this.lastWriteFuture == null) {
            return;
        }
        this.lastWriteFuture.awaitUninterruptibly();
        if (!this.lastWriteFuture.isWritten()) {
            throw new IOException("The bytes could not be written to the session");
        }
    }
}

