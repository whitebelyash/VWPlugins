/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.util.EventListener;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface IoServiceListener
extends EventListener {
    public void serviceActivated(IoService var1) throws Exception;

    public void serviceIdle(IoService var1, IdleStatus var2) throws Exception;

    public void serviceDeactivated(IoService var1) throws Exception;

    public void sessionCreated(IoSession var1) throws Exception;

    public void sessionClosed(IoSession var1) throws Exception;

    public void sessionDestroyed(IoSession var1) throws Exception;
}

