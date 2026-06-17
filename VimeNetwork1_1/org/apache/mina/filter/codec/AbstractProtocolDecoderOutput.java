package org.apache.mina.filter.codec;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractProtocolDecoderOutput implements ProtocolDecoderOutput {
   private final Queue messageQueue = new LinkedList();

   public Queue getMessageQueue() {
      return this.messageQueue;
   }

   public void write(Object message) {
      if (message == null) {
         throw new IllegalArgumentException("message");
      } else {
         this.messageQueue.add(message);
      }
   }
}
