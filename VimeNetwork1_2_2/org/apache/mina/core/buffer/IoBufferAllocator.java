/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.buffer;

import java.nio.ByteBuffer;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoBufferAllocator {
    public IoBuffer allocate(int var1, boolean var2);

    public ByteBuffer allocateNioBuffer(int var1, boolean var2);

    public IoBuffer wrap(ByteBuffer var1);

    public void dispose();
}

