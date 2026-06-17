/*
 * Decompiled with CFR 0.152.
 */
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
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;

public interface IoSession {
    public long getId();

    public IoService getService();

    public IoHandler getHandler();

    public IoSessionConfig getConfig();

    public IoFilterChain getFilterChain();

    public WriteRequestQueue getWriteRequestQueue();

    public TransportMetadata getTransportMetadata();

    public ReadFuture read();

    public WriteFuture write(Object var1);

    public WriteFuture write(Object var1, SocketAddress var2);

    public CloseFuture close(boolean var1);

    public CloseFuture closeNow();

    public CloseFuture closeOnFlush();

    @Deprecated
    public CloseFuture close();

    @Deprecated
    public Object getAttachment();

    @Deprecated
    public Object setAttachment(Object var1);

    public Object getAttribute(Object var1);

    public Object getAttribute(Object var1, Object var2);

    public Object setAttribute(Object var1, Object var2);

    public Object setAttribute(Object var1);

    public Object setAttributeIfAbsent(Object var1, Object var2);

    public Object setAttributeIfAbsent(Object var1);

    public Object removeAttribute(Object var1);

    public boolean removeAttribute(Object var1, Object var2);

    public boolean replaceAttribute(Object var1, Object var2, Object var3);

    public boolean containsAttribute(Object var1);

    public Set<Object> getAttributeKeys();

    public boolean isConnected();

    public boolean isActive();

    public boolean isClosing();

    public boolean isSecured();

    public CloseFuture getCloseFuture();

    public SocketAddress getRemoteAddress();

    public SocketAddress getLocalAddress();

    public SocketAddress getServiceAddress();

    public void setCurrentWriteRequest(WriteRequest var1);

    public void suspendRead();

    public void suspendWrite();

    public void resumeRead();

    public void resumeWrite();

    public boolean isReadSuspended();

    public boolean isWriteSuspended();

    public void updateThroughput(long var1, boolean var3);

    public long getReadBytes();

    public long getWrittenBytes();

    public long getReadMessages();

    public long getWrittenMessages();

    public double getReadBytesThroughput();

    public double getWrittenBytesThroughput();

    public double getReadMessagesThroughput();

    public double getWrittenMessagesThroughput();

    public int getScheduledWriteMessages();

    public long getScheduledWriteBytes();

    public Object getCurrentWriteMessage();

    public WriteRequest getCurrentWriteRequest();

    public long getCreationTime();

    public long getLastIoTime();

    public long getLastReadTime();

    public long getLastWriteTime();

    public boolean isIdle(IdleStatus var1);

    public boolean isReaderIdle();

    public boolean isWriterIdle();

    public boolean isBothIdle();

    public int getIdleCount(IdleStatus var1);

    public int getReaderIdleCount();

    public int getWriterIdleCount();

    public int getBothIdleCount();

    public long getLastIdleTime(IdleStatus var1);

    public long getLastReaderIdleTime();

    public long getLastWriterIdleTime();

    public long getLastBothIdleTime();
}

