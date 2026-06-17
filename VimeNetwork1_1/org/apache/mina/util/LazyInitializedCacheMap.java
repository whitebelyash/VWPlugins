package org.apache.mina.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LazyInitializedCacheMap implements Map {
   private ConcurrentMap cache;

   public LazyInitializedCacheMap() {
      this.cache = new ConcurrentHashMap();
   }

   public LazyInitializedCacheMap(ConcurrentHashMap map) {
      this.cache = map;
   }

   public Object get(Object key) {
      LazyInitializer<V> c = (LazyInitializer)this.cache.get(key);
      return c != null ? c.get() : null;
   }

   public Object remove(Object key) {
      LazyInitializer<V> c = (LazyInitializer)this.cache.remove(key);
      return c != null ? c.get() : null;
   }

   public Object putIfAbsent(Object key, LazyInitializer value) {
      LazyInitializer<V> v = (LazyInitializer)this.cache.get(key);
      if (v == null) {
         v = (LazyInitializer)this.cache.putIfAbsent(key, value);
         if (v == null) {
            return value.get();
         }
      }

      return v.get();
   }

   public Object put(Object key, Object value) {
      LazyInitializer<V> c = (LazyInitializer)this.cache.put(key, new NoopInitializer(value));
      return c != null ? c.get() : null;
   }

   public boolean containsValue(Object value) {
      throw new UnsupportedOperationException();
   }

   public Collection values() {
      throw new UnsupportedOperationException();
   }

   public Set entrySet() {
      throw new UnsupportedOperationException();
   }

   public void putAll(Map m) {
      for(Map.Entry e : m.entrySet()) {
         this.cache.put(e.getKey(), new NoopInitializer(e.getValue()));
      }

   }

   public Collection getValues() {
      return this.cache.values();
   }

   public void clear() {
      this.cache.clear();
   }

   public boolean containsKey(Object key) {
      return this.cache.containsKey(key);
   }

   public boolean isEmpty() {
      return this.cache.isEmpty();
   }

   public Set keySet() {
      return this.cache.keySet();
   }

   public int size() {
      return this.cache.size();
   }

   public class NoopInitializer extends LazyInitializer {
      private Object value;

      public NoopInitializer(Object value) {
         this.value = value;
      }

      public Object init() {
         return this.value;
      }
   }
}
