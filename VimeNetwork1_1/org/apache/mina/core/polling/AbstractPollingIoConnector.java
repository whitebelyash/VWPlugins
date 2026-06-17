package org.apache.mina.core.polling;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.ClosedSelectorException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultConnectFuture;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.util.ExceptionMonitor;

public abstract class AbstractPollingIoConnector extends AbstractIoConnector {
   private final Queue connectQueue;
   private final Queue cancelQueue;
   private final IoProcessor processor;
   private final boolean createdProcessor;
   private final AbstractIoService.ServiceOperationFuture disposalFuture;
   private volatile boolean selectable;
   private final AtomicReference connectorRef;

   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class processorClass) {
      this(sessionConfig, (Executor)null, new SimpleIoProcessorPool(processorClass), true);
   }

   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Class processorClass, int processorCount) {
      this(sessionConfig, (Executor)null, new SimpleIoProcessorPool(processorClass, processorCount), true);
   }

   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, IoProcessor processor) {
      this(sessionConfig, (Executor)null, processor, false);
   }

   protected AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor processor) {
      this(sessionConfig, executor, processor, false);
   }

   private AbstractPollingIoConnector(IoSessionConfig sessionConfig, Executor executor, IoProcessor processor, boolean createdProcessor) {
      super(sessionConfig, executor);
      this.connectQueue = new ConcurrentLinkedQueue();
      this.cancelQueue = new ConcurrentLinkedQueue();
      this.disposalFuture = new AbstractIoService.ServiceOperationFuture();
      this.connectorRef = new AtomicReference();
      if (processor == null) {
         throw new IllegalArgumentException("processor");
      } else {
         this.processor = processor;
         this.createdProcessor = createdProcessor;

         try {
            this.init();
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

   protected abstract void destroy() throws Exception;

   protected abstract Object newHandle(SocketAddress var1) throws Exception;

   protected abstract boolean connect(Object var1, SocketAddress var2) throws Exception;

   protected abstract boolean finishConnect(Object var1) throws Exception;

   protected abstract AbstractIoSession newSession(IoProcessor var1, Object var2) throws Exception;

   protected abstract void close(Object var1) throws Exception;

   protected abstract void wakeup();

   protected abstract int select(int var1) throws Exception;

   protected abstract Iterator selectedHandles();

   protected abstract Iterator allHandles();

   protected abstract void register(Object var1, ConnectionRequest var2) throws Exception;

   protected abstract ConnectionRequest getConnectionRequest(Object var1);

   protected final void dispose0() throws Exception {
      this.startupWorker();
      this.wakeup();
   }

   protected final ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer sessionInitializer) {
      H handle = (H)null;
      boolean success = false;

      label109: {
         Object var8;
         try {
            handle = (H)this.newHandle(localAddress);
            if (!this.connect(handle, remoteAddress)) {
               success = true;
               break label109;
            }

            ConnectFuture future = new DefaultConnectFuture();
            T session = (T)this.newSession(this.processor, handle);
            this.initSession(session, future, sessionInitializer);
            session.getProcessor().add(session);
            success = true;
            var8 = future;
         } catch (Exception e) {
            ConnectFuture session = DefaultConnectFuture.newFailedFuture(e);
            return session;
         } finally {
            if (!success && handle != null) {
               try {
                  this.close(handle);
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               }
            }

         }

         return (ConnectFuture)var8;
      }

      AbstractPollingIoConnector<T, H>.ConnectionRequest request = new ConnectionRequest(handle, sessionInitializer);
      this.connectQueue.add(request);
      this.startupWorker();
      this.wakeup();
      return request;
   }

   private void startupWorker() {
      if (!this.selectable) {
         this.connectQueue.clear();
         this.cancelQueue.clear();
      }

      AbstractPollingIoConnector<T, H>.Connector connector = (Connector)this.connectorRef.get();
      if (connector == null) {
         connector = new Connector();
         if (this.connectorRef.compareAndSet((Object)null, connector)) {
            this.executeWorker(connector);
         }
      }

   }

   private int registerNew() {
      int nHandles = 0;

      while(true) {
         AbstractPollingIoConnector<T, H>.ConnectionRequest req = (ConnectionRequest)this.connectQueue.poll();
         if (req == null) {
            return nHandles;
         }

         H handle = (H)req.handle;

         try {
            this.register(handle, req);
            ++nHandles;
         } catch (Exception e) {
            req.setException(e);

            try {
               this.close(handle);
            } catch (Exception e2) {
               ExceptionMonitor.getInstance().exceptionCaught(e2);
            }
         }
      }
   }

   private int cancelKeys() {
      int nHandles = 0;

      while(true) {
         AbstractPollingIoConnector<T, H>.ConnectionRequest req = (ConnectionRequest)this.cancelQueue.poll();
         if (req == null) {
            if (nHandles > 0) {
               this.wakeup();
            }

            return nHandles;
         }

         H handle = (H)req.handle;

         try {
            this.close(handle);
         } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
         } finally {
            ++nHandles;
         }
      }
   }

   private int processConnections(Iterator handlers) {
      int nHandles = 0;

      while(handlers.hasNext()) {
         H handle = (H)handlers.next();
         handlers.remove();
         AbstractPollingIoConnector<T, H>.ConnectionRequest connectionRequest = this.getConnectionRequest(handle);
         if (connectionRequest != null) {
            boolean success = false;

            try {
               if (this.finishConnect(handle)) {
                  T session = (T)this.newSession(this.processor, handle);
                  this.initSession(session, connectionRequest, connectionRequest.getSessionInitializer());
                  session.getProcessor().add(session);
                  ++nHandles;
               }

               success = true;
            } catch (Exception e) {
               connectionRequest.setException(e);
            } finally {
               if (!success) {
                  this.cancelQueue.offer(connectionRequest);
               }

            }
         }
      }

      return nHandles;
   }

   private void processTimedOutSessions(Iterator handles) {
      long currentTime = System.currentTimeMillis();

      while(handles.hasNext()) {
         H handle = (H)handles.next();
         AbstractPollingIoConnector<T, H>.ConnectionRequest connectionRequest = this.getConnectionRequest(handle);
         if (connectionRequest != null && currentTime >= connectionRequest.deadline) {
            connectionRequest.setException(new ConnectException("Connection timed out."));
            this.cancelQueue.offer(connectionRequest);
         }
      }

   }

   private class Connector implements Runnable {
      private Connector() {
      }

      public void run() {
         assert AbstractPollingIoConnector.this.connectorRef.get() == this;

         int nHandles = 0;

         while(AbstractPollingIoConnector.this.selectable) {
            try {
               int timeout = (int)Math.min(AbstractPollingIoConnector.this.getConnectTimeoutMillis(), 1000L);
               int selected = AbstractPollingIoConnector.this.select(timeout);
               nHandles += AbstractPollingIoConnector.this.registerNew();
               if (nHandles == 0) {
                  AbstractPollingIoConnector.this.connectorRef.set((Object)null);
                  if (AbstractPollingIoConnector.this.connectQueue.isEmpty()) {
                     assert AbstractPollingIoConnector.this.connectorRef.get() != this;
                     break;
                  }

                  if (!AbstractPollingIoConnector.this.connectorRef.compareAndSet((Object)null, this)) {
                     assert AbstractPollingIoConnector.this.connectorRef.get() != this;
                     break;
                  }

                  assert AbstractPollingIoConnector.this.connectorRef.get() == this;
               }

               if (selected > 0) {
                  nHandles -= AbstractPollingIoConnector.this.processConnections(AbstractPollingIoConnector.this.selectedHandles());
               }

               AbstractPollingIoConnector.this.processTimedOutSessions(AbstractPollingIoConnector.this.allHandles());
               nHandles -= AbstractPollingIoConnector.this.cancelKeys();
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

         if (AbstractPollingIoConnector.this.selectable && AbstractPollingIoConnector.this.isDisposing()) {
            AbstractPollingIoConnector.this.selectable = false;

            try {
               if (AbstractPollingIoConnector.this.createdProcessor) {
                  AbstractPollingIoConnector.this.processor.dispose();
               }
            } finally {
               try {
                  synchronized(AbstractPollingIoConnector.this.disposalLock) {
                     if (AbstractPollingIoConnector.this.isDisposing()) {
                        AbstractPollingIoConnector.this.destroy();
                     }
                  }
               } catch (Exception e) {
                  ExceptionMonitor.getInstance().exceptionCaught(e);
               } finally {
                  AbstractPollingIoConnector.this.disposalFuture.setDone();
               }

            }
         }

      }
   }

   public final class ConnectionRequest extends DefaultConnectFuture {
      private final Object handle;
      private final long deadline;
      private final IoSessionInitializer sessionInitializer;

      public ConnectionRequest(Object handle, IoSessionInitializer callback) {
         this.handle = handle;
         long timeout = AbstractPollingIoConnector.this.getConnectTimeoutMillis();
         if (timeout <= 0L) {
            this.deadline = Long.MAX_VALUE;
         } else {
            this.deadline = System.currentTimeMillis() + timeout;
         }

         this.sessionInitializer = callback;
      }

      public Object getHandle() {
         return this.handle;
      }

      public long getDeadline() {
         return this.deadline;
      }

      public IoSessionInitializer getSessionInitializer() {
         return this.sessionInitializer;
      }

      public boolean cancel() {
         if (!this.isDone()) {
            boolean justCancelled = super.cancel();
            if (justCancelled) {
               AbstractPollingIoConnector.this.cancelQueue.add(this);
               AbstractPollingIoConnector.this.startupWorker();
               AbstractPollingIoConnector.this.wakeup();
            }
         }

         return true;
      }
   }
}
