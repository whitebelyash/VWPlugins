package org.apache.mina.core.future;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.mina.core.session.IoSession;

public class CompositeIoFuture extends DefaultIoFuture {
   private final NotifyingListener listener = new NotifyingListener();
   private final AtomicInteger unnotified = new AtomicInteger();
   private volatile boolean constructionFinished;

   public CompositeIoFuture(Iterable children) {
      super((IoSession)null);

      for(IoFuture f : children) {
         f.addListener(this.listener);
         this.unnotified.incrementAndGet();
      }

      this.constructionFinished = true;
      if (this.unnotified.get() == 0) {
         this.setValue(true);
      }

   }

   private class NotifyingListener implements IoFutureListener {
      private NotifyingListener() {
      }

      public void operationComplete(IoFuture future) {
         if (CompositeIoFuture.this.unnotified.decrementAndGet() == 0 && CompositeIoFuture.this.constructionFinished) {
            CompositeIoFuture.this.setValue(true);
         }

      }
   }
}
