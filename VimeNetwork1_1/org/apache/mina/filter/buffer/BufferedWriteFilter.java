package org.apache.mina.filter.buffer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.util.LazyInitializedCacheMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BufferedWriteFilter extends IoFilterAdapter {
   private final Logger logger;
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   private int bufferSize;
   private final LazyInitializedCacheMap buffersMap;

   public BufferedWriteFilter() {
      this(8192, (LazyInitializedCacheMap)null);
   }

   public BufferedWriteFilter(int bufferSize) {
      this(bufferSize, (LazyInitializedCacheMap)null);
   }

   public BufferedWriteFilter(int bufferSize, LazyInitializedCacheMap buffersMap) {
      this.logger = LoggerFactory.getLogger(BufferedWriteFilter.class);
      this.bufferSize = 8192;
      this.bufferSize = bufferSize;
      if (buffersMap == null) {
         this.buffersMap = new LazyInitializedCacheMap();
      } else {
         this.buffersMap = buffersMap;
      }

   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public void setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
   }

   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
      Object data = writeRequest.getMessage();
      if (data instanceof IoBuffer) {
         this.write(session, (IoBuffer)data);
      } else {
         throw new IllegalArgumentException("This filter should only buffer IoBuffer objects");
      }
   }

   private void write(IoSession session, IoBuffer data) {
      IoBuffer dest = (IoBuffer)this.buffersMap.putIfAbsent(session, new IoBufferLazyInitializer(this.bufferSize));
      this.write(session, data, dest);
   }

   private void write(IoSession session, IoBuffer data, IoBuffer buf) {
      try {
         int len = data.remaining();
         if (len >= buf.capacity()) {
            IoFilter.NextFilter nextFilter = session.getFilterChain().getNextFilter((IoFilter)this);
            this.internalFlush(nextFilter, session, buf);
            nextFilter.filterWrite(session, new DefaultWriteRequest(data));
            return;
         }

         if (len > buf.limit() - buf.position()) {
            this.internalFlush(session.getFilterChain().getNextFilter((IoFilter)this), session, buf);
         }

         synchronized(buf) {
            buf.put(data);
         }
      } catch (Exception e) {
         session.getFilterChain().fireExceptionCaught(e);
      }

   }

   private void internalFlush(IoFilter.NextFilter nextFilter, IoSession session, IoBuffer buf) throws Exception {
      IoBuffer tmp = null;
      synchronized(buf) {
         buf.flip();
         tmp = buf.duplicate();
         buf.clear();
      }

      this.logger.debug((String)"Flushing buffer: {}", (Object)tmp);
      nextFilter.filterWrite(session, new DefaultWriteRequest(tmp));
   }

   public void flush(IoSession session) {
      try {
         this.internalFlush(session.getFilterChain().getNextFilter((IoFilter)this), session, (IoBuffer)this.buffersMap.get(session));
      } catch (Exception e) {
         session.getFilterChain().fireExceptionCaught(e);
      }

   }

   private void free(IoSession session) {
      IoBuffer buf = (IoBuffer)this.buffersMap.remove(session);
      if (buf != null) {
         buf.free();
      }

   }

   public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
      this.free(session);
      nextFilter.exceptionCaught(session, cause);
   }

   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      this.free(session);
      nextFilter.sessionClosed(session);
   }
}
