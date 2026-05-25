package net.xtrafrancyz.VimeNetwork.api;

public interface Metrics {
   default void add(String key) {
      this.add(key, 1);
   }

   void add(String var1, int var2);
}
