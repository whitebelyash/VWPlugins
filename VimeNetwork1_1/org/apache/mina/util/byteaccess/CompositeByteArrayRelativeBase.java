package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;

abstract class CompositeByteArrayRelativeBase {
   protected final CompositeByteArray cba;
   protected final ByteArray.Cursor cursor;

   public CompositeByteArrayRelativeBase(CompositeByteArray cba) {
      this.cba = cba;
      this.cursor = cba.cursor(cba.first(), new CompositeByteArray.CursorListener() {
         public void enteredFirstComponent(int componentIndex, ByteArray component) {
         }

         public void enteredLastComponent(int componentIndex, ByteArray component) {
            assert false;

         }

         public void enteredNextComponent(int componentIndex, ByteArray component) {
            CompositeByteArrayRelativeBase.this.cursorPassedFirstComponent();
         }

         public void enteredPreviousComponent(int componentIndex, ByteArray component) {
            assert false;

         }
      });
   }

   public final int getRemaining() {
      return this.cursor.getRemaining();
   }

   public final boolean hasRemaining() {
      return this.cursor.hasRemaining();
   }

   public ByteOrder order() {
      return this.cba.order();
   }

   public final void append(ByteArray ba) {
      this.cba.addLast(ba);
   }

   public final void free() {
      this.cba.free();
   }

   public final int getIndex() {
      return this.cursor.getIndex();
   }

   public final int last() {
      return this.cba.last();
   }

   protected abstract void cursorPassedFirstComponent();
}
