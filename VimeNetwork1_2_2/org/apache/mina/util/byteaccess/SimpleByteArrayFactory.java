/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util.byteaccess;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.util.byteaccess.BufferByteArray;
import org.apache.mina.util.byteaccess.ByteArray;
import org.apache.mina.util.byteaccess.ByteArrayFactory;

public class SimpleByteArrayFactory
implements ByteArrayFactory {
    @Override
    public ByteArray create(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Buffer size must not be negative:" + size);
        }
        IoBuffer bb = IoBuffer.allocate(size);
        BufferByteArray ba = new BufferByteArray(bb){

            @Override
            public void free() {
            }
        };
        return ba;
    }
}

