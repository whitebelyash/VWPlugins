package org.apache.mina.transport.socket.nio;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.DefaultTransportMetadata;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.transport.socket.DatagramSessionConfig;

class NioDatagramSession extends NioSession {
   static final TransportMetadata METADATA = new DefaultTransportMetadata("nio", "datagram", true, false, InetSocketAddress.class, DatagramSessionConfig.class, new Class[]{IoBuffer.class});
   private final InetSocketAddress localAddress;
   private final InetSocketAddress remoteAddress;

   NioDatagramSession(IoService service, DatagramChannel channel, IoProcessor processor, SocketAddress remoteAddress) {
      super(processor, service, channel);
      this.config = new NioDatagramSessionConfig(channel);
      this.config.setAll(service.getSessionConfig());
      this.remoteAddress = (InetSocketAddress)remoteAddress;
      this.localAddress = (InetSocketAddress)channel.socket().getLocalSocketAddress();
   }

   NioDatagramSession(IoService service, DatagramChannel channel, IoProcessor processor) {
      this(service, channel, processor, channel.socket().getRemoteSocketAddress());
   }

   public DatagramSessionConfig getConfig() {
      return (DatagramSessionConfig)this.config;
   }

   DatagramChannel getChannel() {
      return (DatagramChannel)this.channel;
   }

   public TransportMetadata getTransportMetadata() {
      return METADATA;
   }

   public InetSocketAddress getRemoteAddress() {
      return this.remoteAddress;
   }

   public InetSocketAddress getLocalAddress() {
      return this.localAddress;
   }

   public InetSocketAddress getServiceAddress() {
      return (InetSocketAddress)super.getServiceAddress();
   }
}
