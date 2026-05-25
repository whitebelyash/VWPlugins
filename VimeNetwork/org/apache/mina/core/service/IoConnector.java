package org.apache.mina.core.service;

import java.net.SocketAddress;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSessionInitializer;

public interface IoConnector extends IoService {
   /** @deprecated */
   int getConnectTimeout();

   long getConnectTimeoutMillis();

   /** @deprecated */
   void setConnectTimeout(int var1);

   void setConnectTimeoutMillis(long var1);

   SocketAddress getDefaultRemoteAddress();

   void setDefaultRemoteAddress(SocketAddress var1);

   SocketAddress getDefaultLocalAddress();

   void setDefaultLocalAddress(SocketAddress var1);

   ConnectFuture connect();

   ConnectFuture connect(IoSessionInitializer var1);

   ConnectFuture connect(SocketAddress var1);

   ConnectFuture connect(SocketAddress var1, IoSessionInitializer var2);

   ConnectFuture connect(SocketAddress var1, SocketAddress var2);

   ConnectFuture connect(SocketAddress var1, SocketAddress var2, IoSessionInitializer var3);
}
