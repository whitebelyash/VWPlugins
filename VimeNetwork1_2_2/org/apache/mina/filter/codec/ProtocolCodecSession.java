/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec;

import java.util.Queue;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.DefaultWriteFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.AbstractProtocolDecoderOutput;
import org.apache.mina.filter.codec.AbstractProtocolEncoderOutput;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class ProtocolCodecSession
extends DummySession {
    private final WriteFuture notWrittenFuture = DefaultWriteFuture.newNotWrittenFuture(this, new UnsupportedOperationException());
    private final AbstractProtocolEncoderOutput encoderOutput = new AbstractProtocolEncoderOutput(){

        @Override
        public WriteFuture flush() {
            return ProtocolCodecSession.this.notWrittenFuture;
        }
    };
    private final AbstractProtocolDecoderOutput decoderOutput = new AbstractProtocolDecoderOutput(){

        @Override
        public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
        }
    };

    public ProtocolEncoderOutput getEncoderOutput() {
        return this.encoderOutput;
    }

    public Queue<Object> getEncoderOutputQueue() {
        return this.encoderOutput.getMessageQueue();
    }

    public ProtocolDecoderOutput getDecoderOutput() {
        return this.decoderOutput;
    }

    public Queue<Object> getDecoderOutputQueue() {
        return this.decoderOutput.getMessageQueue();
    }
}

