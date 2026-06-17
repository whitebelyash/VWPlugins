/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;

public interface WriteFuture
extends IoFuture {
    public boolean isWritten();

    public Throwable getException();

    public void setWritten();

    public void setException(Throwable var1);

    @Override
    public WriteFuture await() throws InterruptedException;

    @Override
    public WriteFuture awaitUninterruptibly();

    @Override
    public WriteFuture addListener(IoFutureListener<?> var1);

    @Override
    public WriteFuture removeListener(IoFutureListener<?> var1);
}

