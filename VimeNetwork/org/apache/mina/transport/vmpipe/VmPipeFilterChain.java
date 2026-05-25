package org.apache.mina.transport.vmpipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChain;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.apache.mina.core.write.WriteToClosedSessionException;

class VmPipeFilterChain extends DefaultIoFilterChain {
   private final Queue eventQueue = new ConcurrentLinkedQueue();
   private final IoProcessor processor = new VmPipeIoProcessor();
   private volatile boolean flushEnabled;
   private volatile boolean sessionOpened;

   VmPipeFilterChain(AbstractIoSession session) {
      super(session);
   }

   IoProcessor getProcessor() {
      return this.processor;
   }

   public void start() {
      this.flushEnabled = true;
      this.flushEvents();
      flushPendingDataQueues((VmPipeSession)this.getSession());
   }

   private void pushEvent(IoEvent e) {
      this.pushEvent(e, this.flushEnabled);
   }

   private void pushEvent(IoEvent e, boolean flushNow) {
      this.eventQueue.add(e);
      if (flushNow) {
         this.flushEvents();
      }

   }

   private void flushEvents() {
      IoEvent e;
      while((e = (IoEvent)this.eventQueue.poll()) != null) {
         this.fireEvent(e);
      }

   }

   private void fireEvent(IoEvent e) {
      VmPipeSession session = (VmPipeSession)this.getSession();
      IoEventType type = e.getType();
      Object data = e.getParameter();
      if (type == IoEventType.MESSAGE_RECEIVED) {
         if (this.sessionOpened && !session.isReadSuspended() && session.getLock().tryLock()) {
            try {
               if (session.isReadSuspended()) {
                  session.receivedMessageQueue.add(data);
               } else {
                  super.fireMessageReceived(data);
               }
            } finally {
               session.getLock().unlock();
            }
         } else {
            session.receivedMessageQueue.add(data);
         }
      } else if (type == IoEventType.WRITE) {
         super.fireFilterWrite((WriteRequest)data);
      } else if (type == IoEventType.MESSAGE_SENT) {
         super.fireMessageSent((WriteRequest)data);
      } else if (type == IoEventType.EXCEPTION_CAUGHT) {
         super.fireExceptionCaught((Throwable)data);
      } else if (type == IoEventType.SESSION_IDLE) {
         super.fireSessionIdle((IdleStatus)data);
      } else if (type == IoEventType.SESSION_OPENED) {
         super.fireSessionOpened();
         this.sessionOpened = true;
      } else if (type == IoEventType.SESSION_CREATED) {
         session.getLock().lock();

         try {
            super.fireSessionCreated();
         } finally {
            session.getLock().unlock();
         }
      } else if (type == IoEventType.SESSION_CLOSED) {
         flushPendingDataQueues(session);
         super.fireSessionClosed();
      } else if (type == IoEventType.CLOSE) {
         super.fireFilterClose();
      }

   }

   private static void flushPendingDataQueues(VmPipeSession s) {
      s.getProcessor().updateTrafficControl(s);
      s.getRemoteSession().getProcessor().updateTrafficControl(s);
   }

   public void fireFilterClose() {
      this.pushEvent(new IoEvent(IoEventType.CLOSE, this.getSession(), (Object)null));
   }

   public void fireFilterWrite(WriteRequest writeRequest) {
      this.pushEvent(new IoEvent(IoEventType.WRITE, this.getSession(), writeRequest));
   }

   public void fireExceptionCaught(Throwable cause) {
      this.pushEvent(new IoEvent(IoEventType.EXCEPTION_CAUGHT, this.getSession(), cause));
   }

