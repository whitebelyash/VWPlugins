package org.apache.mina.core.future;

import org.apache.mina.core.session.IoSession;

public interface ConnectFuture extends IoFuture {
   IoSession getSession();

   Throwable getException();

   boolean isConnected();

   boolean isCanceled();

   void setSession(IoSession var1);

   void setException(Throwable var1);

   boolean cancel();

   ConnectFuture await() throws InterruptedException;

   ConnectFuture awaitUninterruptibly();

   ConnectFuture addListener(IoFutureListener var1);

   ConnectFuture removeListener(IoFutureListener var1);
}
