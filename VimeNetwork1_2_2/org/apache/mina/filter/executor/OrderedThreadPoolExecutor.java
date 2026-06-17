/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.IoEventQueueHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderedThreadPoolExecutor
extends ThreadPoolExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderedThreadPoolExecutor.class);
    private static final int DEFAULT_INITIAL_THREAD_POOL_SIZE = 0;
    private static final int DEFAULT_MAX_THREAD_POOL = 16;
    private static final int DEFAULT_KEEP_ALIVE = 30;
    private static final IoSession EXIT_SIGNAL = new DummySession();
    private final AttributeKey TASKS_QUEUE = new AttributeKey(this.getClass(), "tasksQueue");
    private final BlockingQueue<IoSession> waitingSessions = new LinkedBlockingQueue<IoSession>();
    private final Set<Worker> workers = new HashSet<Worker>();
    private volatile int largestPoolSize;
    private final AtomicInteger idleWorkers = new AtomicInteger();
    private long completedTaskCount;
    private volatile boolean shutdown;
    private final IoEventQueueHandler eventQueueHandler;

    public OrderedThreadPoolExecutor() {
        this(0, 16, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int maximumPoolSize) {
        this(0, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler eventQueueHandler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), eventQueueHandler);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
    }

    public OrderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler eventQueueHandler) {
        super(0, 1, keepAliveTime, unit, new SynchronousQueue<Runnable>(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (maximumPoolSize == 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        super.setCorePoolSize(corePoolSize);
        super.setMaximumPoolSize(maximumPoolSize);
        this.eventQueueHandler = eventQueueHandler == null ? IoEventQueueHandler.NOOP : eventQueueHandler;
    }

    private SessionTasksQueue getSessionTasksQueue(IoSession session) {
        SessionTasksQueue oldQueue;
        SessionTasksQueue queue = (SessionTasksQueue)session.getAttribute(this.TASKS_QUEUE);
        if (queue == null && (oldQueue = (SessionTasksQueue)session.setAttributeIfAbsent(this.TASKS_QUEUE, queue = new SessionTasksQueue())) != null) {
            queue = oldQueue;
        }
        return queue;
    }

    public IoEventQueueHandler getQueueHandler() {
        return this.eventQueueHandler;
    }

    @Override
    public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addWorker() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            if (this.workers.size() >= super.getMaximumPoolSize()) {
                return;
            }
            Worker worker = new Worker();
            Thread thread = this.getThreadFactory().newThread(worker);
            this.idleWorkers.incrementAndGet();
            thread.start();
            this.workers.add(worker);
            if (this.workers.size() > this.largestPoolSize) {
                this.largestPoolSize = this.workers.size();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void addWorkerIfNecessary() {
        if (this.idleWorkers.get() == 0) {
            Set<Worker> set = this.workers;
            synchronized (set) {
                if (this.workers.isEmpty() || this.idleWorkers.get() == 0) {
                    this.addWorker();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void removeWorker() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            if (this.workers.size() <= super.getCorePoolSize()) {
                return;
            }
            this.waitingSessions.offer(EXIT_SIGNAL);
        }
    }

    @Override
    public int getMaximumPoolSize() {
        return super.getMaximumPoolSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < super.getCorePoolSize()) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        Set<Worker> set = this.workers;
        synchronized (set) {
            super.setMaximumPoolSize(maximumPoolSize);
            for (int difference = this.workers.size() - maximumPoolSize; difference > 0; --difference) {
                this.removeWorker();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
        Set<Worker> set = this.workers;
        synchronized (set) {
            long waitTime;
            while (!this.isTerminated() && (waitTime = deadline - System.currentTimeMillis()) > 0L) {
                this.workers.wait(waitTime);
            }
        }
        return this.isTerminated();
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isTerminated() {
        if (!this.shutdown) {
            return false;
        }
        Set<Worker> set = this.workers;
        synchronized (set) {
            return this.workers.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void shutdown() {
        if (this.shutdown) {
            return;
        }
        this.shutdown = true;
        Set<Worker> set = this.workers;
        synchronized (set) {
            for (int i = this.workers.size(); i > 0; --i) {
                this.waitingSessions.offer(EXIT_SIGNAL);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<Runnable> shutdownNow() {
        IoSession session;
        this.shutdown();
        ArrayList<Runnable> answer = new ArrayList<Runnable>();
        while ((session = (IoSession)this.waitingSessions.poll()) != null) {
            if (session == EXIT_SIGNAL) {
                this.waitingSessions.offer(EXIT_SIGNAL);
                Thread.yield();
                continue;
            }
            SessionTasksQueue sessionTasksQueue = (SessionTasksQueue)session.getAttribute(this.TASKS_QUEUE);
            Queue queue = sessionTasksQueue.tasksQueue;
            synchronized (queue) {
                for (Runnable task : sessionTasksQueue.tasksQueue) {
                    this.getQueueHandler().polled(this, (IoEvent)task);
                    answer.add(task);
                }
                sessionTasksQueue.tasksQueue.clear();
            }
        }
        return answer;
    }

    private void print(Queue<Runnable> queue, IoEvent event) {
        StringBuilder sb = new StringBuilder();
        sb.append("Adding event ").append((Object)event.getType()).append(" to session ").append(event.getSession().getId());
        boolean first = true;
        sb.append("\nQueue : [");
        for (Runnable elem : queue) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append((Object)((IoEvent)elem).getType()).append(", ");
        }
        sb.append("]\n");
        LOGGER.debug(sb.toString());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Runnable task) {
        boolean offerSession;
        if (this.shutdown) {
            this.rejectTask(task);
        }
        this.checkTaskType(task);
        IoEvent event = (IoEvent)task;
        IoSession session = event.getSession();
        SessionTasksQueue sessionTasksQueue = this.getSessionTasksQueue(session);
        Queue tasksQueue = sessionTasksQueue.tasksQueue;
        boolean offerEvent = this.eventQueueHandler.accept(this, event);
        if (offerEvent) {
            Queue queue = tasksQueue;
            synchronized (queue) {
                tasksQueue.offer(event);
                if (sessionTasksQueue.processingCompleted) {
                    sessionTasksQueue.processingCompleted = false;
                    offerSession = true;
                } else {
                    offerSession = false;
                }
                if (LOGGER.isDebugEnabled()) {
                    this.print(tasksQueue, event);
                }
            }
        } else {
            offerSession = false;
        }
        if (offerSession) {
            this.waitingSessions.offer(session);
        }
        this.addWorkerIfNecessary();
        if (offerEvent) {
            this.eventQueueHandler.offered(this, event);
        }
    }

    private void rejectTask(Runnable task) {
        this.getRejectedExecutionHandler().rejectedExecution(task, this);
    }

    private void checkTaskType(Runnable task) {
        if (!(task instanceof IoEvent)) {
            throw new IllegalArgumentException("task must be an IoEvent or its subclass.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getActiveCount() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            return this.workers.size() - this.idleWorkers.get();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public long getCompletedTaskCount() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            long answer = this.completedTaskCount;
            for (Worker w : this.workers) {
                answer += w.completedTaskCount.get();
            }
            return answer;
        }
    }

    @Override
    public int getLargestPoolSize() {
        return this.largestPoolSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int getPoolSize() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            return this.workers.size();
        }
    }

    @Override
    public long getTaskCount() {
        return this.getCompletedTaskCount();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isTerminating() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            return this.isShutdown() && !this.isTerminated();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int prestartAllCoreThreads() {
        int answer = 0;
        Set<Worker> set = this.workers;
        synchronized (set) {
            for (int i = super.getCorePoolSize() - this.workers.size(); i > 0; --i) {
                this.addWorker();
                ++answer;
            }
        }
        return answer;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean prestartCoreThread() {
        Set<Worker> set = this.workers;
        synchronized (set) {
            if (this.workers.size() < super.getCorePoolSize()) {
                this.addWorker();
                return true;
            }
            return false;
        }
    }

    @Override
    public BlockingQueue<Runnable> getQueue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void purge() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Runnable task) {
        boolean removed;
        Queue tasksQueue;
        this.checkTaskType(task);
        IoEvent event = (IoEvent)task;
        IoSession session = event.getSession();
        SessionTasksQueue sessionTasksQueue = (SessionTasksQueue)session.getAttribute(this.TASKS_QUEUE);
        if (sessionTasksQueue == null) {
            return false;
        }
        Queue queue = tasksQueue = sessionTasksQueue.tasksQueue;
        synchronized (queue) {
            removed = tasksQueue.remove(task);
        }
        if (removed) {
            this.getQueueHandler().polled(this, event);
        }
        return removed;
    }

    @Override
    public int getCorePoolSize() {
        return super.getCorePoolSize();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > super.getMaximumPoolSize()) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }
        Set<Worker> set = this.workers;
        synchronized (set) {
            if (super.getCorePoolSize() > corePoolSize) {
                for (int i = super.getCorePoolSize() - corePoolSize; i > 0; --i) {
                    this.removeWorker();
                }
            }
            super.setCorePoolSize(corePoolSize);
        }
    }

    static /* synthetic */ AtomicInteger access$500(OrderedThreadPoolExecutor x0) {
        return x0.idleWorkers;
    }

    static /* synthetic */ Set access$600(OrderedThreadPoolExecutor x0) {
        return x0.workers;
    }

    static /* synthetic */ IoSession access$700() {
        return EXIT_SIGNAL;
    }

    static /* synthetic */ SessionTasksQueue access$800(OrderedThreadPoolExecutor x0, IoSession x1) {
        return x0.getSessionTasksQueue(x1);
    }

    static /* synthetic */ long access$900(OrderedThreadPoolExecutor x0) {
        return x0.completedTaskCount;
    }

    static /* synthetic */ long access$902(OrderedThreadPoolExecutor x0, long x1) {
        x0.completedTaskCount = x1;
        return x0.completedTaskCount;
    }

    private class SessionTasksQueue {
        private final Queue<Runnable> tasksQueue = new ConcurrentLinkedQueue<Runnable>();
        private boolean processingCompleted = true;

        private SessionTasksQueue() {
        }
    }

    private class Worker
    implements Runnable {
        private AtomicLong completedTaskCount = new AtomicLong(0L);
        private Thread thread;

        private Worker() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         */
        @Override
        public void run() {
            block19: {
                this.thread = Thread.currentThread();
                while (true) lbl-1000:
                // 4 sources

                {
                    session = this.fetchSession();
                    OrderedThreadPoolExecutor.access$500(OrderedThreadPoolExecutor.this).decrementAndGet();
                    if (session == null) {
                        var2_2 = OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this);
                        synchronized (var2_2) {
                            if (OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this).size() > OrderedThreadPoolExecutor.this.getCorePoolSize()) {
                                OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this).remove(this);
                                break block19;
                            }
                        }
                    }
                    if (session == OrderedThreadPoolExecutor.access$700()) {
                        break block19;
                    }
                    try {
                        if (session == null) ** GOTO lbl-1000
                        this.runTasks(OrderedThreadPoolExecutor.access$800(OrderedThreadPoolExecutor.this, session));
                    }
                    finally {
                        OrderedThreadPoolExecutor.access$500(OrderedThreadPoolExecutor.this).incrementAndGet();
                        continue;
                    }
                    break;
                }
                ** GOTO lbl-1000
                finally {
                    var1_1 = OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this);
                    synchronized (var1_1) {
                        OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this).remove(this);
                        var2_2 = OrderedThreadPoolExecutor.this;
                        OrderedThreadPoolExecutor.access$902((OrderedThreadPoolExecutor)var2_2, OrderedThreadPoolExecutor.access$900((OrderedThreadPoolExecutor)var2_2) + this.completedTaskCount.get());
                        OrderedThreadPoolExecutor.access$600(OrderedThreadPoolExecutor.this).notifyAll();
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private IoSession fetchSession() {
            IoSession session = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + OrderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
            while (true) {
                try {
                    long waitTime = deadline - currentTime;
                    if (waitTime <= 0L) break;
                    try {
                        session = (IoSession)OrderedThreadPoolExecutor.this.waitingSessions.poll(waitTime, TimeUnit.MILLISECONDS);
                    }
                    finally {
                        if (session == null) {
                            currentTime = System.currentTimeMillis();
                        }
                    }
                }
                catch (InterruptedException e) {
                    continue;
                }
                break;
            }
            return session;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void runTasks(SessionTasksQueue sessionTasksQueue) {
            while (true) {
                Runnable task;
                Queue tasksQueue;
                Queue queue = tasksQueue = sessionTasksQueue.tasksQueue;
                synchronized (queue) {
                    task = (Runnable)tasksQueue.poll();
                    if (task == null) {
                        sessionTasksQueue.processingCompleted = true;
                        break;
                    }
                }
                OrderedThreadPoolExecutor.this.eventQueueHandler.polled(OrderedThreadPoolExecutor.this, (IoEvent)task);
                this.runTask(task);
            }
        }

        private void runTask(Runnable task) {
            OrderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                OrderedThreadPoolExecutor.this.afterExecute(task, null);
                this.completedTaskCount.incrementAndGet();
            }
            catch (RuntimeException e) {
                if (!ran) {
                    OrderedThreadPoolExecutor.this.afterExecute(task, e);
                }
                throw e;
            }
        }
    }
}

