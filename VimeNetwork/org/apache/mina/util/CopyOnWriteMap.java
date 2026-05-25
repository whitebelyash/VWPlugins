package org.apache.mina.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CopyOnWriteMap implements Map, Cloneable {
   private volatile Map internalMap;

   public CopyOnWriteMap() {
      this.internalMap = new HashMap();
   }

   public CopyOnWriteMap(int initialCapacity) {
      this.internalMap = new HashMap(initialCapacity);
   }

   public CopyOnWriteMap(Map data) {
      this.internalMap = new HashMap(data);
   }

   public Object put(Object key, Object value) {
      synchronized(this) {
         Map<K, V> newMap = new HashMap(this.internalMap);
         V val = (V)newMap.put(key, value);
         this.internalMap = newMap;
         return val;
      }
   }

   public Object remove(Object key) {
      synchronized(this) {
         Map<K, V> newMap = new HashMap(this.internalMap);
         V val = (V)newMap.remove(key);
         this.internalMap = newMap;
         return val;
      }
   }

   public void putAll(Map newData) {
      synchronized(this) {
         Map<K, V> newMap = new HashMap(this.internalMap);
         newMap.putAll(newData);
         this.internalMap = newMap;
      }
   }

   public void clear() {
      synchronized(this) {
         this.internalMap = new HashMap();
      }
   }

   public int size() {
      return this.internalMap.size();
   }

   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   public boolean containsKey(Object key) {
      return this.internalMap.containsKey(key);
   }

   public boolean containsValue(Object value) {
      return this.internalMap.containsValue(value);
   }

   public Object get(Object key) {
      return this.internalMap.get(key);
   }

   public Set keySet() {
      return this.internalMap.keySet();
   }

   public Collection values() {
      return this.internalMap.values();
   }

   public Set entrySet() {
      return this.internalMap.entrySet();
   }

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }
}
