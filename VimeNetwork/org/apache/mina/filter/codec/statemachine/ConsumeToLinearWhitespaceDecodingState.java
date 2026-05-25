package org.apache.mina.filter.codec.statemachine;

public abstract class ConsumeToLinearWhitespaceDecodingState extends ConsumeToDynamicTerminatorDecodingState {
   protected boolean isTerminator(byte b) {
      return b == 32 || b == 9;
   }
}
