/*
 * Decompiled with CFR 0.152.
 */
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
import org.apache.mina.core.buffer.IoBuffer;

public class IoBufferWrapper
extends IoBuffer {
    private final IoBuffer buf;

    protected IoBufferWrapper(IoBuffer buf) {
        if (buf == null) {
            throw new IllegalArgumentException("buf");
        }
        this.buf = buf;
    }

    public IoBuffer getParentBuffer() {
        return this.buf;
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public ByteBuffer buf() {
        return this.buf.buf();
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public int position() {
        return this.buf.position();
    }

    @Override
    public IoBuffer position(int newPosition) {
        this.buf.position(newPosition);
        return this;
    }

    @Override
    public int limit() {
        return this.buf.limit();
    }

    @Override
    public IoBuffer limit(int newLimit) {
        this.buf.limit(newLimit);
        return this;
    }

    @Override
    public IoBuffer mark() {
        this.buf.mark();
        return this;
    }

    @Override
    public IoBuffer reset() {
        this.buf.reset();
        return this;
    }

    @Override
    public IoBuffer clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public IoBuffer sweep() {
        this.buf.sweep();
        return this;
    }

    @Override
    public IoBuffer sweep(byte value) {
        this.buf.sweep(value);
        return this;
    }

    @Override
    public IoBuffer flip() {
        this.buf.flip();
        return this;
    }

    @Override
    public IoBuffer rewind() {
        this.buf.rewind();
        return this;
    }

    @Override
    public int remaining() {
        return this.buf.remaining();
    }

    @Override
    public boolean hasRemaining() {
        return this.buf.hasRemaining();
    }

    @Override
    public byte get() {
        return this.buf.get();
    }

    @Override
    public short getUnsigned() {
        return this.buf.getUnsigned();
    }

    @Override
    public IoBuffer put(byte b) {
        this.buf.put(b);
        return this;
    }

    @Override
    public byte get(int index) {
        return this.buf.get(index);
    }

    @Override
    public short getUnsigned(int index) {
        return this.buf.getUnsigned(index);
    }

    @Override
    public IoBuffer put(int index, byte b) {
        this.buf.put(index, b);
        return this;
    }

    @Override
    public IoBuffer get(byte[] dst, int offset, int length) {
        this.buf.get(dst, offset, length);
        return this;
    }

    @Override
    public IoBuffer getSlice(int index, int length) {
        return this.buf.getSlice(index, length);
    }

    @Override
    public IoBuffer getSlice(int length) {
        return this.buf.getSlice(length);
    }

    @Override
    public IoBuffer get(byte[] dst) {
        this.buf.get(dst);
        return this;
    }

    @Override
    public IoBuffer put(IoBuffer src) {
        this.buf.put(src);
        return this;
    }

    @Override
    public IoBuffer put(ByteBuffer src) {
        this.buf.put(src);
        return this;
    }

    @Override
    public IoBuffer put(byte[] src, int offset, int length) {
        this.buf.put(src, offset, length);
        return this;
    }

    @Override
    public IoBuffer put(byte[] src) {
        this.buf.put(src);
        return this;
    }

    @Override
    public IoBuffer compact() {
        this.buf.compact();
        return this;
    }

    public String toString() {
        return this.buf.toString();
    }

    public int hashCode() {
        return this.buf.hashCode();
    }

    public boolean equals(Object ob) {
        return this.buf.equals(ob);
    }

    @Override
    public int compareTo(IoBuffer that) {
        return this.buf.compareTo(that);
    }

    @Override
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    public IoBuffer order(ByteOrder bo) {
        this.buf.order(bo);
        return this;
    }

    @Override
    public char getChar() {
        return this.buf.getChar();
    }

    @Override
    public IoBuffer putChar(char value) {
        this.buf.putChar(value);
        return this;
    }

    @Override
    public char getChar(int index) {
        return this.buf.getChar(index);
    }

    @Override
    public IoBuffer putChar(int index, char value) {
        this.buf.putChar(index, value);
        return this;
    }

    @Override
    public CharBuffer asCharBuffer() {
        return this.buf.asCharBuffer();
    }

    @Override
    public short getShort() {
        return this.buf.getShort();
    }

    @Override
    public int getUnsignedShort() {
        return this.buf.getUnsignedShort();
    }

    @Override
    public IoBuffer putShort(short value) {
        this.buf.putShort(value);
        return this;
    }

    @Override
    public short getShort(int index) {
        return this.buf.getShort(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.buf.getUnsignedShort(index);
    }

    @Override
    public IoBuffer putShort(int index, short value) {
        this.buf.putShort(index, value);
        return this;
    }

    @Override
    public ShortBuffer asShortBuffer() {
        return this.buf.asShortBuffer();
    }

    @Override
    public int getInt() {
        return this.buf.getInt();
    }

    @Override
    public long getUnsignedInt() {
        return this.buf.getUnsignedInt();
    }

    @Override
    public IoBuffer putInt(int value) {
        this.buf.putInt(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(byte value) {
        this.buf.putUnsignedInt(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(int index, byte value) {
        this.buf.putUnsignedInt(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(short value) {
        this.buf.putUnsignedInt(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(int index, short value) {
        this.buf.putUnsignedInt(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(int value) {
        this.buf.putUnsignedInt(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(int index, int value) {
        this.buf.putUnsignedInt(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(long value) {
        this.buf.putUnsignedInt(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedInt(int index, long value) {
        this.buf.putUnsignedInt(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(byte value) {
        this.buf.putUnsignedShort(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(int index, byte value) {
        this.buf.putUnsignedShort(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(short value) {
        this.buf.putUnsignedShort(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(int index, short value) {
        this.buf.putUnsignedShort(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(int value) {
        this.buf.putUnsignedShort(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(int index, int value) {
        this.buf.putUnsignedShort(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(long value) {
        this.buf.putUnsignedShort(value);
        return this;
    }

    @Override
    public IoBuffer putUnsignedShort(int index, long value) {
        this.buf.putUnsignedShort(index, value);
        return this;
    }

    @Override
    public int getInt(int index) {
        return this.buf.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.buf.getUnsignedInt(index);
    }

    @Override
    public IoBuffer putInt(int index, int value) {
        this.buf.putInt(index, value);
        return this;
    }

    @Override
    public IntBuffer asIntBuffer() {
        return this.buf.asIntBuffer();
    }

    @Override
    public long getLong() {
        return this.buf.getLong();
    }

    @Override
    public IoBuffer putLong(long value) {
        this.buf.putLong(value);
        return this;
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong(index);
    }

    @Override
    public IoBuffer putLong(int index, long value) {
        this.buf.putLong(index, value);
        return this;
    }

    @Override
    public LongBuffer asLongBuffer() {
        return this.buf.asLongBuffer();
    }

    @Override
    public float getFloat() {
        return this.buf.getFloat();
    }

    @Override
    public IoBuffer putFloat(float value) {
        this.buf.putFloat(value);
        return this;
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat(index);
    }

    @Override
    public IoBuffer putFloat(int index, float value) {
        this.buf.putFloat(index, value);
        return this;
    }

    @Override
    public FloatBuffer asFloatBuffer() {
        return this.buf.asFloatBuffer();
    }

    @Override
    public double getDouble() {
        return this.buf.getDouble();
    }

    @Override
    public IoBuffer putDouble(double value) {
        this.buf.putDouble(value);
        return this;
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble(index);
    }

    @Override
    public IoBuffer putDouble(int index, double value) {
        this.buf.putDouble(index, value);
        return this;
    }

    @Override
    public DoubleBuffer asDoubleBuffer() {
        return this.buf.asDoubleBuffer();
    }

    @Override
    public String getHexDump() {
        return this.buf.getHexDump();
    }

    @Override
    public String getString(int fieldSize, CharsetDecoder decoder) throws CharacterCodingException {
        return this.buf.getString(fieldSize, decoder);
    }

    @Override
    public String getString(CharsetDecoder decoder) throws CharacterCodingException {
        return this.buf.getString(decoder);
    }

    @Override
    public String getPrefixedString(CharsetDecoder decoder) throws CharacterCodingException {
        return this.buf.getPrefixedString(decoder);
    }

    @Override
    public String getPrefixedString(int prefixLength, CharsetDecoder decoder) throws CharacterCodingException {
        return this.buf.getPrefixedString(prefixLength, decoder);
    }

    @Override
    public IoBuffer putString(CharSequence in, int fieldSize, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putString(in, fieldSize, encoder);
        return this;
    }

    @Override
    public IoBuffer putString(CharSequence in, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putString(in, encoder);
        return this;
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putPrefixedString(in, encoder);
        return this;
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, int prefixLength, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putPrefixedString(in, prefixLength, encoder);
        return this;
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putPrefixedString(in, prefixLength, padding, encoder);
        return this;
    }

    @Override
    public IoBuffer putPrefixedString(CharSequence in, int prefixLength, int padding, byte padValue, CharsetEncoder encoder) throws CharacterCodingException {
        this.buf.putPrefixedString(in, prefixLength, padding, padValue, encoder);
        return this;
    }

    @Override
    public IoBuffer skip(int size) {
        this.buf.skip(size);
        return this;
    }

    @Override
    public IoBuffer fill(byte value, int size) {
        this.buf.fill(value, size);
        return this;
    }

    @Override
    public IoBuffer fillAndReset(byte value, int size) {
        this.buf.fillAndReset(value, size);
        return this;
    }

    @Override
    public IoBuffer fill(int size) {
        this.buf.fill(size);
        return this;
    }

    @Override
    public IoBuffer fillAndReset(int size) {
        this.buf.fillAndReset(size);
        return this;
    }

    @Override
    public boolean isAutoExpand() {
        return this.buf.isAutoExpand();
    }

    @Override
    public IoBuffer setAutoExpand(boolean autoExpand) {
        this.buf.setAutoExpand(autoExpand);
        return this;
    }

    @Override
    public IoBuffer expand(int pos, int expectedRemaining) {
        this.buf.expand(pos, expectedRemaining);
        return this;
    }

    @Override
    public IoBuffer expand(int expectedRemaining) {
        this.buf.expand(expectedRemaining);
        return this;
    }

    @Override
    public Object getObject() throws ClassNotFoundException {
        return this.buf.getObject();
    }

    @Override
    public Object getObject(ClassLoader classLoader) throws ClassNotFoundException {
        return this.buf.getObject(classLoader);
    }

    @Override
    public IoBuffer putObject(Object o) {
        this.buf.putObject(o);
        return this;
    }

    @Override
    public InputStream asInputStream() {
        return this.buf.asInputStream();
    }

    @Override
    public OutputStream asOutputStream() {
        return this.buf.asOutputStream();
    }

    @Override
    public IoBuffer duplicate() {
        return this.buf.duplicate();
    }

    @Override
    public IoBuffer slice() {
        return this.buf.slice();
    }

    @Override
    public IoBuffer asReadOnlyBuffer() {
        return this.buf.asReadOnlyBuffer();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public int minimumCapacity() {
        return this.buf.minimumCapacity();
    }

    @Override
    public IoBuffer minimumCapacity(int minimumCapacity) {
        this.buf.minimumCapacity(minimumCapacity);
        return this;
    }

    @Override
    public IoBuffer capacity(int newCapacity) {
        this.buf.capacity(newCapacity);
        return this;
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public int markValue() {
        return this.buf.markValue();
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public void free() {
        this.buf.free();
    }

    @Override
    public boolean isDerived() {
        return this.buf.isDerived();
    }

    @Override
    public boolean isAutoShrink() {
        return this.buf.isAutoShrink();
    }

    @Override
    public IoBuffer setAutoShrink(boolean autoShrink) {
        this.buf.setAutoShrink(autoShrink);
        return this;
    }

    @Override
    public IoBuffer shrink() {
        this.buf.shrink();
        return this;
    }

    @Override
    public int getMediumInt() {
        return this.buf.getMediumInt();
    }

    @Override
    public int getUnsignedMediumInt() {
        return this.buf.getUnsignedMediumInt();
    }

    @Override
    public int getMediumInt(int index) {
        return this.buf.getMediumInt(index);
    }

    @Override
    public int getUnsignedMediumInt(int index) {
        return this.buf.getUnsignedMediumInt(index);
    }

    @Override
    public IoBuffer putMediumInt(int value) {
        this.buf.putMediumInt(value);
        return this;
    }

    @Override
    public IoBuffer putMediumInt(int index, int value) {
        this.buf.putMediumInt(index, value);
        return this;
    }

    @Override
    public String getHexDump(int lengthLimit) {
        return this.buf.getHexDump(lengthLimit);
    }

    @Override
    public boolean prefixedDataAvailable(int prefixLength) {
        return this.buf.prefixedDataAvailable(prefixLength);
    }

    @Override
    public boolean prefixedDataAvailable(int prefixLength, int maxDataLength) {
        return this.buf.prefixedDataAvailable(prefixLength, maxDataLength);
    }

    @Override
    public int indexOf(byte b) {
        return this.buf.indexOf(b);
    }

    @Override
    public <E extends Enum<E>> E getEnum(Class<E> enumClass) {
        return this.buf.getEnum(enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnum(int index, Class<E> enumClass) {
        return this.buf.getEnum(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnumShort(Class<E> enumClass) {
        return this.buf.getEnumShort(enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnumShort(int index, Class<E> enumClass) {
        return this.buf.getEnumShort(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnumInt(Class<E> enumClass) {
        return this.buf.getEnumInt(enumClass);
    }

    @Override
    public <E extends Enum<E>> E getEnumInt(int index, Class<E> enumClass) {
        return this.buf.getEnumInt(index, enumClass);
    }

    @Override
    public IoBuffer putEnum(Enum<?> e) {
        this.buf.putEnum(e);
        return this;
    }

    @Override
    public IoBuffer putEnum(int index, Enum<?> e) {
        this.buf.putEnum(index, e);
        return this;
    }

    @Override
    public IoBuffer putEnumShort(Enum<?> e) {
        this.buf.putEnumShort(e);
        return this;
    }

    @Override
    public IoBuffer putEnumShort(int index, Enum<?> e) {
        this.buf.putEnumShort(index, e);
        return this;
    }

    @Override
    public IoBuffer putEnumInt(Enum<?> e) {
        this.buf.putEnumInt(e);
        return this;
    }

    @Override
    public IoBuffer putEnumInt(int index, Enum<?> e) {
        this.buf.putEnumInt(index, e);
        return this;
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSet(Class<E> enumClass) {
        return this.buf.getEnumSet(enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSet(int index, Class<E> enumClass) {
        return this.buf.getEnumSet(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetShort(Class<E> enumClass) {
        return this.buf.getEnumSetShort(enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetShort(int index, Class<E> enumClass) {
        return this.buf.getEnumSetShort(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetInt(Class<E> enumClass) {
        return this.buf.getEnumSetInt(enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetInt(int index, Class<E> enumClass) {
        return this.buf.getEnumSetInt(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetLong(Class<E> enumClass) {
        return this.buf.getEnumSetLong(enumClass);
    }

    @Override
    public <E extends Enum<E>> EnumSet<E> getEnumSetLong(int index, Class<E> enumClass) {
        return this.buf.getEnumSetLong(index, enumClass);
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSet(Set<E> set) {
        this.buf.putEnumSet(set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSet(int index, Set<E> set) {
        this.buf.putEnumSet(index, set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetShort(Set<E> set) {
        this.buf.putEnumSetShort(set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetShort(int index, Set<E> set) {
        this.buf.putEnumSetShort(index, set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetInt(Set<E> set) {
        this.buf.putEnumSetInt(set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetInt(int index, Set<E> set) {
        this.buf.putEnumSetInt(index, set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetLong(Set<E> set) {
        this.buf.putEnumSetLong(set);
        return this;
    }

    @Override
    public <E extends Enum<E>> IoBuffer putEnumSetLong(int index, Set<E> set) {
        this.buf.putEnumSetLong(index, set);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(byte value) {
        this.buf.putUnsigned(value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, byte value) {
        this.buf.putUnsigned(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(short value) {
        this.buf.putUnsigned(value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, short value) {
        this.buf.putUnsigned(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int value) {
        this.buf.putUnsigned(value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, int value) {
        this.buf.putUnsigned(index, value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(long value) {
        this.buf.putUnsigned(value);
        return this;
    }

    @Override
    public IoBuffer putUnsigned(int index, long value) {
        this.buf.putUnsigned(index, value);
        return this;
    }
}

