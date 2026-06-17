package org.apache.mina.filter.codec.demux;

public interface MessageEncoderFactory {
   MessageEncoder getEncoder() throws Exception;
}
