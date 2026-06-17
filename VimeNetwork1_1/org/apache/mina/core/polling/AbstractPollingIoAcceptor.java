package org.apache.mina.core.polling;

import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.spi.SelectorProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

public abstract class AbstractPollingIoAcceptor extends AbstractIoAcceptor {
   private final Semaphore lock;
   private final IoProcessor processor;
   private final boolean createdProcessor;
   private final Queue registerQueue;
   private final Queue cancelQueue;
   private final Map boundHandles;
   private final AbstractIoService.ServiceOperationFuture disposalFuture;
   private volatile boolean selectable;
   private AtomicReference acceptorRef;
   protected boolean reuseAddress;
   protected int backlog;

   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class processorClass) {
      this(sessionConfig, (Executor)null, new SimpleIoProcessorPool(processorClass), true, (SelectorProvider)null);
   }

   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class processorClass, int processorCount) {
      this(sessionConfig, (Executor)null, new SimpleIoProcessorPool(processorClass, processorCount), true, (SelectorProvider)null);
   }

   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Class processorClass, int processorCount, SelectorProvider selectorProvider) {
      this(sessionConfig, (Executor)null, new SimpleIoProcessorPool(processorClass, processorCount, selectorProvider), true, selectorProvider);
   }

   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, IoProcessor processor) {
      this(sessionConfig, (Executor)null, processor, false, (SelectorProvider)null);
   }

   protected AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor processor) {
      this(sessionConfig, executor, processor, false, (SelectorProvider)null);
   }

   private AbstractPollingIoAcceptor(IoSessionConfig sessionConfig, Executor executor, IoProcessor processor, boolean createdProcessor, SelectorProvider selectorProvider) {
      super(sessionConfig, executor);
      this.lock = new Semaphore(1);
      this.registerQueue = new ConcurrentLinkedQueue();
      this.cancelQueue = new ConcurrentLinkedQueue();
      this.boundHandles = Collections.synchronizedMap(new HashMap());
      this.disposalFuture = new AbstractIoService.ServiceOperationFuture();
      this.acceptorRef = new AtomicReference();
      this.reuseAddress = false;
      this.backlog = 50;
      if (processor == null) {
         throw new IllegalArgumentException("processor");
      } else {
         this.processor = processor;
         this.createdProcessor = createdProcessor;

         try {
            this.init(selectorProvider);
            this.selectable = true;
         } catch (RuntimeException e) {
            throw e;
         } catch (Exception e) {
            throw new RuntimeIoException("Failed to initialize.", e);
         } finally {
            if (!this.selectable) {
               try {
                  this.destroy();
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               }
            }

         }

      }
   }

   protected abstract void init() throws Exception;

   protected abstract void init(SelectorProvider var1) throws Exception;

   protected abstract void destroy() throws Exception;

   protected abstract int select() throws Exception;

   protected abstract void wakeup();

   protected abstract Iterator selectedHandles();

   protected abstract Object open(SocketAddress var1) throws Exception;

   protected abstract SocketAddress localAddress(Object var1) throws Exception;

   protected abstract AbstractIoSession accept(IoProcessor var1, Object var2) throws Exception;

   protected abstract void close(Object var1) throws Exception;

   protected void dispose0() throws Exception {
      this.unbind();
      this.startupAcceptor();
      this.wakeup();
   }

   protected final Set bindInternal(List localAddresses) throws Exception {
      AbstractIoAcceptor.AcceptorOperationFuture request = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
      this.registerQueue.add(request);
      this.startupAcceptor();

      try {
         this.lock.acquire();
         this.wakeup();
      } finally {
         this.lock.release();
      }

      request.awaitUninterruptibly();
      if (request.getException() != null) {
         throw request.getException();
      } else {
         Set<SocketAddress> newLocalAddresses = new HashSet();

         for(Object handle : this.boundHandles.values()) {
            newLocalAddresses.add(this.localAddress(handle));
         }

         return newLocalAddresses;
      }
   }

   private void startupAcceptor() throws InterruptedException {
      if (!this.selectable) {
         this.registerQueue.clear();
         this.cancelQueue.clear();
      }

      AbstractPollingIoAcceptor<S, H>.Acceptor acceptor = (Acceptor)this.acceptorRef.get();
      if (acceptor == null) {
         this.lock.acquire();
         acceptor = new Acceptor();
         if (this.acceptorRef.compareAndSet((Object)null, acceptor)) {
            this.executeWorker(acceptor);
         } else {
            this.lock.release();
         }
      }

   }

   protected final void unbind0(List localAddresses) throws Exception {
      AbstractIoAcceptor.AcceptorOperationFuture future = new AbstractIoAcceptor.AcceptorOperationFuture(localAddresses);
      this.cancelQueue.add(future);
      this.startupAcceptor();
      this.wakeup();
      future.awaitUninterruptibly();
      if (future.getException() != null) {
         throw future.getException();
      }
   }

   private int registerHandles() {
      while(true) {
         AbstractIoAcceptor.AcceptorOperationFuture future = (AbstractIoAcceptor.AcceptorOperationFuture)this.registerQueue.poll();
         if (future == null) {
            return 0;
         }

         Map<SocketAddress, H> newHandles = new ConcurrentHashMap();
         List<SocketAddress> localAddresses = future.getLocalAddresses();

         int var20;
         try {
            for(SocketAddress a : localAddresses) {
               H handle = (H)this.open(a);
               newHandles.put(this.localAddress(handle), handle);
            }

            this.boundHandles.putAll(newHandles);
            future.setDone();
            var20 = newHandles.size();
         } catch (Exception e) {
            future.setException(e);
            continue;
         } finally {
            if (future.getException() != null) {
               for(Object handle : newHandles.values()) {
                  try {
                     this.close(handle);
                  } catch (Exception e) {
                     ExceptionMonitor.getInstance().exceptionCaught(e);
                  }
               }

               this.wakeup();
            }

         }

         return var20;
      }
   }

   private int unregisterHandles() {
      int cancelledHandles = 0;

      while(true) {
         AbstractIoAcceptor.AcceptorOperationFuture future = (AbstractIoAcceptor.AcceptorOperationFuture)this.cancelQueue.poll();
         if (future == null) {
            return cancelledHandles;
         }

         for(SocketAddress a : future.getLocalAddresses()) {
            H handle = (H)this.boundHandles.remove(a);
            if (handle != null) {
               try {
                  this.close(handle);
                  this.wakeup();
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               } finally {
                  ++cancelledHandles;
               }
            }
         }

         future.setDone();
      }
   }

   public final IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
      throw new UnsupportedOperationException();
   }

   public int getBacklog() {
      return this.backlog;
   }

   public void setBacklog(int backlog) {
      synchronized(this.bindLock) {
         if (this.isActive()) {
            throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
         } else {
            this.backlog = backlog;
         }
      }
   }

   public boolean isReuseAddress() {
      return this.reuseAddress;
   }

   public void setReuseAddress(boolean reuseAddress) {
      synchronized(this.bindLock) {
         if (this.isActive()) {
            throw new IllegalStateException("backlog can't be set while the acceptor is bound.");
         } else {
            this.reuseAddress = reuseAddress;
         }
      }
   }

   public SocketSessionConfig getSessionConfig() {
      return (SocketSessionConfig)this.sessionConfig;
   }

   private class Acceptor implements Runnable {
      private Acceptor() {
      }

      public void run() {
         assert AbstractPollingIoAcceptor.this.acceptorRef.get() == this;

         int nHandles = 0;
         AbstractPollingIoAcceptor.this.lock.release();

         while(AbstractPollingIoAcceptor.this.selectable) {
            try {
               nHandles += AbstractPollingIoAcceptor.this.registerHandles();
               int selected = AbstractPollingIoAcceptor.this.select();
               if (nHandles == 0) {
                  AbstractPollingIoAcceptor.this.acceptorRef.set((Object)null);
                  if (AbstractPollingIoAcceptor.this.registerQueue.isEmpty() && AbstractPollingIoAcceptor.this.cancelQueue.isEmpty()) {
                     assert AbstractPollingIoAcceptor.this.acceptorRef.get() != this;
                     break;
                  }

                  if (!AbstractPollingIoAcceptor.this.acceptorRef.compareAndSet((Object)null, this)) {
                     assert AbstractPollingIoAcceptor.this.acceptorRef.get() != this;
                     break;
                  }

                  assert AbstractPollingIoAcceptor.this.acceptorRef.get() == this;
               }

               if (selected > 0) {
                  this.processHandles(AbstractPollingIoAcceptor.this.selectedHandles());
               }

               nHandles -= AbstractPollingIoAcceptor.this.unregisterHandles();
            } catch (ClosedSelectorException cse) {
               ExceptionMonitor.getInstance().exceptionCaught(cse);
               break;
            } catch (Exception e) {
               ExceptionMonitor.getInstance().exceptionCaught(e);

               try {
                  Thread.sleep(1000L);
               } catch (InterruptedException e1) {
                  ExceptionMonitor.getInstance().exceptionCaught(e1);
               }
            }
         }

         if (AbstractPollingIoAcceptor.this.selectable && AbstractPollingIoAcceptor.this.isDisposing()) {
            AbstractPollingIoAcceptor.this.selectable = false;

            try {
               if (AbstractPollingIoAcceptor.this.createdProcessor) {
                  AbstractPollingIoAcceptor.this.processor.dispose();
               }
            } finally {
               try {
                  synchronized(AbstractPollingIoAcceptor.this.disposalLock) {
                     if (AbstractPollingIoAcceptor.this.isDisposing()) {
                        AbstractPollingIoAcceptor.this.destroy();
                     }
                  }
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               } finally {
                  AbstractPollingIoAcceptor.this.disposalFuture.setDone();
               }

            }
         }

      }

      private void processHandles(Iterator handles) throws Exception {
         while(handles.hasNext()) {
            H handle = (H)handles.next();
            handles.remove();
            S session = (S)AbstractPollingIoAcceptor.this.accept(AbstractPollingIoAcceptor.this.processor, handle);
            if (session != null) {
               AbstractPollingIoAcceptor.this.initSession(session, (IoFuture)null, (IoSessionInitializer)null);
               session.getProcessor().add(session);
            }
         }

      }
   }
}
