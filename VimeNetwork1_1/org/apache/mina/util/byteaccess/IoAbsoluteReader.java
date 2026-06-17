package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoAbsoluteReader {
   int first();

   int last();

   int length();

   ByteArray slice(int var1, int var2);

   ByteOrder order();

   byte get(int var1);

   void get(int var1, IoBuffer var2);

   short getShort(int var1);

   int getInt(int var1);

   long getLong(int var1);

   float getFloat(int var1);

   double getDouble(int var1);

   char getChar(int var1);
}
