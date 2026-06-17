/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.filter.executor.IoEventQueueHandler;

public class UnorderedThreadPoolExecutor
extends ThreadPoolExecutor {
    private static final Runnable EXIT_SIGNAL = new Runnable(){

        @Override
        public void run() {
            throw new Error("This method shouldn't be called. Please file a bug report.");
        }
    };
    private final Set<Worker> workers = new HashSet<Worker>();
    private volatile int corePoolSize;
    private volatile int maximumPoolSize;
    private volatile int largestPoolSize;
    private final AtomicInteger idleWorkers = new AtomicInteger();
    private long completedTaskCount;
    private volatile boolean shutdown;
    private final IoEventQueueHandler queueHandler;

    public UnorderedThreadPoolExecutor() {
        this(16);
    }

    public UnorderedThreadPoolExecutor(int maximumPoolSize) {
        this(0, maximumPoolSize);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, 30L, TimeUnit.SECONDS);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory());
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, IoEventQueueHandler queueHandler) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, Executors.defaultThreadFactory(), queueHandler);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, threadFactory, null);
    }

    public UnorderedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, ThreadFactory threadFactory, IoEventQueueHandler queueHandler) {
        super(0, 1, keepAliveTime, unit, new LinkedBlockingQueue<Runnable>(), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (maximumPoolSize == 0 || maximumPoolSize < corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        if (queueHandler == null) {
            queueHandler = IoEventQueueHandler.NOOP;
        }
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.queueHandler = queueHandler;
    }

    public IoEventQueueHandler getQueueHandler() {
        return this.queueHandler;
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
            if (this.workers.size() >= this.maximumPoolSize) {
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
            if (this.workers.size() <= this.corePoolSize) {
                return;
            }
            this.getQueue().offer(EXIT_SIGNAL);
        }
    }

    @Override
    public int getMaximumPoolSize() {
        return this.maximumPoolSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setMaximumPoolSize(int maximumPoolSize) {
        if (maximumPoolSize <= 0 || maximumPoolSize < this.corePoolSize) {
            throw new IllegalArgumentException("maximumPoolSize: " + maximumPoolSize);
        }
        Set<Worker> set = this.workers;
        synchronized (set) {
            this.maximumPoolSize = maximumPoolSize;
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
                this.getQueue().offer(EXIT_SIGNAL);
            }
        }
    }

    @Override
    public List<Runnable> shutdownNow() {
        Runnable task;
        this.shutdown();
        ArrayList<Runnable> answer = new ArrayList<Runnable>();
        while ((task = (Runnable)this.getQueue().poll()) != null) {
            if (task == EXIT_SIGNAL) {
                this.getQueue().offer(EXIT_SIGNAL);
                Thread.yield();
                continue;
            }
            this.getQueueHandler().polled(this, (IoEvent)task);
            answer.add(task);
        }
        return answer;
    }

    @Override
    public void execute(Runnable task) {
        if (this.shutdown) {
            this.rejectTask(task);
        }
        this.checkTaskType(task);
        IoEvent e = (IoEvent)task;
        boolean offeredEvent = this.queueHandler.accept(this, e);
        if (offeredEvent) {
            this.getQueue().offer(e);
        }
        this.addWorkerIfNecessary();
        if (offeredEvent) {
            this.queueHandler.offered(this, e);
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
            for (int i = this.corePoolSize - this.workers.size(); i > 0; --i) {
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
            if (this.workers.size() < this.corePoolSize) {
                this.addWorker();
                return true;
            }
            return false;
        }
    }

    @Override
    public void purge() {
    }

    @Override
    public boolean remove(Runnable task) {
        boolean removed = super.remove(task);
        if (removed) {
            this.getQueueHandler().polled(this, (IoEvent)task);
        }
        return removed;
    }

    @Override
    public int getCorePoolSize() {
        return this.corePoolSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize < 0) {
            throw new IllegalArgumentException("corePoolSize: " + corePoolSize);
        }
        if (corePoolSize > this.maximumPoolSize) {
            throw new IllegalArgumentException("corePoolSize exceeds maximumPoolSize");
        }
        Set<Worker> set = this.workers;
        synchronized (set) {
            if (this.corePoolSize > corePoolSize) {
                for (int i = this.corePoolSize - corePoolSize; i > 0; --i) {
                    this.removeWorker();
                }
            }
            this.corePoolSize = corePoolSize;
        }
    }

    static /* synthetic */ AtomicInteger access$200(UnorderedThreadPoolExecutor x0) {
        return x0.idleWorkers;
    }

    static /* synthetic */ Set access$300(UnorderedThreadPoolExecutor x0) {
        return x0.workers;
    }

    static /* synthetic */ int access$400(UnorderedThreadPoolExecutor x0) {
        return x0.corePoolSize;
    }

    static /* synthetic */ Runnable access$500() {
        return EXIT_SIGNAL;
    }

    static /* synthetic */ IoEventQueueHandler access$600(UnorderedThreadPoolExecutor x0) {
        return x0.queueHandler;
    }

    static /* synthetic */ long access$700(UnorderedThreadPoolExecutor x0) {
        return x0.completedTaskCount;
    }

    static /* synthetic */ long access$702(UnorderedThreadPoolExecutor x0, long x1) {
        x0.completedTaskCount = x1;
        return x0.completedTaskCount;
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
                    task = this.fetchTask();
                    UnorderedThreadPoolExecutor.access$200(UnorderedThreadPoolExecutor.this).decrementAndGet();
                    if (task == null) {
                        var2_2 = UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this);
                        synchronized (var2_2) {
                            if (UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this).size() > UnorderedThreadPoolExecutor.access$400(UnorderedThreadPoolExecutor.this)) {
                                UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this).remove(this);
                                break block19;
                            }
                        }
                    }
                    if (task == UnorderedThreadPoolExecutor.access$500()) {
                        break block19;
                    }
                    try {
                        if (task == null) ** GOTO lbl-1000
                        UnorderedThreadPoolExecutor.access$600(UnorderedThreadPoolExecutor.this).polled(UnorderedThreadPoolExecutor.this, (IoEvent)task);
                        this.runTask(task);
                    }
                    finally {
                        UnorderedThreadPoolExecutor.access$200(UnorderedThreadPoolExecutor.this).incrementAndGet();
                        continue;
                    }
                    break;
                }
                ** GOTO lbl-1000
                finally {
                    var1_1 = UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this);
                    synchronized (var1_1) {
                        UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this).remove(this);
                        var2_2 = UnorderedThreadPoolExecutor.this;
                        UnorderedThreadPoolExecutor.access$702((UnorderedThreadPoolExecutor)var2_2, UnorderedThreadPoolExecutor.access$700((UnorderedThreadPoolExecutor)var2_2) + this.completedTaskCount.get());
                        UnorderedThreadPoolExecutor.access$300(UnorderedThreadPoolExecutor.this).notifyAll();
                    }
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private Runnable fetchTask() {
            Runnable task = null;
            long currentTime = System.currentTimeMillis();
            long deadline = currentTime + UnorderedThreadPoolExecutor.this.getKeepAliveTime(TimeUnit.MILLISECONDS);
            while (true) {
                try {
                    long waitTime = deadline - currentTime;
                    if (waitTime <= 0L) break;
                    try {
                        task = UnorderedThreadPoolExecutor.this.getQueue().poll(waitTime, TimeUnit.MILLISECONDS);
                    }
                    finally {
                        if (task == null) {
                            currentTime = System.currentTimeMillis();
                        }
                    }
                }
                catch (InterruptedException e) {
                    continue;
                }
                break;
            }
            return task;
        }

        private void runTask(Runnable task) {
            UnorderedThreadPoolExecutor.this.beforeExecute(this.thread, task);
            boolean ran = false;
            try {
                task.run();
                ran = true;
                UnorderedThreadPoolExecutor.this.afterExecute(task, null);
                this.completedTaskCount.incrementAndGet();
            }
            catch (RuntimeException e) {
                if (!ran) {
                    UnorderedThreadPoolExecutor.this.afterExecute(task, e);
                }
                throw e;
            }
        }
    }
}

