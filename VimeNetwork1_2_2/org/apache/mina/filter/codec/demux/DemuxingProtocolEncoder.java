/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.demux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.UnknownMessageTypeException;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;
import org.apache.mina.filter.codec.demux.MessageEncoderFactory;
import org.apache.mina.util.CopyOnWriteMap;
import org.apache.mina.util.IdentityHashSet;

public class DemuxingProtocolEncoder
implements ProtocolEncoder {
    private final AttributeKey STATE = new AttributeKey(this.getClass(), "state");
    private final Map<Class<?>, MessageEncoderFactory> type2encoderFactory = new CopyOnWriteMap();
    private static final Class<?>[] EMPTY_PARAMS = new Class[0];

    public void addMessageEncoder(Class<?> messageType, Class<? extends MessageEncoder> encoderClass) {
        if (encoderClass == null) {
            throw new IllegalArgumentException("encoderClass");
        }
        try {
            encoderClass.getConstructor(EMPTY_PARAMS);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
        }
        boolean registered = false;
        if (MessageEncoder.class.isAssignableFrom(encoderClass)) {
            this.addMessageEncoder(messageType, new DefaultConstructorMessageEncoderFactory(encoderClass));
            registered = true;
        }
        if (!registered) {
            throw new IllegalArgumentException("Unregisterable type: " + encoderClass);
        }
    }

    public <T> void addMessageEncoder(Class<T> messageType, MessageEncoder<? super T> encoder) {
        this.addMessageEncoder(messageType, new SingletonMessageEncoderFactory(encoder));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> void addMessageEncoder(Class<T> messageType, MessageEncoderFactory<? super T> factory) {
        if (messageType == null) {
            throw new IllegalArgumentException("messageType");
        }
        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }
        Map<Class<?>, MessageEncoderFactory> map = this.type2encoderFactory;
        synchronized (map) {
            if (this.type2encoderFactory.containsKey(messageType)) {
                throw new IllegalStateException("The specified message type (" + messageType.getName() + ") is registered already.");
            }
            this.type2encoderFactory.put(messageType, factory);
        }
    }

    public void addMessageEncoder(Iterable<Class<?>> messageTypes, Class<? extends MessageEncoder> encoderClass) {
        for (Class<?> messageType : messageTypes) {
            this.addMessageEncoder(messageType, encoderClass);
        }
    }

    public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoder<? super T> encoder) {
        for (Class<T> clazz : messageTypes) {
            this.addMessageEncoder(clazz, encoder);
        }
    }

    public <T> void addMessageEncoder(Iterable<Class<? extends T>> messageTypes, MessageEncoderFactory<? super T> factory) {
        for (Class<T> clazz : messageTypes) {
            this.addMessageEncoder(clazz, factory);
        }
    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        State state = this.getState(session);
        MessageEncoder<Object> encoder = this.findEncoder(state, message.getClass());
        if (encoder == null) {
            throw new UnknownMessageTypeException("No message encoder found for message: " + message);
        }
        encoder.encode(session, message, out);
    }

    protected MessageEncoder<Object> findEncoder(State state, Class<?> type) {
        return this.findEncoder(state, type, null);
    }

    private MessageEncoder<Object> findEncoder(State state, Class<?> type, Set<Class<?>> triedClasses) {
        Class<?> superclass;
        MessageEncoder encoder = null;
        if (triedClasses != null && triedClasses.contains(type)) {
            return null;
        }
        encoder = (MessageEncoder)state.findEncoderCache.get(type);
        if (encoder != null) {
            return encoder;
        }
        encoder = (MessageEncoder)state.type2encoder.get(type);
        if (encoder == null) {
            Class<?> element;
            Class<?>[] interfaces;
            if (triedClasses == null) {
                triedClasses = new IdentityHashSet();
            }
            triedClasses.add(type);
            Class<?>[] classArray = interfaces = type.getInterfaces();
            int n = classArray.length;
            for (int i = 0; i < n && (encoder = this.findEncoder(state, element = classArray[i], triedClasses)) == null; ++i) {
            }
        }
        if (encoder == null && (superclass = type.getSuperclass()) != null) {
            encoder = this.findEncoder(state, superclass);
        }
        if (encoder != null) {
            state.findEncoderCache.put(type, encoder);
            MessageEncoder<Object> tmpEncoder = state.findEncoderCache.putIfAbsent(type, encoder);
            if (tmpEncoder != null) {
                encoder = tmpEncoder;
            }
        }
        return encoder;
    }

    @Override
    public void dispose(IoSession session) throws Exception {
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

    private static class DefaultConstructorMessageEncoderFactory<T>
    implements MessageEncoderFactory<T> {
        private final Class<MessageEncoder<T>> encoderClass;

        private DefaultConstructorMessageEncoderFactory(Class<MessageEncoder<T>> encoderClass) {
            if (encoderClass == null) {
                throw new IllegalArgumentException("encoderClass");
            }
            if (!MessageEncoder.class.isAssignableFrom(encoderClass)) {
                throw new IllegalArgumentException("encoderClass is not assignable to MessageEncoder");
            }
            this.encoderClass = encoderClass;
        }

        @Override
        public MessageEncoder<T> getEncoder() throws Exception {
            return this.encoderClass.newInstance();
        }
    }

    private static class SingletonMessageEncoderFactory<T>
    implements MessageEncoderFactory<T> {
        private final MessageEncoder<T> encoder;

        private SingletonMessageEncoderFactory(MessageEncoder<T> encoder) {
            if (encoder == null) {
                throw new IllegalArgumentException("encoder");
            }
            this.encoder = encoder;
        }

        @Override
        public MessageEncoder<T> getEncoder() {
            return this.encoder;
        }
    }

    private class State {
        private final ConcurrentHashMap<Class<?>, MessageEncoder> findEncoderCache = new ConcurrentHashMap();
        private final Map<Class<?>, MessageEncoder> type2encoder = new ConcurrentHashMap();

        private State() throws Exception {
            for (Map.Entry e : DemuxingProtocolEncoder.this.type2encoderFactory.entrySet()) {
                this.type2encoder.put((Class<?>)e.getKey(), ((MessageEncoderFactory)e.getValue()).getEncoder());
            }
        }
    }
}

