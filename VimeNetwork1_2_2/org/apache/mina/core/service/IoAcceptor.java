/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;
import java.util.Set;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.session.IoSession;

public interface IoAcceptor
extends IoService {
    public SocketAddress getLocalAddress();

    public Set<SocketAddress> getLocalAddresses();

    public SocketAddress getDefaultLocalAddress();

    public List<SocketAddress> getDefaultLocalAddresses();

    public void setDefaultLocalAddress(SocketAddress var1);

    public void setDefaultLocalAddresses(SocketAddress var1, SocketAddress ... var2);

    public void setDefaultLocalAddresses(Iterable<? extends SocketAddress> var1);

    public void setDefaultLocalAddresses(List<? extends SocketAddress> var1);

    public boolean isCloseOnDeactivation();

    public void setCloseOnDeactivation(boolean var1);

    public void bind() throws IOException;

    public void bind(SocketAddress var1) throws IOException;

    public void bind(SocketAddress var1, SocketAddress ... var2) throws IOException;

    public void bind(SocketAddress ... var1) throws IOException;

    public void bind(Iterable<? extends SocketAddress> var1) throws IOException;

    public void unbind();

    public void unbind(SocketAddress var1);

    public void unbind(SocketAddress var1, SocketAddress ... var2);

    public void unbind(Iterable<? extends SocketAddress> var1);

    public IoSession newSession(SocketAddress var1, SocketAddress var2);
}

