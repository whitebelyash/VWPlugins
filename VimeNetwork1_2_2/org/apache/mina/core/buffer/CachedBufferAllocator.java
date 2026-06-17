/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.buffer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.mina.core.buffer.AbstractIoBuffer;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.IoBufferAllocator;

public class CachedBufferAllocator
implements IoBufferAllocator {
    private static final int DEFAULT_MAX_POOL_SIZE = 8;
    private static final int DEFAULT_MAX_CACHED_BUFFER_SIZE = 262144;
    private final int maxPoolSize;
    private final int maxCachedBufferSize;
    private final ThreadLocal<Map<Integer, Queue<CachedBuffer>>> heapBuffers;
    private final ThreadLocal<Map<Integer, Queue<CachedBuffer>>> directBuffers;

    public CachedBufferAllocator() {
        this(8, 262144);
    }

    public CachedBufferAllocator(int maxPoolSize, int maxCachedBufferSize) {
        if (maxPoolSize < 0) {
            throw new IllegalArgumentException("maxPoolSize: " + maxPoolSize);
        }
        if (maxCachedBufferSize < 0) {
            throw new IllegalArgumentException("maxCachedBufferSize: " + maxCachedBufferSize);
        }
        this.maxPoolSize = maxPoolSize;
        this.maxCachedBufferSize = maxCachedBufferSize;
        this.heapBuffers = new ThreadLocal<Map<Integer, Queue<CachedBuffer>>>(){

            @Override
            protected Map<Integer, Queue<CachedBuffer>> initialValue() {
                return CachedBufferAllocator.this.newPoolMap();
            }
        };
        this.directBuffers = new ThreadLocal<Map<Integer, Queue<CachedBuffer>>>(){

            @Override
            protected Map<Integer, Queue<CachedBuffer>> initialValue() {
                return CachedBufferAllocator.this.newPoolMap();
            }
        };
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public int getMaxCachedBufferSize() {
        return this.maxCachedBufferSize;
    }

    Map<Integer, Queue<CachedBuffer>> newPoolMap() {
        HashMap<Integer, Queue<CachedBuffer>> poolMap = new HashMap<Integer, Queue<CachedBuffer>>();
        for (int i = 0; i < 31; ++i) {
            poolMap.put(1 << i, new ConcurrentLinkedQueue());
        }
        poolMap.put(0, new ConcurrentLinkedQueue());
        poolMap.put(Integer.MAX_VALUE, new ConcurrentLinkedQueue());
        return poolMap;
    }

    @Override
    public IoBuffer allocate(int requestedCapacity, boolean direct) {
        IoBuffer buf;
        int actualCapacity = IoBuffer.normalizeCapacity(requestedCapacity);
        if (this.maxCachedBufferSize != 0 && actualCapacity > this.maxCachedBufferSize) {
            buf = direct ? this.wrap(ByteBuffer.allocateDirect(actualCapacity)) : this.wrap(ByteBuffer.allocate(actualCapacity));
        } else {
            Queue<CachedBuffer> pool = direct ? this.directBuffers.get().get(actualCapacity) : this.heapBuffers.get().get(actualCapacity);
            buf = pool.poll();
            if (buf != null) {
                buf.clear();
                buf.setAutoExpand(false);
                buf.order(ByteOrder.BIG_ENDIAN);
            } else {
                buf = direct ? this.wrap(ByteBuffer.allocateDirect(actualCapacity)) : this.wrap(ByteBuffer.allocate(actualCapacity));
            }
        }
        buf.limit(requestedCapacity);
        return buf;
    }

    @Override
    public ByteBuffer allocateNioBuffer(int capacity, boolean direct) {
        return this.allocate(capacity, direct).buf();
    }

    @Override
    public IoBuffer wrap(ByteBuffer nioBuffer) {
        return new CachedBuffer(nioBuffer);
    }

    @Override
    public void dispose() {
    }

    private class CachedBuffer
    extends AbstractIoBuffer {
        private final Thread ownerThread;
        private ByteBuffer buf;

        protected CachedBuffer(ByteBuffer buf) {
            super(CachedBufferAllocator.this, buf.capacity());
            this.ownerThread = Thread.currentThread();
            this.buf = buf;
            buf.order(ByteOrder.BIG_ENDIAN);
        }

        protected CachedBuffer(CachedBuffer parent, ByteBuffer buf) {
            super(parent);
            this.ownerThread = Thread.currentThread();
            this.buf = buf;
        }

        @Override
        public ByteBuffer buf() {
            if (this.buf == null) {
                throw new IllegalStateException("Buffer has been freed already.");
            }
            return this.buf;
        }

        @Override
        protected void buf(ByteBuffer buf) {
            ByteBuffer oldBuf = this.buf;
            this.buf = buf;
            this.free(oldBuf);
        }

        @Override
        protected IoBuffer duplicate0() {
            return new CachedBuffer(this, this.buf().duplicate());
        }

        @Override
        protected IoBuffer slice0() {
            return new CachedBuffer(this, this.buf().slice());
        }

        @Override
        protected IoBuffer asReadOnlyBuffer0() {
            return new CachedBuffer(this, this.buf().asReadOnlyBuffer());
        }

        @Override
        public byte[] array() {
            return this.buf().array();
        }

        @Override
        public int arrayOffset() {
            return this.buf().arrayOffset();
        }

        @Override
        public boolean hasArray() {
            return this.buf().hasArray();
        }

        @Override
        public void free() {
            this.free(this.buf);
            this.buf = null;
        }

        private void free(ByteBuffer oldBuf) {
            if (oldBuf == null || CachedBufferAllocator.this.maxCachedBufferSize != 0 && oldBuf.capacity() > CachedBufferAllocator.this.maxCachedBufferSize || oldBuf.isReadOnly() || this.isDerived() || Thread.currentThread() != this.ownerThread) {
                return;
            }
            Queue pool = oldBuf.isDirect() ? (Queue)((Map)CachedBufferAllocator.this.directBuffers.get()).get(oldBuf.capacity()) : (Queue)((Map)CachedBufferAllocator.this.heapBuffers.get()).get(oldBuf.capacity());
            if (pool == null) {
                return;
            }
            if (CachedBufferAllocator.this.maxPoolSize == 0 || pool.size() < CachedBufferAllocator.this.maxPoolSize) {
                pool.offer(new CachedBuffer(oldBuf));
            }
        }
    }
}

