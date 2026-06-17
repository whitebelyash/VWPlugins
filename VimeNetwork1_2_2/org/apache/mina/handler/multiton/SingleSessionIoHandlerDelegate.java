/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.multiton;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;
import org.apache.mina.handler.multiton.SingleSessionIoHandlerFactory;

@Deprecated
public class SingleSessionIoHandlerDelegate
implements IoHandler {
    public static final AttributeKey HANDLER = new AttributeKey(SingleSessionIoHandlerDelegate.class, "handler");
    private final SingleSessionIoHandlerFactory factory;

    public SingleSessionIoHandlerDelegate(SingleSessionIoHandlerFactory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("factory");
        }
        this.factory = factory;
    }

    public SingleSessionIoHandlerFactory getFactory() {
        return this.factory;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        SingleSessionIoHandler handler = this.factory.getHandler(session);
        session.setAttribute(HANDLER, handler);
        handler.sessionCreated();
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.sessionOpened();
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.sessionClosed();
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.sessionIdle(status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.exceptionCaught(cause);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.messageReceived(message);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.messageSent(message);
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        SingleSessionIoHandler handler = (SingleSessionIoHandler)session.getAttribute(HANDLER);
        handler.inputClosed(session);
    }
}

