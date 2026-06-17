/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.keepalive;

import org.apache.mina.core.session.IoSession;

public interface KeepAliveMessageFactory {
    public boolean isRequest(IoSession var1, Object var2);

    public boolean isResponse(IoSession var1, Object var2);

    public Object getRequest(IoSession var1);

    public Object getResponse(IoSession var1, Object var2);
}

