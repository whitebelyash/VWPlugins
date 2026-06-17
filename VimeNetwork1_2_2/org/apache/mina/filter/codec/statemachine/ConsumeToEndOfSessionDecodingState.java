/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;

public abstract class ConsumeToEndOfSessionDecodingState
implements DecodingState {
    private IoBuffer buffer;
    private final int maxLength;

    public ConsumeToEndOfSessionDecodingState(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (this.buffer == null) {
            this.buffer = IoBuffer.allocate(256).setAutoExpand(true);
        }
        if (this.buffer.position() + in.remaining() > this.maxLength) {
            throw new ProtocolDecoderException("Received data exceeds " + this.maxLength + " byte(s).");
        }
        this.buffer.put(in);
        return this;
    }

    @Override
    public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
        try {
            if (this.buffer == null) {
                this.buffer = IoBuffer.allocate(0);
            }
            this.buffer.flip();
            DecodingState decodingState = this.finishDecode(this.buffer, out);
            return decodingState;
        }
        finally {
            this.buffer = null;
        }
    }

    protected abstract DecodingState finishDecode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;
}

