package org.apache.mina.filter.codec;

import org.apache.mina.core.future.WriteFuture;

public interface ProtocolEncoderOutput {
   void write(Object var1);

   void mergeAll();

   WriteFuture flush();
}
