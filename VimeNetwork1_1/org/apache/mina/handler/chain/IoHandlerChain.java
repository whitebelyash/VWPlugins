package org.apache.mina.handler.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.IoSession;

public class IoHandlerChain implements IoHandlerCommand {
   private static volatile int nextId = 0;
   private final int id;
   private final String NEXT_COMMAND;
   private final Map name2entry;
   private final Entry head;
   private final Entry tail;

   public IoHandlerChain() {
      this.id = nextId++;
      this.NEXT_COMMAND = IoHandlerChain.class.getName() + '.' + this.id + ".nextCommand";
      this.name2entry = new ConcurrentHashMap();
      this.head = new Entry((Entry)null, (Entry)null, "head", this.createHeadCommand());
      this.tail = new Entry(this.head, (Entry)null, "tail", this.createTailCommand());
      this.head.nextEntry = this.tail;
   }

   private IoHandlerCommand createHeadCommand() {
      return new IoHandlerCommand() {
         public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
            next.execute(session, message);
         }
      };
   }

   private IoHandlerCommand createTailCommand() {
      return new IoHandlerCommand() {
         public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
            next = (IoHandlerCommand.NextCommand)session.getAttribute(IoHandlerChain.this.NEXT_COMMAND);
            if (next != null) {
               next.execute(session, message);
            }

         }
      };
   }

   public Entry getEntry(String name) {
      Entry e = (Entry)this.name2entry.get(name);
      return e == null ? null : e;
   }

   public IoHandlerCommand get(String name) {
      Entry e = this.getEntry(name);
      return e == null ? null : e.getCommand();
   }

   public IoHandlerCommand.NextCommand getNextCommand(String name) {
      Entry e = this.getEntry(name);
      return e == null ? null : e.getNextCommand();
   }

   public synchronized void addFirst(String name, IoHandlerCommand command) {
      this.checkAddable(name);
      this.register(this.head, name, command);
   }

   public synchronized void addLast(String name, IoHandlerCommand command) {
      this.checkAddable(name);
      this.register(this.tail.prevEntry, name, command);
   }

   public synchronized void addBefore(String baseName, String name, IoHandlerCommand command) {
      Entry baseEntry = this.checkOldName(baseName);
      this.checkAddable(name);
      this.register(baseEntry.prevEntry, name, command);
   }

   public synchronized void addAfter(String baseName, String name, IoHandlerCommand command) {
      Entry baseEntry = this.checkOldName(baseName);
      this.checkAddable(name);
      this.register(baseEntry, name, command);
   }

   public synchronized IoHandlerCommand remove(String name) {
      Entry entry = this.checkOldName(name);
      this.deregister(entry);
      return entry.getCommand();
   }

   public synchronized void clear() throws Exception {
      Iterator<String> it = (new ArrayList(this.name2entry.keySet())).iterator();

      while(it.hasNext()) {
         this.remove((String)it.next());
      }

   }

   private void register(Entry prevEntry, String name, IoHandlerCommand command) {
      Entry newEntry = new Entry(prevEntry, prevEntry.nextEntry, name, command);
      prevEntry.nextEntry.prevEntry = newEntry;
      prevEntry.nextEntry = newEntry;
      this.name2entry.put(name, newEntry);
   }

   private void deregister(Entry entry) {
      Entry prevEntry = entry.prevEntry;
      Entry nextEntry = entry.nextEntry;
      prevEntry.nextEntry = nextEntry;
      nextEntry.prevEntry = prevEntry;
      this.name2entry.remove(entry.name);
   }

   private Entry checkOldName(String baseName) {
      Entry e = (Entry)this.name2entry.get(baseName);
      if (e == null) {
         throw new IllegalArgumentException("Unknown filter name:" + baseName);
      } else {
         return e;
      }
   }

   private void checkAddable(String name) {
      if (this.name2entry.containsKey(name)) {
         throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
      }
   }

   public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
      if (next != null) {
         session.setAttribute(this.NEXT_COMMAND, next);
      }

      try {
         this.callNextCommand(this.head, session, message);
      } finally {
         session.removeAttribute(this.NEXT_COMMAND);
      }

   }

   private void callNextCommand(Entry entry, IoSession session, Object message) throws Exception {
      entry.getCommand().execute(entry.getNextCommand(), session, message);
   }

   public List getAll() {
      List<Entry> list = new ArrayList();

      for(Entry e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         list.add(e);
      }

      return list;
   }

   public List getAllReversed() {
      List<Entry> list = new ArrayList();

      for(Entry e = this.tail.prevEntry; e != this.head; e = e.prevEntry) {
         list.add(e);
      }

      return list;
   }

   public boolean contains(String name) {
      return this.getEntry(name) != null;
   }

   public boolean contains(IoHandlerCommand command) {
      for(Entry e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (e.getCommand() == command) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(Class commandType) {
      for(Entry e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (commandType.isAssignableFrom(e.getCommand().getClass())) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("{ ");
      boolean empty = true;

      for(Entry e = this.head.nextEntry; e != this.tail; e = e.nextEntry) {
         if (!empty) {
            buf.append(", ");
         } else {
            empty = false;
         }

         buf.append('(');
         buf.append(e.getName());
         buf.append(':');
         buf.append(e.getCommand());
         buf.append(')');
      }

      if (empty) {
         buf.append("empty");
      }

      buf.append(" }");
      return buf.toString();
   }

   public class Entry {
      private Entry prevEntry;
      private Entry nextEntry;
      private final String name;
      private final IoHandlerCommand command;
      private final IoHandlerCommand.NextCommand nextCommand;

      private Entry(Entry prevEntry, Entry nextEntry, String name, IoHandlerCommand command) {
         if (command == null) {
            throw new IllegalArgumentException("command");
         } else if (name == null) {
            throw new IllegalArgumentException("name");
         } else {
            this.prevEntry = prevEntry;
            this.nextEntry = nextEntry;
            this.name = name;
            this.command = command;
            this.nextCommand = new IoHandlerCommand.NextCommand() {
               public void execute(IoSession session, Object message) throws Exception {
                  Entry nextEntry = Entry.this.nextEntry;
                  IoHandlerChain.this.callNextCommand(nextEntry, session, message);
               }
            };
         }
      }

      public String getName() {
         return this.name;
      }

      public IoHandlerCommand getCommand() {
         return this.command;
      }

      public IoHandlerCommand.NextCommand getNextCommand() {
         return this.nextCommand;
      }
   }
}
