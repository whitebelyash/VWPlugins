/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.codec.serialization;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class ObjectSerializationDecoder
extends CumulativeProtocolDecoder {
    private final ClassLoader classLoader;
    private int maxObjectSize = 0x100000;

    public ObjectSerializationDecoder() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ObjectSerializationDecoder(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new IllegalArgumentException("classLoader");
        }
        this.classLoader = classLoader;
    }

    public int getMaxObjectSize() {
        return this.maxObjectSize;
    }

    public void setMaxObjectSize(int maxObjectSize) {
        if (maxObjectSize <= 0) {
            throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
        }
        this.maxObjectSize = maxObjectSize;
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        if (!in.prefixedDataAvailable(4, this.maxObjectSize)) {
            return false;
        }
        out.write(in.getObject(this.classLoader));
        return true;
    }
}

