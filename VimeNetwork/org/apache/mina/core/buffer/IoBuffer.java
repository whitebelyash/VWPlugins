package org.apache.mina.core.buffer;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.EnumSet;
import java.util.Set;

public abstract class IoBuffer implements Comparable {
   private static IoBufferAllocator allocator = new SimpleBufferAllocator();
   private static boolean useDirectBuffer = false;

   public static IoBufferAllocator getAllocator() {
      return allocator;
   }

   public static void setAllocator(IoBufferAllocator newAllocator) {
      if (newAllocator == null) {
         throw new IllegalArgumentException("allocator");
      } else {
         IoBufferAllocator oldAllocator = allocator;
         allocator = newAllocator;
         if (null != oldAllocator) {
            oldAllocator.dispose();
         }

      }
   }

   public static boolean isUseDirectBuffer() {
      return useDirectBuffer;
   }

   public static void setUseDirectBuffer(boolean useDirectBuffer) {
      IoBuffer.useDirectBuffer = useDirectBuffer;
   }

   public static IoBuffer allocate(int capacity) {
      return allocate(capacity, useDirectBuffer);
   }

   public static IoBuffer allocate(int capacity, boolean useDirectBuffer) {
      if (capacity < 0) {
         throw new IllegalArgumentException("capacity: " + capacity);
      } else {
         return allocator.allocate(capacity, useDirectBuffer);
      }
   }

   public static IoBuffer wrap(ByteBuffer nioBuffer) {
      return allocator.wrap(nioBuffer);
   }

   public static IoBuffer wrap(byte[] byteArray) {
      return wrap(ByteBuffer.wrap(byteArray));
   }

   public static IoBuffer wrap(byte[] byteArray, int offset, int length) {
      return wrap(ByteBuffer.wrap(byteArray, offset, length));
   }

   protected static int normalizeCapacity(int requestedCapacity) {
      if (requestedCapacity < 0) {
         return Integer.MAX_VALUE;
      } else {
         int newCapacity = Integer.highestOneBit(requestedCapacity);
         newCapacity <<= newCapacity < requestedCapacity ? 1 : 0;
         return newCapacity < 0 ? Integer.MAX_VALUE : newCapacity;
      }
   }

   protected IoBuffer() {
   }

   public abstract void free();

   public abstract ByteBuffer buf();

   public abstract boolean isDirect();

   public abstract boolean isDerived();

   public abstract boolean isReadOnly();

   public abstract int minimumCapacity();

   public abstract IoBuffer minimumCapacity(int var1);

   public abstract int capacity();

   public abstract IoBuffer capacity(int var1);

   public abstract boolean isAutoExpand();

   public abstract IoBuffer setAutoExpand(boolean var1);

   public abstract boolean isAutoShrink();

   public abstract IoBuffer setAutoShrink(boolean var1);

   public abstract IoBuffer expand(int var1);

   public abstract IoBuffer expand(int var1, int var2);

   public abstract IoBuffer shrink();

   public abstract int position();

   public abstract IoBuffer position(int var1);

   public abstract int limit();

   public abstract IoBuffer limit(int var1);

   public abstract IoBuffer mark();

   public abstract int markValue();

   public abstract IoBuffer reset();

   public abstract IoBuffer clear();

   public abstract IoBuffer sweep();

   public abstract IoBuffer sweep(byte var1);

   public abstract IoBuffer flip();

   public abstract IoBuffer rewind();

   public abstract int remaining();

   public abstract boolean hasRemaining();

   public abstract IoBuffer duplicate();

   public abstract IoBuffer slice();

   public abstract IoBuffer asReadOnlyBuffer();

   public abstract boolean hasArray();

   public abstract byte[] array();

   public abstract int arrayOffset();

   public abstract byte get();

   public abstract short getUnsigned();

   public abstract IoBuffer put(byte var1);

   public abstract byte get(int var1);

   public abstract short getUnsigned(int var1);

   public abstract IoBuffer put(int var1, byte var2);

