package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public interface MessageEncoder {
   void encode(IoSession var1, Object var2, ProtocolEncoderOutput var3) throws Exception;
}
