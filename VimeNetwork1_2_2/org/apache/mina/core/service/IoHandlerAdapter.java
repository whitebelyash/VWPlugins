/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoHandlerAdapter
implements IoHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IoHandlerAdapter.class);

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("EXCEPTION, please implement " + this.getClass().getName() + ".exceptionCaught() for proper handling:", cause);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        session.closeNow();
    }
}

