/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterLifeCycleException;
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

public class DefaultIoFilterChain
implements IoFilterChain {
    public static final AttributeKey SESSION_CREATED_FUTURE = new AttributeKey(DefaultIoFilterChain.class, "connectFuture");
    private final AbstractIoSession session;
    private final Map<String, IoFilterChain.Entry> name2entry = new ConcurrentHashMap<String, IoFilterChain.Entry>();
    private final EntryImpl head;
    private final EntryImpl tail;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIoFilterChain.class);

    public DefaultIoFilterChain(AbstractIoSession session) {
        if (session == null) {
            throw new IllegalArgumentException("session");
        }
        this.session = session;
        this.head = new EntryImpl(null, null, "head", new HeadFilter());
        this.tail = new EntryImpl(this.head, null, "tail", new TailFilter());
        this.head.nextEntry = this.tail;
    }

    @Override
    public IoSession getSession() {
        return this.session;
    }

    @Override
    public IoFilterChain.Entry getEntry(String name) {
        IoFilterChain.Entry e = this.name2entry.get(name);
        if (e == null) {
            return null;
        }
        return e;
    }

    @Override
    public IoFilterChain.Entry getEntry(IoFilter filter) {
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
            if (e.getFilter() == filter) {
                return e;
            }
            e = e.nextEntry;
        }
        return null;
    }

    @Override
    public IoFilterChain.Entry getEntry(Class<? extends IoFilter> filterType) {
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
            if (filterType.isAssignableFrom(e.getFilter().getClass())) {
                return e;
            }
            e = e.nextEntry;
        }
        return null;
    }

    @Override
    public IoFilter get(String name) {
        IoFilterChain.Entry e = this.getEntry(name);
        if (e == null) {
            return null;
        }
        return e.getFilter();
    }

    @Override
    public IoFilter get(Class<? extends IoFilter> filterType) {
        IoFilterChain.Entry e = this.getEntry(filterType);
        if (e == null) {
            return null;
        }
        return e.getFilter();
    }

    @Override
    public IoFilter.NextFilter getNextFilter(String name) {
        IoFilterChain.Entry e = this.getEntry(name);
        if (e == null) {
            return null;
        }
        return e.getNextFilter();
    }

    @Override
    public IoFilter.NextFilter getNextFilter(IoFilter filter) {
        IoFilterChain.Entry e = this.getEntry(filter);
        if (e == null) {
            return null;
        }
        return e.getNextFilter();
    }

    @Override
    public IoFilter.NextFilter getNextFilter(Class<? extends IoFilter> filterType) {
        IoFilterChain.Entry e = this.getEntry(filterType);
        if (e == null) {
            return null;
        }
        return e.getNextFilter();
    }

    @Override
    public synchronized void addFirst(String name, IoFilter filter) {
        this.checkAddable(name);
        this.register(this.head, name, filter);
    }

    @Override
    public synchronized void addLast(String name, IoFilter filter) {
        this.checkAddable(name);
        this.register(this.tail.prevEntry, name, filter);
    }

    @Override
    public synchronized void addBefore(String baseName, String name, IoFilter filter) {
        EntryImpl baseEntry = this.checkOldName(baseName);
        this.checkAddable(name);
        this.register(baseEntry.prevEntry, name, filter);
    }

    @Override
    public synchronized void addAfter(String baseName, String name, IoFilter filter) {
        EntryImpl baseEntry = this.checkOldName(baseName);
        this.checkAddable(name);
        this.register(baseEntry, name, filter);
    }

    @Override
    public synchronized IoFilter remove(String name) {
        EntryImpl entry = this.checkOldName(name);
        this.deregister(entry);
        return entry.getFilter();
    }

    @Override
    public synchronized void remove(IoFilter filter) {
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
            if (e.getFilter() == filter) {
                this.deregister(e);
                return;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
    }

    @Override
    public synchronized IoFilter remove(Class<? extends IoFilter> filterType) {
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
            if (filterType.isAssignableFrom(e.getFilter().getClass())) {
                IoFilter oldFilter = e.getFilter();
                this.deregister(e);
                return oldFilter;
            }
            e = e.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: " + filterType.getName());
    }

    @Override
    public synchronized IoFilter replace(String name, IoFilter newFilter) {
        EntryImpl entry = this.checkOldName(name);
        IoFilter oldFilter = entry.getFilter();
        try {
            newFilter.onPreAdd(this, name, entry.getNextFilter());
        }
        catch (Exception e) {
            throw new IoFilterLifeCycleException("onPreAdd(): " + name + ':' + newFilter + " in " + this.getSession(), e);
        }
        entry.setFilter(newFilter);
        try {
            newFilter.onPostAdd(this, name, entry.getNextFilter());
        }
        catch (Exception e) {
            entry.setFilter(oldFilter);
            throw new IoFilterLifeCycleException("onPostAdd(): " + name + ':' + newFilter + " in " + this.getSession(), e);
        }
        return oldFilter;
    }

    @Override
    public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
        EntryImpl entry = this.head.nextEntry;
        while (entry != this.tail) {
            if (entry.getFilter() == oldFilter) {
                String oldFilterName = null;
                for (Map.Entry<String, IoFilterChain.Entry> mapping : this.name2entry.entrySet()) {
                    if (entry != mapping.getValue()) continue;
                    oldFilterName = mapping.getKey();
                    break;
                }
                try {
                    newFilter.onPreAdd(this, oldFilterName, entry.getNextFilter());
                }
                catch (Exception e) {
                    throw new IoFilterLifeCycleException("onPreAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
                }
                entry.setFilter(newFilter);
                try {
                    newFilter.onPostAdd(this, oldFilterName, entry.getNextFilter());
                }
                catch (Exception e) {
                    entry.setFilter(oldFilter);
                    throw new IoFilterLifeCycleException("onPostAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
                }
                return;
            }
            entry = entry.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
    }

    @Override
    public synchronized IoFilter replace(Class<? extends IoFilter> oldFilterType, IoFilter newFilter) {
        EntryImpl entry = this.head.nextEntry;
        while (entry != this.tail) {
            if (oldFilterType.isAssignableFrom(entry.getFilter().getClass())) {
                IoFilter oldFilter = entry.getFilter();
                String oldFilterName = null;
                for (Map.Entry<String, IoFilterChain.Entry> mapping : this.name2entry.entrySet()) {
                    if (entry != mapping.getValue()) continue;
                    oldFilterName = mapping.getKey();
                    break;
                }
                try {
                    newFilter.onPreAdd(this, oldFilterName, entry.getNextFilter());
                }
                catch (Exception e) {
                    throw new IoFilterLifeCycleException("onPreAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
                }
                entry.setFilter(newFilter);
                try {
                    newFilter.onPostAdd(this, oldFilterName, entry.getNextFilter());
                }
                catch (Exception e) {
                    entry.setFilter(oldFilter);
                    throw new IoFilterLifeCycleException("onPostAdd(): " + oldFilterName + ':' + newFilter + " in " + this.getSession(), e);
                }
                return oldFilter;
            }
            entry = entry.nextEntry;
        }
        throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
    }

    @Override
    public synchronized void clear() throws Exception {
        ArrayList<IoFilterChain.Entry> l = new ArrayList<IoFilterChain.Entry>(this.name2entry.values());
        for (IoFilterChain.Entry entry : l) {
            try {
                this.deregister((EntryImpl)entry);
            }
            catch (Exception e) {
                throw new IoFilterLifeCycleException("clear(): " + entry.getName() + " in " + this.getSession(), e);
            }
        }
    }

    private void register(EntryImpl prevEntry, String name, IoFilter filter) {
        EntryImpl newEntry = new EntryImpl(prevEntry, prevEntry.nextEntry, name, filter);
        try {
            filter.onPreAdd(this, name, newEntry.getNextFilter());
        }
        catch (Exception e) {
            throw new IoFilterLifeCycleException("onPreAdd(): " + name + ':' + filter + " in " + this.getSession(), e);
        }
        prevEntry.nextEntry.prevEntry = newEntry;
        prevEntry.nextEntry = newEntry;
        this.name2entry.put(name, newEntry);
        try {
            filter.onPostAdd(this, name, newEntry.getNextFilter());
        }
        catch (Exception e) {
            this.deregister0(newEntry);
            throw new IoFilterLifeCycleException("onPostAdd(): " + name + ':' + filter + " in " + this.getSession(), e);
        }
    }

    private void deregister(EntryImpl entry) {
        IoFilter filter = entry.getFilter();
        try {
            filter.onPreRemove(this, entry.getName(), entry.getNextFilter());
        }
        catch (Exception e) {
            throw new IoFilterLifeCycleException("onPreRemove(): " + entry.getName() + ':' + filter + " in " + this.getSession(), e);
        }
        this.deregister0(entry);
        try {
            filter.onPostRemove(this, entry.getName(), entry.getNextFilter());
        }
        catch (Exception e) {
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
        }
        return e;
    }

    private void checkAddable(String name) {
        if (this.name2entry.containsKey(name)) {
            throw new IllegalArgumentException("Other filter is using the same name '" + name + "'");
        }
    }

    @Override
    public void fireSessionCreated() {
        this.callNextSessionCreated(this.head, this.session);
    }

    private void callNextSessionCreated(IoFilterChain.Entry entry, IoSession session) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.sessionCreated(nextFilter, session);
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public void fireSessionOpened() {
        this.callNextSessionOpened(this.head, this.session);
    }

    private void callNextSessionOpened(IoFilterChain.Entry entry, IoSession session) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.sessionOpened(nextFilter, session);
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public void fireSessionClosed() {
        try {
            this.session.getCloseFuture().setClosed();
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
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
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
        }
    }

    @Override
    public void fireSessionIdle(IdleStatus status) {
        this.session.increaseIdleCount(status, System.currentTimeMillis());
        this.callNextSessionIdle(this.head, this.session, status);
    }

    private void callNextSessionIdle(IoFilterChain.Entry entry, IoSession session, IdleStatus status) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.sessionIdle(nextFilter, session, status);
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public void fireMessageReceived(Object message) {
        if (message instanceof IoBuffer) {
            this.session.increaseReadBytes(((IoBuffer)message).remaining(), System.currentTimeMillis());
        }
        this.callNextMessageReceived(this.head, this.session, message);
    }

    private void callNextMessageReceived(IoFilterChain.Entry entry, IoSession session, Object message) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.messageReceived(nextFilter, session, message);
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public void fireMessageSent(WriteRequest request) {
        try {
            request.getFuture().setWritten();
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
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
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
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
            }
            catch (Throwable e) {
                LOGGER.warn("Unexpected exception from exceptionCaught handler.", e);
            }
        } else {
            if (!session.isClosing()) {
                session.closeNow();
            }
            future.setException(cause);
        }
    }

    @Override
    public void fireInputClosed() {
        EntryImpl head = this.head;
        this.callNextInputClosed(head, this.session);
    }

    private void callNextInputClosed(IoFilterChain.Entry entry, IoSession session) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.inputClosed(nextFilter, session);
        }
        catch (Throwable e) {
            this.fireExceptionCaught(e);
        }
    }

    @Override
    public void fireFilterWrite(WriteRequest writeRequest) {
        this.callPreviousFilterWrite(this.tail, this.session, writeRequest);
    }

    private void callPreviousFilterWrite(IoFilterChain.Entry entry, IoSession session, WriteRequest writeRequest) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.filterWrite(nextFilter, session, writeRequest);
        }
        catch (Exception e) {
            writeRequest.getFuture().setException(e);
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            writeRequest.getFuture().setException(e);
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public void fireFilterClose() {
        this.callPreviousFilterClose(this.tail, this.session);
    }

    private void callPreviousFilterClose(IoFilterChain.Entry entry, IoSession session) {
        try {
            IoFilter filter = entry.getFilter();
            IoFilter.NextFilter nextFilter = entry.getNextFilter();
            filter.filterClose(nextFilter, session);
        }
        catch (Exception e) {
            this.fireExceptionCaught(e);
        }
        catch (Error e) {
            this.fireExceptionCaught(e);
            throw e;
        }
    }

    @Override
    public List<IoFilterChain.Entry> getAll() {
        ArrayList<IoFilterChain.Entry> list = new ArrayList<IoFilterChain.Entry>();
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
            list.add(e);
            e = e.nextEntry;
        }
        return list;
    }

    @Override
    public List<IoFilterChain.Entry> getAllReversed() {
        ArrayList<IoFilterChain.Entry> list = new ArrayList<IoFilterChain.Entry>();
        EntryImpl e = this.tail.prevEntry;
        while (e != this.head) {
            list.add(e);
            e = e.prevEntry;
        }
        return list;
    }

    @Override
    public boolean contains(String name) {
        return this.getEntry(name) != null;
    }

    @Override
    public boolean contains(IoFilter filter) {
        return this.getEntry(filter) != null;
    }

    @Override
    public boolean contains(Class<? extends IoFilter> filterType) {
        return this.getEntry(filterType) != null;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{ ");
        boolean empty = true;
        EntryImpl e = this.head.nextEntry;
        while (e != this.tail) {
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
            e = e.nextEntry;
        }
        if (empty) {
            buf.append("empty");
        }
        buf.append(" }");
        return buf.toString();
    }

    private final class EntryImpl
    implements IoFilterChain.Entry {
        private EntryImpl prevEntry;
        private EntryImpl nextEntry;
        private final String name;
        private IoFilter filter;
        private final IoFilter.NextFilter nextFilter;

        private EntryImpl(EntryImpl prevEntry, EntryImpl nextEntry, String name, IoFilter filter) {
            if (filter == null) {
                throw new IllegalArgumentException("filter");
            }
            if (name == null) {
                throw new IllegalArgumentException("name");
            }
            this.prevEntry = prevEntry;
            this.nextEntry = nextEntry;
            this.name = name;
            this.filter = filter;
            this.nextFilter = new IoFilter.NextFilter(){

                @Override
                public void sessionCreated(IoSession session) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextSessionCreated(nextEntry, session);
                }

                @Override
                public void sessionOpened(IoSession session) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextSessionOpened(nextEntry, session);
                }

                @Override
                public void sessionClosed(IoSession session) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextSessionClosed(nextEntry, session);
                }

                @Override
                public void sessionIdle(IoSession session, IdleStatus status) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextSessionIdle(nextEntry, session, status);
                }

                @Override
                public void exceptionCaught(IoSession session, Throwable cause) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextExceptionCaught(nextEntry, session, cause);
                }

                @Override
                public void inputClosed(IoSession session) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextInputClosed(nextEntry, session);
                }

                @Override
                public void messageReceived(IoSession session, Object message) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextMessageReceived(nextEntry, session, message);
                }

                @Override
                public void messageSent(IoSession session, WriteRequest writeRequest) {
                    EntryImpl nextEntry = EntryImpl.this.nextEntry;
                    DefaultIoFilterChain.this.callNextMessageSent(nextEntry, session, writeRequest);
                }

                @Override
                public void filterWrite(IoSession session, WriteRequest writeRequest) {
                    EntryImpl nextEntry = EntryImpl.this.prevEntry;
                    DefaultIoFilterChain.this.callPreviousFilterWrite(nextEntry, session, writeRequest);
                }

                @Override
                public void filterClose(IoSession session) {
                    EntryImpl nextEntry = EntryImpl.this.prevEntry;
                    DefaultIoFilterChain.this.callPreviousFilterClose(nextEntry, session);
                }

                public String toString() {
                    return EntryImpl.this.nextEntry.name;
                }
            };
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public IoFilter getFilter() {
            return this.filter;
        }

        private void setFilter(IoFilter filter) {
            if (filter == null) {
                throw new IllegalArgumentException("filter");
            }
            this.filter = filter;
        }

        @Override
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

        @Override
        public void addAfter(String name, IoFilter filter) {
            DefaultIoFilterChain.this.addAfter(this.getName(), name, filter);
        }

        @Override
        public void addBefore(String name, IoFilter filter) {
            DefaultIoFilterChain.this.addBefore(this.getName(), name, filter);
        }

        @Override
        public void remove() {
            DefaultIoFilterChain.this.remove(this.getName());
        }

        @Override
        public void replace(IoFilter newFilter) {
            DefaultIoFilterChain.this.replace(this.getName(), newFilter);
        }
    }

    private static class TailFilter
    extends IoFilterAdapter {
        private TailFilter() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            try {
                session.getHandler().sessionCreated(session);
            }
            finally {
                ConnectFuture future = (ConnectFuture)session.removeAttribute(SESSION_CREATED_FUTURE);
                if (future != null) {
                    future.setSession(session);
                }
            }
        }

        @Override
        public void sessionOpened(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            session.getHandler().sessionOpened(session);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void sessionClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            AbstractIoSession s = (AbstractIoSession)session;
            try {
                s.getHandler().sessionClosed(session);
            }
            finally {
                try {
                    s.getWriteRequestQueue().dispose(session);
                }
                finally {
                    try {
                        s.getAttributeMap().dispose(session);
                    }
                    finally {
                        try {
                            session.getFilterChain().clear();
                        }
                        finally {
                            if (s.getConfig().isUseReadOperation()) {
                                s.offerClosedReadFuture();
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void sessionIdle(IoFilter.NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
            session.getHandler().sessionIdle(session, status);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void exceptionCaught(IoFilter.NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
            AbstractIoSession s = (AbstractIoSession)session;
            try {
                s.getHandler().exceptionCaught(s, cause);
            }
            finally {
                if (s.getConfig().isUseReadOperation()) {
                    s.offerFailedReadFuture(cause);
                }
            }
        }

        @Override
        public void inputClosed(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            session.getHandler().inputClosed(session);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
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
            }
            finally {
                if (s.getConfig().isUseReadOperation()) {
                    s.offerReadFuture(message);
                }
            }
        }

        @Override
        public void messageSent(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
            ((AbstractIoSession)session).increaseWrittenMessages(writeRequest, System.currentTimeMillis());
            if (session.getService() instanceof AbstractIoService) {
                ((AbstractIoService)session.getService()).getStatistics().updateThroughput(System.currentTimeMillis());
            }
            session.getHandler().messageSent(session, writeRequest.getMessage());
        }

        @Override
        public void filterWrite(IoFilter.NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
            nextFilter.filterWrite(session, writeRequest);
        }

        @Override
        public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            nextFilter.filterClose(session);
        }
    }

    private class HeadFilter
    extends IoFilterAdapter {
        private HeadFilter() {
        }

        @Override
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

        @Override
        public void filterClose(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
            ((AbstractIoSession)session).getProcessor().remove(session);
        }
    }
}

