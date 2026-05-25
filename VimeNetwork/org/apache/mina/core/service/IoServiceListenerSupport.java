package org.apache.mina.core.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ExceptionMonitor;

public class IoServiceListenerSupport {
   private final IoService service;
   private final List listeners = new CopyOnWriteArrayList();
   private final ConcurrentMap managedSessions = new ConcurrentHashMap();
   private final Map readOnlyManagedSessions;
   private final AtomicBoolean activated;
   private volatile long activationTime;
   private volatile int largestManagedSessionCount;
   private AtomicLong cumulativeManagedSessionCount;

   public IoServiceListenerSupport(IoService service) {
      this.readOnlyManagedSessions = Collections.unmodifiableMap(this.managedSessions);
      this.activated = new AtomicBoolean();
      this.largestManagedSessionCount = 0;
      this.cumulativeManagedSessionCount = new AtomicLong(0L);
      if (service == null) {
         throw new IllegalArgumentException("service");
      } else {
         this.service = service;
      }
   }

   public void add(IoServiceListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }

   }

   public void remove(IoServiceListener listener) {
      if (listener != null) {
         this.listeners.remove(listener);
      }

   }

   public long getActivationTime() {
      return this.activationTime;
   }

   public Map getManagedSessions() {
      return this.readOnlyManagedSessions;
   }

   public int getManagedSessionCount() {
      return this.managedSessions.size();
   }

   public int getLargestManagedSessionCount() {
      return this.largestManagedSessionCount;
   }

   public long getCumulativeManagedSessionCount() {
      return this.cumulativeManagedSessionCount.get();
   }

   public boolean isActive() {
      return this.activated.get();
   }

   public void fireServiceActivated() {
      if (this.activated.compareAndSet(false, true)) {
         this.activationTime = System.currentTimeMillis();

         for(IoServiceListener listener : this.listeners) {
            try {
               listener.serviceActivated(this.service);
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);
            }
         }

      }
   }

   public void fireServiceDeactivated() {
      if (this.activated.compareAndSet(true, false)) {
         try {
            for(IoServiceListener listener : this.listeners) {
               try {
                  listener.serviceDeactivated(this.service);
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               }
            }
         } finally {
            this.disconnectSessions();
         }

      }
   }

   public void fireSessionCreated(IoSession session) {
      boolean firstSession = false;
      if (session.getService() instanceof IoConnector) {
         synchronized(this.managedSessions) {
            firstSession = this.managedSessions.isEmpty();
         }
      }

      if (this.managedSessions.putIfAbsent(session.getId(), session) == null) {
         if (firstSession) {
            this.fireServiceActivated();
         }

         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireSessionCreated();
         filterChain.fireSessionOpened();
         int managedSessionCount = this.managedSessions.size();
         if (managedSessionCount > this.largestManagedSessionCount) {
            this.largestManagedSessionCount = managedSessionCount;
         }

         this.cumulativeManagedSessionCount.incrementAndGet();

         for(IoServiceListener l : this.listeners) {
            try {
               l.sessionCreated(session);
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);
            }
         }

      }
   }

   public void fireSessionDestroyed(IoSession session) {
      if (this.managedSessions.remove(session.getId()) != null) {
         session.getFilterChain().fireSessionClosed();
         boolean var14 = false;

         try {
            var14 = true;

            for(IoServiceListener l : this.listeners) {
               try {
                  l.sessionDestroyed(session);
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               }
            }

            var14 = false;
         } finally {
            if (var14) {
               if (session.getService() instanceof IoConnector) {
                  boolean lastSession = false;
                  synchronized(this.managedSessions) {
                     lastSession = this.managedSessions.isEmpty();
                  }

                  if (lastSession) {
                     this.fireServiceDeactivated();
                  }
               }

            }
         }

         if (session.getService() instanceof IoConnector) {
            boolean lastSession = false;
            synchronized(this.managedSessions) {
               lastSession = this.managedSessions.isEmpty();
            }

            if (lastSession) {
               this.fireServiceDeactivated();
            }
         }

      }
   }

   private void disconnectSessions() {
      if (this.service instanceof IoAcceptor) {
         if (((IoAcceptor)this.service).isCloseOnDeactivation()) {
            Object lock = new Object();
            IoFutureListener<IoFuture> listener = new LockNotifyingListener(lock);

            for(IoSession s : this.managedSessions.values()) {
               s.closeNow().addListener(listener);
            }

            try {
               synchronized(lock) {
                  while(!this.managedSessions.isEmpty()) {
                     lock.wait(500L);
                  }
               }
            } catch (InterruptedException var7) {
            }

         }
      }
   }

   private static class LockNotifyingListener implements IoFutureListener {
      private final Object lock;

      public LockNotifyingListener(Object lock) {
         this.lock = lock;
      }

      public void operationComplete(IoFuture future) {
         synchronized(this.lock) {
            this.lock.notifyAll();
         }
      }
   }
}
