/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;

public class DecodingStateProtocolDecoder
implements ProtocolDecoder {
    private final DecodingState state;
    private final Queue<IoBuffer> undecodedBuffers = new ConcurrentLinkedQueue<IoBuffer>();
    private IoSession session;

    public DecodingStateProtocolDecoder(DecodingState state) {
        if (state == null) {
            throw new IllegalArgumentException("state");
        }
        this.state = state;
    }

    @Override
    public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        IoBuffer b;
        if (this.session == null) {
            this.session = session;
        } else if (this.session != session) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " is a stateful decoder.  " + "You have to create one per session.");
        }
        this.undecodedBuffers.offer(in);
        while ((b = this.undecodedBuffers.peek()) != null) {
            int oldRemaining = b.remaining();
            this.state.decode(b, out);
            int newRemaining = b.remaining();
            if (newRemaining != 0) {
                if (oldRemaining != newRemaining) continue;
                throw new IllegalStateException(DecodingState.class.getSimpleName() + " must " + "consume at least one byte per decode().");
            }
            this.undecodedBuffers.poll();
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        this.state.finishDecode(out);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
    }
}

