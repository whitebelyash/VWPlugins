package org.apache.mina.core.session;

import org.apache.mina.core.write.WriteRequest;

public class IoEvent implements Runnable {
   private final IoEventType type;
   private final IoSession session;
   private final Object parameter;

   public IoEvent(IoEventType type, IoSession session, Object parameter) {
      if (type == null) {
         throw new IllegalArgumentException("type");
      } else if (session == null) {
         throw new IllegalArgumentException("session");
      } else {
         this.type = type;
         this.session = session;
         this.parameter = parameter;
      }
   }

   public IoEventType getType() {
      return this.type;
   }

   public IoSession getSession() {
      return this.session;
   }

   public Object getParameter() {
      return this.parameter;
   }

   public void run() {
      this.fire();
   }

   public void fire() {
      switch (this.getType()) {
         case MESSAGE_RECEIVED:
            this.getSession().getFilterChain().fireMessageReceived(this.getParameter());
            break;
         case MESSAGE_SENT:
            this.getSession().getFilterChain().fireMessageSent((WriteRequest)this.getParameter());
            break;
         case WRITE:
            this.getSession().getFilterChain().fireFilterWrite((WriteRequest)this.getParameter());
            break;
         case CLOSE:
            this.getSession().getFilterChain().fireFilterClose();
            break;
         case EXCEPTION_CAUGHT:
            this.getSession().getFilterChain().fireExceptionCaught((Throwable)this.getParameter());
            break;
         case SESSION_IDLE:
            this.getSession().getFilterChain().fireSessionIdle((IdleStatus)this.getParameter());
            break;
         case SESSION_OPENED:
            this.getSession().getFilterChain().fireSessionOpened();
            break;
         case SESSION_CREATED:
            this.getSession().getFilterChain().fireSessionCreated();
            break;
         case SESSION_CLOSED:
            this.getSession().getFilterChain().fireSessionClosed();
            break;
         default:
            throw new IllegalArgumentException("Unknown event type: " + this.getType());
      }

   }

   public String toString() {
      return this.getParameter() == null ? "[" + this.getSession() + "] " + this.getType().name() : "[" + this.getSession() + "] " + this.getType().name() + ": " + this.getParameter();
   }
}
