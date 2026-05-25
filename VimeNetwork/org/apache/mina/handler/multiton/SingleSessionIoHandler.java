package org.apache.mina.handler.multiton;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/** @deprecated */
@Deprecated
public interface SingleSessionIoHandler {
   void sessionCreated() throws Exception;

   void sessionOpened() throws Exception;

   void sessionClosed() throws Exception;

   void sessionIdle(IdleStatus var1) throws Exception;

   void exceptionCaught(Throwable var1) throws Exception;

   void inputClosed(IoSession var1);

   void messageReceived(Object var1) throws Exception;

   void messageSent(Object var1) throws Exception;
}
