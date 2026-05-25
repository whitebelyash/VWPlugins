package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class FixedLengthDecodingState implements DecodingState {
   private final int length;
   private IoBuffer buffer;

   public FixedLengthDecodingState(int length) {
      this.length = length;
   }

   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
      if (this.buffer == null) {
         if (in.remaining() >= this.length) {
            int limit = in.limit();
            in.limit(in.position() + this.length);
            IoBuffer product = in.slice();
            in.position(in.position() + this.length);
            in.limit(limit);
            return this.finishDecode(product, out);
         } else {
            this.buffer = IoBuffer.allocate(this.length);
            this.buffer.put(in);
            return this;
         }
      } else if (in.remaining() >= this.length - this.buffer.position()) {
         int limit = in.limit();
         in.limit(in.position() + this.length - this.buffer.position());
         this.buffer.put(in);
         in.limit(limit);
         IoBuffer product = this.buffer;
         this.buffer = null;
         return this.finishDecode(product.flip(), out);
      } else {
         this.buffer.put(in);
         return this;
      }
   }

   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
      IoBuffer readData;
      if (this.buffer == null) {
         readData = IoBuffer.allocate(0);
      } else {
         readData = this.buffer.flip();
         this.buffer = null;
      }

      return this.finishDecode(readData, out);
   }

   protected abstract DecodingState finishDecode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;
}
