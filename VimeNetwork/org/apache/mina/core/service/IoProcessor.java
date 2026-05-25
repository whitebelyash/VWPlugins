package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoProcessor {
   boolean isDisposing();

   boolean isDisposed();

   void dispose();

   void add(IoSession var1);

   void flush(IoSession var1);

   void write(IoSession var1, WriteRequest var2);

   void updateTrafficControl(IoSession var1);

   void remove(IoSession var1);
}
