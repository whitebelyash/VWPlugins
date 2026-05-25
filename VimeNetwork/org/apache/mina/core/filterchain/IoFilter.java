package org.apache.mina.core.filterchain;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoFilter {
   void init() throws Exception;

   void destroy() throws Exception;

   void onPreAdd(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

   void onPostAdd(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

   void onPreRemove(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

   void onPostRemove(IoFilterChain var1, String var2, NextFilter var3) throws Exception;

   void sessionCreated(NextFilter var1, IoSession var2) throws Exception;

   void sessionOpened(NextFilter var1, IoSession var2) throws Exception;

   void sessionClosed(NextFilter var1, IoSession var2) throws Exception;

   void sessionIdle(NextFilter var1, IoSession var2, IdleStatus var3) throws Exception;

   void exceptionCaught(NextFilter var1, IoSession var2, Throwable var3) throws Exception;

   void inputClosed(NextFilter var1, IoSession var2) throws Exception;

   void messageReceived(NextFilter var1, IoSession var2, Object var3) throws Exception;

   void messageSent(NextFilter var1, IoSession var2, WriteRequest var3) throws Exception;

   void filterClose(NextFilter var1, IoSession var2) throws Exception;

   void filterWrite(NextFilter var1, IoSession var2, WriteRequest var3) throws Exception;

   public interface NextFilter {
      void sessionCreated(IoSession var1);

      void sessionOpened(IoSession var1);

      void sessionClosed(IoSession var1);

      void sessionIdle(IoSession var1, IdleStatus var2);

      void exceptionCaught(IoSession var1, Throwable var2);

      void inputClosed(IoSession var1);

      void messageReceived(IoSession var1, Object var2);

      void messageSent(IoSession var1, WriteRequest var2);

      void filterWrite(IoSession var1, WriteRequest var2);

      void filterClose(IoSession var1);
   }
}
