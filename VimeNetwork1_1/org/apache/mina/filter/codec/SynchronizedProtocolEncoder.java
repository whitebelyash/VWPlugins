package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public class SynchronizedProtocolEncoder implements ProtocolEncoder {
   private final ProtocolEncoder encoder;

   public SynchronizedProtocolEncoder(ProtocolEncoder encoder) {
      if (encoder == null) {
         throw new IllegalArgumentException("encoder");
      } else {
         this.encoder = encoder;
      }
   }

   public ProtocolEncoder getEncoder() {
      return this.encoder;
   }

   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
      synchronized(this.encoder) {
         this.encoder.encode(session, message, out);
      }
   }

   public void dispose(IoSession session) throws Exception {
      synchronized(this.encoder) {
         this.encoder.dispose(session);
      }
   }
}
