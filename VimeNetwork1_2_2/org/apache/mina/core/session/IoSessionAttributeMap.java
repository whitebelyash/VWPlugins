/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import java.util.Set;
import org.apache.mina.core.session.IoSession;

public interface IoSessionAttributeMap {
    public Object getAttribute(IoSession var1, Object var2, Object var3);

    public Object setAttribute(IoSession var1, Object var2, Object var3);

    public Object setAttributeIfAbsent(IoSession var1, Object var2, Object var3);

    public Object removeAttribute(IoSession var1, Object var2);

    public boolean removeAttribute(IoSession var1, Object var2, Object var3);

    public boolean replaceAttribute(IoSession var1, Object var2, Object var3, Object var4);

    public boolean containsAttribute(IoSession var1, Object var2);

    public Set<Object> getAttributeKeys(IoSession var1);

    public void dispose(IoSession var1) throws Exception;
}

