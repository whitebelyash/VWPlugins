/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.demux;

import org.apache.mina.core.session.IoSession;

public interface MessageHandler<E> {
    public static final MessageHandler<Object> NOOP = new MessageHandler<Object>(){

        @Override
        public void handleMessage(IoSession session, Object message) {
        }
    };

    public void handleMessage(IoSession var1, E var2) throws Exception;
}

