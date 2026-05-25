package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface KeepAliveRequestTimeoutHandler {
   KeepAliveRequestTimeoutHandler NOOP = new KeepAliveRequestTimeoutHandler() {
      public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
      }
   };
   KeepAliveRequestTimeoutHandler LOG = new KeepAliveRequestTimeoutHandler() {
      private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

      public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
         this.LOGGER.warn((String)"A keep-alive response message was not received within {} second(s).", (Object)filter.getRequestTimeout());
      }
   };
   KeepAliveRequestTimeoutHandler EXCEPTION = new KeepAliveRequestTimeoutHandler() {
      public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
         throw new KeepAliveRequestTimeoutException("A keep-alive response message was not received within " + filter.getRequestTimeout() + " second(s).");
      }
   };
   KeepAliveRequestTimeoutHandler CLOSE = new KeepAliveRequestTimeoutHandler() {
      private final Logger LOGGER = LoggerFactory.getLogger(KeepAliveFilter.class);

      public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
         this.LOGGER.warn((String)"Closing the session because a keep-alive response message was not received within {} second(s).", (Object)filter.getRequestTimeout());
         session.closeNow();
      }
   };
   KeepAliveRequestTimeoutHandler DEAF_SPEAKER = new KeepAliveRequestTimeoutHandler() {
      public void keepAliveRequestTimedOut(KeepAliveFilter filter, IoSession session) throws Exception {
         throw new Error("Shouldn't be invoked.  Please file a bug report.");
      }
   };

   void keepAliveRequestTimedOut(KeepAliveFilter var1, IoSession var2) throws Exception;
}
