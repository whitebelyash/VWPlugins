package org.apache.mina.util.byteaccess;

import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.mina.core.buffer.IoBuffer;

public final class CompositeByteArray extends AbstractByteArray {
   private final ByteArrayList bas;
   private ByteOrder order;
   private final ByteArrayFactory byteArrayFactory;

   public CompositeByteArray() {
      this((ByteArrayFactory)null);
   }

   public CompositeByteArray(ByteArrayFactory byteArrayFactory) {
      this.bas = new ByteArrayList();
      this.byteArrayFactory = byteArrayFactory;
   }

   public ByteArray getFirst() {
      return this.bas.isEmpty() ? null : this.bas.getFirst().getByteArray();
   }

   public void addFirst(ByteArray ba) {
      this.addHook(ba);
      this.bas.addFirst(ba);
   }

   public ByteArray removeFirst() {
      ByteArrayList.Node node = this.bas.removeFirst();
      return node == null ? null : node.getByteArray();
   }

   public ByteArray removeTo(int index) {
      if (index >= this.first() && index <= this.last()) {
         CompositeByteArray prefix = new CompositeByteArray(this.byteArrayFactory);
         int remaining = index - this.first();

         while(remaining > 0) {
            final ByteArray component = this.removeFirst();
            if (component.last() <= remaining) {
               prefix.addLast(component);
               remaining -= component.last();
            } else {
               IoBuffer bb = component.getSingleIoBuffer();
               int originalLimit = bb.limit();
               bb.position(0);
               bb.limit(remaining);
               IoBuffer bb1 = bb.slice();
               bb.position(remaining);
               bb.limit(originalLimit);
               IoBuffer bb2 = bb.slice();
               ByteArray ba1 = new BufferByteArray(bb1) {
                  public void free() {
                  }
               };
               prefix.addLast(ba1);
               remaining -= ba1.last();
               ByteArray ba2 = new BufferByteArray(bb2) {
                  public void free() {
                     component.free();
                  }
               };
               this.addFirst(ba2);
            }
         }

         return prefix;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void addLast(ByteArray ba) {
      this.addHook(ba);
      this.bas.addLast(ba);
   }

   public ByteArray removeLast() {
      ByteArrayList.Node node = this.bas.removeLast();
      return node == null ? null : node.getByteArray();
   }

   public void free() {
      while(!this.bas.isEmpty()) {
         ByteArrayList.Node node = this.bas.getLast();
         node.getByteArray().free();
         this.bas.removeLast();
      }

   }

   private void checkBounds(int index, int accessSize) {
      int upper = index + accessSize;
      if (index < this.first()) {
         throw new IndexOutOfBoundsException("Index " + index + " less than start " + this.first() + ".");
      } else if (upper > this.last()) {
         throw new IndexOutOfBoundsException("Index " + upper + " greater than length " + this.last() + ".");
      }
   }

   public Iterable getIoBuffers() {
      if (this.bas.isEmpty()) {
         return Collections.emptyList();
      } else {
         Collection<IoBuffer> result = new ArrayList();
         ByteArrayList.Node node = this.bas.getFirst();

         for(IoBuffer bb : node.getByteArray().getIoBuffers()) {
            result.add(bb);
         }

         while(node.hasNextNode()) {
            node = node.getNextNode();

            for(IoBuffer bb : node.getByteArray().getIoBuffers()) {
               result.add(bb);
            }
         }

         return result;
      }
   }

   public IoBuffer getSingleIoBuffer() {
      if (this.byteArrayFactory == null) {
         throw new IllegalStateException("Can't get single buffer from CompositeByteArray unless it has a ByteArrayFactory.");
      } else if (this.bas.isEmpty()) {
         ByteArray ba = this.byteArrayFactory.create(1);
         return ba.getSingleIoBuffer();
      } else {
         int actualLength = this.last() - this.first();
         ByteArrayList.Node node = this.bas.getFirst();
         ByteArray ba = node.getByteArray();
         if (ba.last() == actualLength) {
            return ba.getSingleIoBuffer();
         } else {
            ByteArray target = this.byteArrayFactory.create(actualLength);
            IoBuffer bb = target.getSingleIoBuffer();
            ByteArray.Cursor cursor = this.cursor();
            cursor.put(bb);

            while(!this.bas.isEmpty()) {
               ByteArrayList.Node node = this.bas.getLast();
               ByteArray component = node.getByteArray();
               this.bas.removeLast();
               component.free();
            }

            this.bas.addLast(target);
            return bb;
         }
      }
   }

   public ByteArray.Cursor cursor() {
      return new CursorImpl();
   }

   public ByteArray.Cursor cursor(int index) {
      return new CursorImpl(index);
   }

   public ByteArray.Cursor cursor(CursorListener listener) {
      return new CursorImpl(listener);
   }

   public ByteArray.Cursor cursor(int index, CursorListener listener) {
      return new CursorImpl(index, listener);
   }

   public ByteArray slice(int index, int length) {
      return this.cursor(index).slice(length);
   }

   public byte get(int index) {
      return this.cursor(index).get();
   }

   public void put(int index, byte b) {
      this.cursor(index).put(b);
   }

   public void get(int index, IoBuffer bb) {
      this.cursor(index).get(bb);
   }

   public void put(int index, IoBuffer bb) {
      this.cursor(index).put(bb);
   }

   public int first() {
      return this.bas.firstByte();
   }

   public int last() {
      return this.bas.lastByte();
   }

   private void addHook(ByteArray ba) {
      if (ba.first() != 0) {
         throw new IllegalArgumentException("Cannot add byte array that doesn't start from 0: " + ba.first());
      } else {
         if (this.order == null) {
            this.order = ba.order();
         } else if (!this.order.equals(ba.order())) {
            throw new IllegalArgumentException("Cannot add byte array with different byte order: " + ba.order());
         }

      }
   }

   public ByteOrder order() {
      if (this.order == null) {
         throw new IllegalStateException("Byte order not yet set.");
      } else {
         return this.order;
      }
   }

   public void order(ByteOrder order) {
      if (order == null || !order.equals(this.order)) {
         this.order = order;
         if (!this.bas.isEmpty()) {
            for(ByteArrayList.Node node = this.bas.getFirst(); node.hasNextNode(); node = node.getNextNode()) {
               node.getByteArray().order(order);
            }
         }
      }

   }

   public short getShort(int index) {
      return this.cursor(index).getShort();
   }

   public void putShort(int index, short s) {
      this.cursor(index).putShort(s);
   }

   public int getInt(int index) {
      return this.cursor(index).getInt();
   }

   public void putInt(int index, int i) {
      this.cursor(index).putInt(i);
   }

   public long getLong(int index) {
      return this.cursor(index).getLong();
   }

   public void putLong(int index, long l) {
      this.cursor(index).putLong(l);
   }

   public float getFloat(int index) {
      return this.cursor(index).getFloat();
   }

   public void putFloat(int index, float f) {
      this.cursor(index).putFloat(f);
   }

   public double getDouble(int index) {
      return this.cursor(index).getDouble();
   }

   public void putDouble(int index, double d) {
      this.cursor(index).putDouble(d);
   }

   public char getChar(int index) {
      return this.cursor(index).getChar();
   }

   public void putChar(int index, char c) {
      this.cursor(index).putChar(c);
   }

   private class CursorImpl implements ByteArray.Cursor {
      private int index;
      private final CursorListener listener;
      private ByteArrayList.Node componentNode;
      private int componentIndex;
      private ByteArray.Cursor componentCursor;

      public CursorImpl() {
         this(0, (CursorListener)null);
      }

      public CursorImpl(int index) {
         this(index, (CursorListener)null);
      }

      public CursorImpl(CursorListener listener) {
         this(0, listener);
      }

      public CursorImpl(int index, CursorListener listener) {
         this.index = index;
         this.listener = listener;
      }

      public int getIndex() {
         return this.index;
      }

      public void setIndex(int index) {
         CompositeByteArray.this.checkBounds(index, 0);
         this.index = index;
      }

      public void skip(int length) {
         this.setIndex(this.index + length);
      }

      public ByteArray slice(int length) {
         CompositeByteArray slice = new CompositeByteArray(CompositeByteArray.this.byteArrayFactory);

         int componentSliceSize;
         for(int remaining = length; remaining > 0; remaining -= componentSliceSize) {
            this.prepareForAccess(remaining);
            componentSliceSize = Math.min(remaining, this.componentCursor.getRemaining());
            ByteArray componentSlice = this.componentCursor.slice(componentSliceSize);
            slice.addLast(componentSlice);
            this.index += componentSliceSize;
         }

         return slice;
      }

      public ByteOrder order() {
         return CompositeByteArray.this.order();
      }

      private void prepareForAccess(int accessSize) {
         if (this.componentNode != null && this.componentNode.isRemoved()) {
            this.componentNode = null;
            this.componentCursor = null;
         }

         CompositeByteArray.this.checkBounds(this.index, accessSize);
         ByteArrayList.Node oldComponentNode = this.componentNode;
         if (this.componentNode == null) {
            int basMidpoint = (CompositeByteArray.this.last() - CompositeByteArray.this.first()) / 2 + CompositeByteArray.this.first();
            if (this.index <= basMidpoint) {
               this.componentNode = CompositeByteArray.this.bas.getFirst();
               this.componentIndex = CompositeByteArray.this.first();
               if (this.listener != null) {
                  this.listener.enteredFirstComponent(this.componentIndex, this.componentNode.getByteArray());
               }
            } else {
               this.componentNode = CompositeByteArray.this.bas.getLast();
               this.componentIndex = CompositeByteArray.this.last() - this.componentNode.getByteArray().last();
               if (this.listener != null) {
                  this.listener.enteredLastComponent(this.componentIndex, this.componentNode.getByteArray());
               }
            }
         }

         while(this.index < this.componentIndex) {
            this.componentNode = this.componentNode.getPreviousNode();
            this.componentIndex -= this.componentNode.getByteArray().last();
            if (this.listener != null) {
               this.listener.enteredPreviousComponent(this.componentIndex, this.componentNode.getByteArray());
            }
         }

         while(this.index >= this.componentIndex + this.componentNode.getByteArray().length()) {
            this.componentIndex += this.componentNode.getByteArray().last();
            this.componentNode = this.componentNode.getNextNode();
            if (this.listener != null) {
               this.listener.enteredNextComponent(this.componentIndex, this.componentNode.getByteArray());
            }
         }

         int internalComponentIndex = this.index - this.componentIndex;
         if (this.componentNode == oldComponentNode) {
            this.componentCursor.setIndex(internalComponentIndex);
         } else {
            this.componentCursor = this.componentNode.getByteArray().cursor(internalComponentIndex);
         }

      }

      public int getRemaining() {
         return CompositeByteArray.this.last() - this.index + 1;
      }

      public boolean hasRemaining() {
         return this.getRemaining() > 0;
      }

      public byte get() {
         this.prepareForAccess(1);
         byte b = this.componentCursor.get();
         ++this.index;
         return b;
      }

      public void put(byte b) {
         this.prepareForAccess(1);
         this.componentCursor.put(b);
         ++this.index;
      }

      public void get(IoBuffer bb) {
         while(bb.hasRemaining()) {
            int remainingBefore = bb.remaining();
            this.prepareForAccess(remainingBefore);
            this.componentCursor.get(bb);
            int remainingAfter = bb.remaining();
            int chunkSize = remainingBefore - remainingAfter;
            this.index += chunkSize;
         }

      }

      public void put(IoBuffer bb) {
         while(bb.hasRemaining()) {
            int remainingBefore = bb.remaining();
            this.prepareForAccess(remainingBefore);
            this.componentCursor.put(bb);
            int remainingAfter = bb.remaining();
            int chunkSize = remainingBefore - remainingAfter;
            this.index += chunkSize;
         }

      }

      public short getShort() {
         this.prepareForAccess(2);
         if (this.componentCursor.getRemaining() >= 4) {
            short s = this.componentCursor.getShort();
            this.index += 2;
            return s;
         } else {
            byte b0 = this.get();
            byte b1 = this.get();
            return CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN) ? (short)(b0 << 8 | b1 & 255) : (short)(b1 << 8 | b0 & 255);
         }
      }

      public void putShort(short s) {
         this.prepareForAccess(2);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putShort(s);
            this.index += 2;
         } else {
            byte b0;
            byte b1;
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
               b0 = (byte)(s >> 8 & 255);
               b1 = (byte)(s >> 0 & 255);
            } else {
               b0 = (byte)(s >> 0 & 255);
               b1 = (byte)(s >> 8 & 255);
            }

            this.put(b0);
            this.put(b1);
         }

      }

