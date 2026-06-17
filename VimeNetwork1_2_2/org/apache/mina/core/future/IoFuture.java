/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;

public interface IoFuture {
    public IoSession getSession();

    public IoFuture await() throws InterruptedException;

    public boolean await(long var1, TimeUnit var3) throws InterruptedException;

    public boolean await(long var1) throws InterruptedException;

    public IoFuture awaitUninterruptibly();

    public boolean awaitUninterruptibly(long var1, TimeUnit var3);

    public boolean awaitUninterruptibly(long var1);

    @Deprecated
    public void join();

    @Deprecated
    public boolean join(long var1);

    public boolean isDone();

    public IoFuture addListener(IoFutureListener<?> var1);

    public IoFuture removeListener(IoFutureListener<?> var1);
}

