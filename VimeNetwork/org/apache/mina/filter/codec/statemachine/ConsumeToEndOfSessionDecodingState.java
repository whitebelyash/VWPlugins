package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class ConsumeToEndOfSessionDecodingState implements DecodingState {
   private IoBuffer buffer;
   private final int maxLength;

   public ConsumeToEndOfSessionDecodingState(int maxLength) {
      this.maxLength = maxLength;
   }

   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
      if (this.buffer == null) {
         this.buffer = IoBuffer.allocate(256).setAutoExpand(true);
      }

      if (this.buffer.position() + in.remaining() > this.maxLength) {
         throw new ProtocolDecoderException("Received data exceeds " + this.maxLength + " byte(s).");
      } else {
         this.buffer.put(in);
         return this;
      }
   }

   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
      DecodingState var2;
      try {
         if (this.buffer == null) {
            this.buffer = IoBuffer.allocate(0);
         }

         this.buffer.flip();
         var2 = this.finishDecode(this.buffer, out);
      } finally {
         this.buffer = null;
      }

      return var2;
   }

   protected abstract DecodingState finishDecode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;
}
