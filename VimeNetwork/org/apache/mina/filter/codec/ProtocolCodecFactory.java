package org.apache.mina.filter.codec;

import org.apache.mina.core.session.IoSession;

public interface ProtocolCodecFactory {
   ProtocolEncoder getEncoder(IoSession var1) throws Exception;

   ProtocolDecoder getDecoder(IoSession var1) throws Exception;
}
