/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.filter.executor.DefaultIoEventSizeEstimator;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.apache.mina.filter.executor.IoEventSizeEstimator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoEventQueueThrottle
implements IoEventQueueHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IoEventQueueThrottle.class);
    private final IoEventSizeEstimator eventSizeEstimator;
    private volatile int threshold;
    private final Object lock = new Object();
    private final AtomicInteger counter = new AtomicInteger();
    private int waiters;

    public IoEventQueueThrottle() {
        this(new DefaultIoEventSizeEstimator(), 65536);
    }

    public IoEventQueueThrottle(int threshold) {
        this(new DefaultIoEventSizeEstimator(), threshold);
    }

    public IoEventQueueThrottle(IoEventSizeEstimator eventSizeEstimator, int threshold) {
        if (eventSizeEstimator == null) {
            throw new IllegalArgumentException("eventSizeEstimator");
        }
        this.eventSizeEstimator = eventSizeEstimator;
        this.setThreshold(threshold);
    }

    public IoEventSizeEstimator getEventSizeEstimator() {
        return this.eventSizeEstimator;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getCounter() {
        return this.counter.get();
    }

    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            throw new IllegalArgumentException("threshold: " + threshold);
        }
        this.threshold = threshold;
    }

    @Override
    public boolean accept(Object source, IoEvent event) {
        return true;
    }

    @Override
    public void offered(Object source, IoEvent event) {
        int eventSize = this.estimateSize(event);
        int currentCounter = this.counter.addAndGet(eventSize);
        this.logState();
        if (currentCounter >= this.threshold) {
            this.block();
        }
    }

    @Override
    public void polled(Object source, IoEvent event) {
        int eventSize = this.estimateSize(event);
        int currentCounter = this.counter.addAndGet(-eventSize);
        this.logState();
        if (currentCounter < this.threshold) {
            this.unblock();
        }
    }

    private int estimateSize(IoEvent event) {
        int size = this.getEventSizeEstimator().estimateSize(event);
        if (size < 0) {
            throw new IllegalStateException(IoEventSizeEstimator.class.getSimpleName() + " returned " + "a negative value (" + size + "): " + event);
        }
        return size;
    }

    private void logState() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + " state: " + this.counter.get() + " / " + this.getThreshold());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void block() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + " blocked: " + this.counter.get() + " >= " + this.threshold);
        }
        Object object = this.lock;
        synchronized (object) {
            while (this.counter.get() >= this.threshold) {
                ++this.waiters;
                try {
                    this.lock.wait();
                }
                catch (InterruptedException interruptedException) {}
                continue;
                finally {
                    --this.waiters;
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(Thread.currentThread().getName() + " unblocked: " + this.counter.get() + " < " + this.threshold);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void unblock() {
        Object object = this.lock;
        synchronized (object) {
            if (this.waiters > 0) {
                this.lock.notifyAll();
            }
        }
    }
}

