package org.apache.mina.core.future;

public interface WriteFuture extends IoFuture {
   boolean isWritten();

   Throwable getException();

   void setWritten();

   void setException(Throwable var1);

   WriteFuture await() throws InterruptedException;

   WriteFuture awaitUninterruptibly();

   WriteFuture addListener(IoFutureListener var1);

   WriteFuture removeListener(IoFutureListener var1);
}
