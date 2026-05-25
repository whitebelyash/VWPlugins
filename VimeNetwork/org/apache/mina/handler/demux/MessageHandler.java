package org.apache.mina.handler.demux;

import org.apache.mina.core.session.IoSession;

public interface MessageHandler {
   MessageHandler NOOP = new MessageHandler() {
      public void handleMessage(IoSession session, Object message) {
      }
   };

   void handleMessage(IoSession var1, Object var2) throws Exception;
}
