package org.apache.mina.core.service;

import java.util.Map;
import java.util.Set;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionDataStructureFactory;

public interface IoService {
   TransportMetadata getTransportMetadata();

   void addListener(IoServiceListener var1);

   void removeListener(IoServiceListener var1);

   boolean isDisposing();

   boolean isDisposed();

   void dispose();

   void dispose(boolean var1);

   IoHandler getHandler();

   void setHandler(IoHandler var1);

   Map getManagedSessions();

   int getManagedSessionCount();

   IoSessionConfig getSessionConfig();

   IoFilterChainBuilder getFilterChainBuilder();

   void setFilterChainBuilder(IoFilterChainBuilder var1);

   DefaultIoFilterChainBuilder getFilterChain();

   boolean isActive();

   long getActivationTime();

   Set broadcast(Object var1);

   IoSessionDataStructureFactory getSessionDataStructureFactory();

   void setSessionDataStructureFactory(IoSessionDataStructureFactory var1);

   int getScheduledWriteBytes();

   int getScheduledWriteMessages();

   IoServiceStatistics getStatistics();
}
