/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.util.EnumSet;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;

public class ExecutorFilter
extends IoFilterAdapter {
    private EnumSet<IoEventType> eventTypes;
    private Executor executor;
    private boolean manageableExecutor;
    private static final int DEFAULT_MAX_POOL_SIZE = 16;
    private static final int BASE_THREAD_NUMBER = 0;
    private static final long DEFAULT_KEEPALIVE_TIME = 30L;
    private static final boolean MANAGEABLE_EXECUTOR = true;
    private static final boolean NOT_MANAGEABLE_EXECUTOR = false;
    private static final IoEventType[] DEFAULT_EVENT_SET = new IoEventType[]{IoEventType.EXCEPTION_CAUGHT, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT, IoEventType.SESSION_CLOSED, IoEventType.SESSION_IDLE, IoEventType.SESSION_OPENED};

    public ExecutorFilter() {
        Executor executor = this.createDefaultExecutor(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int maximumPoolSize) {
        Executor executor = this.createDefaultExecutor(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler) {
        OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
        this.init(executor, true, new IoEventType[0]);
    }

    public ExecutorFilter(IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int maximumPoolSize, IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler, IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventType ... eventTypes) {
        Executor executor = this.createDefaultExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler, IoEventType ... eventTypes) {
        OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
        this.init(executor, true, eventTypes);
    }

    public ExecutorFilter(Executor executor) {
        this.init(executor, false, new IoEventType[0]);
    }

    public ExecutorFilter(Executor executor, IoEventType ... eventTypes) {
        this.init(executor, false, eventTypes);
    }

    private Executor createDefaultExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler) {
        OrderedThreadPoolExecutor executor = new OrderedThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, queueHandler);
        return executor;
    }

    private void initEventTypes(IoEventType ... eventTypes) {
        if (eventTypes == null || eventTypes.length == 0) {
            eventTypes = DEFAULT_EVENT_SET;
        }
        this.eventTypes = EnumSet.of(eventTypes[0], eventTypes);
        if (this.eventTypes.contains((Object)IoEventType.SESSION_CREATED)) {
            this.eventTypes = null;
            throw new IllegalArgumentException((Object)((Object)IoEventType.SESSION_CREATED) + " is not allowed.");
        }
    }

    private void init(Executor executor, boolean manageableExecutor, IoEventType ... eventTypes) {
        if (executor == null) {
            throw new IllegalArgumentException("executor");
        }
        this.initEventTypes(eventTypes);
        this.executor = executor;
        this.manageableExecutor = manageableExecutor;
    }

    @Override
    public void destroy() {
        if (this.manageableExecutor) {
            ((ExecutorService)this.executor).shutdown();
        }
    }

    public final Executor getExecutor() {
        return this.executor;
    }

    protected void fireEvent(IoFilterEvent event) {
        this.executor.execute(event);
    }

    @Override
    public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
        if (parent.contains(this)) {
            throw new IllegalArgumentException("You can't add the same filter instance more than once.  Create another instance and add it.");
        }
    }

    @Override
    public final void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) {
        if (this.eventTypes.contains((Object)IoEventType.SESSION_OPENED)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_OPENED, session, null);
            this.fireEvent(event);
        } else {
            nextFilter.sessionOpened(session);
        }
    }

    @Override
    public final void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) {
        if (this.eventTypes.contains((Object)IoEventType.SESSION_CLOSED)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_CLOSED, session, null);
            this.fireEvent(event);
        } else {
            nextFilter.sessionClosed(session);
        }
    }

    @Override
    public final void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) {
        if (this.eventTypes.contains((Object)IoEventType.SESSION_IDLE)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.SESSION_IDLE, session, status);
            this.fireEvent(event);
        } else {
            nextFilter.sessionIdle(session, status);
        }
    }

    @Override
    public final void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) {
        if (this.eventTypes.contains((Object)IoEventType.EXCEPTION_CAUGHT)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.EXCEPTION_CAUGHT, session, cause);
            this.fireEvent(event);
        } else {
            nextFilter.exceptionCaught(session, cause);
        }
    }

    @Override
    public final void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) {
        if (this.eventTypes.contains((Object)IoEventType.MESSAGE_RECEIVED)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.MESSAGE_RECEIVED, session, message);
            this.fireEvent(event);
        } else {
            nextFilter.messageReceived(session, message);
        }
    }

    @Override
    public final void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) {
        if (this.eventTypes.contains((Object)IoEventType.MESSAGE_SENT)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.MESSAGE_SENT, session, writeRequest);
            this.fireEvent(event);
        } else {
            nextFilter.messageSent(session, writeRequest);
        }
    }

    @Override
    public final void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) {
        if (this.eventTypes.contains((Object)IoEventType.WRITE)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.WRITE, session, writeRequest);
            this.fireEvent(event);
        } else {
            nextFilter.filterWrite(session, writeRequest);
        }
    }

    @Override
    public final void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        if (this.eventTypes.contains((Object)IoEventType.CLOSE)) {
            IoFilterEvent event = new IoFilterEvent(nextFilter, IoEventType.CLOSE, session, null);
            this.fireEvent(event);
        } else {
            nextFilter.filterClose(session);
        }
    }
}

