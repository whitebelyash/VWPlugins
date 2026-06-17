/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.vmpipe;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.AbstractIoConnector;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatusChecker;
import org.apache.mina.core.session.IoSessionInitializer;
import org.apache.mina.transport.vmpipe.DefaultVmPipeSessionConfig;
import org.apache.mina.transport.vmpipe.VmPipe;
import org.apache.mina.transport.vmpipe.VmPipeAcceptor;
import org.apache.mina.transport.vmpipe.VmPipeAddress;
import org.apache.mina.transport.vmpipe.VmPipeFilterChain;
import org.apache.mina.transport.vmpipe.VmPipeSession;
import org.apache.mina.transport.vmpipe.VmPipeSessionConfig;
import org.apache.mina.util.ExceptionMonitor;

public final class VmPipeConnector
extends AbstractIoConnector {
    private IdleStatusChecker idleChecker = new IdleStatusChecker();
    private static final Set<VmPipeAddress> TAKEN_LOCAL_ADDRESSES = new HashSet<VmPipeAddress>();
    private static int nextLocalPort = -1;
    private static final IoFutureListener<IoFuture> LOCAL_ADDRESS_RECLAIMER = new LocalAddressReclaimer();

    public VmPipeConnector() {
        this(null);
    }

    public VmPipeConnector(Executor executor) {
        super(new DefaultVmPipeSessionConfig(), executor);
        this.executeWorker(this.idleChecker.getNotifyingTask(), "idleStatusChecker");
    }

    @Override
    public TransportMetadata getTransportMetadata() {
        return VmPipeSession.METADATA;
    }

    @Override
    public VmPipeSessionConfig getSessionConfig() {
        return (VmPipeSessionConfig)this.sessionConfig;
    }

    @Override
    protected ConnectFuture connect0(SocketAddress remoteAddress, SocketAddress localAddress, IoSessionInitializer<? extends ConnectFuture> sessionInitializer) {
        VmPipeAddress actualLocalAddress;
        VmPipe entry = VmPipeAcceptor.boundHandlers.get(remoteAddress);
        if (entry == null) {
            return DefaultConnectFuture.newFailedFuture(new IOException("Endpoint unavailable: " + remoteAddress));
        }
        DefaultConnectFuture future = new DefaultConnectFuture();
        try {
            actualLocalAddress = VmPipeConnector.nextLocalAddress();
        }
        catch (IOException e) {
            return DefaultConnectFuture.newFailedFuture(e);
        }
        VmPipeSession localSession = new VmPipeSession(this, this.getListeners(), actualLocalAddress, this.getHandler(), entry);
        this.initSession(localSession, future, sessionInitializer);
        localSession.getCloseFuture().addListener(LOCAL_ADDRESS_RECLAIMER);
        try {
            IoFilterChain filterChain = localSession.getFilterChain();
            this.getFilterChainBuilder().buildFilterChain(filterChain);
            this.getListeners().fireSessionCreated(localSession);
            this.idleChecker.addSession(localSession);
        }
        catch (Exception e) {
            future.setException(e);
            return future;
        }
        VmPipeSession remoteSession = localSession.getRemoteSession();
        ((VmPipeAcceptor)remoteSession.getService()).doFinishSessionInitialization(remoteSession, null);
        try {
            IoFilterChain filterChain = remoteSession.getFilterChain();
            entry.getAcceptor().getFilterChainBuilder().buildFilterChain(filterChain);
            entry.getListeners().fireSessionCreated(remoteSession);
            this.idleChecker.addSession(remoteSession);
        }
        catch (Exception e) {
            ExceptionMonitor.getInstance().exceptionCaught(e);
            remoteSession.closeNow();
        }
        ((VmPipeFilterChain)localSession.getFilterChain()).start();
        ((VmPipeFilterChain)remoteSession.getFilterChain()).start();
        return future;
    }

    @Override
    protected void dispose0() throws Exception {
        this.idleChecker.getNotifyingTask().cancel();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static VmPipeAddress nextLocalAddress() throws IOException {
        Set<VmPipeAddress> set = TAKEN_LOCAL_ADDRESSES;
        synchronized (set) {
            if (nextLocalPort >= 0) {
                nextLocalPort = -1;
            }
            for (int i = 0; i < Integer.MAX_VALUE; ++i) {
                VmPipeAddress answer;
                if (TAKEN_LOCAL_ADDRESSES.contains(answer = new VmPipeAddress(nextLocalPort--))) continue;
                TAKEN_LOCAL_ADDRESSES.add(answer);
                return answer;
            }
        }
        throw new IOException("Can't assign a local VM pipe port.");
    }

    private static class LocalAddressReclaimer
    implements IoFutureListener<IoFuture> {
        private LocalAddressReclaimer() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void operationComplete(IoFuture future) {
            Set set = TAKEN_LOCAL_ADDRESSES;
            synchronized (set) {
                TAKEN_LOCAL_ADDRESSES.remove(future.getSession().getLocalAddress());
            }
        }
    }
}

