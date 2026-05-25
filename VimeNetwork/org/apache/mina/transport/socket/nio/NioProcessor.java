package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.polling.AbstractPollingIoProcessor;
import org.apache.mina.core.session.SessionState;

public final class NioProcessor extends AbstractPollingIoProcessor {
   private Selector selector;
   private SelectorProvider selectorProvider = null;

   public NioProcessor(Executor executor) {
      super(executor);

      try {
         this.selector = Selector.open();
      } catch (IOException e) {
         throw new RuntimeIoException("Failed to open a selector.", e);
      }
   }

   public NioProcessor(Executor executor, SelectorProvider selectorProvider) {
      super(executor);

      try {
         if (selectorProvider == null) {
            this.selector = Selector.open();
         } else {
            this.selector = selectorProvider.openSelector();
         }

      } catch (IOException e) {
         throw new RuntimeIoException("Failed to open a selector.", e);
      }
   }

   protected void doDispose() throws Exception {
      this.selector.close();
   }

   protected int select(long timeout) throws Exception {
      return this.selector.select(timeout);
   }

   protected int select() throws Exception {
      return this.selector.select();
   }

   protected boolean isSelectorEmpty() {
      return this.selector.keys().isEmpty();
   }

   protected void wakeup() {
      this.wakeupCalled.getAndSet(true);
      this.selector.wakeup();
   }

   protected Iterator allSessions() {
      return new IoSessionIterator(this.selector.keys());
   }

   protected Iterator selectedSessions() {
      return new IoSessionIterator(this.selector.selectedKeys());
   }

   protected void init(NioSession session) throws Exception {
      SelectableChannel ch = (SelectableChannel)session.getChannel();
      ch.configureBlocking(false);
      session.setSelectionKey(ch.register(this.selector, 1, session));
   }

   protected void destroy(NioSession session) throws Exception {
      ByteChannel ch = session.getChannel();
      SelectionKey key = session.getSelectionKey();
      if (key != null) {
         key.cancel();
      }

      if (ch.isOpen()) {
         ch.close();
      }

   }

   protected void registerNewSelector() throws IOException {
      synchronized(this.selector) {
         Set<SelectionKey> keys = this.selector.keys();
         Selector newSelector = null;
         Object var11;
         if (this.selectorProvider == null) {
            var11 = Selector.open();
         } else {
            var11 = this.selectorProvider.openSelector();
         }

         for(SelectionKey key : keys) {
            SelectableChannel ch = key.channel();
            NioSession session = (NioSession)key.attachment();
            SelectionKey newKey = ch.register((Selector)var11, key.interestOps(), session);
            session.setSelectionKey(newKey);
         }

         this.selector.close();
         this.selector = (Selector)var11;
      }
   }

   protected boolean isBrokenConnection() throws IOException {
      boolean brokenSession = false;
      synchronized(this.selector) {
         for(SelectionKey key : this.selector.keys()) {
            SelectableChannel channel = key.channel();
            if (channel instanceof DatagramChannel && !((DatagramChannel)channel).isConnected() || channel instanceof SocketChannel && !((SocketChannel)channel).isConnected()) {
               key.cancel();
               brokenSession = true;
            }
         }

         return brokenSession;
      }
   }

   protected SessionState getState(NioSession session) {
      SelectionKey key = session.getSelectionKey();
      if (key == null) {
         return SessionState.OPENING;
      } else {
         return key.isValid() ? SessionState.OPENED : SessionState.CLOSING;
      }
   }

   protected boolean isReadable(NioSession session) {
      SelectionKey key = session.getSelectionKey();
      return key != null && key.isValid() && key.isReadable();
   }

   protected boolean isWritable(NioSession session) {
      SelectionKey key = session.getSelectionKey();
      return key != null && key.isValid() && key.isWritable();
   }

   protected boolean isInterestedInRead(NioSession session) {
      SelectionKey key = session.getSelectionKey();
      return key != null && key.isValid() && (key.interestOps() & 1) != 0;
   }

   protected boolean isInterestedInWrite(NioSession session) {
      SelectionKey key = session.getSelectionKey();
      return key != null && key.isValid() && (key.interestOps() & 4) != 0;
   }

   protected void setInterestedInRead(NioSession session, boolean isInterested) throws Exception {
      SelectionKey key = session.getSelectionKey();
      if (key != null && key.isValid()) {
         int oldInterestOps = key.interestOps();
         int newInterestOps;
         if (isInterested) {
            newInterestOps = oldInterestOps | 1;
         } else {
            newInterestOps = oldInterestOps & -2;
         }

         if (oldInterestOps != newInterestOps) {
            key.interestOps(newInterestOps);
         }

      }
   }

   protected void setInterestedInWrite(NioSession session, boolean isInterested) throws Exception {
      SelectionKey key = session.getSelectionKey();
      if (key != null && key.isValid()) {
         int newInterestOps = key.interestOps();
         if (isInterested) {
            newInterestOps |= 4;
         } else {
            newInterestOps &= -5;
         }

         key.interestOps(newInterestOps);
      }
   }

   protected int read(NioSession session, IoBuffer buf) throws Exception {
      ByteChannel channel = session.getChannel();
      return channel.read(buf.buf());
   }

   protected int write(NioSession session, IoBuffer buf, int length) throws IOException {
      if (buf.remaining() <= length) {
         return session.getChannel().write(buf.buf());
      } else {
         int oldLimit = buf.limit();
         buf.limit(buf.position() + length);

         int var5;
         try {
            var5 = session.getChannel().write(buf.buf());
         } finally {
            buf.limit(oldLimit);
         }

         return var5;
      }
   }

   protected int transferFile(NioSession session, FileRegion region, int length) throws Exception {
      try {
         return (int)region.getFileChannel().transferTo(region.getPosition(), (long)length, session.getChannel());
      } catch (IOException e) {
         String message = e.getMessage();
         if (message != null && message.contains("temporarily unavailable")) {
            return 0;
         } else {
            throw e;
         }
      }
   }

   protected static class IoSessionIterator implements Iterator {
      private final Iterator iterator;

      private IoSessionIterator(Set keys) {
         this.iterator = keys.iterator();
      }

      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      public Object next() {
         SelectionKey key = (SelectionKey)this.iterator.next();
         NioSession nioSession = (NioSession)key.attachment();
         return nioSession;
      }

      public void remove() {
         this.iterator.remove();
      }
   }
}
