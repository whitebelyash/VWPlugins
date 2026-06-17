/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderFactory;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

public class DemuxingProtocolDecoder
extends CumulativeProtocolDecoder {
    private final AttributeKey STATE = new AttributeKey(this.getClass(), "state");
    private MessageDecoderFactory[] decoderFactories = new MessageDecoderFactory[0];
    private static final Class<?>[] EMPTY_PARAMS = new Class[0];

    public void addMessageDecoder(Class<? extends MessageDecoder> decoderClass) {
        if (decoderClass == null) {
            throw new IllegalArgumentException("decoderClass");
        }
        try {
            decoderClass.getConstructor(EMPTY_PARAMS);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
        }
        boolean registered = false;
        if (MessageDecoder.class.isAssignableFrom(decoderClass)) {
            this.addMessageDecoder(new DefaultConstructorMessageDecoderFactory(decoderClass));
            registered = true;
        }
        if (!registered) {
            throw new IllegalArgumentException("Unregisterable type: " + decoderClass);
        }
    }

    public void addMessageDecoder(MessageDecoder decoder) {
        this.addMessageDecoder(new SingletonMessageDecoderFactory(decoder));
    }

    public void addMessageDecoder(MessageDecoderFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }
        MessageDecoderFactory[] decoderFactories = this.decoderFactories;
        MessageDecoderFactory[] newDecoderFactories = new MessageDecoderFactory[decoderFactories.length + 1];
        System.arraycopy(decoderFactories, 0, newDecoderFactories, 0, decoderFactories.length);
        newDecoderFactories[decoderFactories.length] = factory;
        this.decoderFactories = newDecoderFactories;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        State state = this.getState(session);
        if (state.currentDecoder == null) {
            MessageDecoder[] decoders = state.decoders;
            int undecodables = 0;
            for (int i = decoders.length - 1; i >= 0; --i) {
                MessageDecoderResult result;
                MessageDecoder decoder = decoders[i];
                int limit = in.limit();
                int pos = in.position();
                try {
                    result = decoder.decodable(session, in);
                }
                finally {
                    in.position(pos);
                    in.limit(limit);
                }
                if (result == MessageDecoder.OK) {
                    state.currentDecoder = decoder;
                    break;
                }
                if (result == MessageDecoder.NOT_OK) {
                    ++undecodables;
                    continue;
                }
                if (result == MessageDecoder.NEED_DATA) continue;
                throw new IllegalStateException("Unexpected decode result (see your decodable()): " + result);
            }
            if (undecodables == decoders.length) {
                String dump = in.getHexDump();
                in.position(in.limit());
                ProtocolDecoderException e = new ProtocolDecoderException("No appropriate message decoder: " + dump);
                e.setHexdump(dump);
                throw e;
            }
            if (state.currentDecoder == null) {
                return false;
            }
        }
        try {
            MessageDecoderResult result = state.currentDecoder.decode(session, in, out);
            if (result == MessageDecoder.OK) {
                state.currentDecoder = null;
                return true;
            }
            if (result == MessageDecoder.NEED_DATA) {
                return false;
            }
            if (result == MessageDecoder.NOT_OK) {
                state.currentDecoder = null;
                throw new ProtocolDecoderException("Message decoder returned NOT_OK.");
            }
            state.currentDecoder = null;
            throw new IllegalStateException("Unexpected decode result (see your decode()): " + result);
        }
        catch (Exception e) {
            state.currentDecoder = null;
            throw e;
        }
    }

    @Override
    public void finishDecode(IoSession session, ProtocolDecoderOutput out) throws Exception {
        super.finishDecode(session, out);
        State state = this.getState(session);
        MessageDecoder currentDecoder = state.currentDecoder;
        if (currentDecoder == null) {
            return;
        }
        currentDecoder.finishDecode(session, out);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        super.dispose(session);
        session.removeAttribute(this.STATE);
    }

    private State getState(IoSession session) throws Exception {
        State oldState;
        State state = (State)session.getAttribute(this.STATE);
        if (state == null && (oldState = (State)session.setAttributeIfAbsent(this.STATE, state = new State())) != null) {
            state = oldState;
        }
        return state;
    }

    private static class DefaultConstructorMessageDecoderFactory
    implements MessageDecoderFactory {
        private final Class<?> decoderClass;

        private DefaultConstructorMessageDecoderFactory(Class<?> decoderClass) {
            if (decoderClass == null) {
                throw new IllegalArgumentException("decoderClass");
            }
            if (!MessageDecoder.class.isAssignableFrom(decoderClass)) {
                throw new IllegalArgumentException("decoderClass is not assignable to MessageDecoder");
            }
            this.decoderClass = decoderClass;
        }

        @Override
        public MessageDecoder getDecoder() throws Exception {
            return (MessageDecoder)this.decoderClass.newInstance();
        }
    }

    private static class SingletonMessageDecoderFactory
    implements MessageDecoderFactory {
        private final MessageDecoder decoder;

        private SingletonMessageDecoderFactory(MessageDecoder decoder) {
            if (decoder == null) {
                throw new IllegalArgumentException("decoder");
            }
            this.decoder = decoder;
        }

        @Override
        public MessageDecoder getDecoder() {
            return this.decoder;
        }
    }

    private class State {
        private final MessageDecoder[] decoders;
        private MessageDecoder currentDecoder;

        private State() throws Exception {
            MessageDecoderFactory[] decoderFactories = DemuxingProtocolDecoder.this.decoderFactories;
            this.decoders = new MessageDecoder[decoderFactories.length];
            for (int i = decoderFactories.length - 1; i >= 0; --i) {
                this.decoders[i] = decoderFactories[i].getDecoder();
            }
        }
    }
}

