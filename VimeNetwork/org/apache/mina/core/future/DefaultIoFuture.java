package org.apache.mina.core.future;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.polling.AbstractPollingIoProcessor;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.util.ExceptionMonitor;

public class DefaultIoFuture implements IoFuture {
   private static final long DEAD_LOCK_CHECK_INTERVAL = 5000L;
   private final IoSession session;
   private final Object lock;
   private IoFutureListener firstListener;
   private List otherListeners;
   private Object result;
   private boolean ready;
   private int waiters;

   public DefaultIoFuture(IoSession session) {
      this.session = session;
      this.lock = this;
   }

   public IoSession getSession() {
      return this.session;
   }

   /** @deprecated */
   @Deprecated
   public void join() {
      this.awaitUninterruptibly();
   }

   /** @deprecated */
   @Deprecated
   public boolean join(long timeoutMillis) {
      return this.awaitUninterruptibly(timeoutMillis);
   }

   public IoFuture await() throws InterruptedException {
      synchronized(this.lock) {
         while(!this.ready) {
            ++this.waiters;

            try {
               this.lock.wait(5000L);
            } finally {
               --this.waiters;
               if (!this.ready) {
                  this.checkDeadLock();
               }

            }
         }

         return this;
      }
   }

   public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
      return this.await0(unit.toMillis(timeout), true);
   }

   public boolean await(long timeoutMillis) throws InterruptedException {
      return this.await0(timeoutMillis, true);
   }

   public IoFuture awaitUninterruptibly() {
      try {
         this.await0(Long.MAX_VALUE, false);
      } catch (InterruptedException var2) {
      }

      return this;
   }

   public boolean awaitUninterruptibly(long timeout, TimeUnit unit) {
      try {
         return this.await0(unit.toMillis(timeout), false);
      } catch (InterruptedException var5) {
         throw new InternalError();
      }
   }

   public boolean awaitUninterruptibly(long timeoutMillis) {
      try {
         return this.await0(timeoutMillis, false);
      } catch (InterruptedException var4) {
         throw new InternalError();
      }
   }

   private boolean await0(long timeoutMillis, boolean interruptable) throws InterruptedException {
      long endTime = System.currentTimeMillis() + timeoutMillis;
      if (endTime < 0L) {
         endTime = Long.MAX_VALUE;
      }

      synchronized(this.lock) {
         if (!this.ready && timeoutMillis > 0L) {
            ++this.waiters;

            try {
               while(true) {
                  try {
                     long timeOut = Math.min(timeoutMillis, 5000L);
                     this.lock.wait(timeOut);
                  } catch (InterruptedException e) {
                     if (interruptable) {
                        throw e;
                     }
                  }

                  if (this.ready || endTime < System.currentTimeMillis()) {
                     boolean e = this.ready;
                     return e;
                  }

                  this.checkDeadLock();
               }
            } finally {
               --this.waiters;
               if (!this.ready) {
                  this.checkDeadLock();
               }

            }
         } else {
            return this.ready;
         }
      }
   }

   private void checkDeadLock() {
      if (this instanceof CloseFuture || this instanceof WriteFuture || this instanceof ReadFuture || this instanceof ConnectFuture) {
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

         for(StackTraceElement stackElement : stackTrace) {
            if (AbstractPollingIoProcessor.class.getName().equals(stackElement.getClassName())) {
               IllegalStateException e = new IllegalStateException("t");
               e.getStackTrace();
               throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
            }
         }

         for(StackTraceElement s : stackTrace) {
            try {
               Class<?> cls = DefaultIoFuture.class.getClassLoader().loadClass(s.getClassName());
               if (IoProcessor.class.isAssignableFrom(cls)) {
                  throw new IllegalStateException("DEAD LOCK: " + IoFuture.class.getSimpleName() + ".await() was invoked from an I/O processor thread.  " + "Please use " + IoFutureListener.class.getSimpleName() + " or configure a proper thread model alternatively.");
               }
            } catch (ClassNotFoundException var7) {
            }
         }

      }
   }

   public boolean isDone() {
      synchronized(this.lock) {
         return this.ready;
      }
   }

   public boolean setValue(Object newValue) {
      synchronized(this.lock) {
         if (this.ready) {
            return false;
         }

         this.result = newValue;
         this.ready = true;
         if (this.waiters > 0) {
            this.lock.notifyAll();
         }
      }

      this.notifyListeners();
      return true;
   }

   protected Object getValue() {
      synchronized(this.lock) {
         return this.result;
      }
   }

   public IoFuture addListener(IoFutureListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener");
      } else {
         synchronized(this.lock) {
            if (this.ready) {
               this.notifyListener(listener);
            } else if (this.firstListener == null) {
               this.firstListener = listener;
            } else {
               if (this.otherListeners == null) {
                  this.otherListeners = new ArrayList(1);
               }

               this.otherListeners.add(listener);
            }

            return this;
         }
      }
   }

   public IoFuture removeListener(IoFutureListener listener) {
      if (listener == null) {
         throw new IllegalArgumentException("listener");
      } else {
         synchronized(this.lock) {
            if (!this.ready) {
               if (listener == this.firstListener) {
                  if (this.otherListeners != null && !this.otherListeners.isEmpty()) {
                     this.firstListener = (IoFutureListener)this.otherListeners.remove(0);
                  } else {
                     this.firstListener = null;
                  }
               } else if (this.otherListeners != null) {
                  this.otherListeners.remove(listener);
               }
            }

            return this;
         }
      }
   }

   private void notifyListeners() {
      if (this.firstListener != null) {
         this.notifyListener(this.firstListener);
         this.firstListener = null;
         if (this.otherListeners != null) {
            for(IoFutureListener listener : this.otherListeners) {
               this.notifyListener(listener);
            }

            this.otherListeners = null;
         }
      }

   }

   private void notifyListener(IoFutureListener listener) {
      try {
         listener.operationComplete(this);
      } catch (Exception e) {
         ExceptionMonitor.getInstance().exceptionCaught(e);
      }

   }
}
