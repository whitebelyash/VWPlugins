package org.apache.mina.core.filterchain;

public interface IoFilterChainBuilder {
   IoFilterChainBuilder NOOP = new IoFilterChainBuilder() {
      public void buildFilterChain(IoFilterChain chain) throws Exception {
      }

      public String toString() {
         return "NOOP";
      }
   };

   void buildFilterChain(IoFilterChain var1) throws Exception;
}
