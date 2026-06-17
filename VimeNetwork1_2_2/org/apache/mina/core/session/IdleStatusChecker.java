/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.util.Set;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.util.ConcurrentHashSet;

public class IdleStatusChecker {
    private final Set<AbstractIoSession> sessions = new ConcurrentHashSet<AbstractIoSession>();
    private final NotifyingTask notifyingTask = new NotifyingTask();
    private final IoFutureListener<IoFuture> sessionCloseListener = new SessionCloseListener();

    public void addSession(AbstractIoSession session) {
        this.sessions.add(session);
        CloseFuture closeFuture = session.getCloseFuture();
        closeFuture.addListener(this.sessionCloseListener);
    }

    private void removeSession(AbstractIoSession session) {
        this.sessions.remove(session);
    }

    public NotifyingTask getNotifyingTask() {
        return this.notifyingTask;
    }

    private class SessionCloseListener
    implements IoFutureListener<IoFuture> {
        @Override
        public void operationComplete(IoFuture future) {
            IdleStatusChecker.this.removeSession((AbstractIoSession)future.getSession());
        }
    }

    public class NotifyingTask
    implements Runnable {
        private volatile boolean cancelled;
        private volatile Thread thread;

        NotifyingTask() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            block7: {
                this.thread = Thread.currentThread();
                block5: while (true) {
                    while (!this.cancelled) {
                        long currentTime = System.currentTimeMillis();
                        this.notifySessions(currentTime);
                        try {
                            Thread.sleep(1000L);
                            continue block5;
                        }
                        catch (InterruptedException interruptedException) {
                        }
                    }
                    break block7;
                    {
                        continue block5;
                        break;
                    }
                    break;
                }
                finally {
                    this.thread = null;
                }
            }
        }

        public void cancel() {
            this.cancelled = true;
            Thread thread = this.thread;
            if (thread != null) {
                thread.interrupt();
            }
        }

        private void notifySessions(long currentTime) {
            for (AbstractIoSession session : IdleStatusChecker.this.sessions) {
                if (!session.isConnected()) continue;
                AbstractIoSession.notifyIdleSession(session, currentTime);
            }
        }
    }
}

