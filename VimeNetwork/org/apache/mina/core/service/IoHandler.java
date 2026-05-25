package org.apache.mina.core.service;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface IoHandler {
   void sessionCreated(IoSession var1) throws Exception;

   void sessionOpened(IoSession var1) throws Exception;

   void sessionClosed(IoSession var1) throws Exception;

   void sessionIdle(IoSession var1, IdleStatus var2) throws Exception;

   void exceptionCaught(IoSession var1, Throwable var2) throws Exception;

   void messageReceived(IoSession var1, Object var2) throws Exception;

   void messageSent(IoSession var1, Object var2) throws Exception;

   void inputClosed(IoSession var1) throws Exception;
}
