/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.demux;

import org.apache.mina.core.session.IoSession;

public interface ExceptionHandler<E extends Throwable> {
    public static final ExceptionHandler<Throwable> NOOP = new ExceptionHandler<Throwable>(){

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) {
        }
    };
    public static final ExceptionHandler<Throwable> CLOSE = new ExceptionHandler<Throwable>(){

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) {
            session.closeNow();
        }
    };

    public void exceptionCaught(IoSession var1, E var2) throws Exception;
}

