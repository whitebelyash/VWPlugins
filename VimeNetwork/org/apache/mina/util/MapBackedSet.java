package org.apache.mina.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class MapBackedSet extends AbstractSet implements Serializable {
   private static final long serialVersionUID = -8347878570391674042L;
   protected final Map map;

   public MapBackedSet(Map map) {
      this.map = map;
   }

   public MapBackedSet(Map map, Collection c) {
      this.map = map;
      this.addAll(c);
   }

   public int size() {
      return this.map.size();
   }

   public boolean contains(Object o) {
      return this.map.containsKey(o);
   }

   public Iterator iterator() {
      return this.map.keySet().iterator();
   }

   public boolean add(Object o) {
      return this.map.put(o, Boolean.TRUE) == null;
   }

   public boolean remove(Object o) {
      return this.map.remove(o) != null;
   }

   public void clear() {
      this.map.clear();
   }
}
