package org.apache.mina.core.service;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.Set;
import org.apache.mina.core.session.IoSessionConfig;
import org.apache.mina.util.IdentityHashSet;

public class DefaultTransportMetadata implements TransportMetadata {
   private final String providerName;
   private final String name;
   private final boolean connectionless;
   private final boolean fragmentation;
   private final Class addressType;
   private final Class sessionConfigType;
   private final Set envelopeTypes;

   public DefaultTransportMetadata(String providerName, String name, boolean connectionless, boolean fragmentation, Class addressType, Class sessionConfigType, Class... envelopeTypes) {
      if (providerName == null) {
         throw new IllegalArgumentException("providerName");
      } else if (name == null) {
         throw new IllegalArgumentException("name");
      } else {
         providerName = providerName.trim().toLowerCase();
         if (providerName.length() == 0) {
            throw new IllegalArgumentException("providerName is empty.");
         } else {
            name = name.trim().toLowerCase();
            if (name.length() == 0) {
               throw new IllegalArgumentException("name is empty.");
            } else if (addressType == null) {
               throw new IllegalArgumentException("addressType");
            } else if (envelopeTypes == null) {
               throw new IllegalArgumentException("envelopeTypes");
            } else if (envelopeTypes.length == 0) {
               throw new IllegalArgumentException("envelopeTypes is empty.");
            } else if (sessionConfigType == null) {
               throw new IllegalArgumentException("sessionConfigType");
            } else {
               this.providerName = providerName;
               this.name = name;
               this.connectionless = connectionless;
               this.fragmentation = fragmentation;
               this.addressType = addressType;
               this.sessionConfigType = sessionConfigType;
               Set<Class<? extends Object>> newEnvelopeTypes = new IdentityHashSet();

               for(Class c : envelopeTypes) {
                  newEnvelopeTypes.add(c);
               }

               this.envelopeTypes = Collections.unmodifiableSet(newEnvelopeTypes);
            }
         }
      }
   }

   public Class getAddressType() {
      return this.addressType;
   }

   public Set getEnvelopeTypes() {
      return this.envelopeTypes;
   }

   public Class getSessionConfigType() {
      return this.sessionConfigType;
   }

   public String getProviderName() {
      return this.providerName;
   }

   public String getName() {
      return this.name;
   }

   public boolean isConnectionless() {
      return this.connectionless;
   }

   public boolean hasFragmentation() {
      return this.fragmentation;
   }

   public String toString() {
      return this.name;
   }
}
