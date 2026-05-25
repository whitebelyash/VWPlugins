package org.apache.mina.core.future;

public interface ReadFuture extends IoFuture {
   Object getMessage();

   boolean isRead();

   boolean isClosed();

   Throwable getException();

   void setRead(Object var1);

   void setClosed();

   void setException(Throwable var1);

   ReadFuture await() throws InterruptedException;

   ReadFuture awaitUninterruptibly();

   ReadFuture addListener(IoFutureListener var1);

   ReadFuture removeListener(IoFutureListener var1);
}
