/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.service;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoProcessor<S extends IoSession> {
    public boolean isDisposing();

    public boolean isDisposed();

    public void dispose();

    public void add(S var1);

    public void flush(S var1);

    public void write(S var1, WriteRequest var2);

    public void updateTrafficControl(S var1);

    public void remove(S var1);
}