      public int getInt() {
         this.prepareForAccess(4);
         if (this.componentCursor.getRemaining() >= 4) {
            int i = this.componentCursor.getInt();
            this.index += 4;
            return i;
         } else {
            byte b0 = this.get();
            byte b1 = this.get();
            byte b2 = this.get();
            byte b3 = this.get();
            return CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN) ? b0 << 24 | (b1 & 255) << 16 | (b2 & 255) << 8 | b3 & 255 : b3 << 24 | (b2 & 255) << 16 | (b1 & 255) << 8 | b0 & 255;
         }
      }

      public void putInt(int i) {
         this.prepareForAccess(4);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putInt(i);
            this.index += 4;
         } else {
            byte b0;
            byte b1;
            byte b2;
            byte b3;
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
               b0 = (byte)(i >> 24 & 255);
               b1 = (byte)(i >> 16 & 255);
               b2 = (byte)(i >> 8 & 255);
               b3 = (byte)(i >> 0 & 255);
            } else {
               b0 = (byte)(i >> 0 & 255);
               b1 = (byte)(i >> 8 & 255);
               b2 = (byte)(i >> 16 & 255);
               b3 = (byte)(i >> 24 & 255);
            }

            this.put(b0);
            this.put(b1);
            this.put(b2);
            this.put(b3);
         }

      }

      public long getLong() {
         this.prepareForAccess(8);
         if (this.componentCursor.getRemaining() >= 4) {
            long l = this.componentCursor.getLong();
            this.index += 8;
            return l;
         } else {
            byte b0 = this.get();
            byte b1 = this.get();
            byte b2 = this.get();
            byte b3 = this.get();
            byte b4 = this.get();
            byte b5 = this.get();
            byte b6 = this.get();
            byte b7 = this.get();
            return CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN) ? ((long)b0 & 255L) << 56 | ((long)b1 & 255L) << 48 | ((long)b2 & 255L) << 40 | ((long)b3 & 255L) << 32 | ((long)b4 & 255L) << 24 | ((long)b5 & 255L) << 16 | ((long)b6 & 255L) << 8 | (long)b7 & 255L : ((long)b7 & 255L) << 56 | ((long)b6 & 255L) << 48 | ((long)b5 & 255L) << 40 | ((long)b4 & 255L) << 32 | ((long)b3 & 255L) << 24 | ((long)b2 & 255L) << 16 | ((long)b1 & 255L) << 8 | (long)b0 & 255L;
         }
      }

      public void putLong(long l) {
         this.prepareForAccess(8);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putLong(l);
            this.index += 8;
         } else {
            byte b0;
            byte b1;
            byte b2;
            byte b3;
            byte b4;
            byte b5;
            byte b6;
            byte b7;
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
               b0 = (byte)((int)(l >> 56 & 255L));
               b1 = (byte)((int)(l >> 48 & 255L));
               b2 = (byte)((int)(l >> 40 & 255L));
               b3 = (byte)((int)(l >> 32 & 255L));
               b4 = (byte)((int)(l >> 24 & 255L));
               b5 = (byte)((int)(l >> 16 & 255L));
               b6 = (byte)((int)(l >> 8 & 255L));
               b7 = (byte)((int)(l >> 0 & 255L));
            } else {
               b0 = (byte)((int)(l >> 0 & 255L));
               b1 = (byte)((int)(l >> 8 & 255L));
               b2 = (byte)((int)(l >> 16 & 255L));
               b3 = (byte)((int)(l >> 24 & 255L));
               b4 = (byte)((int)(l >> 32 & 255L));
               b5 = (byte)((int)(l >> 40 & 255L));
               b6 = (byte)((int)(l >> 48 & 255L));
               b7 = (byte)((int)(l >> 56 & 255L));
            }

            this.put(b0);
            this.put(b1);
            this.put(b2);
            this.put(b3);
            this.put(b4);
            this.put(b5);
            this.put(b6);
            this.put(b7);
         }

      }

      public float getFloat() {
         this.prepareForAccess(4);
         if (this.componentCursor.getRemaining() >= 4) {
            float f = this.componentCursor.getFloat();
            this.index += 4;
            return f;
         } else {
            int i = this.getInt();
            return Float.intBitsToFloat(i);
         }
      }

      public void putFloat(float f) {
         this.prepareForAccess(4);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putFloat(f);
            this.index += 4;
         } else {
            int i = Float.floatToIntBits(f);
            this.putInt(i);
         }

      }

      public double getDouble() {
         this.prepareForAccess(8);
         if (this.componentCursor.getRemaining() >= 4) {
            double d = this.componentCursor.getDouble();
            this.index += 8;
            return d;
         } else {
            long l = this.getLong();
            return Double.longBitsToDouble(l);
         }
      }

      public void putDouble(double d) {
         this.prepareForAccess(8);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putDouble(d);
            this.index += 8;
         } else {
            long l = Double.doubleToLongBits(d);
            this.putLong(l);
         }

      }

      public char getChar() {
         this.prepareForAccess(2);
         if (this.componentCursor.getRemaining() >= 4) {
            char c = this.componentCursor.getChar();
            this.index += 2;
            return c;
         } else {
            byte b0 = this.get();
            byte b1 = this.get();
            return CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN) ? (char)(b0 << 8 | b1 & 255) : (char)(b1 << 8 | b0 & 255);
         }
      }

      public void putChar(char c) {
         this.prepareForAccess(2);
         if (this.componentCursor.getRemaining() >= 4) {
            this.componentCursor.putChar(c);
            this.index += 2;
         } else {
            byte b0;
            byte b1;
            if (CompositeByteArray.this.order.equals(ByteOrder.BIG_ENDIAN)) {
               b0 = (byte)(c >> 8 & 255);
               b1 = (byte)(c >> 0 & 255);
            } else {
               b0 = (byte)(c >> 0 & 255);
               b1 = (byte)(c >> 8 & 255);
            }

            this.put(b0);
            this.put(b1);
         }

      }
   }

   public interface CursorListener {
      void enteredFirstComponent(int var1, ByteArray var2);

      void enteredNextComponent(int var1, ByteArray var2);

      void enteredPreviousComponent(int var1, ByteArray var2);

      void enteredLastComponent(int var1, ByteArray var2);
   }
}
