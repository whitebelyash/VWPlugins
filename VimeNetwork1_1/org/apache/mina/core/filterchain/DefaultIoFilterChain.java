package org.apache.mina.core.filterchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.AbstractIoService;
import org.apache.mina.core.session.AbstractIoSession;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.core.write.WriteRequestQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIoFilterChain implements IoFilterChain {
   public static final AttributeKey SESSION_CREATED_FUTURE = new AttributeKey(DefaultIoFilterChain.class, "connectFuture");
   private final AbstractIoSession session;
   private final Map name2entry = new ConcurrentHashMap();
   private final EntryImpl head;
   private final EntryImpl tail;
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIoFilterChain.class);

   public DefaultIoFilterChain(AbstractIoSession session) {
      if (session == null) {
         throw new IllegalArgumentException("session");
      } else {
         this.session = session;
         this.head = new EntryImpl((EntryImpl)null, (EntryImpl)null, "head", new HeadFilter());
         this.tail = new EntryImpl(this.head, (EntryImpl)null, "tail", new TailFilter());
         this.head.nextEntry = this.tail;
      }
   }

   public IoSession getSession() {
      return this.session;
   }

   public IoFilterChain.Entry getEntry(String name) {
      IoFilterChain.Entry e = (IoFilterChain.Entry)this.name2entry.get(name);
      return e == null ? null : e;
   }

   public IoFilterChain.Entry getEntry(IoFilter filter) {
      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (e.getFilter() == filter) {
            return e;
         }
      }

      return null;
   }

   public IoFilterChain.Entry getEntry(Class filterType) {
      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (filterType.isAssignableFrom(e.getFilter().getClass())) {
            return e;
         }
      }

      return null;
   }

   public IoFilter get(String name) {
      IoFilterChain.Entry e = this.getEntry(name);
      return e == null ? null : e.getFilter();
   }

   public IoFilter get(Class filterType) {
      IoFilterChain.Entry e = this.getEntry(filterType);
      return e == null ? null : e.getFilter();
   }

   public IoFilter.NextFilter getNextFilter(String name) {
      IoFilterChain.Entry e = this.getEntry(name);
      return e == null ? null : e.getNextFilter();
   }

   public IoFilter.NextFilter getNextFilter(IoFilter filter) {
      IoFilterChain.Entry e = this.getEntry(filter);
      return e == null ? null : e.getNextFilter();
   }

   public IoFilter.NextFilter getNextFilter(Class filterType) {
      IoFilterChain.Entry e = this.getEntry(filterType);
      return e == null ? null : e.getNextFilter();
   }

   public synchronized void addFirst(String name, IoFilter filter) {
      this.checkAddable(name);
      this.register(this.head, name, filter);
   }

   public synchronized void addLast(String name, IoFilter filter) {
      this.checkAddable(name);
      this.register(this.tail.prevEntry, name, filter);
   }

   public synchronized void addBefore(String baseName, String name, IoFilter filter) {
      EntryImpl baseEntry = this.checkOldName(baseName);
      this.checkAddable(name);
      this.register(baseEntry.prevEntry, name, filter);
   }

   public synchronized void addAfter(String baseName, String name, IoFilter filter) {
      EntryImpl baseEntry = this.checkOldName(baseName);
      this.checkAddable(name);
      this.register(baseEntry, name, filter);
   }

   public synchronized IoFilter remove(String name) {
      EntryImpl entry = this.checkOldName(name);
      this.deregister(entry);
      return entry.getFilter();
   }

   public synchronized void remove(IoFilter filter) {
      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (e.getFilter() == filter) {
            this.deregister(e);
            return;
         }
      }

      throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
   }

   public synchronized IoFilter remove(Class filterType) {
      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (filterType.isAssignableFrom(e.getFilter().getClass())) {
            IoFilter oldFilter = e.getFilter();
            this.deregister(e);
            return oldFilter;
         }
      }

      throw new IllegalArgumentException("Filter not found: " + filterType.getName());
   }

   public synchronized IoFilter replace(String name, IoFilter newFilter) {
      EntryImpl entry = this.checkOldName(name);
      IoFilter oldFilter = entry.getFilter();

      try {
         newFilter.onPreAdd(this, name, entry.getNextFilter());
      } catch (Exception e) {
         throw new IoFilterLifeCycleException("onPreAdd(): " + name + ':' + newFilter + " in " + this.getSession(), e);
      }

      entry.setFilter(newFilter);

      try {
         newFilter.onPostAdd(this, name, entry.getNextFilter());
         return oldFilter;
      } catch (Exception e) {
         entry.setFilter(oldFilter);
         throw new IoFilterLifeCycleException("onPostAdd(): " + name + ':' + newFilter + " in " + this.getSession(), e);
      }
   }

   public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
      for(EntryImpl entry = this.head.nextEntry; entry != this.tail; entry = entry.nextEntry) {
         if (entry.getFilter() == oldFilter) {
            String oldFilterName = null;

            for(Map.Entry mapping : this.name2entry.entrySet()) {
               if (entry == mapping.getValue()) {
                  oldFilterName = (String)mapping.getKey();
                  break;
               }
            }

            try {
               newFilter.onPreAdd(this, oldFilterName, entry.getNextFilter());
            } catch (Exception e) {
               throw new IoFilterLifeCycleException("onPreAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
            }

            entry.setFilter(newFilter);

            try {
               newFilter.onPostAdd(this, oldFilterName, entry.getNextFilter());
               return;
            } catch (Exception e) {
               entry.setFilter(oldFilter);
               throw new IoFilterLifeCycleException("onPostAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
            }
         }
      }

      throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
   }

   public synchronized IoFilter replace(Class oldFilterType, IoFilter newFilter) {
      for(EntryImpl entry = this.head.nextEntry; entry != this.tail; entry = entry.nextEntry) {
         if (oldFilterType.isAssignableFrom(entry.getFilter().getClass())) {
            IoFilter oldFilter = entry.getFilter();
            String oldFilterName = null;

            for(Map.Entry mapping : this.name2entry.entrySet()) {
               if (entry == mapping.getValue()) {
                  oldFilterName = (String)mapping.getKey();
                  break;
               }
            }

            try {
               newFilter.onPreAdd(this, oldFilterName, entry.getNextFilter());
            } catch (Exception e) {
               throw new IoFilterLifeCycleException("onPreAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
            }

            entry.setFilter(newFilter);

            try {
               newFilter.onPostAdd(this, oldFilterName, entry.getNextFilter());
               return oldFilter;
            } catch (Exception e) {
               entry.setFilter(oldFilter);
               throw new IoFilterLifeCycleException("onPostAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
            }
         }
      }

      throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
   }

   public synchronized void clear() throws Exception {
      for(IoFilterChain.Entry entry : new ArrayList(this.name2entry.values())) {
         try {
            this.deregister((EntryImpl)entry);
         } catch (Exception e) {
            throw new IoFilterLifeCycleException("clear(): " + entry.getName() + " in " + this.getSession(), e);
         }
      }

   }

   private void register(EntryImpl prevEntry, String name, IoFilter filter) {
      EntryImpl newEntry = new EntryImpl(prevEntry, prevEntry.nextEntry, name, filter);

      try {
         filter.onPreAdd(this, name, newEntry.getNextFilter());
      } catch (Exception e) {
         throw new IoFilterLifeCycleException("onPreAdd(): " + name + ':' + filter + " in " + this.getSession(), e);
      }

      prevEntry.nextEntry.prevEntry = newEntry;
      prevEntry.nextEntry = newEntry;
      this.name2entry.put(name, newEntry);

      try {
         filter.onPostAdd(this, name, newEntry.getNextFilter());
      } catch (Exception e) {
         this.deregister0(newEntry);
         throw new IoFilterLifeCycleException("onPostAdd(): " + name + ':' + filter + " in " + this.getSession(), e);
      }
   }

   private void deregister(EntryImpl entry) {
      IoFilter filter = entry.getFilter();

      try {
         filter.onPreRemove(this, entry.getName(), entry.getNextFilter());
      } catch (Exception e) {
         throw new IoFilterLifeCycleException("onPreRemove(): " + entry.getName() + ':' + filter + " in " + this.getSession(), e);
      }

      this.deregister0(entry);

      try {
         filter.onPostRemove(this, entry.getName(), entry.getNextFilter());
      } catch (Exception e) {
         throw new IoFilterLifeCycleException("onPostRemove(): " + entry.getName() + ':' + filter + " in " + this.getSession(), e);
      }
   }

   private void deregister0(EntryImpl entry) {
      EntryImpl prevEntry = entry.prevEntry;
      EntryImpl nextEntry = entry.nextEntry;
      prevEntry.nextEntry = nextEntry;
      nextEntry.prevEntry = prevEntry;
      this.name2entry.remove(entry.name);
   }

   private EntryImpl checkOldName(String baseName) {
      EntryImpl e = (EntryImpl)this.name2entry.get(baseName);
      if (e == null) {
         throw new IllegalArgumentException("Filter not found:" + baseName);
      } else {
         return e;
      }
   }

   private void checkAddable(String name) {
      if (this.name2entry.containsKey(name)) {
         throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
      }
   }

   public void fireSessionCreated() {
      this.callNextSessionCreated(this.head, this.session);
   }

   private void callNextSessionCreated(IoFilterChain.Entry entry, IoSession session) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.sessionCreated(nextFilter, session);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireSessionOpened() {
      this.callNextSessionOpened(this.head, this.session);
   }

   private void callNextSessionOpened(IoFilterChain.Entry entry, IoSession session) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.sessionOpened(nextFilter, session);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireSessionClosed() {
      try {
         this.session.getCloseFuture().setClosed();
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

      this.callNextSessionClosed(this.head, this.session);
   }

   private void callNextSessionClosed(IoFilterChain.Entry entry, IoSession session) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.sessionClosed(nextFilter, session);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
      }

   }

   public void fireSessionIdle(IdleStatus status) {
      this.session.increaseIdleCount(status, System.currentTimeMillis());
      this.callNextSessionIdle(this.head, this.session, status);
   }

   private void callNextSessionIdle(IoFilterChain.Entry entry, IoSession session, IdleStatus status) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.sessionIdle(nextFilter, session, status);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireMessageReceived(Object message) {
      if (message instanceof IoBuffer) {
         this.session.increaseReadBytes((long)((IoBuffer)message).remaining(), System.currentTimeMillis());
      }

      this.callNextMessageReceived(this.head, this.session, message);
   }

   private void callNextMessageReceived(IoFilterChain.Entry entry, IoSession session, Object message) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.messageReceived(nextFilter, session, message);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireMessageSent(WriteRequest request) {
      try {
         request.getFuture().setWritten();
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

      if (!request.isEncoded()) {
         this.callNextMessageSent(this.head, this.session, request);
      }

   }

   private void callNextMessageSent(IoFilterChain.Entry entry, IoSession session, WriteRequest writeRequest) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.messageSent(nextFilter, session, writeRequest);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireExceptionCaught(Throwable cause) {
      this.callNextExceptionCaught(this.head, this.session, cause);
   }

   private void callNextExceptionCaught(IoFilterChain.Entry entry, IoSession session, Throwable cause) {
      ConnectFuture future = (ConnectFuture)session.removeAttribute(SESSION_CREATED_FUTURE);
      if (future == null) {
         try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.exceptionCaught(nextFilter, session, cause);
         } catch (Throwable e) {
            LOGGER.warn("Unexpected exception from exceptionCaught handler.", e);
         }
      } else {
         if (!session.isClosing()) {
            session.closeNow();
         }

         future.setException(cause);
      }

   }

   public void fireInputClosed() {
      IoFilterChain.Entry head = this.head;
      this.callNextInputClosed(head, this.session);
   }

   private void callNextInputClosed(IoFilterChain.Entry entry, IoSession session) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.inputClosed(nextFilter, session);
      } catch (Throwable e) {
         this.fireExceptionCaught(e);
      }

   }

   public void fireFilterWrite(WriteRequest writeRequest) {
      this.callPreviousFilterWrite(this.tail, this.session, writeRequest);
   }

   private void callPreviousFilterWrite(IoFilterChain.Entry entry, IoSession session, WriteRequest writeRequest) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.filterWrite(nextFilter, session, writeRequest);
      } catch (Exception e) {
         writeRequest.getFuture().setException(e);
         this.fireExceptionCaught(e);
      } catch (Error e) {
         writeRequest.getFuture().setException(e);
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public void fireFilterClose() {
      this.callPreviousFilterClose(this.tail, this.session);
   }

   private void callPreviousFilterClose(IoFilterChain.Entry entry, IoSession session) {
      try {
         IoFilter filter = entry.getFilter();
         IoFilter.NextFilter nextFilter = entry.getNextFilter();
         filter.filterClose(nextFilter, session);
      } catch (Exception e) {
         this.fireExceptionCaught(e);
      } catch (Error e) {
         this.fireExceptionCaught(e);
         throw e;
      }

   }

   public List getAll() {
      List<IoFilterChain.Entry> list = new ArrayList();

      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         list.add(e);
      }

      return list;
   }

   public List getAllReversed() {
      List<IoFilterChain.Entry> list = new ArrayList();

      for(EntryImpl e = this.tail.prevEntry; e != this.head; e = e.prevEntry) {
         list.add(e);
      }

      return list;
   }

   public boolean contains(String name) {
      return this.getEntry(name) != null;
   }

   public boolean contains(IoFilter filter) {
      return this.getEntry(filter) != null;
   }

   public boolean contains(Class filterType) {
      return this.getEntry(filterType) != null;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("{ ");
      boolean empty = true;

      for(EntryImpl e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (!empty) {
            buf.append(", ");
         } else {
            empty = false;
         }

         buf.append('(');
         buf.append(e.getName());
         buf.append(':');
         buf.append(e.getFilter());
         buf.append(')');
      }

      if (empty) {
         buf.append("empty");
      }

      buf.append(" }");
      return buf.toString();
   }

   private class HeadFilter extends IoFilterAdapter {
      private HeadFilter() {
      }

      public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
         AbstractIoSession s = (AbstractIoSession)session;
         if (writeRequest.getMessage() instanceof IoBuffer) {
            IoBuffer buffer = (IoBuffer)writeRequest.getMessage();
            buffer.mark();
            int remaining = buffer.remaining();
            if (remaining > 0) {
               s.increaseScheduledWriteBytes(remaining);
            }
         } else {
            s.increaseScheduledWriteMessages();
         }

         WriteRequestQueue writeRequestQueue = s.getWriteRequestQueue();
         if (!s.isWriteSuspended()) {
            if (writeRequestQueue.isEmpty(session)) {
               s.getProcessor().write(s, writeRequest);
            } else {
               s.getWriteRequestQueue().offer(s, writeRequest);
               s.getProcessor().flush(s);
            }
         } else {
            s.getWriteRequestQueue().offer(s, writeRequest);
         }

      }

      public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         ((AbstractIoSession)session).getProcessor().remove(session);
      }
   }

   private static class TailFilter extends IoFilterAdapter {
      private TailFilter() {
      }

      public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         try {
            session.getHandler().sessionCreated(session);
         } finally {
            ConnectFuture future = (ConnectFuture)session.removeAttribute(DefaultIoFilterChain.SESSION_CREATED_FUTURE);
            if (future != null) {
               future.setSession(session);
            }

         }

      }

      public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         session.getHandler().sessionOpened(session);
      }

      public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         AbstractIoSession s = (AbstractIoSession)session;

         try {
            s.getHandler().sessionClosed(session);
         } finally {
            try {
               s.getWriteRequestQueue().dispose(session);
            } finally {
               try {
                  s.getAttributeMap().dispose(session);
               } finally {
                  try {
                     session.getFilterChain().clear();
                  } finally {
                     if (s.getConfig().isUseReadOperation()) {
                        s.offerClosedReadFuture();
                     }

                  }
               }
            }
         }

      }

      public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
         session.getHandler().sessionIdle(session, status);
      }

      public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
         AbstractIoSession s = (AbstractIoSession)session;

         try {
            s.getHandler().exceptionCaught(s, cause);
         } finally {
            if (s.getConfig().isUseReadOperation()) {
               s.offerFailedReadFuture(cause);
            }

         }

      }

      public void inputClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         session.getHandler().inputClosed(session);
      }

      public void messageReceived(IoFilter.NextFilter nextFilter, IoSession session, Object message) throws Exception {
         AbstractIoSession s = (AbstractIoSession)session;
         if (!(message instanceof IoBuffer)) {
            s.increaseReadMessages(System.currentTimeMillis());
         } else if (!((IoBuffer)message).hasRemaining()) {
            s.increaseReadMessages(System.currentTimeMillis());
         }

         if (session.getService() instanceof AbstractIoService) {
            ((AbstractIoService)session.getService()).getStatistics().updateThroughput(System.currentTimeMillis());
         }

         try {
            session.getHandler().messageReceived(s, message);
         } finally {
            if (s.getConfig().isUseReadOperation()) {
               s.offerReadFuture(message);
            }

         }

      }

      public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
         ((AbstractIoSession)session).increaseWrittenMessages(writeRequest, System.currentTimeMillis());
         if (session.getService() instanceof AbstractIoService) {
            ((AbstractIoService)session.getService()).getStatistics().updateThroughput(System.currentTimeMillis());
         }

         session.getHandler().messageSent(session, writeRequest.getMessage());
      }

      public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
         nextFilter.filterWrite(session, writeRequest);
      }

      public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
         nextFilter.filterClose(session);
      }
   }

   private final class EntryImpl implements IoFilterChain.Entry {
      private EntryImpl prevEntry;
      private EntryImpl nextEntry;
      private final String name;
      private IoFilter filter;
      private final IoFilter.NextFilter nextFilter;

      private EntryImpl(EntryImpl prevEntry, EntryImpl nextEntry, String name, IoFilter filter) {
         if (filter == null) {
            throw new IllegalArgumentException("filter");
         } else if (name == null) {
            throw new IllegalArgumentException("name");
         } else {
            this.prevEntry = prevEntry;
            this.nextEntry = nextEntry;
            this.name = name;
            this.filter = filter;
            this.nextFilter = new IoFilter.NextFilter() {
               public void sessionCreated(IoSession session) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextSessionCreated(nextEntry, session);
               }

               public void sessionOpened(IoSession session) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextSessionOpened(nextEntry, session);
               }

               public void sessionClosed(IoSession session) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextSessionClosed(nextEntry, session);
               }

               public void sessionIdle(IoSession session, IdleStatus status) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextSessionIdle(nextEntry, session, status);
               }

               public void exceptionCaught(IoSession session, Throwable cause) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextExceptionCaught(nextEntry, session, cause);
               }

               public void inputClosed(IoSession session) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextInputClosed(nextEntry, session);
               }

               public void messageReceived(IoSession session, Object message) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextMessageReceived(nextEntry, session, message);
               }

               public void messageSent(IoSession session, WriteRequest writeRequest) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.nextEntry;
                  DefaultIoFilterChain.this.callNextMessageSent(nextEntry, session, writeRequest);
               }

               public void filterWrite(IoSession session, WriteRequest writeRequest) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.prevEntry;
                  DefaultIoFilterChain.this.callPreviousFilterWrite(nextEntry, session, writeRequest);
               }

               public void filterClose(IoSession session) {
                  IoFilterChain.Entry nextEntry = EntryImpl.this.prevEntry;
                  DefaultIoFilterChain.this.callPreviousFilterClose(nextEntry, session);
               }

               public String toString() {
                  return EntryImpl.this.nextEntry.name;
               }
            };
         }
      }

      public String getName() {
         return this.name;
      }

      public IoFilter getFilter() {
         return this.filter;
      }

      private void setFilter(IoFilter filter) {
         if (filter == null) {
            throw new IllegalArgumentException("filter");
         } else {
            this.filter = filter;
         }
      }

      public IoFilter.NextFilter getNextFilter() {
         return this.nextFilter;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("('").append(this.getName()).append('\'');
         sb.append(", prev: '");
         if (this.prevEntry != null) {
            sb.append(this.prevEntry.name);
            sb.append(':');
            sb.append(this.prevEntry.getFilter().getClass().getSimpleName());
         } else {
            sb.append("null");
         }

         sb.append("', next: '");
         if (this.nextEntry != null) {
            sb.append(this.nextEntry.name);
            sb.append(':');
            sb.append(this.nextEntry.getFilter().getClass().getSimpleName());
         } else {
            sb.append("null");
         }

         sb.append("')");
         return sb.toString();
      }

      public void addAfter(String name, IoFilter filter) {
         DefaultIoFilterChain.this.addAfter(this.getName(), name, filter);
      }

      public void addBefore(String name, IoFilter filter) {
         DefaultIoFilterChain.this.addBefore(this.getName(), name, filter);
      }

      public void remove() {
         DefaultIoFilterChain.this.remove(this.getName());
      }

      public void replace(IoFilter newFilter) {
         DefaultIoFilterChain.this.replace(this.getName(), newFilter);
      }
   }
}
