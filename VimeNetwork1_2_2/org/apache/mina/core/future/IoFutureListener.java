/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import java.util.EventListener;
import org.apache.mina.core.future.IoFuture;

public interface IoFutureListener<F extends IoFuture>
extends EventListener {
    public static final IoFutureListener<IoFuture> CLOSE = new IoFutureListener<IoFuture>(){

        @Override
        public void operationComplete(IoFuture future) {
            future.getSession().closeNow();
        }
    };

    public void operationComplete(F var1);
}

