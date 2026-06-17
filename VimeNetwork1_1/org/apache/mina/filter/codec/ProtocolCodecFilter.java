package org.apache.mina.filter.codec;

import java.net.SocketAddress;
import java.util.Queue;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.file.FileRegion;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.future.DefaultWriteFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.DefaultWriteRequest;
import org.apache.mina.core.write.NothingWrittenException;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolCodecFilter extends IoFilterAdapter {
   private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolCodecFilter.class);
   private static final Class[] EMPTY_PARAMS = new Class[0];
   private static final IoBuffer EMPTY_BUFFER = IoBuffer.wrap(new byte[0]);
   private static final AttributeKey ENCODER = new AttributeKey(ProtocolCodecFilter.class, "encoder");
   private static final AttributeKey DECODER = new AttributeKey(ProtocolCodecFilter.class, "decoder");
   private static final AttributeKey DECODER_OUT = new AttributeKey(ProtocolCodecFilter.class, "decoderOut");
   private static final AttributeKey ENCODER_OUT = new AttributeKey(ProtocolCodecFilter.class, "encoderOut");
   private final ProtocolCodecFactory factory;

   public ProtocolCodecFilter(ProtocolCodecFactory factory) {
      if (factory == null) {
         throw new IllegalArgumentException("factory");
      } else {
         this.factory = factory;
      }
   }

   public ProtocolCodecFilter(final ProtocolEncoder encoder, final ProtocolDecoder decoder) {
      if (encoder == null) {
         throw new IllegalArgumentException("encoder");
      } else if (decoder == null) {
         throw new IllegalArgumentException("decoder");
      } else {
         this.factory = new ProtocolCodecFactory() {
            public ProtocolEncoder getEncoder(IoSession session) {
               return encoder;
            }

            public ProtocolDecoder getDecoder(IoSession session) {
               return decoder;
            }
         };
      }
   }

   public ProtocolCodecFilter(Class encoderClass, Class decoderClass) {
      if (encoderClass == null) {
         throw new IllegalArgumentException("encoderClass");
      } else if (decoderClass == null) {
         throw new IllegalArgumentException("decoderClass");
      } else if (!ProtocolEncoder.class.isAssignableFrom(encoderClass)) {
         throw new IllegalArgumentException("encoderClass: " + encoderClass.getName());
      } else if (!ProtocolDecoder.class.isAssignableFrom(decoderClass)) {
         throw new IllegalArgumentException("decoderClass: " + decoderClass.getName());
      } else {
         try {
            encoderClass.getConstructor(EMPTY_PARAMS);
         } catch (NoSuchMethodException var9) {
            throw new IllegalArgumentException("encoderClass doesn't have a public default constructor.");
         }

         try {
            decoderClass.getConstructor(EMPTY_PARAMS);
         } catch (NoSuchMethodException var8) {
            throw new IllegalArgumentException("decoderClass doesn't have a public default constructor.");
         }

         final ProtocolEncoder encoder;
         try {
            encoder = (ProtocolEncoder)encoderClass.newInstance();
         } catch (Exception var7) {
            throw new IllegalArgumentException("encoderClass cannot be initialized");
         }

         final ProtocolDecoder decoder;
         try {
            decoder = (ProtocolDecoder)decoderClass.newInstance();
         } catch (Exception var6) {
            throw new IllegalArgumentException("decoderClass cannot be initialized");
         }

         this.factory = new ProtocolCodecFactory() {
            public ProtocolEncoder getEncoder(IoSession session) throws Exception {
               return encoder;
            }

            public ProtocolDecoder getDecoder(IoSession session) throws Exception {
               return decoder;
            }
         };
      }
   }

   public ProtocolEncoder getEncoder(IoSession session) {
      return (ProtocolEncoder)session.getAttribute(ENCODER);
   }

   public void onPreAdd(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
      if (parent.contains((IoFilter)this)) {
         throw new IllegalArgumentException("You can't add the same filter instance more than once.  Create another instance and add it.");
      }
   }

   public void onPostRemove(IoFilterChain parent, String name, IoFilter.NextFilter nextFilter) throws Exception {
      this.disposeCodec(parent.getSession());
   }

   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
      LOGGER.debug((String)"Processing a MESSAGE_RECEIVED for session {}", (Object)session.getId());
      if (!(message instanceof IoBuffer)) {
         nextFilter.messageReceived(session, message);
      } else {
         IoBuffer in = (IoBuffer)message;
         ProtocolDecoder decoder = this.factory.getDecoder(session);
         ProtocolDecoderOutput decoderOut = this.getDecoderOut(session, nextFilter);

         while(in.hasRemaining()) {
            int oldPos = in.position();

            try {
               synchronized(session) {
                  decoder.decode(session, in, decoderOut);
               }

               decoderOut.flush(nextFilter, session);
            } catch (Exception var12) {
               ProtocolDecoderException pde;
               if (var12 instanceof ProtocolDecoderException) {
                  pde = (ProtocolDecoderException)var12;
               } else {
                  pde = new ProtocolDecoderException(var12);
               }

               if (pde.getHexdump() == null) {
                  int curPos = in.position();
                  in.position(oldPos);
                  pde.setHexdump(in.getHexDump());
                  in.position(curPos);
               }

               decoderOut.flush(nextFilter, session);
               nextFilter.exceptionCaught(session, pde);
               if (!(var12 instanceof RecoverableProtocolDecoderException) || in.position() == oldPos) {
                  break;
               }
            }
         }

      }
   }

   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
      if (!(writeRequest instanceof EncodedWriteRequest)) {
         if (writeRequest instanceof MessageWriteRequest) {
            MessageWriteRequest wrappedRequest = (MessageWriteRequest)writeRequest;
            nextFilter.messageSent(session, wrappedRequest.getParentRequest());
         } else {
            nextFilter.messageSent(session, writeRequest);
         }

      }
   }

   public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
      Object message = writeRequest.getMessage();
      if (!(message instanceof IoBuffer) && !(message instanceof FileRegion)) {
         ProtocolEncoder encoder = this.factory.getEncoder(session);
         ProtocolEncoderOutput encoderOut = this.getEncoderOut(session, nextFilter, writeRequest);
         if (encoder == null) {
            throw new ProtocolEncoderException("The encoder is null for the session " + session);
         } else {
            try {
               encoder.encode(session, message, encoderOut);
               Queue<Object> bufferQueue = ((AbstractProtocolEncoderOutput)encoderOut).getMessageQueue();

               while(!bufferQueue.isEmpty()) {
                  Object encodedMessage = bufferQueue.poll();
                  if (encodedMessage == null) {
                     break;
                  }

                  if (!(encodedMessage instanceof IoBuffer) || ((IoBuffer)encodedMessage).hasRemaining()) {
                     SocketAddress destination = writeRequest.getDestination();
                     WriteRequest encodedWriteRequest = new EncodedWriteRequest(encodedMessage, (WriteFuture)null, destination);
                     nextFilter.filterWrite(session, encodedWriteRequest);
                  }
               }

               nextFilter.filterWrite(session, new MessageWriteRequest(writeRequest));
            } catch (Exception e) {
               ProtocolEncoderException pee;
               if (e instanceof ProtocolEncoderException) {
                  pee = (ProtocolEncoderException)e;
               } else {
                  pee = new ProtocolEncoderException(e);
               }

               throw pee;
            }
         }
      } else {
         nextFilter.filterWrite(session, writeRequest);
      }
   }

   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      ProtocolDecoder decoder = this.factory.getDecoder(session);
      ProtocolDecoderOutput decoderOut = this.getDecoderOut(session, nextFilter);

      try {
         decoder.finishDecode(session, decoderOut);
      } catch (Exception e) {
         ProtocolDecoderException pde;
         if (e instanceof ProtocolDecoderException) {
            pde = (ProtocolDecoderException)e;
         } else {
            pde = new ProtocolDecoderException(e);
         }

         throw pde;
      } finally {
         this.disposeCodec(session);
         decoderOut.flush(nextFilter, session);
      }

      nextFilter.sessionClosed(session);
   }

   private void disposeCodec(IoSession session) {
      this.disposeEncoder(session);
      this.disposeDecoder(session);
      this.disposeDecoderOut(session);
   }

   private void disposeEncoder(IoSession session) {
      ProtocolEncoder encoder = (ProtocolEncoder)session.removeAttribute(ENCODER);
      if (encoder != null) {
         try {
            encoder.dispose(session);
         } catch (Exception var4) {
            LOGGER.warn("Failed to dispose: " + encoder.getClass().getName() + " (" + encoder + ')');
         }

      }
   }

   private void disposeDecoder(IoSession session) {
      ProtocolDecoder decoder = (ProtocolDecoder)session.removeAttribute(DECODER);
      if (decoder != null) {
         try {
            decoder.dispose(session);
         } catch (Exception var4) {
            LOGGER.warn("Failed to dispose: " + decoder.getClass().getName() + " (" + decoder + ')');
         }

      }
   }

   private ProtocolDecoderOutput getDecoderOut(IoSession session, IoFilter.NextFilter nextFilter) {
      ProtocolDecoderOutput out = (ProtocolDecoderOutput)session.getAttribute(DECODER_OUT);
      if (out == null) {
         out = new ProtocolDecoderOutputImpl();
         session.setAttribute(DECODER_OUT, out);
      }

      return out;
   }

   private ProtocolEncoderOutput getEncoderOut(IoSession session, IoFilter.NextFilter nextFilter, WriteRequest writeRequest) {
      ProtocolEncoderOutput out = (ProtocolEncoderOutput)session.getAttribute(ENCODER_OUT);
      if (out == null) {
         out = new ProtocolEncoderOutputImpl(session, nextFilter, writeRequest);
         session.setAttribute(ENCODER_OUT, out);
      }

      return out;
   }

   private void disposeDecoderOut(IoSession session) {
      session.removeAttribute(DECODER_OUT);
   }

   private static class EncodedWriteRequest extends DefaultWriteRequest {
      public EncodedWriteRequest(Object encodedMessage, WriteFuture future, SocketAddress destination) {
         super(encodedMessage, future, destination);
      }

      public boolean isEncoded() {
         return true;
      }
   }

   private static class MessageWriteRequest extends WriteRequestWrapper {
      public MessageWriteRequest(WriteRequest writeRequest) {
         super(writeRequest);
      }

      public Object getMessage() {
         return ProtocolCodecFilter.EMPTY_BUFFER;
      }

      public String toString() {
         return "MessageWriteRequest, parent : " + super.toString();
      }
   }

   private static class ProtocolDecoderOutputImpl extends AbstractProtocolDecoderOutput {
      public ProtocolDecoderOutputImpl() {
      }

      public void flush(IoFilter.NextFilter nextFilter, IoSession session) {
         Queue<Object> messageQueue = this.getMessageQueue();

         while(!messageQueue.isEmpty()) {
            nextFilter.messageReceived(session, messageQueue.poll());
         }

      }
   }

   private static class ProtocolEncoderOutputImpl extends AbstractProtocolEncoderOutput {
      private final IoSession session;
      private final IoFilter.NextFilter nextFilter;
      private final SocketAddress destination;

      public ProtocolEncoderOutputImpl(IoSession session, IoFilter.NextFilter nextFilter, WriteRequest writeRequest) {
         this.session = session;
         this.nextFilter = nextFilter;
         this.destination = writeRequest.getDestination();
      }

      public WriteFuture flush() {
         Queue<Object> bufferQueue = this.getMessageQueue();
         WriteFuture future = null;

         while(!bufferQueue.isEmpty()) {
            Object encodedMessage = bufferQueue.poll();
            if (encodedMessage == null) {
               break;
            }

            if (!(encodedMessage instanceof IoBuffer) || ((IoBuffer)encodedMessage).hasRemaining()) {
               future = new DefaultWriteFuture(this.session);
               this.nextFilter.filterWrite(this.session, new EncodedWriteRequest(encodedMessage, future, this.destination));
            }
         }

         if (future == null) {
            future = DefaultWriteFuture.newNotWrittenFuture(this.session, new NothingWrittenException(AbstractIoSession.MESSAGE_SENT_REQUEST));
         }

         return future;
      }
   }
}
