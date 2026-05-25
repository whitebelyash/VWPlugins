package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import org.apache.mina.core.buffer.IoBuffer;

public interface ByteArray extends IoAbsoluteReader, IoAbsoluteWriter {
   int first();

   int last();

   ByteOrder order();

   void order(ByteOrder var1);

   void free();

   Iterable getIoBuffers();

   IoBuffer getSingleIoBuffer();

   boolean equals(Object var1);

   byte get(int var1);

   void get(int var1, IoBuffer var2);

   int getInt(int var1);

   Cursor cursor();

   Cursor cursor(int var1);

   public interface Cursor extends IoRelativeReader, IoRelativeWriter {
      int getIndex();

      void setIndex(int var1);

      int getRemaining();

      boolean hasRemaining();

      byte get();

      void get(IoBuffer var1);

      int getInt();
   }
}
