package org.apache.mina.filter.codec;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;

public interface ProtocolDecoderOutput {
   void write(Object var1);

   void flush(IoFilter.NextFilter var1, IoSession var2);
}
