/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface IoHandler {
    public void sessionCreated(IoSession var1) throws Exception;

    public void sessionOpened(IoSession var1) throws Exception;

    public void sessionClosed(IoSession var1) throws Exception;

    public void sessionIdle(IoSession var1, IdleStatus var2) throws Exception;

    public void exceptionCaught(IoSession var1, Throwable var2) throws Exception;

    public void messageReceived(IoSession var1, Object var2) throws Exception;

    public void messageSent(IoSession var1, Object var2) throws Exception;

    public void inputClosed(IoSession var1) throws Exception;
}

