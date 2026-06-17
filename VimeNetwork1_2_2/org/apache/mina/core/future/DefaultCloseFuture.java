/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

public class DefaultCloseFuture
extends DefaultIoFuture
implements CloseFuture {
    public DefaultCloseFuture(IoSession session) {
        super(session);
    }

    @Override
    public boolean isClosed() {
        if (this.isDone()) {
            return (Boolean)this.getValue();
        }
        return false;
    }

    @Override
    public void setClosed() {
        this.setValue(Boolean.TRUE);
    }

    @Override
    public CloseFuture await() throws InterruptedException {
        return (CloseFuture)super.await();
    }

    @Override
    public CloseFuture awaitUninterruptibly() {
        return (CloseFuture)super.awaitUninterruptibly();
    }

    @Override
    public CloseFuture addListener(IoFutureListener<?> listener) {
        return (CloseFuture)super.addListener(listener);
    }

    @Override
    public CloseFuture removeListener(IoFutureListener<?> listener) {
        return (CloseFuture)super.removeListener(listener);
    }
}

