package org.apache.mina.handler.chain;

import org.apache.mina.core.session.IoSession;

public interface IoHandlerCommand {
   void execute(NextCommand var1, IoSession var2, Object var3) throws Exception;

   public interface NextCommand {
      void execute(IoSession var1, Object var2) throws Exception;
   }
}
