package org.apache.mina.filter.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public interface ProtocolDecoder {
   void decode(IoSession var1, IoBuffer var2, ProtocolDecoderOutput var3) throws Exception;

   void finishDecode(IoSession var1, ProtocolDecoderOutput var2) throws Exception;

   void dispose(IoSession var1) throws Exception;
}
