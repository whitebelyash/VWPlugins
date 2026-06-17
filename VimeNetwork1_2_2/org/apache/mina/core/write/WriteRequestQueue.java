/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.write;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface WriteRequestQueue {
    public WriteRequest poll(IoSession var1);

    public void offer(IoSession var1, WriteRequest var2);

    public boolean isEmpty(IoSession var1);

    public void clear(IoSession var1);

    public void dispose(IoSession var1);

    public int size();
}

