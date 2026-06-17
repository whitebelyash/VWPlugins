/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.multiton.SingleSessionIoHandler;

@Deprecated
public interface SingleSessionIoHandlerFactory {
    public SingleSessionIoHandler getHandler(IoSession var1) throws Exception;
}

