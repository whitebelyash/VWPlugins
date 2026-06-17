/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.firewall;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionThrottleFilter
extends IoFilterAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionThrottleFilter.class);
    private static final long DEFAULT_TIME = 1000L;
    private long allowedInterval;
    private final Map<String, Long> clients;
    private Lock lock = new ReentrantLock();

    public ConnectionThrottleFilter() {
        this(1000L);
    }

    public ConnectionThrottleFilter(long allowedInterval) {
        this.allowedInterval = allowedInterval;
        this.clients = new ConcurrentHashMap<String, Long>();
        ExpiredSessionThread cleanupThread = new ExpiredSessionThread();
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    public void setAllowedInterval(long allowedInterval) {
        this.lock.lock();
        try {
            this.allowedInterval = allowedInterval;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isConnectionOk(IoSession session) {
        SocketAddress remoteAddress = session.getRemoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            InetSocketAddress addr = (InetSocketAddress)remoteAddress;
            long now = System.currentTimeMillis();
            this.lock.lock();
            try {
                if (this.clients.containsKey(addr.getAddress().getHostAddress())) {
                    LOGGER.debug("This is not a new client");
                    Long lastConnTime = this.clients.get(addr.getAddress().getHostAddress());
                    this.clients.put(addr.getAddress().getHostAddress(), now);
                    if (now - lastConnTime < this.allowedInterval) {
                        LOGGER.warn("Session connection interval too short");
                        boolean bl = false;
                        return bl;
                    }
                    boolean bl = true;
                    return bl;
                }
                this.clients.put(addr.getAddress().getHostAddress(), now);
            }
            finally {
                this.lock.unlock();
            }
            return true;
        }
        return false;
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        if (!this.isConnectionOk(session)) {
            LOGGER.warn("Connections coming in too fast; closing.");
            session.closeNow();
        }
        nextFilter.sessionCreated(session);
    }

    private class ExpiredSessionThread
    extends Thread {
        private ExpiredSessionThread() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void run() {
            try {
                Thread.sleep(ConnectionThrottleFilter.this.allowedInterval);
            }
            catch (InterruptedException e) {
                return;
            }
            long currentTime = System.currentTimeMillis();
            ConnectionThrottleFilter.this.lock.lock();
            try {
                for (String session : ConnectionThrottleFilter.this.clients.keySet()) {
                    long creationTime = (Long)ConnectionThrottleFilter.this.clients.get(session);
                    if (creationTime + ConnectionThrottleFilter.this.allowedInterval >= currentTime) continue;
                    ConnectionThrottleFilter.this.clients.remove(session);
                }
            }
            finally {
                ConnectionThrottleFilter.this.lock.unlock();
            }
        }
    }
}