   public abstract IoBuffer get(byte[] var1, int var2, int var3);

   public abstract IoBuffer get(byte[] var1);

   public abstract IoBuffer getSlice(int var1, int var2);

   public abstract IoBuffer getSlice(int var1);

   public abstract IoBuffer put(ByteBuffer var1);

   public abstract IoBuffer put(IoBuffer var1);

   public abstract IoBuffer put(byte[] var1, int var2, int var3);

   public abstract IoBuffer put(byte[] var1);

   public abstract IoBuffer compact();

   public abstract ByteOrder order();

   public abstract IoBuffer order(ByteOrder var1);

   public abstract char getChar();

   public abstract IoBuffer putChar(char var1);

   public abstract char getChar(int var1);

   public abstract IoBuffer putChar(int var1, char var2);

   public abstract CharBuffer asCharBuffer();

   public abstract short getShort();

   public abstract int getUnsignedShort();

   public abstract IoBuffer putShort(short var1);

   public abstract short getShort(int var1);

   public abstract int getUnsignedShort(int var1);

   public abstract IoBuffer putShort(int var1, short var2);

   public abstract ShortBuffer asShortBuffer();

   public abstract int getInt();

   public abstract long getUnsignedInt();

   public abstract int getMediumInt();

   public abstract int getUnsignedMediumInt();

   public abstract int getMediumInt(int var1);

   public abstract int getUnsignedMediumInt(int var1);

   public abstract IoBuffer putMediumInt(int var1);

   public abstract IoBuffer putMediumInt(int var1, int var2);

   public abstract IoBuffer putInt(int var1);

   public abstract IoBuffer putUnsigned(byte var1);

   public abstract IoBuffer putUnsigned(int var1, byte var2);

   public abstract IoBuffer putUnsigned(short var1);

   public abstract IoBuffer putUnsigned(int var1, short var2);

   public abstract IoBuffer putUnsigned(int var1);

   public abstract IoBuffer putUnsigned(int var1, int var2);

   public abstract IoBuffer putUnsigned(long var1);

   public abstract IoBuffer putUnsigned(int var1, long var2);

   public abstract IoBuffer putUnsignedInt(byte var1);

   public abstract IoBuffer putUnsignedInt(int var1, byte var2);

   public abstract IoBuffer putUnsignedInt(short var1);

   public abstract IoBuffer putUnsignedInt(int var1, short var2);

   public abstract IoBuffer putUnsignedInt(int var1);

   public abstract IoBuffer putUnsignedInt(int var1, int var2);

   public abstract IoBuffer putUnsignedInt(long var1);

   public abstract IoBuffer putUnsignedInt(int var1, long var2);

   public abstract IoBuffer putUnsignedShort(byte var1);

   public abstract IoBuffer putUnsignedShort(int var1, byte var2);

   public abstract IoBuffer putUnsignedShort(short var1);

   public abstract IoBuffer putUnsignedShort(int var1, short var2);

   public abstract IoBuffer putUnsignedShort(int var1);

   public abstract IoBuffer putUnsignedShort(int var1, int var2);

   public abstract IoBuffer putUnsignedShort(long var1);

   public abstract IoBuffer putUnsignedShort(int var1, long var2);

   public abstract int getInt(int var1);

   public abstract long getUnsignedInt(int var1);

   public abstract IoBuffer putInt(int var1, int var2);

   public abstract IntBuffer asIntBuffer();

   public abstract long getLong();

   public abstract IoBuffer putLong(long var1);

   public abstract long getLong(int var1);

   public abstract IoBuffer putLong(int var1, long var2);

   public abstract LongBuffer asLongBuffer();

   public abstract float getFloat();

   public abstract IoBuffer putFloat(float var1);

   public abstract float getFloat(int var1);

   public abstract IoBuffer putFloat(int var1, float var2);

   public abstract FloatBuffer asFloatBuffer();

   public abstract double getDouble();

   public abstract IoBuffer putDouble(double var1);

