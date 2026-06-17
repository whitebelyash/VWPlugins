/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.vmpipe;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChain;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.transport.vmpipe.VmPipeSession;

class VmPipeFilterChain
extends DefaultIoFilterChain {
    private final Queue<IoEvent> eventQueue = new ConcurrentLinkedQueue<IoEvent>();
    private final IoProcessor<VmPipeSession> processor = new VmPipeIoProcessor();
    private volatile boolean flushEnabled;
    private volatile boolean sessionOpened;

    VmPipeFilterChain(AbstractIoSession session) {
        super(session);
    }

    IoProcessor<VmPipeSession> getProcessor() {
        return this.processor;
    }

    public void start() {
        this.flushEnabled = true;
        this.flushEvents();
        VmPipeFilterChain.flushPendingDataQueues((VmPipeSession)this.getSession());
    }

    private void pushEvent(IoEvent e) {
        this.pushEvent(e, this.flushEnabled);
    }

    private void pushEvent(IoEvent e, boolean flushNow) {
        this.eventQueue.add(e);
        if (flushNow) {
            this.flushEvents();
        }
    }

    private void flushEvents() {
        IoEvent e;
        while ((e = this.eventQueue.poll()) != null) {
            this.fireEvent(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireEvent(IoEvent e) {
        VmPipeSession session = (VmPipeSession)this.getSession();
        IoEventType type = e.getType();
        Object data = e.getParameter();
        if (type == IoEventType.MESSAGE_RECEIVED) {
            if (this.sessionOpened && !session.isReadSuspended() && session.getLock().tryLock()) {
                try {
                    if (session.isReadSuspended()) {
                        session.receivedMessageQueue.add(data);
                    }
                    super.fireMessageReceived(data);
                }
                finally {
                    session.getLock().unlock();
                }
            } else {
                session.receivedMessageQueue.add(data);
            }
        } else if (type == IoEventType.WRITE) {
            super.fireFilterWrite((WriteRequest)data);
        } else if (type == IoEventType.MESSAGE_SENT) {
            super.fireMessageSent((WriteRequest)data);
        } else if (type == IoEventType.EXCEPTION_CAUGHT) {
            super.fireExceptionCaught((Throwable)data);
        } else if (type == IoEventType.SESSION_IDLE) {
            super.fireSessionIdle((IdleStatus)data);
        } else if (type == IoEventType.SESSION_OPENED) {
            super.fireSessionOpened();
            this.sessionOpened = true;
        } else if (type == IoEventType.SESSION_CREATED) {
            session.getLock().lock();
            try {
                super.fireSessionCreated();
            }
            finally {
                session.getLock().unlock();
            }
        } else if (type == IoEventType.SESSION_CLOSED) {
            VmPipeFilterChain.flushPendingDataQueues(session);
            super.fireSessionClosed();
        } else if (type == IoEventType.CLOSE) {
            super.fireFilterClose();
        }
    }

    private static void flushPendingDataQueues(VmPipeSession s) {
        s.getProcessor().updateTrafficControl(s);
        s.getRemoteSession().getProcessor().updateTrafficControl(s);
    }

    @Override
    public void fireFilterClose() {
        this.pushEvent(new IoEvent(IoEventType.CLOSE, this.getSession(), null));
    }

    @Override
    public void fireFilterWrite(WriteRequest writeRequest) {
        this.pushEvent(new IoEvent(IoEventType.WRITE, this.getSession(), writeRequest));
    }

    @Override
    public void fireExceptionCaught(Throwable cause) {
        this.pushEvent(new IoEvent(IoEventType.EXCEPTION_CAUGHT, this.getSession(), cause));
    }

    @Override
    public void fireMessageSent(WriteRequest request) {
        this.pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, this.getSession(), request));
    }

    @Override
    public void fireSessionClosed() {
        this.pushEvent(new IoEvent(IoEventType.SESSION_CLOSED, this.getSession(), null));
    }

    @Override
    public void fireSessionCreated() {
        this.pushEvent(new IoEvent(IoEventType.SESSION_CREATED, this.getSession(), null));
    }

    @Override
    public void fireSessionIdle(IdleStatus status) {
        this.pushEvent(new IoEvent(IoEventType.SESSION_IDLE, this.getSession(), status));
    }

    @Override
    public void fireSessionOpened() {
        this.pushEvent(new IoEvent(IoEventType.SESSION_OPENED, this.getSession(), null));
    }

    @Override
    public void fireMessageReceived(Object message) {
        this.pushEvent(new IoEvent(IoEventType.MESSAGE_RECEIVED, this.getSession(), message));
    }

    private class VmPipeIoProcessor
    implements IoProcessor<VmPipeSession> {
        private VmPipeIoProcessor() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void flush(VmPipeSession session) {
            WriteRequestQueue queue = session.getWriteRequestQueue0();
            if (!session.isClosing()) {
                session.getLock().lock();
                try {
                    WriteRequest req;
                    if (queue.isEmpty(session)) {
                        return;
                    }
                    long currentTime = System.currentTimeMillis();
                    while ((req = queue.poll(session)) != null) {
                        Object m = req.getMessage();
                        VmPipeFilterChain.this.pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, session, req), false);
                        session.getRemoteSession().getFilterChain().fireMessageReceived(this.getMessageCopy(m));
                        if (!(m instanceof IoBuffer)) continue;
                        session.increaseWrittenBytes0(((IoBuffer)m).remaining(), currentTime);
                    }
                }
                finally {
                    if (VmPipeFilterChain.this.flushEnabled) {
                        VmPipeFilterChain.this.flushEvents();
                    }
                    session.getLock().unlock();
                }
                VmPipeFilterChain.flushPendingDataQueues(session);
            } else {
                WriteRequest req;
                ArrayList<WriteRequest> failedRequests = new ArrayList<WriteRequest>();
                while ((req = queue.poll(session)) != null) {
                    failedRequests.add(req);
                }
                if (!failedRequests.isEmpty()) {
                    WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);
                    for (WriteRequest r : failedRequests) {
                        r.getFuture().setException(cause);
                    }
                    session.getFilterChain().fireExceptionCaught(cause);
                }
            }
        }

        @Override
        public void write(VmPipeSession session, WriteRequest writeRequest) {
            WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
            writeRequestQueue.offer(session, writeRequest);
            if (!session.isWriteSuspended()) {
                this.flush(session);
            }
        }

        private Object getMessageCopy(Object message) {
            Object messageCopy = message;
            if (message instanceof IoBuffer) {
                IoBuffer rb = (IoBuffer)message;
                rb.mark();
                IoBuffer wb = IoBuffer.allocate(rb.remaining());
                wb.put(rb);
                wb.flip();
                rb.reset();
                messageCopy = wb;
            }
            return messageCopy;
        }

        @Override
        public void remove(VmPipeSession session) {
            try {
                session.getLock().lock();
                if (!session.getCloseFuture().isClosed()) {
                    session.getServiceListeners().fireSessionDestroyed(session);
                    session.getRemoteSession().closeNow();
                }
            }
            finally {
                session.getLock().unlock();
            }
        }

        @Override
        public void add(VmPipeSession session) {
        }

        @Override
        public void updateTrafficControl(VmPipeSession session) {
            if (!session.isReadSuspended()) {
                ArrayList data = new ArrayList();
                session.receivedMessageQueue.drainTo(data);
                for (Object aData : data) {
                    VmPipeFilterChain.this.fireMessageReceived(aData);
                }
            }
            if (!session.isWriteSuspended()) {
                this.flush(session);
            }
        }

        @Override
        public void dispose() {
        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public boolean isDisposing() {
            return false;
        }
    }
}

