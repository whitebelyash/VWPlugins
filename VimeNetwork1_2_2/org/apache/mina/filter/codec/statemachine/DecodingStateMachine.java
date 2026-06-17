/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.statemachine;

import java.util.ArrayList;
import java.util.List;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.statemachine.DecodingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DecodingStateMachine
implements DecodingState {
    private final Logger log = LoggerFactory.getLogger(DecodingStateMachine.class);
    private final List<Object> childProducts = new ArrayList<Object>();
    private final ProtocolDecoderOutput childOutput = new ProtocolDecoderOutput(){

        @Override
        public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
        }

        @Override
        public void write(Object message) {
            DecodingStateMachine.this.childProducts.add(message);
        }
    };
    private DecodingState currentState;
    private boolean initialized;

    protected abstract DecodingState init() throws Exception;

    protected abstract DecodingState finishDecode(List<Object> var1, ProtocolDecoderOutput var2) throws Exception;

    protected abstract void destroy() throws Exception;

    @Override
    public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        DecodingState state = this.getCurrentState();
        int limit = in.limit();
        int pos = in.position();
        try {
            DecodingState oldState;
            while (pos != limit) {
                oldState = state;
                if ((state = state.decode(in, this.childOutput)) == null) {
                    DecodingState decodingState = this.finishDecode(this.childProducts, out);
                    return decodingState;
                }
                int newPos = in.position();
                if (newPos == pos && oldState == state) break;
                pos = newPos;
            }
            oldState = this;
            return oldState;
        }
        catch (Exception e) {
            state = null;
            throw e;
        }
        finally {
            this.currentState = state;
            if (state == null) {
                this.cleanup();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
        DecodingState nextState;
        DecodingState state = this.getCurrentState();
        try {
            DecodingState oldState;
            do {
                oldState = state;
                if ((state = state.finishDecode(this.childOutput)) != null) continue;
                break;
            } while (oldState != state);
        }
        catch (Exception e) {
            state = null;
            this.log.debug("Ignoring the exception caused by a closed session.", e);
        }
        finally {
            this.currentState = state;
            nextState = this.finishDecode(this.childProducts, out);
            if (state == null) {
                this.cleanup();
            }
        }
        return nextState;
    }

    private void cleanup() {
        if (!this.initialized) {
            throw new IllegalStateException();
        }
        this.initialized = false;
        this.childProducts.clear();
        try {
            this.destroy();
        }
        catch (Exception e2) {
            this.log.warn("Failed to destroy a decoding state machine.", e2);
        }
    }

    private DecodingState getCurrentState() throws Exception {
        DecodingState state = this.currentState;
        if (state == null) {
            state = this.init();
            this.initialized = true;
        }
        return state;
    }
}

