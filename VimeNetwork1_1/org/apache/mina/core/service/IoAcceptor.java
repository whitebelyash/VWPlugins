package org.apache.mina.core.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import org.apache.mina.core.session.IoSession;

public interface IoAcceptor extends IoService {
   SocketAddress getLocalAddress();

   Set getLocalAddresses();

   SocketAddress getDefaultLocalAddress();

   List getDefaultLocalAddresses();

   void setDefaultLocalAddress(SocketAddress var1);

   void setDefaultLocalAddresses(SocketAddress var1, SocketAddress... var2);

   void setDefaultLocalAddresses(Iterable var1);

   void setDefaultLocalAddresses(List var1);

   boolean isCloseOnDeactivation();

   void setCloseOnDeactivation(boolean var1);

   void bind() throws IOException;

   void bind(SocketAddress var1) throws IOException;

   void bind(SocketAddress var1, SocketAddress... var2) throws IOException;

   void bind(SocketAddress... var1) throws IOException;

   void bind(Iterable var1) throws IOException;

   void unbind();

   void unbind(SocketAddress var1);

   void unbind(SocketAddress var1, SocketAddress... var2);

   void unbind(Iterable var1);

   IoSession newSession(SocketAddress var1, SocketAddress var2);
}
