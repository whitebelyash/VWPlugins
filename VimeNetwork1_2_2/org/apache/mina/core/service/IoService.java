/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.util.Map;
import java.util.Set;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.service.IoServiceStatistics;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.core.session.IoSessionDataStructureFactory;

public interface IoService {
    public TransportMetadata getTransportMetadata();

    public void addListener(IoServiceListener var1);

    public void removeListener(IoServiceListener var1);

    public boolean isDisposing();

    public boolean isDisposed();

    public void dispose();

    public void dispose(boolean var1);

    public IoHandler getHandler();

    public void setHandler(IoHandler var1);

    public Map<Long, IoSession> getManagedSessions();

    public int getManagedSessionCount();

    public IoSessionConfig getSessionConfig();

    public IoFilterChainBuilder getFilterChainBuilder();

    public void setFilterChainBuilder(IoFilterChainBuilder var1);

    public DefaultIoFilterChainBuilder getFilterChain();

    public boolean isActive();

    public long getActivationTime();

    public Set<WriteFuture> broadcast(Object var1);

    public IoSessionDataStructureFactory getSessionDataStructureFactory();

    public void setSessionDataStructureFactory(IoSessionDataStructureFactory var1);

    public int getScheduledWriteBytes();

    public int getScheduledWriteMessages();

    public IoServiceStatistics getStatistics();
}

