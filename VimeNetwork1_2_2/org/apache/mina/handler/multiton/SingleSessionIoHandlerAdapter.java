/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;

@Deprecated
public class SingleSessionIoHandlerAdapter
implements SingleSessionIoHandler {
    private final IoSession session;

    public SingleSessionIoHandlerAdapter(IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }
        this.session = session;
    }

    protected IoSession getSession() {
        return this.session;
    }

    @Override
    public void exceptionCaught(Throwable th) throws Exception {
    }

    @Override
    public void inputClosed(IoSession session) {
    }

    @Override
    public void messageReceived(Object message) throws Exception {
    }

    @Override
    public void messageSent(Object message) throws Exception {
    }

    @Override
    public void sessionClosed() throws Exception {
    }

    @Override
    public void sessionCreated() throws Exception {
    }

    @Override
    public void sessionIdle(IdleStatus status) throws Exception {
    }

    @Override
    public void sessionOpened() throws Exception {
    }
}

