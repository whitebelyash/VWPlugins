/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;

public abstract class ConsumeToDynamicTerminatorDecodingState
implements DecodingState {
    private IoBuffer buffer;

    @Override
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int beginPos = in.position();
        int terminatorPos = -1;
        int limit = in.limit();
        for (int i = beginPos; i < limit; ++i) {
            byte b = in.get(i);
            if (!this.isTerminator(b)) continue;
            terminatorPos = i;
            break;
        }
        if (terminatorPos >= 0) {
            IoBuffer product;
            if (beginPos < terminatorPos) {
                in.limit(terminatorPos);
                if (this.buffer == null) {
                    product = in.slice();
                } else {
                    this.buffer.put(in);
                    product = this.buffer.flip();
                    this.buffer = null;
                }
                in.limit(limit);
            } else if (this.buffer == null) {
                product = IoBuffer.allocate(0);
            } else {
                product = this.buffer.flip();
                this.buffer = null;
            }
            in.position(terminatorPos + 1);
            return this.finishDecode(product, out);
        }
        if (this.buffer == null) {
            this.buffer = IoBuffer.allocate(in.remaining());
            this.buffer.setAutoExpand(true);
        }
        this.buffer.put(in);
        return this;
    }

    @Override
    public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
        IoBuffer product;
        if (this.buffer == null) {
            product = IoBuffer.allocate(0);
        } else {
            product = this.buffer.flip();
            this.buffer = null;
        }
        return this.finishDecode(product, out);
    }

    protected abstract boolean isTerminator(byte var1);

    protected abstract DecodingState finishDecode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;
}

