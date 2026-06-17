package org.apache.mina.filter.codec.demux;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.UnknownMessageTypeException;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.util.CopyOnWriteMap;
import org.apache.mina.util.IdentityHashSet;

public class DemuxingProtocolEncoder implements ProtocolEncoder {
   private final AttributeKey STATE = new AttributeKey(this.getClass(), "state");
   private final Map type2encoderFactory = new CopyOnWriteMap();
   private static final Class[] EMPTY_PARAMS = new Class[0];

   public void addMessageEncoder(Class messageType, Class encoderClass) {
      if (encoderClass == null) {
         throw new IllegalArgumentException("encoderClass");
      } else {
         try {
            encoderClass.getConstructor(EMPTY_PARAMS);
         } catch (NoSuchMethodException var4) {
            throw new IllegalArgumentException("The specified class doesn't have a public default constructor.");
         }

         boolean registered = false;
         if (MessageEncoder.class.isAssignableFrom(encoderClass)) {
            this.addMessageEncoder((Class)messageType, (MessageEncoderFactory)(new DefaultConstructorMessageEncoderFactory(encoderClass)));
            registered = true;
         }

         if (!registered) {
            throw new IllegalArgumentException("Unregisterable type: " + encoderClass);
         }
      }
   }

   public void addMessageEncoder(Class messageType, MessageEncoder encoder) {
      this.addMessageEncoder((Class)messageType, (MessageEncoderFactory)(new SingletonMessageEncoderFactory(encoder)));
   }

   public void addMessageEncoder(Class messageType, MessageEncoderFactory factory) {
      if (messageType == null) {
         throw new IllegalArgumentException("messageType");
      } else if (factory == null) {
         throw new IllegalArgumentException("factory");
      } else {
         synchronized(this.type2encoderFactory) {
            if (this.type2encoderFactory.containsKey(messageType)) {
               throw new IllegalStateException("The specified message type (" + messageType.getName() + ") is registered already.");
            } else {
               this.type2encoderFactory.put(messageType, factory);
            }
         }
      }
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

   public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
      State state = this.getState(session);
      MessageEncoder<Object> encoder = this.findEncoder(state, message.getClass());
      if (encoder != null) {
         encoder.encode(session, message, out);
      } else {
         throw new UnknownMessageTypeException("No message encoder found for message: " + message);
      }
   }

   protected MessageEncoder findEncoder(State state, Class type) {
      return this.findEncoder(state, type, (Set)null);
   }

   private MessageEncoder findEncoder(State state, Class type, Set triedClasses) {
      MessageEncoder encoder = null;
      if (triedClasses != null && triedClasses.contains(type)) {
         return null;
      } else {
         encoder = (MessageEncoder)state.findEncoderCache.get(type);
         if (encoder != null) {
            return encoder;
         } else {
            encoder = (MessageEncoder)state.type2encoder.get(type);
            if (encoder == null) {
               if (triedClasses == null) {
                  triedClasses = new IdentityHashSet();
               }

               triedClasses.add(type);
               Class<?>[] interfaces = type.getInterfaces();

               for(Class element : interfaces) {
                  encoder = this.findEncoder(state, element, triedClasses);
                  if (encoder != null) {
                     break;
                  }
               }
            }

            if (encoder == null) {
               Class<?> superclass = type.getSuperclass();
               if (superclass != null) {
                  encoder = this.findEncoder(state, superclass);
               }
            }

            if (encoder != null) {
               state.findEncoderCache.put(type, encoder);
               MessageEncoder<Object> tmpEncoder = (MessageEncoder)state.findEncoderCache.putIfAbsent(type, encoder);
               if (tmpEncoder != null) {
                  encoder = tmpEncoder;
               }
            }

            return encoder;
         }
      }
   }

   public void dispose(IoSession session) throws Exception {
      session.removeAttribute(this.STATE);
   }

   private State getState(IoSession session) throws Exception {
      State state = (State)session.getAttribute(this.STATE);
      if (state == null) {
         state = new State();
         State oldState = (State)session.setAttributeIfAbsent(this.STATE, state);
         if (oldState != null) {
            state = oldState;
         }
      }

      return state;
   }

   private class State {
      private final ConcurrentHashMap findEncoderCache;
      private final Map type2encoder;

      private State() throws Exception {
         this.findEncoderCache = new ConcurrentHashMap();
         this.type2encoder = new ConcurrentHashMap();

         for(Map.Entry e : DemuxingProtocolEncoder.this.type2encoderFactory.entrySet()) {
            this.type2encoder.put(e.getKey(), ((MessageEncoderFactory)e.getValue()).getEncoder());
         }

      }
   }

   private static class SingletonMessageEncoderFactory implements MessageEncoderFactory {
      private final MessageEncoder encoder;

      private SingletonMessageEncoderFactory(MessageEncoder encoder) {
         if (encoder == null) {
            throw new IllegalArgumentException("encoder");
         } else {
            this.encoder = encoder;
         }
      }

      public MessageEncoder getEncoder() {
         return this.encoder;
      }
   }

   private static class DefaultConstructorMessageEncoderFactory implements MessageEncoderFactory {
      private final Class encoderClass;

      private DefaultConstructorMessageEncoderFactory(Class encoderClass) {
         if (encoderClass == null) {
            throw new IllegalArgumentException("encoderClass");
         } else if (!MessageEncoder.class.isAssignableFrom(encoderClass)) {
            throw new IllegalArgumentException("encoderClass is not assignable to MessageEncoder");
         } else {
            this.encoderClass = encoderClass;
         }
      }

      public MessageEncoder getEncoder() throws Exception {
         return (MessageEncoder)this.encoderClass.newInstance();
      }
   }
}