   public abstract double getDouble(int var1);

   public abstract IoBuffer putDouble(int var1, double var2);

   public abstract DoubleBuffer asDoubleBuffer();

   public abstract InputStream asInputStream();

   public abstract OutputStream asOutputStream();

   public abstract String getHexDump();

   public abstract String getHexDump(int var1);

   public abstract String getString(CharsetDecoder var1) throws CharacterCodingException;

   public abstract String getString(int var1, CharsetDecoder var2) throws CharacterCodingException;

   public abstract IoBuffer putString(CharSequence var1, CharsetEncoder var2) throws CharacterCodingException;

   public abstract IoBuffer putString(CharSequence var1, int var2, CharsetEncoder var3) throws CharacterCodingException;

   public abstract String getPrefixedString(CharsetDecoder var1) throws CharacterCodingException;

   public abstract String getPrefixedString(int var1, CharsetDecoder var2) throws CharacterCodingException;

   public abstract IoBuffer putPrefixedString(CharSequence var1, CharsetEncoder var2) throws CharacterCodingException;

   public abstract IoBuffer putPrefixedString(CharSequence var1, int var2, CharsetEncoder var3) throws CharacterCodingException;

   public abstract IoBuffer putPrefixedString(CharSequence var1, int var2, int var3, CharsetEncoder var4) throws CharacterCodingException;

   public abstract IoBuffer putPrefixedString(CharSequence var1, int var2, int var3, byte var4, CharsetEncoder var5) throws CharacterCodingException;

   public abstract Object getObject() throws ClassNotFoundException;

   public abstract Object getObject(ClassLoader var1) throws ClassNotFoundException;

   public abstract IoBuffer putObject(Object var1);

   public abstract boolean prefixedDataAvailable(int var1);

   public abstract boolean prefixedDataAvailable(int var1, int var2);

   public abstract int indexOf(byte var1);

   public abstract IoBuffer skip(int var1);

   public abstract IoBuffer fill(byte var1, int var2);

   public abstract IoBuffer fillAndReset(byte var1, int var2);

   public abstract IoBuffer fill(int var1);

   public abstract IoBuffer fillAndReset(int var1);

   public abstract Enum getEnum(Class var1);

   public abstract Enum getEnum(int var1, Class var2);

   public abstract Enum getEnumShort(Class var1);

   public abstract Enum getEnumShort(int var1, Class var2);

   public abstract Enum getEnumInt(Class var1);

   public abstract Enum getEnumInt(int var1, Class var2);

   public abstract IoBuffer putEnum(Enum var1);

   public abstract IoBuffer putEnum(int var1, Enum var2);

   public abstract IoBuffer putEnumShort(Enum var1);

   public abstract IoBuffer putEnumShort(int var1, Enum var2);

   public abstract IoBuffer putEnumInt(Enum var1);

   public abstract IoBuffer putEnumInt(int var1, Enum var2);

   public abstract EnumSet getEnumSet(Class var1);

   public abstract EnumSet getEnumSet(int var1, Class var2);

   public abstract EnumSet getEnumSetShort(Class var1);

   public abstract EnumSet getEnumSetShort(int var1, Class var2);

   public abstract EnumSet getEnumSetInt(Class var1);

   public abstract EnumSet getEnumSetInt(int var1, Class var2);

   public abstract EnumSet getEnumSetLong(Class var1);

   public abstract EnumSet getEnumSetLong(int var1, Class var2);

   public abstract IoBuffer putEnumSet(Set var1);

   public abstract IoBuffer putEnumSet(int var1, Set var2);

   public abstract IoBuffer putEnumSetShort(Set var1);

   public abstract IoBuffer putEnumSetShort(int var1, Set var2);

   public abstract IoBuffer putEnumSetInt(Set var1);

   public abstract IoBuffer putEnumSetInt(int var1, Set var2);

   public abstract IoBuffer putEnumSetLong(Set var1);

   public abstract IoBuffer putEnumSetLong(int var1, Set var2);
}
