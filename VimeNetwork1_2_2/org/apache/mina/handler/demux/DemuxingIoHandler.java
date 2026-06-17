/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.demux;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.UnknownMessageTypeException;
import org.apache.mina.handler.demux.ExceptionHandler;
import org.apache.mina.handler.demux.MessageHandler;
import org.apache.mina.util.IdentityHashSet;

public class DemuxingIoHandler
extends IoHandlerAdapter {
    private final Map<Class<?>, MessageHandler<?>> receivedMessageHandlerCache = new ConcurrentHashMap();
    private final Map<Class<?>, MessageHandler<?>> receivedMessageHandlers = new ConcurrentHashMap();
    private final Map<Class<?>, MessageHandler<?>> sentMessageHandlerCache = new ConcurrentHashMap();
    private final Map<Class<?>, MessageHandler<?>> sentMessageHandlers = new ConcurrentHashMap();
    private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlerCache = new ConcurrentHashMap();
    private final Map<Class<?>, ExceptionHandler<?>> exceptionHandlers = new ConcurrentHashMap();

    public <E> MessageHandler<? super E> addReceivedMessageHandler(Class<E> type, MessageHandler<? super E> handler) {
        this.receivedMessageHandlerCache.clear();
        return this.receivedMessageHandlers.put(type, handler);
    }

    public <E> MessageHandler<? super E> removeReceivedMessageHandler(Class<E> type) {
        this.receivedMessageHandlerCache.clear();
        return this.receivedMessageHandlers.remove(type);
    }

    public <E> MessageHandler<? super E> addSentMessageHandler(Class<E> type, MessageHandler<? super E> handler) {
        this.sentMessageHandlerCache.clear();
        return this.sentMessageHandlers.put(type, handler);
    }

    public <E> MessageHandler<? super E> removeSentMessageHandler(Class<E> type) {
        this.sentMessageHandlerCache.clear();
        return this.sentMessageHandlers.remove(type);
    }

    public <E extends Throwable> ExceptionHandler<? super E> addExceptionHandler(Class<E> type, ExceptionHandler<? super E> handler) {
        this.exceptionHandlerCache.clear();
        return this.exceptionHandlers.put(type, handler);
    }

    public <E extends Throwable> ExceptionHandler<? super E> removeExceptionHandler(Class<E> type) {
        this.exceptionHandlerCache.clear();
        return this.exceptionHandlers.remove(type);
    }

    public <E> MessageHandler<? super E> getMessageHandler(Class<E> type) {
        return this.receivedMessageHandlers.get(type);
    }

    public Map<Class<?>, MessageHandler<?>> getReceivedMessageHandlerMap() {
        return Collections.unmodifiableMap(this.receivedMessageHandlers);
    }

    public Map<Class<?>, MessageHandler<?>> getSentMessageHandlerMap() {
        return Collections.unmodifiableMap(this.sentMessageHandlers);
    }

    public Map<Class<?>, ExceptionHandler<?>> getExceptionHandlerMap() {
        return Collections.unmodifiableMap(this.exceptionHandlers);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        MessageHandler<Object> handler = this.findReceivedMessageHandler(message.getClass());
        if (handler == null) {
            throw new UnknownMessageTypeException("No message handler found for message type: " + message.getClass().getSimpleName());
        }
        handler.handleMessage(session, message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        MessageHandler<Object> handler = this.findSentMessageHandler(message.getClass());
        if (handler == null) {
            throw new UnknownMessageTypeException("No handler found for message type: " + message.getClass().getSimpleName());
        }
        handler.handleMessage(session, message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        ExceptionHandler<Throwable> handler = this.findExceptionHandler(cause.getClass());
        if (handler == null) {
            throw new UnknownMessageTypeException("No handler found for exception type: " + cause.getClass().getSimpleName());
        }
        handler.exceptionCaught(session, cause);
    }

    protected MessageHandler<Object> findReceivedMessageHandler(Class<?> type) {
        return this.findReceivedMessageHandler(type, null);
    }

    protected MessageHandler<Object> findSentMessageHandler(Class<?> type) {
        return this.findSentMessageHandler(type, null);
    }

    protected ExceptionHandler<Throwable> findExceptionHandler(Class<? extends Throwable> type) {
        return this.findExceptionHandler(type, null);
    }

    private MessageHandler<Object> findReceivedMessageHandler(Class<?> type, Set<Class<?>> triedClasses) {
        return (MessageHandler)this.findHandler(this.receivedMessageHandlers, this.receivedMessageHandlerCache, type, triedClasses);
    }

    private MessageHandler<Object> findSentMessageHandler(Class<?> type, Set<Class<?>> triedClasses) {
        return (MessageHandler)this.findHandler(this.sentMessageHandlers, this.sentMessageHandlerCache, type, triedClasses);
    }

    private ExceptionHandler<Throwable> findExceptionHandler(Class<?> type, Set<Class<?>> triedClasses) {
        return (ExceptionHandler)this.findHandler(this.exceptionHandlers, this.exceptionHandlerCache, type, triedClasses);
    }

    private Object findHandler(Map<Class<?>, ?> handlers, Map handlerCache, Class<?> type, Set<Class<?>> triedClasses) {
        Class<?> superclass;
        if (triedClasses != null && triedClasses.contains(type)) {
            return null;
        }
        Object handler = handlerCache.get(type);
        if (handler != null) {
            return handler;
        }
        handler = handlers.get(type);
        if (handler == null) {
            Class<?> element;
            Class<?>[] interfaces;
            if (triedClasses == null) {
                triedClasses = new IdentityHashSet();
            }
            triedClasses.add(type);
            Class<?>[] classArray = interfaces = type.getInterfaces();
            int n = classArray.length;
            for (int i = 0; i < n && (handler = this.findHandler(handlers, handlerCache, element = classArray[i], triedClasses)) == null; ++i) {
            }
        }
        if (handler == null && (superclass = type.getSuperclass()) != null) {
            handler = this.findHandler(handlers, handlerCache, superclass, null);
        }
        if (handler != null) {
            handlerCache.put(type, handler);
        }
        return handler;
    }
}

