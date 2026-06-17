/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Set;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.util.IdentityHashSet;

public class DefaultTransportMetadata
implements TransportMetadata {
    private final String providerName;
    private final String name;
    private final boolean connectionless;
    private final boolean fragmentation;
    private final Class<? extends SocketAddress> addressType;
    private final Class<? extends IoSessionConfig> sessionConfigType;
    private final Set<Class<? extends Object>> envelopeTypes;

    public DefaultTransportMetadata(String providerName, String name, boolean connectionless, boolean fragmentation, Class<? extends SocketAddress> addressType, Class<? extends IoSessionConfig> sessionConfigType, Class<?> ... envelopeTypes) {
        if (providerName == null) {
            throw new IllegalArgumentException("providerName");
        }
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        if ((providerName = providerName.trim().toLowerCase()).length() == 0) {
            throw new IllegalArgumentException("providerName is empty.");
        }
        if ((name = name.trim().toLowerCase()).length() == 0) {
            throw new IllegalArgumentException("name is empty.");
        }
        if (addressType == null) {
            throw new IllegalArgumentException("addressType");
        }
        if (envelopeTypes == null) {
            throw new IllegalArgumentException("envelopeTypes");
        }
        if (envelopeTypes.length == 0) {
            throw new IllegalArgumentException("envelopeTypes is empty.");
        }
        if (sessionConfigType == null) {
            throw new IllegalArgumentException("sessionConfigType");
        }
        this.providerName = providerName;
        this.name = name;
        this.connectionless = connectionless;
        this.fragmentation = fragmentation;
        this.addressType = addressType;
        this.sessionConfigType = sessionConfigType;
        IdentityHashSet newEnvelopeTypes = new IdentityHashSet();
        for (Class<?> c : envelopeTypes) {
            newEnvelopeTypes.add(c);
        }
        this.envelopeTypes = Collections.unmodifiableSet(newEnvelopeTypes);
    }

    @Override
    public Class<? extends SocketAddress> getAddressType() {
        return this.addressType;
    }

    @Override
    public Set<Class<? extends Object>> getEnvelopeTypes() {
        return this.envelopeTypes;
    }

    @Override
    public Class<? extends IoSessionConfig> getSessionConfigType() {
        return this.sessionConfigType;
    }

    @Override
    public String getProviderName() {
        return this.providerName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isConnectionless() {
        return this.connectionless;
    }

    @Override
    public boolean hasFragmentation() {
        return this.fragmentation;
    }

    public String toString() {
        return this.name;
    }
}

