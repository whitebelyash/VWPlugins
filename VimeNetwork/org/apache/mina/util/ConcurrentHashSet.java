package org.apache.mina.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashSet extends MapBackedSet {
   private static final long serialVersionUID = 8518578988740277828L;

   public ConcurrentHashSet() {
      super(new ConcurrentHashMap());
   }

   public ConcurrentHashSet(Collection c) {
      super(new ConcurrentHashMap(), c);
   }

   public boolean add(Object o) {
      Boolean answer = (Boolean)((ConcurrentMap)this.map).putIfAbsent(o, Boolean.TRUE);
      return answer == null;
   }
}
