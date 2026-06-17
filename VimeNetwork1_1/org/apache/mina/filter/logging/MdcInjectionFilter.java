package org.apache.mina.filter.logging;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.util.CommonEventFilter;
import org.slf4j.MDC;

public class MdcInjectionFilter extends CommonEventFilter {
   private static final AttributeKey CONTEXT_KEY = new AttributeKey(MdcInjectionFilter.class, "context");
   private ThreadLocal callDepth = new ThreadLocal() {
      protected Integer initialValue() {
         return 0;
      }
   };
   private EnumSet mdcKeys;

   public MdcInjectionFilter(EnumSet keys) {
      this.mdcKeys = keys.clone();
   }

   public MdcInjectionFilter(MdcKey... keys) {
      Set<MdcKey> keySet = new HashSet(Arrays.asList(keys));
      this.mdcKeys = EnumSet.copyOf(keySet);
   }

   public MdcInjectionFilter() {
      this.mdcKeys = EnumSet.allOf(MdcKey.class);
   }

   protected void filter(IoFilterEvent event) throws Exception {
      int currentCallDepth = (Integer)this.callDepth.get();
      this.callDepth.set(currentCallDepth + 1);
      Map<String, String> context = this.getAndFillContext(event.getSession());
      if (currentCallDepth == 0) {
         for(Map.Entry e : context.entrySet()) {
            MDC.put((String)e.getKey(), (String)e.getValue());
         }
      }

      try {
         event.fire();
      } finally {
         if (currentCallDepth == 0) {
            for(String key : context.keySet()) {
               MDC.remove(key);
            }

            this.callDepth.remove();
         } else {
            this.callDepth.set(currentCallDepth);
         }

      }

   }

   private Map getAndFillContext(IoSession session) {
      Map<String, String> context = getContext(session);
      if (context.isEmpty()) {
         this.fillContext(session, context);
      }

      return context;
   }

   private static Map getContext(IoSession session) {
      Map<String, String> context = (Map)session.getAttribute(CONTEXT_KEY);
      if (context == null) {
         context = new ConcurrentHashMap();
         session.setAttribute(CONTEXT_KEY, context);
      }

      return context;
   }

   protected void fillContext(IoSession session, Map context) {
      if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.handlerClass)) {
         context.put(MdcInjectionFilter.MdcKey.handlerClass.name(), session.getHandler().getClass().getName());
      }

      if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.remoteAddress)) {
         context.put(MdcInjectionFilter.MdcKey.remoteAddress.name(), session.getRemoteAddress().toString());
      }

      if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.localAddress)) {
         context.put(MdcInjectionFilter.MdcKey.localAddress.name(), session.getLocalAddress().toString());
      }

      if (session.getTransportMetadata().getAddressType() == InetSocketAddress.class) {
         InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
         InetSocketAddress localAddress = (InetSocketAddress)session.getLocalAddress();
         if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.remoteIp)) {
            context.put(MdcInjectionFilter.MdcKey.remoteIp.name(), remoteAddress.getAddress().getHostAddress());
         }

         if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.remotePort)) {
            context.put(MdcInjectionFilter.MdcKey.remotePort.name(), String.valueOf(remoteAddress.getPort()));
         }

         if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.localIp)) {
            context.put(MdcInjectionFilter.MdcKey.localIp.name(), localAddress.getAddress().getHostAddress());
         }

         if (this.mdcKeys.contains(MdcInjectionFilter.MdcKey.localPort)) {
            context.put(MdcInjectionFilter.MdcKey.localPort.name(), String.valueOf(localAddress.getPort()));
         }
      }

   }

   public static String getProperty(IoSession session, String key) {
      if (key == null) {
         throw new IllegalArgumentException("key should not be null");
      } else {
         Map<String, String> context = getContext(session);
         String answer = (String)context.get(key);
         return answer != null ? answer : MDC.get(key);
      }
   }

   public static void setProperty(IoSession session, String key, String value) {
      if (key == null) {
         throw new IllegalArgumentException("key should not be null");
      } else {
         if (value == null) {
            removeProperty(session, key);
         }

         Map<String, String> context = getContext(session);
         context.put(key, value);
         MDC.put(key, value);
      }
   }

   public static void removeProperty(IoSession session, String key) {
      if (key == null) {
         throw new IllegalArgumentException("key should not be null");
      } else {
         Map<String, String> context = getContext(session);
         context.remove(key);
         MDC.remove(key);
      }
   }

   public static enum MdcKey {
      handlerClass,
      remoteAddress,
      localAddress,
      remoteIp,
      remotePort,
      localIp,
      localPort;
   }
}
