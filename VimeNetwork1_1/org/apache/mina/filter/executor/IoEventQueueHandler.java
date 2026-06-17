package org.apache.mina.filter.executor;

import java.util.EventListener;
import org.apache.mina.core.session.IoEvent;

public interface IoEventQueueHandler extends EventListener {
   IoEventQueueHandler NOOP = new IoEventQueueHandler() {
      public boolean accept(Object source, IoEvent event) {
         return true;
      }

      public void offered(Object source, IoEvent event) {
      }

      public void polled(Object source, IoEvent event) {
      }
   };

   boolean accept(Object var1, IoEvent var2);

   void offered(Object var1, IoEvent var2);

   void polled(Object var1, IoEvent var2);
}
