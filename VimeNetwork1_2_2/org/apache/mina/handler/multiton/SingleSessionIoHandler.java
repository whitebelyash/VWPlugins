/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

@Deprecated
public interface SingleSessionIoHandler {
    public void sessionCreated() throws Exception;

    public void sessionOpened() throws Exception;

    public void sessionClosed() throws Exception;

    public void sessionIdle(IdleStatus var1) throws Exception;

    public void exceptionCaught(Throwable var1) throws Exception;

    public void inputClosed(IoSession var1);

    public void messageReceived(Object var1) throws Exception;

    public void messageSent(Object var1) throws Exception;
}

