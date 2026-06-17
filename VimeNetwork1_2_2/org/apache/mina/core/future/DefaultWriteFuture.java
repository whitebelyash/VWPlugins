/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public class DefaultWriteFuture
extends DefaultIoFuture
implements WriteFuture {
    public static WriteFuture newWrittenFuture(IoSession session) {
        DefaultWriteFuture writtenFuture = new DefaultWriteFuture(session);
        writtenFuture.setWritten();
        return writtenFuture;
    }

    public static WriteFuture newNotWrittenFuture(IoSession session, Throwable cause) {
        DefaultWriteFuture unwrittenFuture = new DefaultWriteFuture(session);
        unwrittenFuture.setException(cause);
        return unwrittenFuture;
    }

    public DefaultWriteFuture(IoSession session) {
        super(session);
    }

    @Override
    public boolean isWritten() {
        Object v;
        if (this.isDone() && (v = this.getValue()) instanceof Boolean) {
            return (Boolean)v;
        }
        return false;
    }

    @Override
    public Throwable getException() {
        Object v;
        if (this.isDone() && (v = this.getValue()) instanceof Throwable) {
            return (Throwable)v;
        }
        return null;
    }

    @Override
    public void setWritten() {
        this.setValue(Boolean.TRUE);
    }

    @Override
    public void setException(Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception");
        }
        this.setValue(exception);
    }

    @Override
    public WriteFuture await() throws InterruptedException {
        return (WriteFuture)super.await();
    }

    @Override
    public WriteFuture awaitUninterruptibly() {
        return (WriteFuture)super.awaitUninterruptibly();
    }

    @Override
    public WriteFuture addListener(IoFutureListener<?> listener) {
        return (WriteFuture)super.addListener(listener);
    }

    @Override
    public WriteFuture removeListener(IoFutureListener<?> listener) {
        return (WriteFuture)super.removeListener(listener);
    }
}

