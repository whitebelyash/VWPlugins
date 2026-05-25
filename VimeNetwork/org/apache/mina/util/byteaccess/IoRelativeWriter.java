package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoRelativeWriter {
   int getRemaining();

   boolean hasRemaining();

   void skip(int var1);

   ByteOrder order();

   void put(byte var1);

   void put(IoBuffer var1);

   void putShort(short var1);

   void putInt(int var1);

   void putLong(long var1);

   void putFloat(float var1);

   void putDouble(double var1);

   void putChar(char var1);
}
