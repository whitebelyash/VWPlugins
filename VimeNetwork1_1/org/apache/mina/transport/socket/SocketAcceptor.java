package org.apache.mina.transport.socket;

import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoAcceptor;

public interface SocketAcceptor extends IoAcceptor {
   InetSocketAddress getLocalAddress();

   InetSocketAddress getDefaultLocalAddress();

   void setDefaultLocalAddress(InetSocketAddress var1);

   boolean isReuseAddress();

   void setReuseAddress(boolean var1);

   int getBacklog();

   void setBacklog(int var1);

   SocketSessionConfig getSessionConfig();
}
