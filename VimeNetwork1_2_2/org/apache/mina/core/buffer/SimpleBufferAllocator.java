/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.apache.mina.core.buffer.AbstractIoBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferAllocator;

public class SimpleBufferAllocator
implements IoBufferAllocator {
    @Override
    public IoBuffer allocate(int capacity, boolean direct) {
        return this.wrap(this.allocateNioBuffer(capacity, direct));
    }

    @Override
    public ByteBuffer allocateNioBuffer(int capacity, boolean direct) {
        ByteBuffer nioBuffer = direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
        return nioBuffer;
    }

    @Override
    public IoBuffer wrap(ByteBuffer nioBuffer) {
        return new SimpleBuffer(nioBuffer);
    }

    @Override
    public void dispose() {
    }

    private class SimpleBuffer
    extends AbstractIoBuffer {
        private ByteBuffer buf;

        protected SimpleBuffer(ByteBuffer buf) {
            super(SimpleBufferAllocator.this, buf.capacity());
            this.buf = buf;
            buf.order(ByteOrder.BIG_ENDIAN);
        }

        protected SimpleBuffer(SimpleBuffer parent, ByteBuffer buf) {
            super(parent);
            this.buf = buf;
        }

        @Override
        public ByteBuffer buf() {
            return this.buf;
        }

        @Override
        protected void buf(ByteBuffer buf) {
            this.buf = buf;
        }

        @Override
        protected IoBuffer duplicate0() {
            return new SimpleBuffer(this, this.buf.duplicate());
        }

        @Override
        protected IoBuffer slice0() {
            return new SimpleBuffer(this, this.buf.slice());
        }

        @Override
        protected IoBuffer asReadOnlyBuffer0() {
            return new SimpleBuffer(this, this.buf.asReadOnlyBuffer());
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
        public boolean hasArray() {
            return this.buf.hasArray();
        }

        @Override
        public void free() {
        }
    }
}

