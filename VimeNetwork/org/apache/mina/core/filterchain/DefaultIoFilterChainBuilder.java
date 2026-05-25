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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultIoFilterChainBuilder implements IoFilterChainBuilder {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultIoFilterChainBuilder.class);
   private final List entries;

   public DefaultIoFilterChainBuilder() {
      this.entries = new CopyOnWriteArrayList();
   }

   public DefaultIoFilterChainBuilder(DefaultIoFilterChainBuilder filterChain) {
      if (filterChain == null) {
         throw new IllegalArgumentException("filterChain");
      } else {
         this.entries = new CopyOnWriteArrayList(filterChain.entries);
      }
   }

   public IoFilterChain.Entry getEntry(String name) {
      for(IoFilterChain.Entry e : this.entries) {
         if (e.getName().equals(name)) {
            return e;
         }
      }

      return null;
   }

   public IoFilterChain.Entry getEntry(IoFilter filter) {
      for(IoFilterChain.Entry e : this.entries) {
         if (e.getFilter() == filter) {
            return e;
         }
      }

      return null;
   }

   public IoFilterChain.Entry getEntry(Class filterType) {
      for(IoFilterChain.Entry e : this.entries) {
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

   public List getAll() {
      return new ArrayList(this.entries);
   }

   public List getAllReversed() {
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

   public boolean contains(Class filterType) {
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

      while(i.hasNext()) {
         IoFilterChain.Entry base = (IoFilterChain.Entry)i.next();
         if (base.getName().equals(baseName)) {
            this.register(i.previousIndex(), new EntryImpl(name, filter));
            break;
         }
      }

   }

   public synchronized void addAfter(String baseName, String name, IoFilter filter) {
      this.checkBaseName(baseName);
      ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();

      while(i.hasNext()) {
         IoFilterChain.Entry base = (IoFilterChain.Entry)i.next();
         if (base.getName().equals(baseName)) {
            this.register(i.nextIndex(), new EntryImpl(name, filter));
            break;
         }
      }

   }

   public synchronized IoFilter remove(String name) {
      if (name == null) {
         throw new IllegalArgumentException("name");
      } else {
         ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();

         while(i.hasNext()) {
            IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
            if (e.getName().equals(name)) {
               this.entries.remove(i.previousIndex());
               return e.getFilter();
            }
         }

         throw new IllegalArgumentException("Unknown filter name: " + name);
      }
   }

   public synchronized IoFilter remove(IoFilter filter) {
      if (filter == null) {
         throw new IllegalArgumentException("filter");
      } else {
         ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();

         while(i.hasNext()) {
            IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
            if (e.getFilter() == filter) {
               this.entries.remove(i.previousIndex());
               return e.getFilter();
            }
         }

         throw new IllegalArgumentException("Filter not found: " + filter.getClass().getName());
      }
   }

   public synchronized IoFilter remove(Class filterType) {
      if (filterType == null) {
         throw new IllegalArgumentException("filterType");
      } else {
         ListIterator<IoFilterChain.Entry> i = this.entries.listIterator();

         while(i.hasNext()) {
            IoFilterChain.Entry e = (IoFilterChain.Entry)i.next();
            if (filterType.isAssignableFrom(e.getFilter().getClass())) {
               this.entries.remove(i.previousIndex());
               return e.getFilter();
            }
         }

         throw new IllegalArgumentException("Filter not found: " + filterType.getName());
      }
   }

   public synchronized IoFilter replace(String name, IoFilter newFilter) {
      this.checkBaseName(name);
      EntryImpl e = (EntryImpl)this.getEntry(name);
      IoFilter oldFilter = e.getFilter();
      e.setFilter(newFilter);
      return oldFilter;
   }

   public synchronized void replace(IoFilter oldFilter, IoFilter newFilter) {
      for(IoFilterChain.Entry e : this.entries) {
         if (e.getFilter() == oldFilter) {
            ((EntryImpl)e).setFilter(newFilter);
            return;
         }
      }

      throw new IllegalArgumentException("Filter not found: " + oldFilter.getClass().getName());
   }

   public synchronized void replace(Class oldFilterType, IoFilter newFilter) {
      for(IoFilterChain.Entry e : this.entries) {
         if (oldFilterType.isAssignableFrom(e.getFilter().getClass())) {
            ((EntryImpl)e).setFilter(newFilter);
            return;
         }
      }

      throw new IllegalArgumentException("Filter not found: " + oldFilterType.getName());
   }

   public synchronized void clear() {
      this.entries.clear();
   }

   public void setFilters(Map filters) {
      if (filters == null) {
         throw new IllegalArgumentException("filters");
      } else if (!this.isOrderedMap(filters)) {
         throw new IllegalArgumentException("filters is not an ordered map. Please try " + LinkedHashMap.class.getName() + ".");
      } else {
         Map var7 = new LinkedHashMap(filters);

         for(Map.Entry e : var7.entrySet()) {
            if (e.getKey() == null) {
               throw new IllegalArgumentException("filters contains a null key.");
            }

            if (e.getValue() == null) {
               throw new IllegalArgumentException("filters contains a null value.");
            }
         }

         synchronized(this) {
            this.clear();

            for(Map.Entry e : var7.entrySet()) {
               this.addLast((String)e.getKey(), (IoFilter)e.getValue());
            }

         }
      }
   }

   private boolean isOrderedMap(Map map) {
      Class<?> mapType = map.getClass();
      if (LinkedHashMap.class.isAssignableFrom(mapType)) {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug((String)"{} is an ordered map.", (Object)mapType.getSimpleName());
         }

         return true;
      } else {
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug((String)"{} is not a {}", (Object)mapType.getName(), (Object)LinkedHashMap.class.getSimpleName());
         }

         for(Class<?> type = mapType; type != null; type = type.getSuperclass()) {
            for(Class i : type.getInterfaces()) {
               if (i.getName().endsWith("OrderedMap")) {
                  if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug((String)"{} is an ordered map (guessed from that it implements OrderedMap interface.)", (Object)mapType.getSimpleName());
                  }

                  return true;
               }
            }
         }

         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug((String)"{} doesn't implement OrderedMap interface.", (Object)mapType.getName());
         }

         LOGGER.debug("Last resort; trying to create a new map instance with a default constructor and test if insertion order is maintained.");

         Map<String, IoFilter> newMap;
         try {
            newMap = (Map)mapType.newInstance();
         } catch (Exception e) {
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug((String)"Failed to create a new map instance of '{}'.", (Object)mapType.getName(), (Object)e);
            }

            return false;
         }

         Random rand = new Random();
         List<String> expectedNames = new ArrayList();
         IoFilter dummyFilter = new IoFilterAdapter();

         for(int i = 0; i < 65536; ++i) {
            String filterName = String.valueOf(rand.nextInt());
            if (!newMap.containsKey(filterName)) {
               newMap.put(filterName, dummyFilter);
               expectedNames.add(filterName);
               Iterator<String> it = expectedNames.iterator();

               for(Object key : newMap.keySet()) {
                  if (!((String)it.next()).equals(key)) {
                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug((String)"The specified map didn't pass the insertion order test after {} tries.", (Object)(i + 1));
                     }

                     return false;
                  }
               }
            }
         }

         LOGGER.debug("The specified map passed the insertion order test.");
         return true;
      }
   }

   public void buildFilterChain(IoFilterChain chain) throws Exception {
      for(IoFilterChain.Entry e : this.entries) {
         chain.addLast(e.getName(), e.getFilter());
      }

   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("{ ");
      boolean empty = true;

      for(IoFilterChain.Entry e : this.entries) {
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
      } else if (!this.contains(baseName)) {
         throw new IllegalArgumentException("Unknown filter name: " + baseName);
      }
   }

   private void register(int index, IoFilterChain.Entry e) {
      if (this.contains(e.getName())) {
         throw new IllegalArgumentException("Other filter is using the same name: " + e.getName());
      } else {
         this.entries.add(index, e);
      }
   }

   private final class EntryImpl implements IoFilterChain.Entry {
      private final String name;
      private volatile IoFilter filter;

      private EntryImpl(String name, IoFilter filter) {
         if (name == null) {
            throw new IllegalArgumentException("name");
         } else if (filter == null) {
            throw new IllegalArgumentException("filter");
         } else {
            this.name = name;
            this.filter = filter;
         }
      }

      public String getName() {
         return this.name;
      }

      public IoFilter getFilter() {
         return this.filter;
      }

      private void setFilter(IoFilter filter) {
         this.filter = filter;
      }

      public IoFilter.NextFilter getNextFilter() {
         throw new IllegalStateException();
      }

      public String toString() {
         return "(" + this.getName() + ':' + this.filter + ')';
      }

      public void addAfter(String name, IoFilter filter) {
         DefaultIoFilterChainBuilder.this.addAfter(this.getName(), name, filter);
      }

      public void addBefore(String name, IoFilter filter) {
         DefaultIoFilterChainBuilder.this.addBefore(this.getName(), name, filter);
      }

      public void remove() {
         DefaultIoFilterChainBuilder.this.remove(this.getName());
      }

      public void replace(IoFilter newFilter) {
         DefaultIoFilterChainBuilder.this.replace(this.getName(), newFilter);
      }
   }
}
