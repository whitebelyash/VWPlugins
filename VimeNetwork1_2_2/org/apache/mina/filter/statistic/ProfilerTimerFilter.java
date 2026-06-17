/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.statistic;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class ProfilerTimerFilter
extends IoFilterAdapter {
    private volatile TimeUnit timeUnit;
    private TimerWorker messageReceivedTimerWorker;
    private boolean profileMessageReceived = false;
    private TimerWorker messageSentTimerWorker;
    private boolean profileMessageSent = false;
    private TimerWorker sessionCreatedTimerWorker;
    private boolean profileSessionCreated = false;
    private TimerWorker sessionOpenedTimerWorker;
    private boolean profileSessionOpened = false;
    private TimerWorker sessionIdleTimerWorker;
    private boolean profileSessionIdle = false;
    private TimerWorker sessionClosedTimerWorker;
    private boolean profileSessionClosed = false;

    public ProfilerTimerFilter() {
        this(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
    }

    public ProfilerTimerFilter(TimeUnit timeUnit) {
        this(timeUnit, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
    }

    public ProfilerTimerFilter(TimeUnit timeUnit, IoEventType ... eventTypes) {
        this.timeUnit = timeUnit;
        this.setProfilers(eventTypes);
    }

    private void setProfilers(IoEventType ... eventTypes) {
        block8: for (IoEventType type : eventTypes) {
            switch (type) {
                case MESSAGE_RECEIVED: {
                    this.messageReceivedTimerWorker = new TimerWorker();
                    this.profileMessageReceived = true;
                    continue block8;
                }
                case MESSAGE_SENT: {
                    this.messageSentTimerWorker = new TimerWorker();
                    this.profileMessageSent = true;
                    continue block8;
                }
                case SESSION_CREATED: {
                    this.sessionCreatedTimerWorker = new TimerWorker();
                    this.profileSessionCreated = true;
                    continue block8;
                }
                case SESSION_OPENED: {
                    this.sessionOpenedTimerWorker = new TimerWorker();
                    this.profileSessionOpened = true;
                    continue block8;
                }
                case SESSION_IDLE: {
                    this.sessionIdleTimerWorker = new TimerWorker();
                    this.profileSessionIdle = true;
                    continue block8;
                }
                case SESSION_CLOSED: {
                    this.sessionClosedTimerWorker = new TimerWorker();
                    this.profileSessionClosed = true;
                    continue block8;
                }
            }
        }
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public void profile(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                this.profileMessageReceived = true;
                if (this.messageReceivedTimerWorker == null) {
                    this.messageReceivedTimerWorker = new TimerWorker();
                }
                return;
            }
            case MESSAGE_SENT: {
                this.profileMessageSent = true;
                if (this.messageSentTimerWorker == null) {
                    this.messageSentTimerWorker = new TimerWorker();
                }
                return;
            }
            case SESSION_CREATED: {
                this.profileSessionCreated = true;
                if (this.sessionCreatedTimerWorker == null) {
                    this.sessionCreatedTimerWorker = new TimerWorker();
                }
                return;
            }
            case SESSION_OPENED: {
                this.profileSessionOpened = true;
                if (this.sessionOpenedTimerWorker == null) {
                    this.sessionOpenedTimerWorker = new TimerWorker();
                }
                return;
            }
            case SESSION_IDLE: {
                this.profileSessionIdle = true;
                if (this.sessionIdleTimerWorker == null) {
                    this.sessionIdleTimerWorker = new TimerWorker();
                }
                return;
            }
            case SESSION_CLOSED: {
                this.profileSessionClosed = true;
                if (this.sessionClosedTimerWorker == null) {
                    this.sessionClosedTimerWorker = new TimerWorker();
                }
                return;
            }
        }
    }

    public void stopProfile(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                this.profileMessageReceived = false;
                return;
            }
            case MESSAGE_SENT: {
                this.profileMessageSent = false;
                return;
            }
            case SESSION_CREATED: {
                this.profileSessionCreated = false;
                return;
            }
            case SESSION_OPENED: {
                this.profileSessionOpened = false;
                return;
            }
            case SESSION_IDLE: {
                this.profileSessionIdle = false;
                return;
            }
            case SESSION_CLOSED: {
                this.profileSessionClosed = false;
                return;
            }
        }
    }

    public Set<IoEventType> getEventsToProfile() {
        HashSet<IoEventType> set = new HashSet<IoEventType>();
        if (this.profileMessageReceived) {
            set.add(IoEventType.MESSAGE_RECEIVED);
        }
        if (this.profileMessageSent) {
            set.add(IoEventType.MESSAGE_SENT);
        }
        if (this.profileSessionCreated) {
            set.add(IoEventType.SESSION_CREATED);
        }
        if (this.profileSessionOpened) {
            set.add(IoEventType.SESSION_OPENED);
        }
        if (this.profileSessionIdle) {
            set.add(IoEventType.SESSION_IDLE);
        }
        if (this.profileSessionClosed) {
            set.add(IoEventType.SESSION_CLOSED);
        }
        return set;
    }

    public void setEventsToProfile(IoEventType ... eventTypes) {
        this.setProfilers(eventTypes);
    }

    @Override
    public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
        if (this.profileMessageReceived) {
            long start = this.timeNow();
            nextFilter.messageReceived(session, message);
            long end = this.timeNow();
            this.messageReceivedTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.messageReceived(session, message);
        }
    }

    @Override
    public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        if (this.profileMessageSent) {
            long start = this.timeNow();
            nextFilter.messageSent(session, writeRequest);
            long end = this.timeNow();
            this.messageSentTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.messageSent(session, writeRequest);
        }
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        if (this.profileSessionCreated) {
            long start = this.timeNow();
            nextFilter.sessionCreated(session);
            long end = this.timeNow();
            this.sessionCreatedTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.sessionCreated(session);
        }
    }

    @Override
    public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        if (this.profileSessionOpened) {
            long start = this.timeNow();
            nextFilter.sessionOpened(session);
            long end = this.timeNow();
            this.sessionOpenedTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.sessionOpened(session);
        }
    }

    @Override
    public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        if (this.profileSessionIdle) {
            long start = this.timeNow();
            nextFilter.sessionIdle(session, status);
            long end = this.timeNow();
            this.sessionIdleTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.sessionIdle(session, status);
        }
    }

    @Override
    public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        if (this.profileSessionClosed) {
            long start = this.timeNow();
            nextFilter.sessionClosed(session);
            long end = this.timeNow();
            this.sessionClosedTimerWorker.addNewDuration(end - start);
        } else {
            nextFilter.sessionClosed(session);
        }
    }

    public double getAverageTime(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                if (!this.profileMessageReceived) break;
                return this.messageReceivedTimerWorker.getAverage();
            }
            case MESSAGE_SENT: {
                if (!this.profileMessageSent) break;
                return this.messageSentTimerWorker.getAverage();
            }
            case SESSION_CREATED: {
                if (!this.profileSessionCreated) break;
                return this.sessionCreatedTimerWorker.getAverage();
            }
            case SESSION_OPENED: {
                if (!this.profileSessionOpened) break;
                return this.sessionOpenedTimerWorker.getAverage();
            }
            case SESSION_IDLE: {
                if (!this.profileSessionIdle) break;
                return this.sessionIdleTimerWorker.getAverage();
            }
            case SESSION_CLOSED: {
                if (!this.profileSessionClosed) break;
                return this.sessionClosedTimerWorker.getAverage();
            }
        }
        throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
    }

    public long getTotalCalls(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                if (!this.profileMessageReceived) break;
                return this.messageReceivedTimerWorker.getCallsNumber();
            }
            case MESSAGE_SENT: {
                if (!this.profileMessageSent) break;
                return this.messageSentTimerWorker.getCallsNumber();
            }
            case SESSION_CREATED: {
                if (!this.profileSessionCreated) break;
                return this.sessionCreatedTimerWorker.getCallsNumber();
            }
            case SESSION_OPENED: {
                if (!this.profileSessionOpened) break;
                return this.sessionOpenedTimerWorker.getCallsNumber();
            }
            case SESSION_IDLE: {
                if (!this.profileSessionIdle) break;
                return this.sessionIdleTimerWorker.getCallsNumber();
            }
            case SESSION_CLOSED: {
                if (!this.profileSessionClosed) break;
                return this.sessionClosedTimerWorker.getCallsNumber();
            }
        }
        throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
    }

    public long getTotalTime(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                if (!this.profileMessageReceived) break;
                return this.messageReceivedTimerWorker.getTotal();
            }
            case MESSAGE_SENT: {
                if (!this.profileMessageSent) break;
                return this.messageSentTimerWorker.getTotal();
            }
            case SESSION_CREATED: {
                if (!this.profileSessionCreated) break;
                return this.sessionCreatedTimerWorker.getTotal();
            }
            case SESSION_OPENED: {
                if (!this.profileSessionOpened) break;
                return this.sessionOpenedTimerWorker.getTotal();
            }
            case SESSION_IDLE: {
                if (!this.profileSessionIdle) break;
                return this.sessionIdleTimerWorker.getTotal();
            }
            case SESSION_CLOSED: {
                if (!this.profileSessionClosed) break;
                return this.sessionClosedTimerWorker.getTotal();
            }
        }
        throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
    }

    public long getMinimumTime(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                if (!this.profileMessageReceived) break;
                return this.messageReceivedTimerWorker.getMinimum();
            }
            case MESSAGE_SENT: {
                if (!this.profileMessageSent) break;
                return this.messageSentTimerWorker.getMinimum();
            }
            case SESSION_CREATED: {
                if (!this.profileSessionCreated) break;
                return this.sessionCreatedTimerWorker.getMinimum();
            }
            case SESSION_OPENED: {
                if (!this.profileSessionOpened) break;
                return this.sessionOpenedTimerWorker.getMinimum();
            }
            case SESSION_IDLE: {
                if (!this.profileSessionIdle) break;
                return this.sessionIdleTimerWorker.getMinimum();
            }
            case SESSION_CLOSED: {
                if (!this.profileSessionClosed) break;
                return this.sessionClosedTimerWorker.getMinimum();
            }
        }
        throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
    }

    public long getMaximumTime(IoEventType type) {
        switch (type) {
            case MESSAGE_RECEIVED: {
                if (!this.profileMessageReceived) break;
                return this.messageReceivedTimerWorker.getMaximum();
            }
            case MESSAGE_SENT: {
                if (!this.profileMessageSent) break;
                return this.messageSentTimerWorker.getMaximum();
            }
            case SESSION_CREATED: {
                if (!this.profileSessionCreated) break;
                return this.sessionCreatedTimerWorker.getMaximum();
            }
            case SESSION_OPENED: {
                if (!this.profileSessionOpened) break;
                return this.sessionOpenedTimerWorker.getMaximum();
            }
            case SESSION_IDLE: {
                if (!this.profileSessionIdle) break;
                return this.sessionIdleTimerWorker.getMaximum();
            }
            case SESSION_CLOSED: {
                if (!this.profileSessionClosed) break;
                return this.sessionClosedTimerWorker.getMaximum();
            }
        }
        throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
    }

    private long timeNow() {
        switch (this.timeUnit) {
            case SECONDS: {
                return System.currentTimeMillis() / 1000L;
            }
            case MICROSECONDS: {
                return System.nanoTime() / 1000L;
            }
            case NANOSECONDS: {
                return System.nanoTime();
            }
        }
        return System.currentTimeMillis();
    }

    private class TimerWorker {
        private final AtomicLong total;
        private final AtomicLong callsNumber;
        private final AtomicLong minimum;
        private final AtomicLong maximum;
        private final Object lock = new Object();

        public TimerWorker() {
            this.total = new AtomicLong();
            this.callsNumber = new AtomicLong();
            this.minimum = new AtomicLong();
            this.maximum = new AtomicLong();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addNewDuration(long duration) {
            this.callsNumber.incrementAndGet();
            this.total.addAndGet(duration);
            Object object = this.lock;
            synchronized (object) {
                if (duration < this.minimum.longValue()) {
                    this.minimum.set(duration);
                }
                if (duration > this.maximum.longValue()) {
                    this.maximum.set(duration);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public double getAverage() {
            Object object = this.lock;
            synchronized (object) {
                return this.total.longValue() / this.callsNumber.longValue();
            }
        }

        public long getCallsNumber() {
            return this.callsNumber.longValue();
        }

        public long getTotal() {
            return this.total.longValue();
        }

        public long getMinimum() {
            return this.minimum.longValue();
        }

        public long getMaximum() {
            return this.maximum.longValue();
        }
    }
}

