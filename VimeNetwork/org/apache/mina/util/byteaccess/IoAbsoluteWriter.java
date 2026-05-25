package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoAbsoluteWriter {
   int first();

   int last();

   ByteOrder order();

   void put(int var1, byte var2);

   void put(int var1, IoBuffer var2);

   void putShort(int var1, short var2);

   void putInt(int var1, int var2);

   void putLong(int var1, long var2);

   void putFloat(int var1, float var2);

   void putDouble(int var1, double var2);

   void putChar(int var1, char var2);
}
