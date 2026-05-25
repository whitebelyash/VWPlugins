package org.apache.mina.handler.demux;

import org.apache.mina.core.session.IoSession;

public interface ExceptionHandler {
   ExceptionHandler NOOP = new ExceptionHandler() {
      public void exceptionCaught(IoSession session, Throwable cause) {
      }
   };
   ExceptionHandler CLOSE = new ExceptionHandler() {
      public void exceptionCaught(IoSession session, Throwable cause) {
         session.closeNow();
      }
   };

   void exceptionCaught(IoSession var1, Throwable var2) throws Exception;
}
