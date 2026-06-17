/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.polling.AbstractPollingIoProcessor;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ExceptionMonitor;

public class DefaultIoFuture
implements IoFuture {
    private static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;
    private final IoSession session;
    private final Object lock;
    private IoFutureListener<?> firstListener;
    private List<IoFutureListener<?>> otherListeners;
    private Object result;
    private boolean ready;
    private int waiters;

    public DefaultIoFuture(IoSession session) {
        this.session = session;
        this.lock = this;
    }

    @Override
    public IoSession getSession() {
        return this.session;
    }

    @Override
    @Deprecated
    public void join() {
        this.awaitUninterruptibly();
    }

    @Override
    @Deprecated
    public boolean join(long timeoutMillis) {
        return this.awaitUninterruptibly(timeoutMillis);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public IoFuture await() throws InterruptedException {
        Object object = this.lock;
        synchronized (object) {
            while (!this.ready) {
                ++this.waiters;
                try {
                    this.lock.wait(5000L);
                }
                finally {
                    --this.waiters;
                    if (this.ready) continue;
                    this.checkDeadLock();
                }
            }
            return this;
        }
    }

    @Override
    public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
        return this.await0(unit.toMillis(timeout), true);
    }

    @Override
    public boolean await(long timeoutMillis) throws InterruptedException {
        return this.await0(timeoutMillis, true);
    }

    @Override
    public IoFuture awaitUninterruptibly() {
        try {
            this.await0(Long.MAX_VALUE, false);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        return this;
    }

    @Override
    public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
        try {
            return this.await0(unit.toMillis(timeout), false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    @Override
    public boolean awaitUninterruptibly(long timeoutMillis) {
        try {
            return this.await0(timeoutMillis, false);
        }
        catch (InterruptedException e) {
            throw new InternalError();
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean await0(long timeoutMillis, boolean interruptable) throws InterruptedException {
        long endTime = System.currentTimeMillis() + timeoutMillis;
        if (endTime < 0L) {
            endTime = Long.MAX_VALUE;
        }
        Object object = this.lock;
        synchronized (object) {
            if (this.ready || timeoutMillis <= 0L) {
                return this.ready;
            }
            ++this.waiters;
            try {
                while (true) {
                    block13: {
                        try {
                            long timeOut = Math.min(timeoutMillis, 5000L);
                            this.lock.wait(timeOut);
                        }
                        catch (InterruptedException e) {
                            if (!interruptable) break block13;
                            throw e;
                        }
                    }
                    if (this.ready || endTime < System.currentTimeMillis()) {
                        boolean bl = this.ready;
                        return bl;
                    }
                    this.checkDeadLock();
                }
            }
            finally {
                --this.waiters;
                if (!this.ready) {
                    this.checkDeadLock();
                }
            }
        }
    }

    private void checkDeadLock() {
        StackTraceElement[] stackTrace;
        if (!(this instanceof CloseFuture || this instanceof WriteFuture || this instanceof ReadFuture || this instanceof ConnectFuture)) {
            return;
        }
        for (StackTraceElement stackElement : stackTrace = Thread.currentThread().getStackTrace()) {
            if (!AbstractPollingIoProcessor.class.getName().equals(stackElement.getClassName())) continue;
            IllegalStateException e = new IllegalStateException("t");
            e.getStackTrace();
            throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
        }
        for (StackTraceElement s : stackTrace) {
            try {
                Class<?> cls = DefaultIoFuture.class.getClassLoader().loadClass(s.getClassName());
                if (!IoProcessor.class.isAssignableFrom(cls)) continue;
                throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
            }
            catch (ClassNotFoundException classNotFoundException) {
                // empty catch block
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isDone() {
        Object object = this.lock;
        synchronized (object) {
            return this.ready;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean setValue(Object newValue) {
        Object object = this.lock;
        synchronized (object) {
            if (this.ready) {
                return false;
            }
            this.result = newValue;
            this.ready = true;
            if (this.waiters > 0) {
                this.lock.notifyAll();
            }
        }
        this.notifyListeners();
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getValue() {
        Object object = this.lock;
        synchronized (object) {
            return this.result;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IoFuture addListener(IoFutureListener<?> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        Object object = this.lock;
        synchronized (object) {
            if (this.ready) {
                this.notifyListener(listener);
            } else if (this.firstListener == null) {
                this.firstListener = listener;
            } else {
                if (this.otherListeners == null) {
                    this.otherListeners = new ArrayList(1);
                }
                this.otherListeners.add(listener);
            }
        }
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public IoFuture removeListener(IoFutureListener<?> listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener");
        }
        Object object = this.lock;
        synchronized (object) {
            if (!this.ready) {
                if (listener == this.firstListener) {
                    this.firstListener = this.otherListeners != null && !this.otherListeners.isEmpty() ? this.otherListeners.remove(0) : null;
                } else if (this.otherListeners != null) {
                    this.otherListeners.remove(listener);
                }
            }
        }
        return this;
    }

    private void notifyListeners() {
        if (this.firstListener != null) {
            this.notifyListener(this.firstListener);
            this.firstListener = null;
            if (this.otherListeners != null) {
                for (IoFutureListener<?> listener : this.otherListeners) {
                    this.notifyListener(listener);
                }
                this.otherListeners = null;
            }
        }
    }

    private void notifyListener(IoFutureListener listener) {
        try {
            listener.operationComplete(this);
        }
        catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
        }
    }
}

