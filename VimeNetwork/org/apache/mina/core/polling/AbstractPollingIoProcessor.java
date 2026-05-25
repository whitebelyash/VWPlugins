package org.apache.mina.core.polling;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.nio.channels.ClosedSelectorException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.future.DefaultIoFuture;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoServiceListenerSupport;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.SessionState;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.transport.socket.AbstractDatagramSessionConfig;
import org.apache.mina.util.ExceptionMonitor;
import org.apache.mina.util.NamePreservingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPollingIoProcessor implements IoProcessor {
   private static final Logger LOG = LoggerFactory.getLogger(IoProcessor.class);
   private static final long SELECT_TIMEOUT = 1000L;
   private static final ConcurrentHashMap threadIds = new ConcurrentHashMap();
   private final String threadName;
   private final Executor executor;
   private final Queue newSessions = new ConcurrentLinkedQueue();
   private final Queue removingSessions = new ConcurrentLinkedQueue();
   private final Queue flushingSessions = new ConcurrentLinkedQueue();
   private final Queue trafficControllingSessions = new ConcurrentLinkedQueue();
   private final AtomicReference processorRef = new AtomicReference();
   private long lastIdleCheckTime;
   private final Object disposalLock = new Object();
   private volatile boolean disposing;
   private volatile boolean disposed;
   private final DefaultIoFuture disposalFuture = new DefaultIoFuture((IoSession)null);
   protected AtomicBoolean wakeupCalled = new AtomicBoolean(false);

   protected AbstractPollingIoProcessor(Executor executor) {
      if (executor == null) {
         throw new IllegalArgumentException("executor");
      } else {
         this.threadName = this.nextThreadName();
         this.executor = executor;
      }
   }

   private String nextThreadName() {
      Class<?> cls = this.getClass();
      AtomicInteger threadId = (AtomicInteger)threadIds.putIfAbsent(cls, new AtomicInteger(1));
      int newThreadId;
      if (threadId == null) {
         newThreadId = 1;
      } else {
         newThreadId = threadId.incrementAndGet();
      }

      return cls.getSimpleName() + '-' + newThreadId;
   }

   public final boolean isDisposing() {
      return this.disposing;
   }

   public final boolean isDisposed() {
      return this.disposed;
   }

   public final void dispose() {
      if (!this.disposed && !this.disposing) {
         synchronized(this.disposalLock) {
            this.disposing = true;
            this.startupProcessor();
         }

         this.disposalFuture.awaitUninterruptibly();
         this.disposed = true;
      }
   }

   protected abstract void doDispose() throws Exception;

   protected abstract int select(long var1) throws Exception;

   protected abstract int select() throws Exception;

   protected abstract boolean isSelectorEmpty();

   protected abstract void wakeup();

   protected abstract Iterator allSessions();

   protected abstract Iterator selectedSessions();

   protected abstract SessionState getState(AbstractIoSession var1);

   protected abstract boolean isWritable(AbstractIoSession var1);

   protected abstract boolean isReadable(AbstractIoSession var1);

   protected abstract void setInterestedInWrite(AbstractIoSession var1, boolean var2) throws Exception;

   protected abstract void setInterestedInRead(AbstractIoSession var1, boolean var2) throws Exception;

   protected abstract boolean isInterestedInRead(AbstractIoSession var1);

   protected abstract boolean isInterestedInWrite(AbstractIoSession var1);

   protected abstract void init(AbstractIoSession var1) throws Exception;

   protected abstract void destroy(AbstractIoSession var1) throws Exception;

   protected abstract int read(AbstractIoSession var1, IoBuffer var2) throws Exception;

   protected abstract int write(AbstractIoSession var1, IoBuffer var2, int var3) throws IOException;

   protected abstract int transferFile(AbstractIoSession var1, FileRegion var2, int var3) throws Exception;

   public final void add(AbstractIoSession session) {
      if (!this.disposed && !this.disposing) {
         this.newSessions.add(session);
         this.startupProcessor();
      } else {
         throw new IllegalStateException("Already disposed.");
      }
   }

   public final void remove(AbstractIoSession session) {
      this.scheduleRemove(session);
      this.startupProcessor();
   }

   private void scheduleRemove(AbstractIoSession session) {
      if (!this.removingSessions.contains(session)) {
         this.removingSessions.add(session);
      }

   }

   public void write(AbstractIoSession session, WriteRequest writeRequest) {
      WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
      writeRequestQueue.offer(session, writeRequest);
      if (!session.isWriteSuspended()) {
         this.flush(session);
      }

   }

   public final void flush(AbstractIoSession session) {
      if (session.setScheduledForFlush(true)) {
         this.flushingSessions.add(session);
         this.wakeup();
      }

   }

   private void scheduleFlush(AbstractIoSession session) {
      if (session.setScheduledForFlush(true)) {
         this.flushingSessions.add(session);
      }

   }

   public final void updateTrafficMask(AbstractIoSession session) {
      this.trafficControllingSessions.add(session);
      this.wakeup();
   }

   private void startupProcessor() {
      AbstractPollingIoProcessor<S>.Processor processor = (Processor)this.processorRef.get();
      if (processor == null) {
         processor = new Processor();
         if (this.processorRef.compareAndSet((Object)null, processor)) {
            this.executor.execute(new NamePreservingRunnable(processor, this.threadName));
         }
      }

      this.wakeup();
   }

   protected abstract void registerNewSelector() throws IOException;

   protected abstract boolean isBrokenConnection() throws IOException;

   private int handleNewSessions() {
      int addedSessions = 0;

      for(S session = (S)((AbstractIoSession)this.newSessions.poll()); session != null; session = (S)((AbstractIoSession)this.newSessions.poll())) {
         if (this.addNow(session)) {
            ++addedSessions;
         }
      }

      return addedSessions;
   }

   private boolean addNow(AbstractIoSession session) {
      boolean registered = false;

      try {
         this.init(session);
         registered = true;
         IoFilterChainBuilder chainBuilder = session.getService().getFilterChainBuilder();
         chainBuilder.buildFilterChain(session.getFilterChain());
         IoServiceListenerSupport listeners = ((AbstractIoService)session.getService()).getListeners();
         listeners.fireSessionCreated(session);
      } catch (Exception e) {
         ExceptionMonitor.getInstance().exceptionCaught(e);

         try {
            this.destroy(session);
         } catch (Exception e1) {
            ExceptionMonitor.getInstance().exceptionCaught(e1);
         } finally {
            registered = false;
         }
      }

      return registered;
   }

   private int removeSessions() {
      int removedSessions = 0;

      for(S session = (S)((AbstractIoSession)this.removingSessions.poll()); session != null; session = (S)((AbstractIoSession)this.removingSessions.poll())) {
         SessionState state = this.getState(session);
         switch (state) {
            case OPENED:
               if (this.removeNow(session)) {
                  ++removedSessions;
               }
               break;
            case CLOSING:
               ++removedSessions;
               break;
            case OPENING:
               this.newSessions.remove(session);
               if (this.removeNow(session)) {
                  ++removedSessions;
               }
               break;
            default:
               throw new IllegalStateException(String.valueOf(state));
         }
      }

      return removedSessions;
   }

   private boolean removeNow(AbstractIoSession session) {
      this.clearWriteRequestQueue(session);

      try {
         this.destroy(session);
         boolean var2 = true;
         return var2;
      } catch (Exception e) {
         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireExceptionCaught(e);
      } finally {
         try {
            this.clearWriteRequestQueue(session);
            ((AbstractIoService)session.getService()).getListeners().fireSessionDestroyed(session);
         } catch (Exception e) {
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(e);
         }

      }

      return false;
   }

   private void clearWriteRequestQueue(AbstractIoSession session) {
      WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
      List<WriteRequest> failedRequests = new ArrayList();
      WriteRequest req;
      if ((req = writeRequestQueue.poll(session)) != null) {
         Object message = req.getMessage();
         if (message instanceof IoBuffer) {
            IoBuffer buf = (IoBuffer)message;
            if (buf.hasRemaining()) {
               buf.reset();
               failedRequests.add(req);
            } else {
               IoFilterChain filterChain = session.getFilterChain();
               filterChain.fireMessageSent(req);
            }
         } else {
            failedRequests.add(req);
         }

         while((req = writeRequestQueue.poll(session)) != null) {
            failedRequests.add(req);
         }
      }

      if (!failedRequests.isEmpty()) {
         WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);

         for(WriteRequest r : failedRequests) {
            session.decreaseScheduledBytesAndMessages(r);
            r.getFuture().setException(cause);
         }

         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireExceptionCaught(cause);
      }

   }

   private void process() throws Exception {
      Iterator<S> i = this.selectedSessions();

      while(i.hasNext()) {
         S session = (S)((AbstractIoSession)i.next());
         this.process(session);
         i.remove();
      }

   }

   private void process(AbstractIoSession session) {
      if (this.isReadable(session) && !session.isReadSuspended()) {
         this.read(session);
      }

      if (this.isWritable(session) && !session.isWriteSuspended() && session.setScheduledForFlush(true)) {
         this.flushingSessions.add(session);
      }

   }

   private void read(AbstractIoSession session) {
      IoSessionConfig config = session.getConfig();
      int bufferSize = config.getReadBufferSize();
      IoBuffer buf = IoBuffer.allocate(bufferSize);
      boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();

      try {
         int readBytes = 0;

         int ret;
         try {
            if (hasFragmentation) {
               while((ret = this.read(session, buf)) > 0) {
                  readBytes += ret;
                  if (!buf.hasRemaining()) {
                     break;
                  }
               }
            } else {
               ret = this.read(session, buf);
               if (ret > 0) {
                  readBytes = ret;
               }
            }
         } finally {
            buf.flip();
         }

         if (readBytes > 0) {
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireMessageReceived(buf);
            IoBuffer var13 = null;
            if (hasFragmentation) {
               if (readBytes << 1 < config.getReadBufferSize()) {
                  session.decreaseReadBufferSize();
               } else if (readBytes == config.getReadBufferSize()) {
                  session.increaseReadBufferSize();
               }
            }
         }

         if (ret < 0) {
            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireInputClosed();
         }
      } catch (Exception var12) {
         if (var12 instanceof IOException && (!(var12 instanceof PortUnreachableException) || !AbstractDatagramSessionConfig.class.isAssignableFrom(config.getClass()) || ((AbstractDatagramSessionConfig)config).isCloseOnPortUnreachable())) {
            this.scheduleRemove(session);
         }

         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireExceptionCaught(var12);
      }

   }

   private void notifyIdleSessions(long currentTime) throws Exception {
      if (currentTime - this.lastIdleCheckTime >= 1000L) {
         this.lastIdleCheckTime = currentTime;
         AbstractIoSession.notifyIdleness(this.allSessions(), currentTime);
      }

   }

   private void flush(long currentTime) {
      if (!this.flushingSessions.isEmpty()) {
         do {
            S session = (S)((AbstractIoSession)this.flushingSessions.poll());
            if (session == null) {
               break;
            }

            session.unscheduledForFlush();
            SessionState state = this.getState(session);
            switch (state) {
               case OPENED:
                  try {
                     boolean flushedAll = this.flushNow(session, currentTime);
                     if (flushedAll && !session.getWriteRequestQueue().isEmpty(session) && !session.isScheduledForFlush()) {
                        this.scheduleFlush(session);
                     }
                  } catch (Exception e) {
                     this.scheduleRemove(session);
                     session.closeNow();
                     IoFilterChain filterChain = session.getFilterChain();
                     filterChain.fireExceptionCaught(e);
                  }
               case CLOSING:
                  break;
               case OPENING:
                  this.scheduleFlush(session);
                  return;
               default:
                  throw new IllegalStateException(String.valueOf(state));
            }
         } while(!this.flushingSessions.isEmpty());

      }
   }

   private boolean flushNow(AbstractIoSession session, long currentTime) {
      if (!session.isConnected()) {
         this.scheduleRemove(session);
         return false;
      } else {
         boolean hasFragmentation = session.getTransportMetadata().hasFragmentation();
         WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
         int maxWrittenBytes = session.getConfig().getMaxReadBufferSize() + (session.getConfig().getMaxReadBufferSize() >>> 1);
         int writtenBytes = 0;
         WriteRequest req = null;

         try {
            this.setInterestedInWrite(session, false);

            do {
               req = session.getCurrentWriteRequest();
               if (req == null) {
                  req = writeRequestQueue.poll(session);
                  if (req == null) {
                     break;
                  }

                  session.setCurrentWriteRequest(req);
               }

               Object message = req.getMessage();
               int localWrittenBytes;
               if (message instanceof IoBuffer) {
                  localWrittenBytes = this.writeBuffer(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
                  if (localWrittenBytes > 0 && ((IoBuffer)message).hasRemaining()) {
                     int var10000 = writtenBytes + localWrittenBytes;
                     this.setInterestedInWrite(session, true);
                     return false;
                  }
               } else {
                  if (!(message instanceof FileRegion)) {
                     throw new IllegalStateException("Don't know how to handle message of type '" + message.getClass().getName() + "'.  Are you missing a protocol encoder?");
                  }

                  localWrittenBytes = this.writeFile(session, req, hasFragmentation, maxWrittenBytes - writtenBytes, currentTime);
                  if (localWrittenBytes > 0 && ((FileRegion)message).getRemainingBytes() > 0L) {
                     int var14 = writtenBytes + localWrittenBytes;
                     this.setInterestedInWrite(session, true);
                     return false;
                  }
               }

               if (localWrittenBytes == 0) {
                  if (!req.equals(AbstractIoSession.MESSAGE_SENT_REQUEST)) {
                     this.setInterestedInWrite(session, true);
                     return false;
                  }
               } else {
                  writtenBytes += localWrittenBytes;
                  if (writtenBytes >= maxWrittenBytes) {
                     this.scheduleFlush(session);
                     return false;
                  }
               }

               if (message instanceof IoBuffer) {
                  ((IoBuffer)message).free();
               }
            } while(writtenBytes < maxWrittenBytes);

            return true;
         } catch (Exception var11) {
            if (req != null) {
               req.getFuture().setException(var11);
            }

            IoFilterChain filterChain = session.getFilterChain();
            filterChain.fireExceptionCaught(var11);
            return false;
         }
      }
   }

   private int writeBuffer(AbstractIoSession session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
      IoBuffer buf = (IoBuffer)req.getMessage();
      int localWrittenBytes = 0;
      if (buf.hasRemaining()) {
         int length;
         if (hasFragmentation) {
            length = Math.min(buf.remaining(), maxLength);
         } else {
            length = buf.remaining();
         }

         try {
            localWrittenBytes = this.write(session, buf, length);
         } catch (IOException var11) {
            buf.free();
            session.closeNow();
            this.removeNow(session);
            return 0;
         }
      }

      session.increaseWrittenBytes(localWrittenBytes, currentTime);
      if (!buf.hasRemaining() || !hasFragmentation && localWrittenBytes != 0) {
         Object originalMessage = req.getOriginalRequest().getMessage();
         if (originalMessage instanceof IoBuffer) {
            buf = (IoBuffer)req.getOriginalRequest().getMessage();
            int pos = buf.position();
            buf.reset();
            this.fireMessageSent(session, req);
            buf.position(pos);
         } else {
            this.fireMessageSent(session, req);
         }
      }

      return localWrittenBytes;
   }

   private int writeFile(AbstractIoSession session, WriteRequest req, boolean hasFragmentation, int maxLength, long currentTime) throws Exception {
      FileRegion region = (FileRegion)req.getMessage();
      int localWrittenBytes;
      if (region.getRemainingBytes() > 0L) {
         int length;
         if (hasFragmentation) {
            length = (int)Math.min(region.getRemainingBytes(), (long)maxLength);
         } else {
            length = (int)Math.min(2147483647L, region.getRemainingBytes());
         }

         localWrittenBytes = this.transferFile(session, region, length);
         region.update((long)localWrittenBytes);
      } else {
         localWrittenBytes = 0;
      }

      session.increaseWrittenBytes(localWrittenBytes, currentTime);
      if (region.getRemainingBytes() <= 0L || !hasFragmentation && localWrittenBytes != 0) {
         this.fireMessageSent(session, req);
      }

      return localWrittenBytes;
   }

   private void fireMessageSent(AbstractIoSession session, WriteRequest req) {
      session.setCurrentWriteRequest((WriteRequest)null);
      IoFilterChain filterChain = session.getFilterChain();
      filterChain.fireMessageSent(req);
   }

   private void updateTrafficMask() {
      for(int queueSize = this.trafficControllingSessions.size(); queueSize > 0; --queueSize) {
         S session = (S)((AbstractIoSession)this.trafficControllingSessions.poll());
         if (session == null) {
            return;
         }

         SessionState state = this.getState(session);
         switch (state) {
            case OPENED:
               this.updateTrafficControl(session);
            case CLOSING:
               break;
            case OPENING:
               this.trafficControllingSessions.add(session);
               break;
            default:
               throw new IllegalStateException(String.valueOf(state));
         }
      }

   }

   public void updateTrafficControl(AbstractIoSession session) {
      try {
         this.setInterestedInRead(session, !session.isReadSuspended());
      } catch (Exception e) {
         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireExceptionCaught(e);
      }

      try {
         this.setInterestedInWrite(session, !session.getWriteRequestQueue().isEmpty(session) && !session.isWriteSuspended());
      } catch (Exception e) {
         IoFilterChain filterChain = session.getFilterChain();
         filterChain.fireExceptionCaught(e);
      }

   }

   private class Processor implements Runnable {
      private Processor() {
      }

      public void run() {
         assert AbstractPollingIoProcessor.this.processorRef.get() == this;

         int nSessions = 0;
         AbstractPollingIoProcessor.this.lastIdleCheckTime = System.currentTimeMillis();
         int nbTries = 10;

         while(true) {
            try {
               long t0 = System.currentTimeMillis();
               int selected = AbstractPollingIoProcessor.this.select(1000L);
               long t1 = System.currentTimeMillis();
               long delta = t1 - t0;
               if (!AbstractPollingIoProcessor.this.wakeupCalled.getAndSet(false) && selected == 0 && delta < 100L) {
                  if (AbstractPollingIoProcessor.this.isBrokenConnection()) {
                     AbstractPollingIoProcessor.LOG.warn("Broken connection");
                  } else if (nbTries == 0) {
                     AbstractPollingIoProcessor.LOG.warn("Create a new selector. Selected is 0, delta = " + delta);
                     AbstractPollingIoProcessor.this.registerNewSelector();
                     nbTries = 10;
                  } else {
                     --nbTries;
                  }
               } else {
                  nbTries = 10;
               }

               nSessions += AbstractPollingIoProcessor.this.handleNewSessions();
               AbstractPollingIoProcessor.this.updateTrafficMask();
               if (selected > 0) {
                  AbstractPollingIoProcessor.this.process();
               }

               long currentTime = System.currentTimeMillis();
               AbstractPollingIoProcessor.this.flush(currentTime);
               nSessions -= AbstractPollingIoProcessor.this.removeSessions();
               AbstractPollingIoProcessor.this.notifyIdleSessions(currentTime);
               if (nSessions == 0) {
                  AbstractPollingIoProcessor.this.processorRef.set((Object)null);
                  if (AbstractPollingIoProcessor.this.newSessions.isEmpty() && AbstractPollingIoProcessor.this.isSelectorEmpty()) {
                     assert AbstractPollingIoProcessor.this.processorRef.get() != this;
                     break;
                  }

                  assert AbstractPollingIoProcessor.this.processorRef.get() != this;

                  if (!AbstractPollingIoProcessor.this.processorRef.compareAndSet((Object)null, this)) {
                     assert AbstractPollingIoProcessor.this.processorRef.get() != this;
                     break;
                  }

                  assert AbstractPollingIoProcessor.this.processorRef.get() == this;
               }

               if (AbstractPollingIoProcessor.this.isDisposing()) {
                  boolean hasKeys = false;
                  Iterator<S> i = AbstractPollingIoProcessor.this.allSessions();

                  while(i.hasNext()) {
                     IoSession session = (IoSession)i.next();
                     if (session.isActive()) {
                        AbstractPollingIoProcessor.this.scheduleRemove((AbstractIoSession)session);
                        hasKeys = true;
                     }
                  }

                  if (hasKeys) {
                     AbstractPollingIoProcessor.this.wakeup();
                  }
               }
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

         try {
            synchronized(AbstractPollingIoProcessor.this.disposalLock) {
               if (AbstractPollingIoProcessor.this.disposing) {
                  AbstractPollingIoProcessor.this.doDispose();
               }
            }
         } catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
         } finally {
            AbstractPollingIoProcessor.this.disposalFuture.setValue(true);
         }

      }
   }
}
