package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Executor;
import org.apache.mina.core.polling.AbstractPollingIoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.SocketSessionConfig;

public final class NioSocketConnector extends AbstractPollingIoConnector implements SocketConnector {
   private volatile Selector selector;

   public NioSocketConnector() {
      super(new DefaultSocketSessionConfig(), (Class)NioProcessor.class);
      ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
   }

   public NioSocketConnector(int processorCount) {
      super(new DefaultSocketSessionConfig(), NioProcessor.class, processorCount);
      ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
   }

   public NioSocketConnector(IoProcessor processor) {
      super(new DefaultSocketSessionConfig(), (IoProcessor)processor);
      ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
   }

   public NioSocketConnector(Executor executor, IoProcessor processor) {
      super(new DefaultSocketSessionConfig(), executor, processor);
      ((DefaultSocketSessionConfig)this.getSessionConfig()).init(this);
   }

   public NioSocketConnector(Class processorClass, int processorCount) {
      super(new DefaultSocketSessionConfig(), processorClass, processorCount);
   }

   public NioSocketConnector(Class processorClass) {
      super(new DefaultSocketSessionConfig(), (Class)processorClass);
   }

   protected void init() throws Exception {
      this.selector = Selector.open();
   }

   protected void destroy() throws Exception {
      if (this.selector != null) {
         this.selector.close();
      }

   }

   public TransportMetadata getTransportMetadata() {
      return NioSocketSession.METADATA;
   }

   public SocketSessionConfig getSessionConfig() {
      return (SocketSessionConfig)this.sessionConfig;
   }

   public InetSocketAddress getDefaultRemoteAddress() {
      return (InetSocketAddress)super.getDefaultRemoteAddress();
   }

   public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
      super.setDefaultRemoteAddress(defaultRemoteAddress);
   }

   protected Iterator allHandles() {
      return new SocketChannelIterator(this.selector.keys());
   }

   protected boolean connect(SocketChannel handle, SocketAddress remoteAddress) throws Exception {
      return handle.connect(remoteAddress);
   }

   protected AbstractPollingIoConnector.ConnectionRequest getConnectionRequest(SocketChannel handle) {
      SelectionKey key = handle.keyFor(this.selector);
      return key != null && key.isValid() ? (AbstractPollingIoConnector.ConnectionRequest)key.attachment() : null;
   }

   protected void close(SocketChannel handle) throws Exception {
      SelectionKey key = handle.keyFor(this.selector);
      if (key != null) {
         key.cancel();
      }

      handle.close();
   }

   protected boolean finishConnect(SocketChannel handle) throws Exception {
      if (handle.finishConnect()) {
         SelectionKey key = handle.keyFor(this.selector);
         if (key != null) {
            key.cancel();
         }

         return true;
      } else {
         return false;
      }
   }

   protected SocketChannel newHandle(SocketAddress localAddress) throws Exception {
      SocketChannel ch = SocketChannel.open();
      int receiveBufferSize = this.getSessionConfig().getReceiveBufferSize();
      if (receiveBufferSize > 65535) {
         ch.socket().setReceiveBufferSize(receiveBufferSize);
      }

      if (localAddress != null) {
         try {
            ch.socket().bind(localAddress);
         } catch (IOException ioe) {
            String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
            Exception e = new IOException(newMessage);
            e.initCause(ioe.getCause());
            ch.close();
            throw e;
         }
      }

      ch.configureBlocking(false);
      return ch;
   }

   protected NioSession newSession(IoProcessor processor, SocketChannel handle) {
      return new NioSocketSession(this, processor, handle);
   }

   protected void register(SocketChannel handle, AbstractPollingIoConnector.ConnectionRequest request) throws Exception {
      handle.register(this.selector, 8, request);
   }

   protected int select(int timeout) throws Exception {
      return this.selector.select((long)timeout);
   }

   protected Iterator selectedHandles() {
      return new SocketChannelIterator(this.selector.selectedKeys());
   }

   protected void wakeup() {
      this.selector.wakeup();
   }

   private static class SocketChannelIterator implements Iterator {
      private final Iterator i;

      private SocketChannelIterator(Collection selectedKeys) {
         this.i = selectedKeys.iterator();
      }

      public boolean hasNext() {
         return this.i.hasNext();
      }

      public SocketChannel next() {
         SelectionKey key = (SelectionKey)this.i.next();
         return (SocketChannel)key.channel();
      }

      public void remove() {
         this.i.remove();
      }
   }
}
