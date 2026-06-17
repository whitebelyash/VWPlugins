/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;

public interface CloseFuture
extends IoFuture {
    public boolean isClosed();

    public void setClosed();

    @Override
    public CloseFuture await() throws InterruptedException;

    @Override
    public CloseFuture awaitUninterruptibly();

    @Override
    public CloseFuture addListener(IoFutureListener<?> var1);

    @Override
    public CloseFuture removeListener(IoFutureListener<?> var1);
}

