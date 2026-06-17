/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

public class DefaultConnectFuture
extends DefaultIoFuture
implements ConnectFuture {
    private static final Object CANCELED = new Object();

    public DefaultConnectFuture() {
        super(null);
    }

    public static ConnectFuture newFailedFuture(Throwable exception) {
        DefaultConnectFuture failedFuture = new DefaultConnectFuture();
        failedFuture.setException(exception);
        return failedFuture;
    }

    @Override
    public IoSession getSession() {
        Object v = this.getValue();
        if (v instanceof IoSession) {
            return (IoSession)v;
        }
        if (v instanceof RuntimeException) {
            throw (RuntimeException)v;
        }
        if (v instanceof Error) {
            throw (Error)v;
        }
        if (v instanceof Throwable) {
            throw (RuntimeIoException)new RuntimeIoException("Failed to get the session.").initCause((Throwable)v);
        }
        return null;
    }

    @Override
    public Throwable getException() {
        Object v = this.getValue();
        if (v instanceof Throwable) {
            return (Throwable)v;
        }
        return null;
    }

    @Override
    public boolean isConnected() {
        return this.getValue() instanceof IoSession;
    }

    @Override
    public boolean isCanceled() {
        return this.getValue() == CANCELED;
    }

    @Override
    public void setSession(IoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }
        this.setValue(session);
    }

    @Override
    public void setException(Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception");
        }
        this.setValue(exception);
    }

    @Override
    public boolean cancel() {
        return this.setValue(CANCELED);
    }

    @Override
    public ConnectFuture await() throws InterruptedException {
        return (ConnectFuture)super.await();
    }

    @Override
    public ConnectFuture awaitUninterruptibly() {
        return (ConnectFuture)super.awaitUninterruptibly();
    }

    @Override
    public ConnectFuture addListener(IoFutureListener<?> listener) {
        return (ConnectFuture)super.addListener(listener);
    }

    @Override
    public ConnectFuture removeListener(IoFutureListener<?> listener) {
        return (ConnectFuture)super.removeListener(listener);
    }
}

