package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

public interface SocketSessionConfig extends IoSessionConfig {
   boolean isReuseAddress();

   void setReuseAddress(boolean var1);

   int getReceiveBufferSize();

   void setReceiveBufferSize(int var1);

   int getSendBufferSize();

   void setSendBufferSize(int var1);

   int getTrafficClass();

   void setTrafficClass(int var1);

   boolean isKeepAlive();

   void setKeepAlive(boolean var1);

   boolean isOobInline();

   void setOobInline(boolean var1);

   int getSoLinger();

   void setSoLinger(int var1);

   boolean isTcpNoDelay();

   void setTcpNoDelay(boolean var1);
}
