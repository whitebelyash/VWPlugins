package org.apache.mina.core.service;

import java.util.EventListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

public interface IoServiceListener extends EventListener {
   void serviceActivated(IoService var1) throws Exception;

   void serviceIdle(IoService var1, IdleStatus var2) throws Exception;

   void serviceDeactivated(IoService var1) throws Exception;

   void sessionCreated(IoSession var1) throws Exception;

   void sessionClosed(IoSession var1) throws Exception;

   void sessionDestroyed(IoSession var1) throws Exception;
}
