package org.apache.mina.proxy.event;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoSessionEvent {
   private static final Logger logger = LoggerFactory.getLogger(IoSessionEvent.class);
   private final IoFilter.NextFilter nextFilter;
   private final IoSession session;
   private final IoSessionEventType type;
   private IdleStatus status;

   public IoSessionEvent(IoFilter.NextFilter nextFilter, IoSession session, IoSessionEventType type) {
      this.nextFilter = nextFilter;
      this.session = session;
      this.type = type;
   }

   public IoSessionEvent(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) {
      this(nextFilter, session, IoSessionEventType.IDLE);
      this.status = status;
   }

   public void deliverEvent() {
      logger.debug((String)"Delivering event {}", (Object)this);
      deliverEvent(this.nextFilter, this.session, this.type, this.status);
   }

   private static void deliverEvent(IoFilter.NextFilter nextFilter, IoSession session, IoSessionEventType type, IdleStatus status) {
      switch (type) {
         case CREATED:
            nextFilter.sessionCreated(session);
            break;
         case OPENED:
            nextFilter.sessionOpened(session);
            break;
         case IDLE:
            nextFilter.sessionIdle(session, status);
            break;
         case CLOSED:
            nextFilter.sessionClosed(session);
      }

   }

   public String toString() {
      StringBuilder sb = new StringBuilder(IoSessionEvent.class.getSimpleName());
      sb.append('@');
      sb.append(Integer.toHexString(this.hashCode()));
      sb.append(" - [ ").append(this.session);
      sb.append(", ").append(this.type);
      sb.append(']');
      return sb.toString();
   }

   public IdleStatus getStatus() {
      return this.status;
   }

   public IoFilter.NextFilter getNextFilter() {
      return this.nextFilter;
   }

   public IoSession getSession() {
      return this.session;
   }

   public IoSessionEventType getType() {
      return this.type;
   }
}
