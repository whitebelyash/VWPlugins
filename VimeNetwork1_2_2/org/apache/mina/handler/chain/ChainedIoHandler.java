/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.chain;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.chain.IoHandlerChain;

public class ChainedIoHandler
extends IoHandlerAdapter {
    private final IoHandlerChain chain;

    public ChainedIoHandler() {
        this.chain = new IoHandlerChain();
    }

    public ChainedIoHandler(IoHandlerChain chain) {
        if (chain == null) {
            throw new IllegalArgumentException("chain");
        }
        this.chain = chain;
    }

    public IoHandlerChain getChain() {
        return this.chain;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        this.chain.execute(null, session, message);
    }
}

