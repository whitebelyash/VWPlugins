package org.apache.mina.core.service;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.session.IoSessionConfig;

public abstract class AbstractIoAcceptor extends AbstractIoService implements IoAcceptor {
   private final List defaultLocalAddresses = new ArrayList();
   private final List unmodifiableDefaultLocalAddresses;
   private final Set boundAddresses;
   private boolean disconnectOnUnbind;
   protected final Object bindLock;

   protected AbstractIoAcceptor(IoSessionConfig sessionConfig, Executor executor) {
      super(sessionConfig, executor);
      this.unmodifiableDefaultLocalAddresses = Collections.unmodifiableList(this.defaultLocalAddresses);
      this.boundAddresses = new HashSet();
      this.disconnectOnUnbind = true;
      this.bindLock = new Object();
      this.defaultLocalAddresses.add((Object)null);
   }

   public SocketAddress getLocalAddress() {
      Set<SocketAddress> localAddresses = this.getLocalAddresses();
      return localAddresses.isEmpty() ? null : (SocketAddress)localAddresses.iterator().next();
   }

   public final Set getLocalAddresses() {
      Set<SocketAddress> localAddresses = new HashSet();
      synchronized(this.boundAddresses) {
         localAddresses.addAll(this.boundAddresses);
         return localAddresses;
      }
   }

   public SocketAddress getDefaultLocalAddress() {
      return this.defaultLocalAddresses.isEmpty() ? null : (SocketAddress)this.defaultLocalAddresses.iterator().next();
   }

   public final void setDefaultLocalAddress(SocketAddress localAddress) {
      this.setDefaultLocalAddresses(localAddress);
   }

   public final List getDefaultLocalAddresses() {
      return this.unmodifiableDefaultLocalAddresses;
   }

   public final void setDefaultLocalAddresses(List localAddresses) {
      if (localAddresses == null) {
         throw new IllegalArgumentException("localAddresses");
      } else {
         this.setDefaultLocalAddresses((Iterable)localAddresses);
      }
   }

   public final void setDefaultLocalAddresses(Iterable localAddresses) {
      if (localAddresses == null) {
         throw new IllegalArgumentException("localAddresses");
      } else {
         synchronized(this.bindLock) {
            synchronized(this.boundAddresses) {
               if (!this.boundAddresses.isEmpty()) {
                  throw new IllegalStateException("localAddress can't be set while the acceptor is bound.");
               }

               Collection<SocketAddress> newLocalAddresses = new ArrayList();

               for(SocketAddress a : localAddresses) {
                  this.checkAddressType(a);
                  newLocalAddresses.add(a);
               }

               if (newLocalAddresses.isEmpty()) {
                  throw new IllegalArgumentException("empty localAddresses");
               }

               this.defaultLocalAddresses.clear();
               this.defaultLocalAddresses.addAll(newLocalAddresses);
            }

         }
      }
   }

   public final void setDefaultLocalAddresses(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
      if (otherLocalAddresses == null) {
         otherLocalAddresses = new SocketAddress[0];
      }

      Collection<SocketAddress> newLocalAddresses = new ArrayList(otherLocalAddresses.length + 1);
      newLocalAddresses.add(firstLocalAddress);

      for(SocketAddress a : otherLocalAddresses) {
         newLocalAddresses.add(a);
      }

      this.setDefaultLocalAddresses((Iterable)newLocalAddresses);
   }

   public final boolean isCloseOnDeactivation() {
      return this.disconnectOnUnbind;
   }

   public final void setCloseOnDeactivation(boolean disconnectClientsOnUnbind) {
      this.disconnectOnUnbind = disconnectClientsOnUnbind;
   }

   public final void bind() throws IOException {
      this.bind((Iterable)this.getDefaultLocalAddresses());
   }

   public final void bind(SocketAddress localAddress) throws IOException {
      if (localAddress == null) {
         throw new IllegalArgumentException("localAddress");
      } else {
         List<SocketAddress> localAddresses = new ArrayList(1);
         localAddresses.add(localAddress);
         this.bind((Iterable)localAddresses);
      }
   }

   public final void bind(SocketAddress... addresses) throws IOException {
      if (addresses != null && addresses.length != 0) {
         List<SocketAddress> localAddresses = new ArrayList(2);

         for(SocketAddress address : addresses) {
            localAddresses.add(address);
         }

         this.bind((Iterable)localAddresses);
      } else {
         this.bind((Iterable)this.getDefaultLocalAddresses());
      }
   }

   public final void bind(SocketAddress firstLocalAddress, SocketAddress... addresses) throws IOException {
      if (firstLocalAddress == null) {
         this.bind((Iterable)this.getDefaultLocalAddresses());
      }

      if (addresses != null && addresses.length != 0) {
         List<SocketAddress> localAddresses = new ArrayList(2);
         localAddresses.add(firstLocalAddress);

         for(SocketAddress address : addresses) {
            localAddresses.add(address);
         }

         this.bind((Iterable)localAddresses);
      } else {
         this.bind((Iterable)this.getDefaultLocalAddresses());
      }
   }

