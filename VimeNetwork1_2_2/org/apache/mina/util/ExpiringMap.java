/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.mina.util.ExpirationListener;

public class ExpiringMap<K, V>
implements Map<K, V> {
    public static final int DEFAULT_TIME_TO_LIVE = 60;
    public static final int DEFAULT_EXPIRATION_INTERVAL = 1;
    private static volatile int expirerCount = 1;
    private final ConcurrentHashMap<K, ExpiringObject> delegate;
    private final CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners;
    private final Expirer expirer;

    public ExpiringMap() {
        this(60, 1);
    }

    public ExpiringMap(int timeToLive) {
        this(timeToLive, 1);
    }

    public ExpiringMap(int timeToLive, int expirationInterval) {
        this(new ConcurrentHashMap(), new CopyOnWriteArrayList<ExpirationListener<V>>(), timeToLive, expirationInterval);
    }

    private ExpiringMap(ConcurrentHashMap<K, ExpiringObject> delegate, CopyOnWriteArrayList<ExpirationListener<V>> expirationListeners, int timeToLive, int expirationInterval) {
        this.delegate = delegate;
        this.expirationListeners = expirationListeners;
        this.expirer = new Expirer();
        this.expirer.setTimeToLive(timeToLive);
        this.expirer.setExpirationInterval(expirationInterval);
    }

    @Override
    public V put(K key, V value) {
        ExpiringObject answer = this.delegate.put(key, new ExpiringObject(key, value, System.currentTimeMillis()));
        if (answer == null) {
            return null;
        }
        return answer.getValue();
    }

    @Override
    public V get(Object key) {
        ExpiringObject object = this.delegate.get(key);
        if (object != null) {
            object.setLastAccessTime(System.currentTimeMillis());
            return object.getValue();
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        ExpiringObject answer = this.delegate.remove(key);
        if (answer == null) {
            return null;
        }
        return answer.getValue();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.delegate.containsValue(value);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }

    @Override
    public Set<K> keySet() {
        return this.delegate.keySet();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> inMap) {
        for (Map.Entry<K, V> e : inMap.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    public void addExpirationListener(ExpirationListener<V> listener) {
        this.expirationListeners.add(listener);
    }

    public void removeExpirationListener(ExpirationListener<V> listener) {
        this.expirationListeners.remove(listener);
    }

    public Expirer getExpirer() {
        return this.expirer;
    }

    public int getExpirationInterval() {
        return this.expirer.getExpirationInterval();
    }

    public int getTimeToLive() {
        return this.expirer.getTimeToLive();
    }

    public void setExpirationInterval(int expirationInterval) {
        this.expirer.setExpirationInterval(expirationInterval);
    }

    public void setTimeToLive(int timeToLive) {
        this.expirer.setTimeToLive(timeToLive);
    }

    static /* synthetic */ int access$008() {
        return expirerCount++;
    }

    public class Expirer
    implements Runnable {
        private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
        private long timeToLiveMillis;
        private long expirationIntervalMillis;
        private boolean running = false;
        private final Thread expirerThread = new Thread((Runnable)this, "ExpiringMapExpirer-" + ExpiringMap.access$008());

        public Expirer() {
            this.expirerThread.setDaemon(true);
        }

        @Override
        public void run() {
            while (this.running) {
                this.processExpires();
                try {
                    Thread.sleep(this.expirationIntervalMillis);
                }
                catch (InterruptedException interruptedException) {}
            }
        }

        private void processExpires() {
            long timeNow = System.currentTimeMillis();
            for (ExpiringObject o : ExpiringMap.this.delegate.values()) {
                long timeIdle;
                if (this.timeToLiveMillis <= 0L || (timeIdle = timeNow - o.getLastAccessTime()) < this.timeToLiveMillis) continue;
                ExpiringMap.this.delegate.remove(o.getKey());
                for (ExpirationListener listener : ExpiringMap.this.expirationListeners) {
                    listener.expired(o.getValue());
                }
            }
        }

        public void startExpiring() {
            this.stateLock.writeLock().lock();
            try {
                if (!this.running) {
                    this.running = true;
                    this.expirerThread.start();
                }
            }
            finally {
                this.stateLock.writeLock().unlock();
            }
        }

        public void startExpiringIfNotStarted() {
            this.stateLock.readLock().lock();
            try {
                if (this.running) {
                    return;
                }
            }
            finally {
                this.stateLock.readLock().unlock();
            }
            this.stateLock.writeLock().lock();
            try {
                if (!this.running) {
                    this.running = true;
                    this.expirerThread.start();
                }
            }
            finally {
                this.stateLock.writeLock().unlock();
            }
        }

        public void stopExpiring() {
            this.stateLock.writeLock().lock();
            try {
                if (this.running) {
                    this.running = false;
                    this.expirerThread.interrupt();
                }
            }
            finally {
                this.stateLock.writeLock().unlock();
            }
        }

        public boolean isRunning() {
            this.stateLock.readLock().lock();
            try {
                boolean bl = this.running;
                return bl;
            }
            finally {
                this.stateLock.readLock().unlock();
            }
        }

        public int getTimeToLive() {
            this.stateLock.readLock().lock();
            try {
                int n = (int)this.timeToLiveMillis / 1000;
                return n;
            }
            finally {
                this.stateLock.readLock().unlock();
            }
        }

        public void setTimeToLive(long timeToLive) {
            this.stateLock.writeLock().lock();
            try {
                this.timeToLiveMillis = timeToLive * 1000L;
            }
            finally {
                this.stateLock.writeLock().unlock();
            }
        }

        public int getExpirationInterval() {
            this.stateLock.readLock().lock();
            try {
                int n = (int)this.expirationIntervalMillis / 1000;
                return n;
            }
            finally {
                this.stateLock.readLock().unlock();
            }
        }

        public void setExpirationInterval(long expirationInterval) {
            this.stateLock.writeLock().lock();
            try {
                this.expirationIntervalMillis = expirationInterval * 1000L;
            }
            finally {
                this.stateLock.writeLock().unlock();
            }
        }
    }

    private class ExpiringObject {
        private K key;
        private V value;
        private long lastAccessTime;
        private final ReadWriteLock lastAccessTimeLock = new ReentrantReadWriteLock();

        ExpiringObject(K key, V value, long lastAccessTime) {
            if (value == null) {
                throw new IllegalArgumentException("An expiring object cannot be null.");
            }
            this.key = key;
            this.value = value;
            this.lastAccessTime = lastAccessTime;
        }

        public long getLastAccessTime() {
            this.lastAccessTimeLock.readLock().lock();
            try {
                long l = this.lastAccessTime;
                return l;
            }
            finally {
                this.lastAccessTimeLock.readLock().unlock();
            }
        }

        public void setLastAccessTime(long lastAccessTime) {
            this.lastAccessTimeLock.writeLock().lock();
            try {
                this.lastAccessTime = lastAccessTime;
            }
            finally {
                this.lastAccessTimeLock.writeLock().unlock();
            }
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public boolean equals(Object obj) {
            return this.value.equals(obj);
        }

        public int hashCode() {
            return this.value.hashCode();
        }
    }
}

