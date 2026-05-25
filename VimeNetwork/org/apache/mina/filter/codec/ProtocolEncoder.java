package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public interface ProtocolEncoder {
   void encode(IoSession var1, Object var2, ProtocolEncoderOutput var3) throws Exception;

   void dispose(IoSession var1) throws Exception;
}
