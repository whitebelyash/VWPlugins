package org.apache.mina.handler.demux;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.UnknownMessageTypeException;
import org.apache.mina.util.IdentityHashSet;

public class DemuxingIoHandler extends IoHandlerAdapter {
   private final Map receivedMessageHandlerCache = new ConcurrentHashMap();
   private final Map receivedMessageHandlers = new ConcurrentHashMap();
   private final Map sentMessageHandlerCache = new ConcurrentHashMap();
   private final Map sentMessageHandlers = new ConcurrentHashMap();
   private final Map exceptionHandlerCache = new ConcurrentHashMap();
   private final Map exceptionHandlers = new ConcurrentHashMap();

   public MessageHandler addReceivedMessageHandler(Class type, MessageHandler handler) {
      this.receivedMessageHandlerCache.clear();
      return (MessageHandler)this.receivedMessageHandlers.put(type, handler);
   }

   public MessageHandler removeReceivedMessageHandler(Class type) {
      this.receivedMessageHandlerCache.clear();
      return (MessageHandler)this.receivedMessageHandlers.remove(type);
   }

   public MessageHandler addSentMessageHandler(Class type, MessageHandler handler) {
      this.sentMessageHandlerCache.clear();
      return (MessageHandler)this.sentMessageHandlers.put(type, handler);
   }

   public MessageHandler removeSentMessageHandler(Class type) {
      this.sentMessageHandlerCache.clear();
      return (MessageHandler)this.sentMessageHandlers.remove(type);
   }

   public ExceptionHandler addExceptionHandler(Class type, ExceptionHandler handler) {
      this.exceptionHandlerCache.clear();
      return (ExceptionHandler)this.exceptionHandlers.put(type, handler);
   }

   public ExceptionHandler removeExceptionHandler(Class type) {
      this.exceptionHandlerCache.clear();
      return (ExceptionHandler)this.exceptionHandlers.remove(type);
   }

   public MessageHandler getMessageHandler(Class type) {
      return (MessageHandler)this.receivedMessageHandlers.get(type);
   }

   public Map getReceivedMessageHandlerMap() {
      return Collections.unmodifiableMap(this.receivedMessageHandlers);
   }

   public Map getSentMessageHandlerMap() {
      return Collections.unmodifiableMap(this.sentMessageHandlers);
   }

   public Map getExceptionHandlerMap() {
      return Collections.unmodifiableMap(this.exceptionHandlers);
   }

   public void messageReceived(IoSession session, Object message) throws Exception {
      MessageHandler<Object> handler = this.findReceivedMessageHandler(message.getClass());
      if (handler != null) {
         handler.handleMessage(session, message);
      } else {
         throw new UnknownMessageTypeException("No message handler found for message type: " + message.getClass().getSimpleName());
      }
   }

   public void messageSent(IoSession session, Object message) throws Exception {
      MessageHandler<Object> handler = this.findSentMessageHandler(message.getClass());
      if (handler != null) {
         handler.handleMessage(session, message);
      } else {
         throw new UnknownMessageTypeException("No handler found for message type: " + message.getClass().getSimpleName());
      }
   }

   public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
      ExceptionHandler<Throwable> handler = this.findExceptionHandler(cause.getClass());
      if (handler != null) {
         handler.exceptionCaught(session, cause);
      } else {
         throw new UnknownMessageTypeException("No handler found for exception type: " + cause.getClass().getSimpleName());
      }
   }

   protected MessageHandler findReceivedMessageHandler(Class type) {
      return this.findReceivedMessageHandler(type, (Set)null);
   }

   protected MessageHandler findSentMessageHandler(Class type) {
      return this.findSentMessageHandler(type, (Set)null);
   }

   protected ExceptionHandler findExceptionHandler(Class type) {
      return this.findExceptionHandler(type, (Set)null);
   }

   private MessageHandler findReceivedMessageHandler(Class type, Set triedClasses) {
      return (MessageHandler)this.findHandler(this.receivedMessageHandlers, this.receivedMessageHandlerCache, type, triedClasses);
   }

   private MessageHandler findSentMessageHandler(Class type, Set triedClasses) {
      return (MessageHandler)this.findHandler(this.sentMessageHandlers, this.sentMessageHandlerCache, type, triedClasses);
   }

   private ExceptionHandler findExceptionHandler(Class type, Set triedClasses) {
      return (ExceptionHandler)this.findHandler(this.exceptionHandlers, this.exceptionHandlerCache, type, triedClasses);
   }

   private Object findHandler(Map handlers, Map handlerCache, Class type, Set triedClasses) {
      if (triedClasses != null && triedClasses.contains(type)) {
         return null;
      } else {
         Object handler = handlerCache.get(type);
         if (handler != null) {
            return handler;
         } else {
            handler = handlers.get(type);
            if (handler == null) {
               if (triedClasses == null) {
                  triedClasses = new IdentityHashSet();
               }

               triedClasses.add(type);
               Class<?>[] interfaces = type.getInterfaces();

               for(Class element : interfaces) {
                  handler = this.findHandler(handlers, handlerCache, element, triedClasses);
                  if (handler != null) {
                     break;
                  }
               }
            }

            if (handler == null) {
               Class<?> superclass = type.getSuperclass();
               if (superclass != null) {
                  handler = this.findHandler(handlers, handlerCache, superclass, (Set)null);
               }
            }

            if (handler != null) {
               handlerCache.put(type, handler);
            }

            return handler;
         }
      }
   }
}
