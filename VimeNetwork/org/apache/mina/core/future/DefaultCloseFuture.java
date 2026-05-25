package org.apache.mina.core.future;

import org.apache.mina.core.session.IoSession;

public class DefaultCloseFuture extends DefaultIoFuture implements CloseFuture {
   public DefaultCloseFuture(IoSession session) {
      super(session);
   }

   public boolean isClosed() {
      return this.isDone() ? (Boolean)this.getValue() : false;
   }

   public void setClosed() {
      this.setValue(Boolean.TRUE);
   }

   public CloseFuture await() throws InterruptedException {
      return (CloseFuture)super.await();
   }

   public CloseFuture awaitUninterruptibly() {
      return (CloseFuture)super.awaitUninterruptibly();
   }

   public CloseFuture addListener(IoFutureListener listener) {
      return (CloseFuture)super.addListener(listener);
   }

   public CloseFuture removeListener(IoFutureListener listener) {
      return (CloseFuture)super.removeListener(listener);
   }
}
