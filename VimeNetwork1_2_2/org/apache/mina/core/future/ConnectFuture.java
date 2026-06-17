/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

public interface ConnectFuture
extends IoFuture {
    @Override
    public IoSession getSession();

    public Throwable getException();

    public boolean isConnected();

    public boolean isCanceled();

    public void setSession(IoSession var1);

    public void setException(Throwable var1);

    public boolean cancel();

    @Override
    public ConnectFuture await() throws InterruptedException;

    @Override
    public ConnectFuture awaitUninterruptibly();

    @Override
    public ConnectFuture addListener(IoFutureListener<?> var1);

    @Override
    public ConnectFuture removeListener(IoFutureListener<?> var1);
}

