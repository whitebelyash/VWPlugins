/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.write;

import java.net.SocketAddress;
import org.apache.mina.core.future.WriteFuture;

public interface WriteRequest {
    public WriteRequest getOriginalRequest();

    public WriteFuture getFuture();

    public Object getMessage();

    public SocketAddress getDestination();

    public boolean isEncoded();
}

