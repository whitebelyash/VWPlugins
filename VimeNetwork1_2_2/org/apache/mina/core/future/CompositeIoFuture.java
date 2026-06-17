/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;

public class CompositeIoFuture<E extends IoFuture>
extends DefaultIoFuture {
    private final NotifyingListener listener = new NotifyingListener();
    private final AtomicInteger unnotified = new AtomicInteger();
    private volatile boolean constructionFinished;

    public CompositeIoFuture(Iterable<E> children) {
        super(null);
        for (IoFuture f : children) {
            f.addListener(this.listener);
            this.unnotified.incrementAndGet();
        }
        this.constructionFinished = true;
        if (this.unnotified.get() == 0) {
            this.setValue(true);
        }
    }

    private class NotifyingListener
    implements IoFutureListener<IoFuture> {
        private NotifyingListener() {
        }

        @Override
        public void operationComplete(IoFuture future) {
            if (CompositeIoFuture.this.unnotified.decrementAndGet() == 0 && CompositeIoFuture.this.constructionFinished) {
                CompositeIoFuture.this.setValue(true);
            }
        }
    }
}

