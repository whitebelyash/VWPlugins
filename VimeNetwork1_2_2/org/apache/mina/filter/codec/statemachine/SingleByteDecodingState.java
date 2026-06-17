/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;

public abstract class SingleByteDecodingState
implements DecodingState {
    @Override
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (in.hasRemaining()) {
            return this.finishDecode(in.get(), out);
        }
        return this;
    }

    @Override
    public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
        throw new ProtocolDecoderException("Unexpected end of session while waiting for a single byte.");
    }

    protected abstract DecodingState finishDecode(byte var1, ProtocolDecoderOutput var2) throws Exception;
}

