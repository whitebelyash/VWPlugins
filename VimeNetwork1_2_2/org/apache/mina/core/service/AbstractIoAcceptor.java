/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSessionConfig;

public abstract class AbstractIoAcceptor
extends AbstractIoService
implements IoAcceptor {
    private final List<SocketAddress> defaultLocalAddresses = new ArrayList<SocketAddress>();
    private final List<SocketAddress> unmodifiableDefaultLocalAddresses = Collections.unmodifiableList(this.defaultLocalAddresses);
    private final Set<SocketAddress> boundAddresses = new HashSet<SocketAddress>();
    private boolean disconnectOnUnbind = true;
    protected final Object bindLock = new Object();

    protected AbstractIoAcceptor(IoSessionConfig sessionConfig, Executor executor) {
        super(sessionConfig, executor);
        this.defaultLocalAddresses.add(null);
    }

    @Override
    public SocketAddress getLocalAddress() {
        Set<SocketAddress> localAddresses = this.getLocalAddresses();
        if (localAddresses.isEmpty()) {
            return null;
        }
        return localAddresses.iterator().next();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final Set<SocketAddress> getLocalAddresses() {
        HashSet<SocketAddress> localAddresses = new HashSet<SocketAddress>();
        Set<SocketAddress> set = this.boundAddresses;
        synchronized (set) {
            localAddresses.addAll(this.boundAddresses);
        }
        return localAddresses;
    }

    @Override
    public SocketAddress getDefaultLocalAddress() {
        if (this.defaultLocalAddresses.isEmpty()) {
            return null;
        }
        return this.defaultLocalAddresses.iterator().next();
    }

    @Override
    public final void setDefaultLocalAddress(SocketAddress localAddress) {
        this.setDefaultLocalAddresses(localAddress, new SocketAddress[0]);
    }

    @Override
    public final List<SocketAddress> getDefaultLocalAddresses() {
        return this.unmodifiableDefaultLocalAddresses;
    }

    @Override
    public final void setDefaultLocalAddresses(List<? extends SocketAddress> localAddresses) {
        if (localAddresses == null) {
            throw new IllegalArgumentException("localAddresses");
        }
        this.setDefaultLocalAddresses((Iterable<? extends SocketAddress>)localAddresses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void setDefaultLocalAddresses(Iterable<? extends SocketAddress> localAddresses) {
        if (localAddresses == null) {
            throw new IllegalArgumentException("localAddresses");
        }
        Object object = this.bindLock;
        synchronized (object) {
            Set<SocketAddress> set = this.boundAddresses;
            synchronized (set) {
                if (!this.boundAddresses.isEmpty()) {
                    throw new IllegalStateException("localAddress can't be set while the acceptor is bound.");
                }
                ArrayList<SocketAddress> newLocalAddresses = new ArrayList<SocketAddress>();
                for (SocketAddress socketAddress : localAddresses) {
                    this.checkAddressType(socketAddress);
                    newLocalAddresses.add(socketAddress);
                }
                if (newLocalAddresses.isEmpty()) {
                    throw new IllegalArgumentException("empty localAddresses");
                }
                this.defaultLocalAddresses.clear();
                this.defaultLocalAddresses.addAll(newLocalAddresses);
            }
        }
    }

    @Override
    public final void setDefaultLocalAddresses(SocketAddress firstLocalAddress, SocketAddress ... otherLocalAddresses) {
        if (otherLocalAddresses == null) {
            otherLocalAddresses = new SocketAddress[]{};
        }
        ArrayList<SocketAddress> newLocalAddresses = new ArrayList<SocketAddress>(otherLocalAddresses.length + 1);
        newLocalAddresses.add(firstLocalAddress);
        for (SocketAddress a : otherLocalAddresses) {
            newLocalAddresses.add(a);
        }
        this.setDefaultLocalAddresses((Iterable<? extends SocketAddress>)newLocalAddresses);
    }

    @Override
    public final boolean isCloseOnDeactivation() {
        return this.disconnectOnUnbind;
    }

    @Override
    public final void setCloseOnDeactivation(boolean disconnectClientsOnUnbind) {
        this.disconnectOnUnbind = disconnectClientsOnUnbind;
    }

    @Override
    public final void bind() throws IOException {
        this.bind(this.getDefaultLocalAddresses());
    }

    @Override
    public final void bind(SocketAddress localAddress) throws IOException {
        if (localAddress == null) {
            throw new IllegalArgumentException("localAddress");
        }
        ArrayList<SocketAddress> localAddresses = new ArrayList<SocketAddress>(1);
        localAddresses.add(localAddress);
        this.bind(localAddresses);
    }

    @Override
    public final void bind(SocketAddress ... addresses) throws IOException {
        if (addresses == null || addresses.length == 0) {
            this.bind(this.getDefaultLocalAddresses());
            return;
        }
        ArrayList<SocketAddress> localAddresses = new ArrayList<SocketAddress>(2);
        for (SocketAddress address : addresses) {
            localAddresses.add(address);
        }
        this.bind(localAddresses);
    }

    @Override
    public final void bind(SocketAddress firstLocalAddress, SocketAddress ... addresses) throws IOException {
        if (firstLocalAddress == null) {
            this.bind(this.getDefaultLocalAddresses());
        }
        if (addresses == null || addresses.length == 0) {
            this.bind(this.getDefaultLocalAddresses());
            return;
        }
        ArrayList<SocketAddress> localAddresses = new ArrayList<SocketAddress>(2);
        localAddresses.add(firstLocalAddress);
        for (SocketAddress address : addresses) {
            localAddresses.add(address);
        }
        this.bind(localAddresses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void bind(Iterable<? extends SocketAddress> localAddresses) throws IOException {
        if (this.isDisposing()) {
            throw new IllegalStateException("The Accpetor disposed is being disposed.");
        }
        if (localAddresses == null) {
            throw new IllegalArgumentException("localAddresses");
        }
        ArrayList<SocketAddress> localAddressesCopy = new ArrayList<SocketAddress>();
        for (SocketAddress object : localAddresses) {
            this.checkAddressType(object);
            localAddressesCopy.add(object);
        }
        if (localAddressesCopy.isEmpty()) {
            throw new IllegalArgumentException("localAddresses is empty.");
        }
        boolean activate = false;
        Object object = this.bindLock;
        synchronized (object) {
            Set<SocketAddress> set = this.boundAddresses;
            synchronized (set) {
                if (this.boundAddresses.isEmpty()) {
                    activate = true;
                }
            }
            if (this.getHandler() == null) {
                throw new IllegalStateException("handler is not set.");
            }
            try {
                Set<SocketAddress> addresses = this.bindInternal(localAddressesCopy);
                Set<SocketAddress> set2 = this.boundAddresses;
                synchronized (set2) {
                    this.boundAddresses.addAll(addresses);
                }
            }
            catch (IOException e) {
                throw e;
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeIoException("Failed to bind to: " + this.getLocalAddresses(), e);
            }
        }
        if (activate) {
            this.getListeners().fireServiceActivated();
        }
    }

    @Override
    public final void unbind() {
        this.unbind(this.getLocalAddresses());
    }

    @Override
    public final void unbind(SocketAddress localAddress) {
        if (localAddress == null) {
            throw new IllegalArgumentException("localAddress");
        }
        ArrayList<SocketAddress> localAddresses = new ArrayList<SocketAddress>(1);
        localAddresses.add(localAddress);
        this.unbind(localAddresses);
    }

    @Override
    public final void unbind(SocketAddress firstLocalAddress, SocketAddress ... otherLocalAddresses) {
        if (firstLocalAddress == null) {
            throw new IllegalArgumentException("firstLocalAddress");
        }
        if (otherLocalAddresses == null) {
            throw new IllegalArgumentException("otherLocalAddresses");
        }
        ArrayList<SocketAddress> localAddresses = new ArrayList<SocketAddress>();
        localAddresses.add(firstLocalAddress);
        Collections.addAll(localAddresses, otherLocalAddresses);
        this.unbind(localAddresses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void unbind(Iterable<? extends SocketAddress> localAddresses) {
        if (localAddresses == null) {
            throw new IllegalArgumentException("localAddresses");
        }
        boolean deactivate = false;
        Object object = this.bindLock;
        synchronized (object) {
            Set<SocketAddress> set = this.boundAddresses;
            synchronized (set) {
                if (this.boundAddresses.isEmpty()) {
                    return;
                }
                ArrayList<SocketAddress> localAddressesCopy = new ArrayList<SocketAddress>();
                int specifiedAddressCount = 0;
                for (SocketAddress socketAddress : localAddresses) {
                    ++specifiedAddressCount;
                    if (socketAddress == null || !this.boundAddresses.contains(socketAddress)) continue;
                    localAddressesCopy.add(socketAddress);
                }
                if (specifiedAddressCount == 0) {
                    throw new IllegalArgumentException("localAddresses is empty.");
                }
                if (!localAddressesCopy.isEmpty()) {
                    try {
                        this.unbind0(localAddressesCopy);
                    }
                    catch (RuntimeException e) {
                        throw e;
                    }
                    catch (Exception e) {
                        throw new RuntimeIoException("Failed to unbind from: " + this.getLocalAddresses(), e);
                    }
                    this.boundAddresses.removeAll(localAddressesCopy);
                    if (this.boundAddresses.isEmpty()) {
                        deactivate = true;
                    }
                }
            }
        }
        if (deactivate) {
            this.getListeners().fireServiceDeactivated();
        }
    }

    protected abstract Set<SocketAddress> bindInternal(List<? extends SocketAddress> var1) throws Exception;

    protected abstract void unbind0(List<? extends SocketAddress> var1) throws Exception;

    public String toString() {
        TransportMetadata m = this.getTransportMetadata();
        return '(' + m.getProviderName() + ' ' + m.getName() + " acceptor: " + (this.isActive() ? "localAddress(es): " + this.getLocalAddresses() + ", managedSessionCount: " + this.getManagedSessionCount() : "not bound") + ')';
    }

    private void checkAddressType(SocketAddress a) {
        if (a != null && !this.getTransportMetadata().getAddressType().isAssignableFrom(a.getClass())) {
            throw new IllegalArgumentException("localAddress type: " + a.getClass().getSimpleName() + " (expected: " + this.getTransportMetadata().getAddressType().getSimpleName() + ")");
        }
    }

    public static class AcceptorOperationFuture
    extends AbstractIoService.ServiceOperationFuture {
        private final List<SocketAddress> localAddresses;

        public AcceptorOperationFuture(List<? extends SocketAddress> localAddresses) {
            this.localAddresses = new ArrayList<SocketAddress>(localAddresses);
        }

        public final List<SocketAddress> getLocalAddresses() {
            return Collections.unmodifiableList(this.localAddresses);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Acceptor operation : ");
            if (this.localAddresses != null) {
                boolean isFirst = true;
                for (SocketAddress address : this.localAddresses) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(address);
                }
            }
            return sb.toString();
        }
    }
}

