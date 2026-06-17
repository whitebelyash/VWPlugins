package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public abstract class IntegerDecodingState implements DecodingState {
   private int counter;

   public DecodingState decode(IoBuffer in, ProtocolDecoderOutput out) throws Exception {
      int firstByte = 0;
      int secondByte = 0;

      for(int thirdByte = 0; in.hasRemaining(); ++this.counter) {
         switch (this.counter) {
            case 0:
               firstByte = in.getUnsigned();
               break;
            case 1:
               secondByte = in.getUnsigned();
               break;
            case 2:
               thirdByte = in.getUnsigned();
               break;
            case 3:
               this.counter = 0;
               return this.finishDecode(firstByte << 24 | secondByte << 16 | thirdByte << 8 | in.getUnsigned(), out);
            default:
               throw new InternalError();
         }
      }

      return this;
   }

   public DecodingState finishDecode(ProtocolDecoderOutput out) throws Exception {
      throw new ProtocolDecoderException("Unexpected end of session while waiting for an integer.");
   }

   protected abstract DecodingState finishDecode(int var1, ProtocolDecoderOutput var2) throws Exception;
}
