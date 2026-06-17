/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.handler.chain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.chain.IoHandlerCommand;

public class IoHandlerChain
implements IoHandlerCommand {
    private static volatile int nextId = 0;
    private final int id = nextId++;
    private final String NEXT_COMMAND = IoHandlerChain.class.getName() + '.' + this.id + ".nextCommand";
    private final Map<String, Entry> name2entry = new ConcurrentHashMap<String, Entry>();
    private final Entry head = new Entry(null, null, "head", this.createHeadCommand());
    private final Entry tail = new Entry(this.head, null, "tail", this.createTailCommand());

    public IoHandlerChain() {
        this.head.nextEntry = this.tail;
    }

    private IoHandlerCommand createHeadCommand() {
        return new IoHandlerCommand(){

            @Override
            public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
                next.execute(session, message);
            }
        };
    }

    private IoHandlerCommand createTailCommand() {
        return new IoHandlerCommand(){

            @Override
            public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
                next = (IoHandlerCommand.NextCommand)session.getAttribute(IoHandlerChain.this.NEXT_COMMAND);
                if (next != null) {
                    next.execute(session, message);
                }
            }
        };
    }

    public Entry getEntry(String name) {
        Entry e = this.name2entry.get(name);
        if (e == null) {
            return null;
        }
        return e;
    }

    public IoHandlerCommand get(String name) {
        Entry e = this.getEntry(name);
        if (e == null) {
            return null;
        }
        return e.getCommand();
    }

    public IoHandlerCommand.NextCommand getNextCommand(String name) {
        Entry e = this.getEntry(name);
        if (e == null) {
            return null;
        }
        return e.getNextCommand();
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
        Iterator<String> it = new ArrayList<String>(this.name2entry.keySet()).iterator();
        while (it.hasNext()) {
            this.remove(it.next());
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
        Entry e = this.name2entry.get(baseName);
        if (e == null) {
            throw new IllegalArgumentException("Unknown filter name:" + baseName);
        }
        return e;
    }

    private void checkAddable(String name) {
        if (this.name2entry.containsKey(name)) {
            throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(IoHandlerCommand.NextCommand next, IoSession session, Object message) throws Exception {
        if (next != null) {
            session.setAttribute(this.NEXT_COMMAND, next);
        }
        try {
            this.callNextCommand(this.head, session, message);
        }
        finally {
            session.removeAttribute(this.NEXT_COMMAND);
        }
    }

    private void callNextCommand(Entry entry, IoSession session, Object message) throws Exception {
        entry.getCommand().execute(entry.getNextCommand(), session, message);
    }

    public List<Entry> getAll() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        Entry e = this.head.nextEntry;
        while (e != this.tail) {
            list.add(e);
            e = e.nextEntry;
        }
        return list;
    }

    public List<Entry> getAllReversed() {
        ArrayList<Entry> list = new ArrayList<Entry>();
        Entry e = this.tail.prevEntry;
        while (e != this.head) {
            list.add(e);
            e = e.prevEntry;
        }
        return list;
    }

    public boolean contains(String name) {
        return this.getEntry(name) != null;
    }

    public boolean contains(IoHandlerCommand command) {
        Entry e = this.head.nextEntry;
        while (e != this.tail) {
            if (e.getCommand() == command) {
                return true;
            }
            e = e.nextEntry;
        }
        return false;
    }

    public boolean contains(Class<? extends IoHandlerCommand> commandType) {
        Entry e = this.head.nextEntry;
        while (e != this.tail) {
            if (commandType.isAssignableFrom(e.getCommand().getClass())) {
                return true;
            }
            e = e.nextEntry;
        }
        return false;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{ ");
        boolean empty = true;
        Entry e = this.head.nextEntry;
        while (e != this.tail) {
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
            e = e.nextEntry;
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
            }
            if (name == null) {
                throw new IllegalArgumentException("name");
            }
            this.prevEntry = prevEntry;
            this.nextEntry = nextEntry;
            this.name = name;
            this.command = command;
            this.nextCommand = new IoHandlerCommand.NextCommand(){

                @Override
                public void execute(IoSession session, Object message) throws Exception {
                    Entry nextEntry = Entry.this.nextEntry;
                    IoHandlerChain.this.callNextCommand(nextEntry, session, message);
                }
            };
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

