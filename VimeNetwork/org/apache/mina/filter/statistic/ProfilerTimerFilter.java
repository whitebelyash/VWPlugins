package org.apache.mina.filter.statistic;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class ProfilerTimerFilter extends IoFilterAdapter {
   private volatile TimeUnit timeUnit;
   private TimerWorker messageReceivedTimerWorker;
   private boolean profileMessageReceived;
   private TimerWorker messageSentTimerWorker;
   private boolean profileMessageSent;
   private TimerWorker sessionCreatedTimerWorker;
   private boolean profileSessionCreated;
   private TimerWorker sessionOpenedTimerWorker;
   private boolean profileSessionOpened;
   private TimerWorker sessionIdleTimerWorker;
   private boolean profileSessionIdle;
   private TimerWorker sessionClosedTimerWorker;
   private boolean profileSessionClosed;

   public ProfilerTimerFilter() {
      this(TimeUnit.MILLISECONDS, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
   }

   public ProfilerTimerFilter(TimeUnit timeUnit) {
      this(timeUnit, IoEventType.MESSAGE_RECEIVED, IoEventType.MESSAGE_SENT);
   }

   public ProfilerTimerFilter(TimeUnit timeUnit, IoEventType... eventTypes) {
      this.profileMessageReceived = false;
      this.profileMessageSent = false;
      this.profileSessionCreated = false;
      this.profileSessionOpened = false;
      this.profileSessionIdle = false;
      this.profileSessionClosed = false;
      this.timeUnit = timeUnit;
      this.setProfilers(eventTypes);
   }

   private void setProfilers(IoEventType... eventTypes) {
      for(IoEventType type : eventTypes) {
         switch (type) {
            case MESSAGE_RECEIVED:
               this.messageReceivedTimerWorker = new TimerWorker();
               this.profileMessageReceived = true;
               break;
            case MESSAGE_SENT:
               this.messageSentTimerWorker = new TimerWorker();
               this.profileMessageSent = true;
               break;
            case SESSION_CREATED:
               this.sessionCreatedTimerWorker = new TimerWorker();
               this.profileSessionCreated = true;
               break;
            case SESSION_OPENED:
               this.sessionOpenedTimerWorker = new TimerWorker();
               this.profileSessionOpened = true;
               break;
            case SESSION_IDLE:
               this.sessionIdleTimerWorker = new TimerWorker();
               this.profileSessionIdle = true;
               break;
            case SESSION_CLOSED:
               this.sessionClosedTimerWorker = new TimerWorker();
               this.profileSessionClosed = true;
         }
      }

   }

   public void setTimeUnit(TimeUnit timeUnit) {
      this.timeUnit = timeUnit;
   }

   public void profile(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            this.profileMessageReceived = true;
            if (this.messageReceivedTimerWorker == null) {
               this.messageReceivedTimerWorker = new TimerWorker();
            }

            return;
         case MESSAGE_SENT:
            this.profileMessageSent = true;
            if (this.messageSentTimerWorker == null) {
               this.messageSentTimerWorker = new TimerWorker();
            }

            return;
         case SESSION_CREATED:
            this.profileSessionCreated = true;
            if (this.sessionCreatedTimerWorker == null) {
               this.sessionCreatedTimerWorker = new TimerWorker();
            }

            return;
         case SESSION_OPENED:
            this.profileSessionOpened = true;
            if (this.sessionOpenedTimerWorker == null) {
               this.sessionOpenedTimerWorker = new TimerWorker();
            }

            return;
         case SESSION_IDLE:
            this.profileSessionIdle = true;
            if (this.sessionIdleTimerWorker == null) {
               this.sessionIdleTimerWorker = new TimerWorker();
            }

            return;
         case SESSION_CLOSED:
            this.profileSessionClosed = true;
            if (this.sessionClosedTimerWorker == null) {
               this.sessionClosedTimerWorker = new TimerWorker();
            }

            return;
         default:
      }
   }

   public void stopProfile(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            this.profileMessageReceived = false;
            return;
         case MESSAGE_SENT:
            this.profileMessageSent = false;
            return;
         case SESSION_CREATED:
            this.profileSessionCreated = false;
            return;
         case SESSION_OPENED:
            this.profileSessionOpened = false;
            return;
         case SESSION_IDLE:
            this.profileSessionIdle = false;
            return;
         case SESSION_CLOSED:
            this.profileSessionClosed = false;
            return;
         default:
      }
   }

   public Set getEventsToProfile() {
      Set<IoEventType> set = new HashSet();
      if (this.profileMessageReceived) {
         set.add(IoEventType.MESSAGE_RECEIVED);
      }

      if (this.profileMessageSent) {
         set.add(IoEventType.MESSAGE_SENT);
      }

      if (this.profileSessionCreated) {
         set.add(IoEventType.SESSION_CREATED);
      }

      if (this.profileSessionOpened) {
         set.add(IoEventType.SESSION_OPENED);
      }

      if (this.profileSessionIdle) {
         set.add(IoEventType.SESSION_IDLE);
      }

      if (this.profileSessionClosed) {
         set.add(IoEventType.SESSION_CLOSED);
      }

      return set;
   }

   public void setEventsToProfile(IoEventType... eventTypes) {
      this.setProfilers(eventTypes);
   }

   public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
      if (this.profileMessageReceived) {
         long start = this.timeNow();
         nextFilter.messageReceived(session, message);
         long end = this.timeNow();
         this.messageReceivedTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.messageReceived(session, message);
      }

   }

   public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
      if (this.profileMessageSent) {
         long start = this.timeNow();
         nextFilter.messageSent(session, writeRequest);
         long end = this.timeNow();
         this.messageSentTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.messageSent(session, writeRequest);
      }

   }

   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      if (this.profileSessionCreated) {
         long start = this.timeNow();
         nextFilter.sessionCreated(session);
         long end = this.timeNow();
         this.sessionCreatedTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.sessionCreated(session);
      }

   }

   public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      if (this.profileSessionOpened) {
         long start = this.timeNow();
         nextFilter.sessionOpened(session);
         long end = this.timeNow();
         this.sessionOpenedTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.sessionOpened(session);
      }

   }

   public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
      if (this.profileSessionIdle) {
         long start = this.timeNow();
         nextFilter.sessionIdle(session, status);
         long end = this.timeNow();
         this.sessionIdleTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.sessionIdle(session, status);
      }

   }

   public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      if (this.profileSessionClosed) {
         long start = this.timeNow();
         nextFilter.sessionClosed(session);
         long end = this.timeNow();
         this.sessionClosedTimerWorker.addNewDuration(end - start);
      } else {
         nextFilter.sessionClosed(session);
      }

   }

   public double getAverageTime(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            if (this.profileMessageReceived) {
               return this.messageReceivedTimerWorker.getAverage();
            }
            break;
         case MESSAGE_SENT:
            if (this.profileMessageSent) {
               return this.messageSentTimerWorker.getAverage();
            }
            break;
         case SESSION_CREATED:
            if (this.profileSessionCreated) {
               return this.sessionCreatedTimerWorker.getAverage();
            }
            break;
         case SESSION_OPENED:
            if (this.profileSessionOpened) {
               return this.sessionOpenedTimerWorker.getAverage();
            }
            break;
         case SESSION_IDLE:
            if (this.profileSessionIdle) {
               return this.sessionIdleTimerWorker.getAverage();
            }
            break;
         case SESSION_CLOSED:
            if (this.profileSessionClosed) {
               return this.sessionClosedTimerWorker.getAverage();
            }
      }

      throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
   }

   public long getTotalCalls(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            if (this.profileMessageReceived) {
               return this.messageReceivedTimerWorker.getCallsNumber();
            }
            break;
         case MESSAGE_SENT:
            if (this.profileMessageSent) {
               return this.messageSentTimerWorker.getCallsNumber();
            }
            break;
         case SESSION_CREATED:
            if (this.profileSessionCreated) {
               return this.sessionCreatedTimerWorker.getCallsNumber();
            }
            break;
         case SESSION_OPENED:
            if (this.profileSessionOpened) {
               return this.sessionOpenedTimerWorker.getCallsNumber();
            }
            break;
         case SESSION_IDLE:
            if (this.profileSessionIdle) {
               return this.sessionIdleTimerWorker.getCallsNumber();
            }
            break;
         case SESSION_CLOSED:
            if (this.profileSessionClosed) {
               return this.sessionClosedTimerWorker.getCallsNumber();
            }
      }

      throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
   }

   public long getTotalTime(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            if (this.profileMessageReceived) {
               return this.messageReceivedTimerWorker.getTotal();
            }
            break;
         case MESSAGE_SENT:
            if (this.profileMessageSent) {
               return this.messageSentTimerWorker.getTotal();
            }
            break;
         case SESSION_CREATED:
            if (this.profileSessionCreated) {
               return this.sessionCreatedTimerWorker.getTotal();
            }
            break;
         case SESSION_OPENED:
            if (this.profileSessionOpened) {
               return this.sessionOpenedTimerWorker.getTotal();
            }
            break;
         case SESSION_IDLE:
            if (this.profileSessionIdle) {
               return this.sessionIdleTimerWorker.getTotal();
            }
            break;
         case SESSION_CLOSED:
            if (this.profileSessionClosed) {
               return this.sessionClosedTimerWorker.getTotal();
            }
      }

      throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
   }

   public long getMinimumTime(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            if (this.profileMessageReceived) {
               return this.messageReceivedTimerWorker.getMinimum();
            }
            break;
         case MESSAGE_SENT:
            if (this.profileMessageSent) {
               return this.messageSentTimerWorker.getMinimum();
            }
            break;
         case SESSION_CREATED:
            if (this.profileSessionCreated) {
               return this.sessionCreatedTimerWorker.getMinimum();
            }
            break;
         case SESSION_OPENED:
            if (this.profileSessionOpened) {
               return this.sessionOpenedTimerWorker.getMinimum();
            }
            break;
         case SESSION_IDLE:
            if (this.profileSessionIdle) {
               return this.sessionIdleTimerWorker.getMinimum();
            }
            break;
         case SESSION_CLOSED:
            if (this.profileSessionClosed) {
               return this.sessionClosedTimerWorker.getMinimum();
            }
      }

      throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
   }

   public long getMaximumTime(IoEventType type) {
      switch (type) {
         case MESSAGE_RECEIVED:
            if (this.profileMessageReceived) {
               return this.messageReceivedTimerWorker.getMaximum();
            }
            break;
         case MESSAGE_SENT:
            if (this.profileMessageSent) {
               return this.messageSentTimerWorker.getMaximum();
            }
            break;
         case SESSION_CREATED:
            if (this.profileSessionCreated) {
               return this.sessionCreatedTimerWorker.getMaximum();
            }
            break;
         case SESSION_OPENED:
            if (this.profileSessionOpened) {
               return this.sessionOpenedTimerWorker.getMaximum();
            }
            break;
         case SESSION_IDLE:
            if (this.profileSessionIdle) {
               return this.sessionIdleTimerWorker.getMaximum();
            }
            break;
         case SESSION_CLOSED:
            if (this.profileSessionClosed) {
               return this.sessionClosedTimerWorker.getMaximum();
            }
      }

      throw new IllegalArgumentException("You are not monitoring this event.  Please add this event first.");
   }

   private long timeNow() {
      switch (this.timeUnit) {
         case SECONDS:
            return System.currentTimeMillis() / 1000L;
         case MICROSECONDS:
            return System.nanoTime() / 1000L;
         case NANOSECONDS:
            return System.nanoTime();
         default:
            return System.currentTimeMillis();
      }
   }

   private class TimerWorker {
      private final AtomicLong total = new AtomicLong();
      private final AtomicLong callsNumber = new AtomicLong();
      private final AtomicLong minimum = new AtomicLong();
      private final AtomicLong maximum = new AtomicLong();
      private final Object lock = new Object();

      public TimerWorker() {
      }

      public void addNewDuration(long duration) {
         this.callsNumber.incrementAndGet();
         this.total.addAndGet(duration);
         synchronized(this.lock) {
            if (duration < this.minimum.longValue()) {
               this.minimum.set(duration);
            }

            if (duration > this.maximum.longValue()) {
               this.maximum.set(duration);
            }

         }
      }

      public double getAverage() {
         synchronized(this.lock) {
            return (double)(this.total.longValue() / this.callsNumber.longValue());
         }
      }

      public long getCallsNumber() {
         return this.callsNumber.longValue();
      }

      public long getTotal() {
         return this.total.longValue();
      }

      public long getMinimum() {
         return this.minimum.longValue();
      }

      public long getMaximum() {
         return this.maximum.longValue();
      }
   }
}
