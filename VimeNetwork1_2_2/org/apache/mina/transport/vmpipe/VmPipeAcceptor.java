/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.transport.vmpipe;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatusChecker;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.vmpipe.DefaultVmPipeSessionConfig;
import org.apache.mina.transport.vmpipe.VmPipe;
import org.apache.mina.transport.vmpipe.VmPipeAddress;
import org.apache.mina.transport.vmpipe.VmPipeSession;
import org.apache.mina.transport.vmpipe.VmPipeSessionConfig;

public final class VmPipeAcceptor
extends AbstractIoAcceptor {
    private IdleStatusChecker idleChecker = new IdleStatusChecker();
    static final Map<VmPipeAddress, VmPipe> boundHandlers = new HashMap<VmPipeAddress, VmPipe>();

    public VmPipeAcceptor() {
        this(null);
    }

    public VmPipeAcceptor(Executor executor) {
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
    public VmPipeAddress getLocalAddress() {
        return (VmPipeAddress)super.getLocalAddress();
    }

    @Override
    public VmPipeAddress getDefaultLocalAddress() {
        return (VmPipeAddress)super.getDefaultLocalAddress();
    }

    public void setDefaultLocalAddress(VmPipeAddress localAddress) {
        super.setDefaultLocalAddress(localAddress);
    }

    @Override
    protected void dispose0() throws Exception {
        this.idleChecker.getNotifyingTask().cancel();
        this.unbind();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Set<SocketAddress> bindInternal(List<? extends SocketAddress> localAddresses) throws IOException {
        HashSet<SocketAddress> newLocalAddresses = new HashSet<SocketAddress>();
        Map<VmPipeAddress, VmPipe> map = boundHandlers;
        synchronized (map) {
            VmPipeAddress localAddress;
            for (SocketAddress socketAddress : localAddresses) {
                localAddress = (VmPipeAddress)socketAddress;
                if (localAddress == null || localAddress.getPort() == 0) {
                    localAddress = null;
                    for (int i = 10000; i < Integer.MAX_VALUE; ++i) {
                        VmPipeAddress newLocalAddress = new VmPipeAddress(i);
                        if (boundHandlers.containsKey(newLocalAddress) || newLocalAddresses.contains(newLocalAddress)) continue;
                        localAddress = newLocalAddress;
                        break;
                    }
                    if (localAddress == null) {
                        throw new IOException("No port available.");
                    }
                } else {
                    if (localAddress.getPort() < 0) {
                        throw new IOException("Bind port number must be 0 or above.");
                    }
                    if (boundHandlers.containsKey(localAddress)) {
                        throw new IOException("Address already bound: " + localAddress);
                    }
                }
                newLocalAddresses.add(localAddress);
            }
            for (SocketAddress socketAddress : newLocalAddresses) {
                localAddress = (VmPipeAddress)socketAddress;
                if (!boundHandlers.containsKey(localAddress)) {
                    boundHandlers.put(localAddress, new VmPipe(this, localAddress, this.getHandler(), this.getListeners()));
                    continue;
                }
                for (SocketAddress a2 : newLocalAddresses) {
                    boundHandlers.remove(a2);
                }
                throw new IOException("Duplicate local address: " + socketAddress);
            }
        }
        return newLocalAddresses;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void unbind0(List<? extends SocketAddress> localAddresses) {
        Map<VmPipeAddress, VmPipe> map = boundHandlers;
        synchronized (map) {
            for (SocketAddress socketAddress : localAddresses) {
                boundHandlers.remove(socketAddress);
            }
        }
    }

    @Override
    public IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
        throw new UnsupportedOperationException();
    }

    void doFinishSessionInitialization(IoSession session, IoFuture future) {
        this.initSession(session, future, null);
    }
}

