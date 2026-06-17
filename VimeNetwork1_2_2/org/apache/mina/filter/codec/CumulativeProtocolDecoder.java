/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class CumulativeProtocolDecoder
extends ProtocolDecoderAdapter {
    private final AttributeKey BUFFER = new AttributeKey(this.getClass(), "buffer");
    private boolean transportMetadataFragmentation = true;

    protected CumulativeProtocolDecoder() {
    }

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (this.transportMetadataFragmentation && !session.getTransportMetadata().hasFragmentation()) {
            while (in.hasRemaining() && this.doDecode(session, in, out)) {
            }
            return;
        }
        boolean usingSessionBuffer = true;
        IoBuffer buf = (IoBuffer)session.getAttribute(this.BUFFER);
        if (buf != null) {
            boolean appended = false;
            if (buf.isAutoExpand()) {
                try {
                    buf.put(in);
                    appended = true;
                }
                catch (IllegalStateException illegalStateException) {
                }
                catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                    // empty catch block
                }
            }
            if (appended) {
                buf.flip();
            } else {
                buf.flip();
                IoBuffer newBuf = IoBuffer.allocate(buf.remaining() + in.remaining()).setAutoExpand(true);
                newBuf.order(buf.order());
                newBuf.put(buf);
                newBuf.put(in);
                newBuf.flip();
                buf = newBuf;
                session.setAttribute(this.BUFFER, buf);
            }
        } else {
            buf = in;
            usingSessionBuffer = false;
        }
        do {
            int oldPos = buf.position();
            boolean decoded = this.doDecode(session, buf, out);
            if (!decoded) break;
            if (buf.position() != oldPos) continue;
            throw new IllegalStateException("doDecode() can't return true when buffer is not consumed.");
        } while (buf.hasRemaining());
        if (buf.hasRemaining()) {
            if (usingSessionBuffer && buf.isAutoExpand()) {
                buf.compact();
            } else {
                this.storeRemainingInSession(buf, session);
            }
        } else if (usingSessionBuffer) {
            this.removeSessionBuffer(session);
        }
    }

    protected abstract boolean doDecode(IoSession var1, IoBuffer var2, ProtocolDecoderOutput var3) throws Exception;

    @Override
    public void dispose(IoSession session) throws Exception {
        this.removeSessionBuffer(session);
    }

    private void removeSessionBuffer(IoSession session) {
        session.removeAttribute(this.BUFFER);
    }

    private void storeRemainingInSession(IoBuffer buf, IoSession session) {
        IoBuffer remainingBuf = IoBuffer.allocate(buf.capacity()).setAutoExpand(true);
        remainingBuf.order(buf.order());
        remainingBuf.put(buf);
        session.setAttribute(this.BUFFER, remainingBuf);
    }

    public void setTransportMetadataFragmentation(boolean transportMetadataFragmentation) {
        this.transportMetadataFragmentation = transportMetadataFragmentation;
    }
}

