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

public class ConnectionThrottleFilter extends IoFilterAdapter {
   private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionThrottleFilter.class);
   private static final long DEFAULT_TIME = 1000L;
   private long allowedInterval;
   private final Map clients;
   private Lock lock;

   public ConnectionThrottleFilter() {
      this(1000L);
   }

   public ConnectionThrottleFilter(long allowedInterval) {
      this.lock = new ReentrantLock();
      this.allowedInterval = allowedInterval;
      this.clients = new ConcurrentHashMap();
      ExpiredSessionThread cleanupThread = new ExpiredSessionThread();
      cleanupThread.setDaemon(true);
      cleanupThread.start();
   }

   public void setAllowedInterval(long allowedInterval) {
      this.lock.lock();

      try {
         this.allowedInterval = allowedInterval;
      } finally {
         this.lock.unlock();
      }

   }

   protected boolean isConnectionOk(IoSession session) {
      SocketAddress remoteAddress = session.getRemoteAddress();
      if (remoteAddress instanceof InetSocketAddress) {
         InetSocketAddress addr = (InetSocketAddress)remoteAddress;
         long now = System.currentTimeMillis();
         this.lock.lock();

         boolean var7;
         try {
            if (!this.clients.containsKey(addr.getAddress().getHostAddress())) {
               this.clients.put(addr.getAddress().getHostAddress(), now);
               return true;
            }

            LOGGER.debug("This is not a new client");
            Long lastConnTime = (Long)this.clients.get(addr.getAddress().getHostAddress());
            this.clients.put(addr.getAddress().getHostAddress(), now);
            if (now - lastConnTime >= this.allowedInterval) {
               var7 = true;
               return var7;
            }

            LOGGER.warn("Session connection interval too short");
            var7 = false;
         } finally {
            this.lock.unlock();
         }

         return var7;
      } else {
         return false;
      }
   }

   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      if (!this.isConnectionOk(session)) {
         LOGGER.warn("Connections coming in too fast; closing.");
         session.closeNow();
      }

      nextFilter.sessionCreated(session);
   }

   private class ExpiredSessionThread extends Thread {
      private ExpiredSessionThread() {
      }

      public void run() {
         try {
            Thread.sleep(ConnectionThrottleFilter.this.allowedInterval);
         } catch (InterruptedException var10) {
            return;
         }

         long currentTime = System.currentTimeMillis();
         ConnectionThrottleFilter.this.lock.lock();

         try {
            for(String session : ConnectionThrottleFilter.this.clients.keySet()) {
               long creationTime = (Long)ConnectionThrottleFilter.this.clients.get(session);
               if (creationTime + ConnectionThrottleFilter.this.allowedInterval < currentTime) {
                  ConnectionThrottleFilter.this.clients.remove(session);
               }
            }
         } finally {
            ConnectionThrottleFilter.this.lock.unlock();
         }

      }
   }
}
