package org.apache.mina.core.session;

import org.apache.mina.core.future.IoFuture;

public interface IoSessionInitializer {
   void initializeSession(IoSession var1, IoFuture var2);
}
