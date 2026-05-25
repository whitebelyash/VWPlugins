package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public interface MessageDecoder {
   MessageDecoderResult OK = MessageDecoderResult.OK;
   MessageDecoderResult NEED_DATA = MessageDecoderResult.NEED_DATA;
   MessageDecoderResult NOT_OK = MessageDecoderResult.NOT_OK;

   MessageDecoderResult decodable(IoSession var1, IoBuffer var2);

   MessageDecoderResult decode(IoSession var1, IoBuffer var2, ProtocolDecoderOutput var3) throws Exception;

   void finishDecode(IoSession var1, ProtocolDecoderOutput var2) throws Exception;
}
