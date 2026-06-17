package org.apache.mina.transport.socket.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Iterator;
import org.apache.mina.core.polling.AbstractPollingIoConnector;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.transport.socket.DatagramConnector;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.DefaultDatagramSessionConfig;

public final class NioDatagramConnector extends AbstractPollingIoConnector implements DatagramConnector {
   public NioDatagramConnector() {
      super(new DefaultDatagramSessionConfig(), (Class)NioProcessor.class);
   }

   public NioDatagramConnector(int processorCount) {
      super(new DefaultDatagramSessionConfig(), NioProcessor.class, processorCount);
   }

   public NioDatagramConnector(IoProcessor processor) {
      super(new DefaultDatagramSessionConfig(), (IoProcessor)processor);
   }

   public NioDatagramConnector(Class processorClass, int processorCount) {
      super(new DefaultDatagramSessionConfig(), processorClass, processorCount);
   }

   public NioDatagramConnector(Class processorClass) {
      super(new DefaultDatagramSessionConfig(), (Class)processorClass);
   }

   public TransportMetadata getTransportMetadata() {
      return NioDatagramSession.METADATA;
   }

   public DatagramSessionConfig getSessionConfig() {
      return (DatagramSessionConfig)this.sessionConfig;
   }

   public InetSocketAddress getDefaultRemoteAddress() {
      return (InetSocketAddress)super.getDefaultRemoteAddress();
   }

   public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
      super.setDefaultRemoteAddress(defaultRemoteAddress);
   }

   protected void init() throws Exception {
   }

   protected DatagramChannel newHandle(SocketAddress localAddress) throws Exception {
      DatagramChannel ch = DatagramChannel.open();

      try {
         if (localAddress != null) {
            try {
               ch.socket().bind(localAddress);
               this.setDefaultLocalAddress(localAddress);
            } catch (IOException ioe) {
               String newMessage = "Error while binding on " + localAddress + "\n" + "original message : " + ioe.getMessage();
               Exception e = new IOException(newMessage);
               e.initCause(ioe.getCause());
               ch.close();
               throw e;
            }
         }

         return ch;
      } catch (Exception e) {
         ch.close();
         throw e;
      }
   }

   protected boolean connect(DatagramChannel handle, SocketAddress remoteAddress) throws Exception {
      handle.connect(remoteAddress);
      return true;
   }

   protected NioSession newSession(IoProcessor processor, DatagramChannel handle) {
      NioSession session = new NioDatagramSession(this, handle, processor);
      session.getConfig().setAll(this.getSessionConfig());
      return session;
   }

   protected void close(DatagramChannel handle) throws Exception {
      handle.disconnect();
      handle.close();
   }

   protected Iterator allHandles() {
      return Collections.EMPTY_LIST.iterator();
   }

   protected AbstractPollingIoConnector.ConnectionRequest getConnectionRequest(DatagramChannel handle) {
      throw new UnsupportedOperationException();
   }

   protected void destroy() throws Exception {
   }

   protected boolean finishConnect(DatagramChannel handle) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void register(DatagramChannel handle, AbstractPollingIoConnector.ConnectionRequest request) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected int select(int timeout) throws Exception {
      return 0;
   }

   protected Iterator selectedHandles() {
      return Collections.EMPTY_LIST.iterator();
   }

   protected void wakeup() {
   }
}
