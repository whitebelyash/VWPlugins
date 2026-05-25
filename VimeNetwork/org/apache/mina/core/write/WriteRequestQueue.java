package org.apache.mina.core.write;

import org.apache.mina.core.session.IoSession;

public interface WriteRequestQueue {
   WriteRequest poll(IoSession var1);

   void offer(IoSession var1, WriteRequest var2);

   boolean isEmpty(IoSession var1);

   void clear(IoSession var1);

   void dispose(IoSession var1);

   int size();
}
