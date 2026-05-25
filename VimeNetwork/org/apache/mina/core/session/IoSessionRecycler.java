package org.apache.mina.core.session;

import java.net.SocketAddress;

public interface IoSessionRecycler {
   IoSessionRecycler NOOP = new IoSessionRecycler() {
      public void put(IoSession session) {
      }

      public IoSession recycle(SocketAddress remoteAddress) {
         return null;
      }

      public void remove(IoSession session) {
      }
   };

   void put(IoSession var1);

   IoSession recycle(SocketAddress var1);

   void remove(IoSession var1);
}
