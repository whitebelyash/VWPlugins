/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.net.SocketAddress;
import java.util.Set;
import org.apache.mina.core.session.IoSessionConfig;

public interface TransportMetadata {
    public String getProviderName();

    public String getName();

    public boolean isConnectionless();

    public boolean hasFragmentation();

    public Class<? extends SocketAddress> getAddressType();

    public Set<Class<? extends Object>> getEnvelopeTypes();

    public Class<? extends IoSessionConfig> getSessionConfigType();
}