   public void fireMessageSent(WriteRequest request) {
      this.pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, this.getSession(), request));
   }

   public void fireSessionClosed() {
      this.pushEvent(new IoEvent(IoEventType.SESSION_CLOSED, this.getSession(), (Object)null));
   }

   public void fireSessionCreated() {
      this.pushEvent(new IoEvent(IoEventType.SESSION_CREATED, this.getSession(), (Object)null));
   }

   public void fireSessionIdle(IdleStatus status) {
      this.pushEvent(new IoEvent(IoEventType.SESSION_IDLE, this.getSession(), status));
   }

   public void fireSessionOpened() {
      this.pushEvent(new IoEvent(IoEventType.SESSION_OPENED, this.getSession(), (Object)null));
   }

   public void fireMessageReceived(Object message) {
      this.pushEvent(new IoEvent(IoEventType.MESSAGE_RECEIVED, this.getSession(), message));
   }

   private class VmPipeIoProcessor implements IoProcessor {
      private VmPipeIoProcessor() {
      }

      public void flush(VmPipeSession session) {
         WriteRequestQueue queue = session.getWriteRequestQueue0();
         if (!session.isClosing()) {
            session.getLock().lock();

            label132: {
               try {
                  if (!queue.isEmpty(session)) {
                     long currentTime = System.currentTimeMillis();

                     while(true) {
                        WriteRequest req;
                        if ((req = queue.poll(session)) == null) {
                           break label132;
                        }

                        Object m = req.getMessage();
                        VmPipeFilterChain.this.pushEvent(new IoEvent(IoEventType.MESSAGE_SENT, session, req), false);
                        session.getRemoteSession().getFilterChain().fireMessageReceived(this.getMessageCopy(m));
                        if (m instanceof IoBuffer) {
                           session.increaseWrittenBytes0(((IoBuffer)m).remaining(), currentTime);
                        }
                     }
                  }
               } finally {
                  if (VmPipeFilterChain.this.flushEnabled) {
                     VmPipeFilterChain.this.flushEvents();
                  }

                  session.getLock().unlock();
               }

               return;
            }

            VmPipeFilterChain.flushPendingDataQueues(session);
         } else {
            List<WriteRequest> failedRequests = new ArrayList();

            WriteRequest req;
            while((req = queue.poll(session)) != null) {
               failedRequests.add(req);
            }

            if (!failedRequests.isEmpty()) {
               WriteToClosedSessionException cause = new WriteToClosedSessionException(failedRequests);

               for(WriteRequest r : failedRequests) {
                  r.getFuture().setException(cause);
               }

               session.getFilterChain().fireExceptionCaught(cause);
            }
         }

      }

      public void write(VmPipeSession session, WriteRequest writeRequest) {
         WriteRequestQueue writeRequestQueue = session.getWriteRequestQueue();
         writeRequestQueue.offer(session, writeRequest);
         if (!session.isWriteSuspended()) {
            this.flush(session);
         }

      }

      private Object getMessageCopy(Object message) {
         Object messageCopy = message;
         if (message instanceof IoBuffer) {
            IoBuffer rb = (IoBuffer)message;
            rb.mark();
            IoBuffer wb = IoBuffer.allocate(rb.remaining());
            wb.put(rb);
            wb.flip();
            rb.reset();
            messageCopy = wb;
         }

         return messageCopy;
      }

      public void remove(VmPipeSession session) {
         try {
            session.getLock().lock();
            if (!session.getCloseFuture().isClosed()) {
               session.getServiceListeners().fireSessionDestroyed(session);
               session.getRemoteSession().closeNow();
            }
         } finally {
            session.getLock().unlock();
         }

      }

      public void add(VmPipeSession session) {
      }

      public void updateTrafficControl(VmPipeSession session) {
         if (!session.isReadSuspended()) {
            List<Object> data = new ArrayList();
            session.receivedMessageQueue.drainTo(data);

            for(Object aData : data) {
               VmPipeFilterChain.this.fireMessageReceived(aData);
            }
         }

         if (!session.isWriteSuspended()) {
            this.flush(session);
         }

      }

      public void dispose() {
      }

      public boolean isDisposed() {
         return false;
      }

      public boolean isDisposing() {
         return false;
      }
   }
}
