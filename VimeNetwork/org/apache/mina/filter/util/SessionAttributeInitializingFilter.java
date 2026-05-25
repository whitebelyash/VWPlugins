package org.apache.mina.filter.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class SessionAttributeInitializingFilter extends IoFilterAdapter {
   private final Map attributes = new ConcurrentHashMap();

   public SessionAttributeInitializingFilter() {
   }

   public SessionAttributeInitializingFilter(Map attributes) {
      this.setAttributes(attributes);
   }

   public Object getAttribute(String key) {
      return this.attributes.get(key);
   }

   public Object setAttribute(String key, Object value) {
      return value == null ? this.removeAttribute(key) : this.attributes.put(key, value);
   }

   public Object setAttribute(String key) {
      return this.attributes.put(key, Boolean.TRUE);
   }

   public Object removeAttribute(String key) {
      return this.attributes.remove(key);
   }

   boolean containsAttribute(String key) {
      return this.attributes.containsKey(key);
   }

   public Set getAttributeKeys() {
      return this.attributes.keySet();
   }

   public void setAttributes(Map attributes) {
      if (attributes == null) {
         attributes = new ConcurrentHashMap();
      }

      this.attributes.clear();
      this.attributes.putAll(attributes);
   }

   public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
      for(Map.Entry e : this.attributes.entrySet()) {
         session.setAttribute(e.getKey(), e.getValue());
      }

      nextFilter.sessionCreated(session);
   }
}
