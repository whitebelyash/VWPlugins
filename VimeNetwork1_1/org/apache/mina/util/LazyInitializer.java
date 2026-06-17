package org.apache.mina.util;

public abstract class LazyInitializer {
   private Object value;

   public abstract Object init();

   public Object get() {
      if (this.value == null) {
         this.value = this.init();
      }

      return this.value;
   }
}
