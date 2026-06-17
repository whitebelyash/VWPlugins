package org.apache.mina.core.service;

import java.util.Set;

public interface TransportMetadata {
   String getProviderName();

   String getName();

   boolean isConnectionless();

   boolean hasFragmentation();

   Class getAddressType();

   Set getEnvelopeTypes();

   Class getSessionConfigType();
}
