package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class SkippingState implements DecodingState {
   private int skippedBytes;

   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
      int beginPos = in.position();
      int limit = in.limit();

      for(int i = beginPos; i < limit; ++i) {
         byte b = in.get(i);
         if (!this.canSkip(b)) {
            in.position(i);
            int answer = this.skippedBytes;
            this.skippedBytes = 0;
            return this.finishDecode(answer);
         }

         ++this.skippedBytes;
      }

      in.position(limit);
      return this;
   }

   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
      return this.finishDecode(this.skippedBytes);
   }

   protected abstract boolean canSkip(byte var1);

   protected abstract DecodingState finishDecode(int var1) throws Exception;
}
