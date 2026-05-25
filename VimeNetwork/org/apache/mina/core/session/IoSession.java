package org.apache.mina.core.session;

import java.net.SocketAddress;
import java.util.Set;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public interface IoSession {
   long getId();

   IoService getService();

   IoHandler getHandler();

   IoSessionConfig getConfig();

   IoFilterChain getFilterChain();

   WriteRequestQueue getWriteRequestQueue();

   TransportMetadata getTransportMetadata();

   ReadFuture read();

   WriteFuture write(Object var1);

   WriteFuture write(Object var1, SocketAddress var2);

   /** @deprecated */
   CloseFuture close(boolean var1);

   CloseFuture closeNow();

   CloseFuture closeOnFlush();

   /** @deprecated */
   @Deprecated
   CloseFuture close();

   /** @deprecated */
   @Deprecated
   Object getAttachment();

   /** @deprecated */
   @Deprecated
   Object setAttachment(Object var1);

   Object getAttribute(Object var1);

   Object getAttribute(Object var1, Object var2);

   Object setAttribute(Object var1, Object var2);

   Object setAttribute(Object var1);

   Object setAttributeIfAbsent(Object var1, Object var2);

   Object setAttributeIfAbsent(Object var1);

   Object removeAttribute(Object var1);

   boolean removeAttribute(Object var1, Object var2);

   boolean replaceAttribute(Object var1, Object var2, Object var3);

   boolean containsAttribute(Object var1);

   Set getAttributeKeys();

   boolean isConnected();

   boolean isActive();

   boolean isClosing();

   boolean isSecured();

   CloseFuture getCloseFuture();

   SocketAddress getRemoteAddress();

   SocketAddress getLocalAddress();

   SocketAddress getServiceAddress();

   void setCurrentWriteRequest(WriteRequest var1);

   void suspendRead();

   void suspendWrite();

   void resumeRead();

   void resumeWrite();

   boolean isReadSuspended();

   boolean isWriteSuspended();

   void updateThroughput(long var1, boolean var3);

   long getReadBytes();

   long getWrittenBytes();

   long getReadMessages();

   long getWrittenMessages();

   double getReadBytesThroughput();

   double getWrittenBytesThroughput();

   double getReadMessagesThroughput();

   double getWrittenMessagesThroughput();

   int getScheduledWriteMessages();

   long getScheduledWriteBytes();

   Object getCurrentWriteMessage();

   WriteRequest getCurrentWriteRequest();

   long getCreationTime();

   long getLastIoTime();

   long getLastReadTime();

   long getLastWriteTime();

   boolean isIdle(IdleStatus var1);

   boolean isReaderIdle();

   boolean isWriterIdle();

   boolean isBothIdle();

   int getIdleCount(IdleStatus var1);

   int getReaderIdleCount();

   int getWriterIdleCount();

   int getBothIdleCount();

   long getLastIdleTime(IdleStatus var1);

   long getLastReaderIdleTime();

   long getLastWriterIdleTime();

   long getLastBothIdleTime();
}
