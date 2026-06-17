/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;

public abstract class ConsumeToCrLfDecodingState
implements DecodingState {
    private static final byte CR = 13;
    private static final byte LF = 10;
    private boolean lastIsCR;
    private IoBuffer buffer;

    @Override
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        int beginPos = in.position();
        int limit = in.limit();
        int terminatorPos = -1;
        for (int i = beginPos; i < limit; ++i) {
            byte b = in.get(i);
            if (b == 13) {
                this.lastIsCR = true;
                continue;
            }
            if (b == 10 && this.lastIsCR) {
                terminatorPos = i;
                break;
            }
            this.lastIsCR = false;
        }
        if (terminatorPos >= 0) {
            IoBuffer product;
            int endPos = terminatorPos - 1;
            if (beginPos < endPos) {
                in.limit(endPos);
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
        in.position(beginPos);
        if (this.buffer == null) {
            this.buffer = IoBuffer.allocate(in.remaining());
            this.buffer.setAutoExpand(true);
        }
        this.buffer.put(in);
        if (this.lastIsCR) {
            this.buffer.position(this.buffer.position() - 1);
        }
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

    protected abstract DecodingState finishDecode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;
}

