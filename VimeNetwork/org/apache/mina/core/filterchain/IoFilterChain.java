package org.apache.mina.core.filterchain;

import java.util.List;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public interface IoFilterChain {
   IoSession getSession();

   Entry getEntry(String var1);

   Entry getEntry(IoFilter var1);

   Entry getEntry(Class var1);

   IoFilter get(String var1);

   IoFilter get(Class var1);

   IoFilter.NextFilter getNextFilter(String var1);

   IoFilter.NextFilter getNextFilter(IoFilter var1);

   IoFilter.NextFilter getNextFilter(Class var1);

   List getAll();

   List getAllReversed();

   boolean contains(String var1);

   boolean contains(IoFilter var1);

   boolean contains(Class var1);

   void addFirst(String var1, IoFilter var2);

   void addLast(String var1, IoFilter var2);

   void addBefore(String var1, String var2, IoFilter var3);

   void addAfter(String var1, String var2, IoFilter var3);

   IoFilter replace(String var1, IoFilter var2);

   void replace(IoFilter var1, IoFilter var2);

   IoFilter replace(Class var1, IoFilter var2);

   IoFilter remove(String var1);

   void remove(IoFilter var1);

   IoFilter remove(Class var1);

   void clear() throws Exception;

   void fireSessionCreated();

   void fireSessionOpened();

   void fireSessionClosed();

   void fireSessionIdle(IdleStatus var1);

   void fireMessageReceived(Object var1);

   void fireMessageSent(WriteRequest var1);

   void fireExceptionCaught(Throwable var1);

   void fireInputClosed();

   void fireFilterWrite(WriteRequest var1);

   void fireFilterClose();

   public interface Entry {
      String getName();

      IoFilter getFilter();

      IoFilter.NextFilter getNextFilter();

      void addBefore(String var1, IoFilter var2);

      void addAfter(String var1, IoFilter var2);

      void replace(IoFilter var1);

      void remove();
   }
}
