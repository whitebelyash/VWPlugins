package org.apache.mina.transport.socket;

import org.apache.mina.core.session.IoSessionConfig;

public interface DatagramSessionConfig extends IoSessionConfig {
   boolean isBroadcast();

   void setBroadcast(boolean var1);

   boolean isReuseAddress();

   void setReuseAddress(boolean var1);

   int getReceiveBufferSize();

   void setReceiveBufferSize(int var1);

   int getSendBufferSize();

   void setSendBufferSize(int var1);

   int getTrafficClass();

   void setTrafficClass(int var1);

   boolean isCloseOnPortUnreachable();

   void setCloseOnPortUnreachable(boolean var1);
}
