package org.apache.mina.filter.codec.statemachine;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public interface DecodingState {
   DecodingState decode(IoBuffer var1, ProtocolDecoderOutput var2) throws Exception;

   DecodingState finishDecode(ProtocolDecoderOutput var1) throws Exception;
}
