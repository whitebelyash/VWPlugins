/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;

public interface ReadFuture
extends IoFuture {
    public Object getMessage();

    public boolean isRead();

    public boolean isClosed();

    public Throwable getException();

    public void setRead(Object var1);

    public void setClosed();

    public void setException(Throwable var1);

    @Override
    public ReadFuture await() throws InterruptedException;

    @Override
    public ReadFuture awaitUninterruptibly();

    @Override
    public ReadFuture addListener(IoFutureListener<?> var1);

    @Override
    public ReadFuture removeListener(IoFutureListener<?> var1);
}

