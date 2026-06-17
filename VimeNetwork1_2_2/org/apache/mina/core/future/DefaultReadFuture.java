/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import java.io.IOException;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IoSession;

public class DefaultReadFuture
extends DefaultIoFuture
implements ReadFuture {
    private static final Object CLOSED = new Object();

    public DefaultReadFuture(IoSession session) {
        super(session);
    }

    @Override
    public Object getMessage() {
        if (this.isDone()) {
            Object v = this.getValue();
            if (v == CLOSED) {
                return null;
            }
            if (v instanceof RuntimeException) {
                throw (RuntimeException)v;
            }
            if (v instanceof Error) {
                throw (Error)v;
            }
            if (v instanceof IOException || v instanceof Exception) {
                throw new RuntimeIoException((Exception)v);
            }
            return v;
        }
        return null;
    }

    @Override
    public boolean isRead() {
        if (this.isDone()) {
            Object v = this.getValue();
            return v != CLOSED && !(v instanceof Throwable);
        }
        return false;
    }

    @Override
    public boolean isClosed() {
        if (this.isDone()) {
            return this.getValue() == CLOSED;
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
    public void setClosed() {
        this.setValue(CLOSED);
    }

    @Override
    public void setRead(Object message) {
        if (message == null) {
            throw new IllegalArgumentException("message");
        }
        this.setValue(message);
    }

    @Override
    public void setException(Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception");
        }
        this.setValue(exception);
    }

    @Override
    public ReadFuture await() throws InterruptedException {
        return (ReadFuture)super.await();
    }

    @Override
    public ReadFuture awaitUninterruptibly() {
        return (ReadFuture)super.awaitUninterruptibly();
    }

    @Override
    public ReadFuture addListener(IoFutureListener<?> listener) {
        return (ReadFuture)super.addListener(listener);
    }

    @Override
    public ReadFuture removeListener(IoFutureListener<?> listener) {
        return (ReadFuture)super.removeListener(listener);
    }
}

