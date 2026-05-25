package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface IoRelativeReader {
   int getRemaining();

   boolean hasRemaining();

   void skip(int var1);

   ByteArray slice(int var1);

   ByteOrder order();

   byte get();

   void get(IoBuffer var1);

   short getShort();

   int getInt();

   long getLong();

   float getFloat();

   double getDouble();

   char getChar();
}
