/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.buffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.buffer.IoBufferLazyInitializer;
import org.apache.mina.util.LazyInitializedCacheMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferedWriteFilter
extends IoFilterAdapter {
    private final Logger logger = LoggerFactory.getLogger(BufferedWriteFilter.class);
    public static final int DEFAULT_BUFFER_SIZE = 8192;
    private int bufferSize = 8192;
    private final LazyInitializedCacheMap<IoSession, IoBuffer> buffersMap;

    public BufferedWriteFilter() {
        this(8192, null);
    }

    public BufferedWriteFilter(int bufferSize) {
        this(bufferSize, null);
    }

    public BufferedWriteFilter(int bufferSize, LazyInitializedCacheMap<IoSession, IoBuffer> buffersMap) {
        this.bufferSize = bufferSize;
        this.buffersMap = buffersMap == null ? new LazyInitializedCacheMap() : buffersMap;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        Object data = writeRequest.getMessage();
        if (!(data instanceof IoBuffer)) {
            throw new IllegalArgumentException("This filter should only buffer IoBuffer objects");
        }
        this.write(session, (IoBuffer)data);
    }

    private void write(IoSession session, IoBuffer data) {
        IoBuffer dest = this.buffersMap.putIfAbsent(session, new IoBufferLazyInitializer(this.bufferSize));
        this.write(session, data, dest);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void write(IoSession session, IoBuffer data, IoBuffer buf) {
        try {
            int len = data.remaining();
            if (len >= buf.capacity()) {
                IoFilter.NextFilter nextFilter = session.getFilterChain().getNextFilter(this);
                this.internalFlush(nextFilter, session, buf);
                nextFilter.filterWrite(session, new DefaultWriteRequest(data));
                return;
            }
            if (len > buf.limit() - buf.position()) {
                this.internalFlush(session.getFilterChain().getNextFilter(this), session, buf);
            }
            IoBuffer ioBuffer = buf;
            synchronized (ioBuffer) {
                buf.put(data);
            }
        }
        catch (Exception e) {
            session.getFilterChain().fireExceptionCaught(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void internalFlush(IoFilter.NextFilter nextFilter, IoSession session, IoBuffer buf) throws Exception {
        IoBuffer tmp = null;
        IoBuffer ioBuffer = buf;
        synchronized (ioBuffer) {
            buf.flip();
            tmp = buf.duplicate();
            buf.clear();
        }
        this.logger.debug("Flushing buffer: {}", (Object)tmp);
        nextFilter.filterWrite(session, new DefaultWriteRequest(tmp));
    }

    public void flush(IoSession session) {
        try {
            this.internalFlush(session.getFilterChain().getNextFilter(this), session, this.buffersMap.get(session));
        }
        catch (Exception e) {
            session.getFilterChain().fireExceptionCaught(e);
        }
    }

    private void free(IoSession session) {
        IoBuffer buf = this.buffersMap.remove(session);
        if (buf != null) {
            buf.free();
        }
    }

    @Override
    public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        this.free(session);
        nextFilter.exceptionCaught(session, cause);
    }

    @Override
    public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        this.free(session);
        nextFilter.sessionClosed(session);
    }
}

