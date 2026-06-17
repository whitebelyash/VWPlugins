/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.core.filterchain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.filterchain.IoFilterChain;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIoFilterChainBuilder
implements IoFilterChainBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIoFilterChainBuilder.class);
    private final List<IoFilterChain.Entry> entries;

    public DefaultIoFilterChainBuilder() {
        this.entries = new CopyOnWriteArrayList<IoFilterChain.Entry>();
    }

    public DefaultIoFilterChainBuilder(DefaultIoFilterChainBuilder filterChain) {
        if (filterChain == null) {
            throw new IllegalArgumentException("filterChain");
        }
        this.entries = new CopyOnWriteArrayList<IoFilterChain.Entry>(filterChain.entries);
    }

    public IoFilterChain.Entry getEntry(String name) {
        for (IoFilterChain.Entry e : this.entries) {
            if (!e.getName().equals(name)) continue;
            return e;
        }
        return null;
    }

    public IoFilterChain.Entry getEntry(IoFilter filter) {
        for (IoFilterChain.Entry e : this.entries) {
            if (e.getFilter() != filter) continue;
            return e;
        }
        return null;
    }

    public IoFilterChain.Entry getEntry(Class<? extends IoFilter> filterType) {
        for (IoFilterChain.Entry e : this.entries) {
            if (!filterType.isAssignableFrom(e.getFilter().getClass())) continue;
            return e;
        }
        return null;
    }

    public IoFilter get(String name) {
        IoFilterChain.Entry e = this.getEntry(name);
        if (e == null) {
            return null;
        }
        return e.getFilter();
    }

    public IoFilter get(Class<? extends IoFilter> filterType) {
        IoFilterChain.Entry e = this.getEntry(filterType);
        if (e == null) {
            return null;
        }
        return e.getFilter();
    }

    public List<IoFilterChain.Entry> getAll() {
        return new ArrayList<IoFilterChain.Entry>(this.entries);
    }

    public List<IoFilterChain.Entry> getAllReversed() {
        List<IoFilterChain.Entry> result = this.getAll();
        Collections.reverse(result);
        return result;
    }

    public boolean contains(String name) {
        return this.getEntry(name) != null;
    }

    public boolean contains(IoFilter filter) {
        return this.getEntry(filter) != null;
    }

    public boolean contains(Class<? extends IoFilter> filterType) {
        return this.getEntry(filterType) != null;
    }

    public synchronized void addFirst(String name, IoFilter filter) {
        this.register(0, new EntryImpl(name, filter));
    }

    public synchronized void addLast(String name, IoFilter filter) {
        this.register(this.entries.size(), new EntryImpl(name, filter));
    }

    public synchronized void addBefore(String baseName, String name, IoFilter filter) {
        this.checkBaseName(baseName);
        ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();
        while (i.hasNext()) {
            IoFilterChain.Entry base = i.next();
            if (!base.getName().equals(baseName)) continue;
            this.register(i.previousIndex(), new EntryImpl(name, filter));
            break;
        }
    }

    public synchronized void addAfter(String baseName, String name, IoFilter filter) {
        this.checkBaseName(baseName);
        ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();
        while (i.hasNext()) {
            IoFilterChain.Entry base = i.next();
            if (!base.getName().equals(baseName)) continue;
            this.register(i.nextIndex(), new EntryImpl(name, filter));
            break;
        }
    }

    public synchronized IoFilter remove(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name");
        }
        ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();
        while (i.hasNext()) {
            IoFilterChain.Entry e = i.next();
            if (!e.getName().equals(name)) continue;
            this.entries.remove(i.previousIndex());
            return e.getFilter();
        }
        throw new IllegalArgumentException("Unknown filter name: " + name);
    }

    public synchronized IoFilter remove(IoFilter filter) {
        if (filter == null) {
            throw new IllegalArgumentException("filter");
        }
        ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();
        while (i.hasNext()) {
            IoFilterChain.Entry e = i.next();
            if (e.getFilter() != filter) continue;
            this.entries.remove(i.previousIndex());
            return e.getFilter();
        }
        throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
    }

    public synchronized IoFilter remove(Class<? extends IoFilter> filterType) {
        if (filterType == null) {
            throw new IllegalArgumentException("filterType");
        }
        ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();
        while (i.hasNext()) {
            IoFilterChain.Entry e = i.next();
            if (!filterType.isAssignableFrom(e.getFilter().getClass())) continue;
            this.entries.remove(i.previousIndex());
            return e.getFilter();
        }
        throw new IllegalArgumentException("Filter not found: " + filterType.getName());
    }

    public synchronized IoFilter replace(String name, IoFilter newFilter) {
        this.checkBaseName(name);
        EntryImpl e = (EntryImpl)this.getEntry(name);
        IoFilter oldFilter = e.getFilter();
        e.setFilter(newFilter);
        return oldFilter;
    }

    public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
        for (IoFilterChain.Entry e : this.entries) {
            if (e.getFilter() != oldFilter) continue;
            ((EntryImpl)e).setFilter(newFilter);
            return;
        }
        throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
    }

    public synchronized void replace(Class<? extends IoFilter> oldFilterType, IoFilter newFilter) {
        for (IoFilterChain.Entry e : this.entries) {
            if (!oldFilterType.isAssignableFrom(e.getFilter().getClass())) continue;
            ((EntryImpl)e).setFilter(newFilter);
            return;
        }
        throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
    }

    public synchronized void clear() {
        this.entries.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFilters(Map<String, ? extends IoFilter> filters) {
        if (filters == null) {
            throw new IllegalArgumentException("filters");
        }
        if (!this.isOrderedMap(filters)) {
            throw new IllegalArgumentException("filters is not an ordered map. Please try " + LinkedHashMap.class.getName() + ".");
        }
        filters = new LinkedHashMap<String, IoFilter>(filters);
        for (Map.Entry<String, ? extends IoFilter> e : filters.entrySet()) {
            if (e.getKey() == null) {
                throw new IllegalArgumentException("filters contains a null key.");
            }
            if (e.getValue() != null) continue;
            throw new IllegalArgumentException("filters contains a null value.");
        }
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = this;
        synchronized (defaultIoFilterChainBuilder) {
            this.clear();
            for (Map.Entry<String, ? extends IoFilter> e : filters.entrySet()) {
                this.addLast(e.getKey(), e.getValue());
            }
        }
    }

    private boolean isOrderedMap(Map<String, ? extends IoFilter> map) {
        Map newMap;
        Class<?> mapType = map.getClass();
        if (LinkedHashMap.class.isAssignableFrom(mapType)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("{} is an ordered map.", (Object)mapType.getSimpleName());
            }
            return true;
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} is not a {}", (Object)mapType.getName(), (Object)LinkedHashMap.class.getSimpleName());
        }
        for (Class<?> type = mapType; type != null; type = type.getSuperclass()) {
            for (Class<?> i : type.getInterfaces()) {
                if (!i.getName().endsWith("OrderedMap")) continue;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("{} is an ordered map (guessed from that it implements OrderedMap interface.)", (Object)mapType.getSimpleName());
                }
                return true;
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("{} doesn't implement OrderedMap interface.", (Object)mapType.getName());
        }
        LOGGER.debug("Last resort; trying to create a new map instance with a default constructor and test if insertion order is maintained.");
        try {
            newMap = (Map)mapType.newInstance();
        }
        catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Failed to create a new map instance of '{}'.", (Object)mapType.getName(), (Object)e);
            }
            return false;
        }
        Random rand = new Random();
        ArrayList<String> expectedNames = new ArrayList<String>();
        IoFilterAdapter dummyFilter = new IoFilterAdapter();
        for (int i = 0; i < 65536; ++i) {
            String filterName;
            while (newMap.containsKey(filterName = String.valueOf(rand.nextInt()))) {
            }
            newMap.put(filterName, dummyFilter);
            expectedNames.add(filterName);
            Iterator it = expectedNames.iterator();
            for (Object key : newMap.keySet()) {
                if (((String)it.next()).equals(key)) continue;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("The specified map didn't pass the insertion order test after {} tries.", (Object)(i + 1));
                }
                return false;
            }
        }
        LOGGER.debug("The specified map passed the insertion order test.");
        return true;
    }

    @Override
    public void buildFilterChain(IoFilterChain chain) throws Exception {
        for (IoFilterChain.Entry e : this.entries) {
            chain.addLast(e.getName(), e.getFilter());
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{ ");
        boolean empty = true;
        for (IoFilterChain.Entry e : this.entries) {
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

    private void checkBaseName(String baseName) {
        if (baseName == null) {
            throw new IllegalArgumentException("baseName");
        }
        if (!this.contains(baseName)) {
            throw new IllegalArgumentException("Unknown filter name: " + baseName);
        }
    }

    private void register(int index, IoFilterChain.Entry e) {
        if (this.contains(e.getName())) {
            throw new IllegalArgumentException("Other filter is using the same name: " + e.getName());
        }
        this.entries.add(index, e);
    }

    private final class EntryImpl
    implements IoFilterChain.Entry {
        private final String name;
        private volatile IoFilter filter;

        private EntryImpl(String name, IoFilter filter) {
            if (name == null) {
                throw new IllegalArgumentException("name");
            }
            if (filter == null) {
                throw new IllegalArgumentException("filter");
            }
            this.name = name;
            this.filter = filter;
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
            this.filter = filter;
        }

        @Override
        public IoFilter.NextFilter getNextFilter() {
            throw new IllegalStateException();
        }

        public String toString() {
            return "(" + this.getName() + ':' + this.filter + ')';
        }

        @Override
        public void addAfter(String name, IoFilter filter) {
            DefaultIoFilterChainBuilder.this.addAfter(this.getName(), name, filter);
        }

        @Override
        public void addBefore(String name, IoFilter filter) {
            DefaultIoFilterChainBuilder.this.addBefore(this.getName(), name, filter);
        }

        @Override
        public void remove() {
            DefaultIoFilterChainBuilder.this.remove(this.getName());
        }

        @Override
        public void replace(IoFilter newFilter) {
            DefaultIoFilterChainBuilder.this.replace(this.getName(), newFilter);
        }
    }
}

