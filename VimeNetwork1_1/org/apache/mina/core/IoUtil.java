package org.apache.mina.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

public final class IoUtil {
   private static final IoSession[] EMPTY_SESSIONS = new IoSession[0];

   public static List broadcast(Object message, Collection sessions) {
      List<WriteFuture> answer = new ArrayList(sessions.size());
      broadcast(message, sessions.iterator(), answer);
      return answer;
   }

   public static List broadcast(Object message, Iterable sessions) {
      List<WriteFuture> answer = new ArrayList();
      broadcast(message, sessions.iterator(), answer);
      return answer;
   }

   public static List broadcast(Object message, Iterator sessions) {
      List<WriteFuture> answer = new ArrayList();
      broadcast(message, sessions, answer);
      return answer;
   }

   public static List broadcast(Object message, IoSession... sessions) {
      if (sessions == null) {
         sessions = EMPTY_SESSIONS;
      }

      List<WriteFuture> answer = new ArrayList(sessions.length);
      if (message instanceof IoBuffer) {
         for(IoSession s : sessions) {
            answer.add(s.write(((IoBuffer)message).duplicate()));
         }
      } else {
         for(IoSession s : sessions) {
            answer.add(s.write(message));
         }
      }

      return answer;
   }

   private static void broadcast(Object message, Iterator sessions, Collection answer) {
      if (message instanceof IoBuffer) {
         while(sessions.hasNext()) {
            IoSession s = (IoSession)sessions.next();
            answer.add(s.write(((IoBuffer)message).duplicate()));
         }
      } else {
         while(sessions.hasNext()) {
            IoSession s = (IoSession)sessions.next();
            answer.add(s.write(message));
         }
      }

   }

   public static void await(Iterable futures) throws InterruptedException {
      for(IoFuture f : futures) {
         f.await();
      }

   }

   public static void awaitUninterruptably(Iterable futures) {
      for(IoFuture f : futures) {
         f.awaitUninterruptibly();
      }

   }

   public static boolean await(Iterable futures, long timeout, TimeUnit unit) throws InterruptedException {
      return await(futures, unit.toMillis(timeout));
   }

   public static boolean await(Iterable futures, long timeoutMillis) throws InterruptedException {
      return await0(futures, timeoutMillis, true);
   }

   public static boolean awaitUninterruptibly(Iterable futures, long timeout, TimeUnit unit) {
      return awaitUninterruptibly(futures, unit.toMillis(timeout));
   }

   public static boolean awaitUninterruptibly(Iterable futures, long timeoutMillis) {
      try {
         return await0(futures, timeoutMillis, false);
      } catch (InterruptedException var4) {
         throw new InternalError();
      }
   }

   private static boolean await0(Iterable futures, long timeoutMillis, boolean interruptable) throws InterruptedException {
      long startTime = timeoutMillis <= 0L ? 0L : System.currentTimeMillis();
      long waitTime = timeoutMillis;
      boolean lastComplete = true;

      for(IoFuture f : futures) {
         if (interruptable) {
            lastComplete = f.await(waitTime);
         } else {
            lastComplete = f.awaitUninterruptibly(waitTime);
         }

         waitTime = timeoutMillis - (System.currentTimeMillis() - startTime);
         if ((lastComplete || waitTime <= 0L || lastComplete) && waitTime <= 0L) {
            break;
         }
      }

      Iterator i;
      return lastComplete && !i.hasNext();
   }

   private IoUtil() {
   }
}
