/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.session;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionAttributeMap;
import org.apache.mina.core.write.WriteRequestQueue;

public interface IoSessionDataStructureFactory {
    public IoSessionAttributeMap getAttributeMap(IoSession var1) throws Exception;

    public WriteRequestQueue getWriteRequestQueue(IoSession var1) throws Exception;
}

