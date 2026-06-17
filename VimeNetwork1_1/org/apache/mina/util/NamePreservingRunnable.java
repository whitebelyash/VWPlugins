package org.apache.mina.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NamePreservingRunnable implements Runnable {
   private static final Logger LOGGER = LoggerFactory.getLogger(NamePreservingRunnable.class);
   private final String newName;
   private final Runnable runnable;

   public NamePreservingRunnable(Runnable runnable, String newName) {
      this.runnable = runnable;
      this.newName = newName;
   }

   public void run() {
      Thread currentThread = Thread.currentThread();
      String oldName = currentThread.getName();
      if (this.newName != null) {
         this.setName(currentThread, this.newName);
      }

      try {
         this.runnable.run();
      } finally {
         this.setName(currentThread, oldName);
      }

   }

   private void setName(Thread thread, String name) {
      try {
         thread.setName(name);
      } catch (SecurityException se) {
         if (LOGGER.isWarnEnabled()) {
            LOGGER.warn((String)"Failed to set the thread name.", (Throwable)se);
         }
      }

   }
}
