package org.apache.mina.core.future;

import java.util.concurrent.TimeUnit;
import org.apache.mina.core.session.IoSession;

public interface IoFuture {
   IoSession getSession();

   IoFuture await() throws InterruptedException;

   boolean await(long var1, TimeUnit var3) throws InterruptedException;

   boolean await(long var1) throws InterruptedException;

   IoFuture awaitUninterruptibly();

   boolean awaitUninterruptibly(long var1, TimeUnit var3);

   boolean awaitUninterruptibly(long var1);

   /** @deprecated */
   @Deprecated
   void join();

   /** @deprecated */
   @Deprecated
   boolean join(long var1);

   boolean isDone();

   IoFuture addListener(IoFutureListener var1);

   IoFuture removeListener(IoFutureListener var1);
}
