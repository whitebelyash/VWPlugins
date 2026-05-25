package org.apache.mina.core.future;

import java.util.EventListener;

public interface IoFutureListener extends EventListener {
   IoFutureListener CLOSE = new IoFutureListener() {
      public void operationComplete(IoFuture future) {
         future.getSession().closeNow();
      }
   };

   void operationComplete(IoFuture var1);
}
