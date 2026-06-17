/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.apache.mina.filter.executor.IoEventQueueThrottle;

public class WriteRequestFilter
extends IoFilterAdapter {
    private final IoEventQueueHandler queueHandler;

    public WriteRequestFilter() {
        this(new IoEventQueueThrottle());
    }

    public WriteRequestFilter(IoEventQueueHandler queueHandler) {
        if (queueHandler == null) {
            throw new IllegalArgumentException("queueHandler");
        }
        this.queueHandler = queueHandler;
    }

    public IoEventQueueHandler getQueueHandler() {
        return this.queueHandler;
    }

    @Override
    public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        final IoEvent e = new IoEvent(IoEventType.WRITE, session, writeRequest);
        if (this.queueHandler.accept(this, e)) {
            nextFilter.filterWrite(session, writeRequest);
            WriteFuture writeFuture = writeRequest.getFuture();
            if (writeFuture == null) {
                return;
            }
            this.queueHandler.offered(this, e);
            writeFuture.addListener(new IoFutureListener<WriteFuture>(){

                @Override
                public void operationComplete(WriteFuture future) {
                    WriteRequestFilter.this.queueHandler.polled(WriteRequestFilter.this, e);
                }
            });
        }
    }
}

