package org.apache.mina.filter.codec.demux;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class DemuxingProtocolCodecFactory implements ProtocolCodecFactory {
   private final DemuxingProtocolEncoder encoder = new DemuxingProtocolEncoder();
   private final DemuxingProtocolDecoder decoder = new DemuxingProtocolDecoder();

   public ProtocolEncoder getEncoder(IoSession session) throws Exception {
      return this.encoder;
   }

   public ProtocolDecoder getDecoder(IoSession session) throws Exception {
      return this.decoder;
   }

   public void addMessageEncoder(Class messageType, Class encoderClass) {
      this.encoder.addMessageEncoder(messageType, encoderClass);
   }

   public void addMessageEncoder(Class messageType, MessageEncoder encoder) {
      this.encoder.addMessageEncoder(messageType, encoder);
   }

   public void addMessageEncoder(Class messageType, MessageEncoderFactory factory) {
      this.encoder.addMessageEncoder(messageType, factory);
   }

   public void addMessageEncoder(Iterable messageTypes, Class encoderClass) {
      for(Class messageType : messageTypes) {
         this.addMessageEncoder(messageType, encoderClass);
      }

   }

   public void addMessageEncoder(Iterable messageTypes, MessageEncoder encoder) {
      for(Class messageType : messageTypes) {
         this.addMessageEncoder(messageType, encoder);
      }

   }

   public void addMessageEncoder(Iterable messageTypes, MessageEncoderFactory factory) {
      for(Class messageType : messageTypes) {
         this.addMessageEncoder(messageType, factory);
      }

   }

   public void addMessageDecoder(Class decoderClass) {
      this.decoder.addMessageDecoder(decoderClass);
   }

   public void addMessageDecoder(MessageDecoder decoder) {
      this.decoder.addMessageDecoder(decoder);
   }

   public void addMessageDecoder(MessageDecoderFactory factory) {
      this.decoder.addMessageDecoder(factory);
   }
}
