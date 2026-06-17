package org.slf4j.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.spi.MDCAdapter;

public class BasicMDCAdapter implements MDCAdapter {
   private InheritableThreadLocal inheritableThreadLocal = new InheritableThreadLocal() {
      protected Map childValue(Map parentValue) {
         return parentValue == null ? null : new HashMap(parentValue);
      }
   };

   public void put(String key, String val) {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null");
      } else {
         Map<String, String> map = (Map)this.inheritableThreadLocal.get();
         if (map == null) {
            map = new HashMap();
            this.inheritableThreadLocal.set(map);
         }

         map.put(key, val);
      }
   }

   public String get(String key) {
      Map<String, String> map = (Map)this.inheritableThreadLocal.get();
      return map != null && key != null ? (String)map.get(key) : null;
   }

   public void remove(String key) {
      Map<String, String> map = (Map)this.inheritableThreadLocal.get();
      if (map != null) {
         map.remove(key);
      }

   }

   public void clear() {
      Map<String, String> map = (Map)this.inheritableThreadLocal.get();
      if (map != null) {
         map.clear();
         this.inheritableThreadLocal.remove();
      }

   }

   public Set getKeys() {
      Map<String, String> map = (Map)this.inheritableThreadLocal.get();
      return map != null ? map.keySet() : null;
   }

   public Map getCopyOfContextMap() {
      Map<String, String> oldMap = (Map)this.inheritableThreadLocal.get();
      return oldMap != null ? new HashMap(oldMap) : null;
   }

   public void setContextMap(Map contextMap) {
      this.inheritableThreadLocal.set(new HashMap(contextMap));
   }
}
