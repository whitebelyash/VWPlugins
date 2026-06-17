package org.apache.mina.core.filterchain;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEvent;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IoFilterEvent extends IoEvent {
   private static final Logger LOGGER = LoggerFactory.getLogger(IoFilterEvent.class);
   private static final boolean DEBUG;
   private final IoFilter.NextFilter nextFilter;

   public IoFilterEvent(IoFilter.NextFilter nextFilter, IoEventType type, IoSession session, Object parameter) {
      super(type, session, parameter);
      if (nextFilter == null) {
         throw new IllegalArgumentException("nextFilter must not be null");
      } else {
         this.nextFilter = nextFilter;
      }
   }

   public IoFilter.NextFilter getNextFilter() {
      return this.nextFilter;
   }

   public void fire() {
      IoSession session = this.getSession();
      IoFilter.NextFilter nextFilter = this.getNextFilter();
      IoEventType type = this.getType();
      if (DEBUG) {
         LOGGER.debug((String)"Firing a {} event for session {}", (Object)type, (Object)session.getId());
      }

      switch (type) {
         case MESSAGE_RECEIVED:
            Object parameter = this.getParameter();
            nextFilter.messageReceived(session, parameter);
            break;
         case MESSAGE_SENT:
            WriteRequest writeRequest = (WriteRequest)this.getParameter();
            nextFilter.messageSent(session, writeRequest);
            break;
         case WRITE:
            WriteRequest writeRequest = (WriteRequest)this.getParameter();
            nextFilter.filterWrite(session, writeRequest);
            break;
         case CLOSE:
            nextFilter.filterClose(session);
            break;
         case EXCEPTION_CAUGHT:
            Throwable throwable = (Throwable)this.getParameter();
            nextFilter.exceptionCaught(session, throwable);
            break;
         case SESSION_IDLE:
            nextFilter.sessionIdle(session, (IdleStatus)this.getParameter());
            break;
         case SESSION_OPENED:
            nextFilter.sessionOpened(session);
            break;
         case SESSION_CREATED:
            nextFilter.sessionCreated(session);
            break;
         case SESSION_CLOSED:
            nextFilter.sessionClosed(session);
            break;
         default:
            throw new IllegalArgumentException("Unknown event type: " + type);
      }

      if (DEBUG) {
         LOGGER.debug((String)"Event {} has been fired for session {}", (Object)type, (Object)session.getId());
      }

   }

   static {
      DEBUG = LOGGER.isDebugEnabled();
   }
}
