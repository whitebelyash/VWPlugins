package org.apache.mina.transport.vmpipe;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.service.AbstractIoAcceptor;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.IdleStatusChecker;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;

public final class VmPipeAcceptor extends AbstractIoAcceptor {
   private IdleStatusChecker idleChecker;
   static final Map boundHandlers = new HashMap();

   public VmPipeAcceptor() {
      this((Executor)null);
   }

   public VmPipeAcceptor(Executor executor) {
      super(new DefaultVmPipeSessionConfig(), executor);
      this.idleChecker = new IdleStatusChecker();
      this.executeWorker(this.idleChecker.getNotifyingTask(), "idleStatusChecker");
   }

   public TransportMetadata getTransportMetadata() {
      return VmPipeSession.METADATA;
   }

   public VmPipeSessionConfig getSessionConfig() {
      return (VmPipeSessionConfig)this.sessionConfig;
   }

   public VmPipeAddress getLocalAddress() {
      return (VmPipeAddress)super.getLocalAddress();
   }

   public VmPipeAddress getDefaultLocalAddress() {
      return (VmPipeAddress)super.getDefaultLocalAddress();
   }

   public void setDefaultLocalAddress(VmPipeAddress localAddress) {
      super.setDefaultLocalAddress(localAddress);
   }

   protected void dispose0() throws Exception {
      this.idleChecker.getNotifyingTask().cancel();
      this.unbind();
   }

   protected Set bindInternal(List localAddresses) throws IOException {
      Set<SocketAddress> newLocalAddresses = new HashSet();
      synchronized(boundHandlers) {
         for(SocketAddress a : localAddresses) {
            VmPipeAddress localAddress = (VmPipeAddress)a;
            if (localAddress != null && localAddress.getPort() != 0) {
               if (localAddress.getPort() < 0) {
                  throw new IOException("Bind port number must be 0 or above.");
               }

               if (boundHandlers.containsKey(localAddress)) {
                  throw new IOException("Address already bound: " + localAddress);
               }
            } else {
               localAddress = null;

               for(int i = 10000; i < Integer.MAX_VALUE; ++i) {
                  VmPipeAddress newLocalAddress = new VmPipeAddress(i);
                  if (!boundHandlers.containsKey(newLocalAddress) && !newLocalAddresses.contains(newLocalAddress)) {
                     localAddress = newLocalAddress;
                     break;
                  }
               }

               if (localAddress == null) {
                  throw new IOException("No port available.");
               }
            }

            newLocalAddresses.add(localAddress);
         }

         for(SocketAddress a : newLocalAddresses) {
            VmPipeAddress localAddress = (VmPipeAddress)a;
            if (boundHandlers.containsKey(localAddress)) {
               for(SocketAddress a2 : newLocalAddresses) {
                  boundHandlers.remove(a2);
               }

               throw new IOException("Duplicate local address: " + a);
            }

            boundHandlers.put(localAddress, new VmPipe(this, localAddress, this.getHandler(), this.getListeners()));
         }

         return newLocalAddresses;
      }
   }

   protected void unbind0(List localAddresses) {
      synchronized(boundHandlers) {
         for(SocketAddress a : localAddresses) {
            boundHandlers.remove(a);
         }

      }
   }

   public IoSession newSession(SocketAddress remoteAddress, SocketAddress localAddress) {
      throw new UnsupportedOperationException();
   }

   void doFinishSessionInitialization(IoSession session, IoFuture future) {
      this.initSession(session, future, (IoSessionInitializer)null);
   }
}
