package org.apache.mina.util.byteaccess;

abstract class AbstractByteArray implements ByteArray {
   public final int length() {
      return this.last() - this.first();
   }

   public final boolean equals(Object other) {
      if (other == this) {
         return true;
      } else if (!(other instanceof ByteArray)) {
         return false;
      } else {
         ByteArray otherByteArray = (ByteArray)other;
         if (this.first() == otherByteArray.first() && this.last() == otherByteArray.last() && this.order().equals(otherByteArray.order())) {
            ByteArray.Cursor cursor = this.cursor();
            ByteArray.Cursor otherCursor = otherByteArray.cursor();
            int remaining = cursor.getRemaining();

            while(remaining > 0) {
               if (remaining >= 4) {
                  int i = cursor.getInt();
                  int otherI = otherCursor.getInt();
                  if (i != otherI) {
                     return false;
                  }
               } else {
                  byte b = cursor.get();
                  byte otherB = otherCursor.get();
                  if (b != otherB) {
                     return false;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }
}
