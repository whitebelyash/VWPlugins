package org.apache.mina.core.buffer;

import java.nio.ByteBuffer;

public interface IoBufferAllocator {
   IoBuffer allocate(int var1, boolean var2);

   ByteBuffer allocateNioBuffer(int var1, boolean var2);

   IoBuffer wrap(ByteBuffer var1);

   void dispose();
}