   public final void bind(Iterable localAddresses) throws IOException {
      if (this.isDisposing()) {
         throw new IllegalStateException("The Accpetor disposed is being disposed.");
      } else if (localAddresses == null) {
         throw new IllegalArgumentException("localAddresses");
      } else {
         List<SocketAddress> localAddressesCopy = new ArrayList();

         for(SocketAddress a : localAddresses) {
            this.checkAddressType(a);
            localAddressesCopy.add(a);
         }

         if (localAddressesCopy.isEmpty()) {
            throw new IllegalArgumentException("localAddresses is empty.");
         } else {
            boolean activate = false;
            synchronized(this.bindLock) {
               synchronized(this.boundAddresses) {
                  if (this.boundAddresses.isEmpty()) {
                     activate = true;
                  }
               }

               if (this.getHandler() == null) {
                  throw new IllegalStateException("handler is not set.");
               }

               try {
                  Set<SocketAddress> addresses = this.bindInternal(localAddressesCopy);
                  synchronized(this.boundAddresses) {
                     this.boundAddresses.addAll(addresses);
                  }
               } catch (IOException e) {
                  throw e;
               } catch (RuntimeException e) {
                  throw e;
               } catch (Exception e) {
                  throw new RuntimeIoException("Failed to bind to: " + this.getLocalAddresses(), e);
               }
            }

            if (activate) {
               this.getListeners().fireServiceActivated();
            }

         }
      }
   }

   public final void unbind() {
      this.unbind((Iterable)this.getLocalAddresses());
   }

   public final void unbind(SocketAddress localAddress) {
      if (localAddress == null) {
         throw new IllegalArgumentException("localAddress");
      } else {
         List<SocketAddress> localAddresses = new ArrayList(1);
         localAddresses.add(localAddress);
         this.unbind((Iterable)localAddresses);
      }
   }

   public final void unbind(SocketAddress firstLocalAddress, SocketAddress... otherLocalAddresses) {
      if (firstLocalAddress == null) {
         throw new IllegalArgumentException("firstLocalAddress");
      } else if (otherLocalAddresses == null) {
         throw new IllegalArgumentException("otherLocalAddresses");
      } else {
         List<SocketAddress> localAddresses = new ArrayList();
         localAddresses.add(firstLocalAddress);
         Collections.addAll(localAddresses, otherLocalAddresses);
         this.unbind((Iterable)localAddresses);
      }
   }

   public final void unbind(Iterable localAddresses) {
      if (localAddresses == null) {
         throw new IllegalArgumentException("localAddresses");
      } else {
         boolean deactivate = false;
         synchronized(this.bindLock) {
            label70: {
               synchronized(this.boundAddresses) {
                  if (!this.boundAddresses.isEmpty()) {
                     List<SocketAddress> localAddressesCopy = new ArrayList();
                     int specifiedAddressCount = 0;

                     for(SocketAddress a : localAddresses) {
                        ++specifiedAddressCount;
                        if (a != null && this.boundAddresses.contains(a)) {
                           localAddressesCopy.add(a);
                        }
                     }

                     if (specifiedAddressCount == 0) {
                        throw new IllegalArgumentException("localAddresses is empty.");
                     }

                     if (!localAddressesCopy.isEmpty()) {
                        try {
                           this.unbind0(localAddressesCopy);
                        } catch (RuntimeException e) {
                           throw e;
                        } catch (Exception e) {
                           throw new RuntimeIoException("Failed to unbind from: " + this.getLocalAddresses(), e);
                        }

                        this.boundAddresses.removeAll(localAddressesCopy);
                        if (this.boundAddresses.isEmpty()) {
                           deactivate = true;
                        }
                     }
                     break label70;
                  }
               }

               return;
            }
         }

         if (deactivate) {
            this.getListeners().fireServiceDeactivated();
         }

      }
   }

   protected abstract Set bindInternal(List var1) throws Exception;

   protected abstract void unbind0(List var1) throws Exception;

   public String toString() {
      TransportMetadata m = this.getTransportMetadata();
      return '(' + m.getProviderName() + ' ' + m.getName() + " acceptor: " + (this.isActive() ? "localAddress(es): " + this.getLocalAddresses() + ", managedSessionCount: " + this.getManagedSessionCount() : "not bound") + ')';
   }

   private void checkAddressType(SocketAddress a) {
      if (a != null && !this.getTransportMetadata().getAddressType().isAssignableFrom(a.getClass())) {
         throw new IllegalArgumentException("localAddress type: " + a.getClass().getSimpleName() + " (expected: " + this.getTransportMetadata().getAddressType().getSimpleName() + ")");
      }
   }

   public static class AcceptorOperationFuture extends AbstractIoService.ServiceOperationFuture {
      private final List localAddresses;

      public AcceptorOperationFuture(List localAddresses) {
         this.localAddresses = new ArrayList(localAddresses);
      }

      public final List getLocalAddresses() {
         return Collections.unmodifiableList(this.localAddresses);
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("Acceptor operation : ");
         if (this.localAddresses != null) {
            boolean isFirst = true;

            for(SocketAddress address : this.localAddresses) {
               if (isFirst) {
                  isFirst = false;
               } else {
                  sb.append(", ");
               }

               sb.append(address);
            }
         }

         return sb.toString();
      }
   }
}
