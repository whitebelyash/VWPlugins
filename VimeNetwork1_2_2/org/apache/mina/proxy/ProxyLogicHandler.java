/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.proxy;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.proxy.ProxyAuthException;
import org.apache.mina.proxy.session.ProxyIoSession;

public interface ProxyLogicHandler {
    public boolean isHandshakeComplete();

    public void messageReceived(IoFilter.NextFilter var1, IoBuffer var2) throws ProxyAuthException;

    public void doHandshake(IoFilter.NextFilter var1) throws ProxyAuthException;

    public ProxyIoSession getProxyIoSession();

    public void enqueueWriteRequest(IoFilter.NextFilter var1, WriteRequest var2);
}

