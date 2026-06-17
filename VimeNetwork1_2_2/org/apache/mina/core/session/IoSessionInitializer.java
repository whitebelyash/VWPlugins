/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.session.IoSession;

public interface IoSessionInitializer<T extends IoFuture> {
    public void initializeSession(IoSession var1, T var2);
}

