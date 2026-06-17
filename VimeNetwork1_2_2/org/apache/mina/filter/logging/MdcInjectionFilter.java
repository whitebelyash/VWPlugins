/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.logging;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.filterchain.IoFilterEvent;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.util.CommonEventFilter;
import org.slf4j.MDC;

public class MdcInjectionFilter
extends CommonEventFilter {
    private static final AttributeKey CONTEXT_KEY = new AttributeKey(MdcInjectionFilter.class, "context");
    private ThreadLocal<Integer> callDepth = new ThreadLocal<Integer>(){

        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private EnumSet<MdcKey> mdcKeys;

    public MdcInjectionFilter(EnumSet<MdcKey> keys) {
        this.mdcKeys = keys.clone();
    }

    public MdcInjectionFilter(MdcKey ... keys) {
        HashSet<MdcKey> keySet = new HashSet<MdcKey>(Arrays.asList(keys));
        this.mdcKeys = EnumSet.copyOf(keySet);
    }

    public MdcInjectionFilter() {
        this.mdcKeys = EnumSet.allOf(MdcKey.class);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void filter(IoFilterEvent event) throws Exception {
        block9: {
            int currentCallDepth;
            block8: {
                currentCallDepth = this.callDepth.get();
                this.callDepth.set(currentCallDepth + 1);
                Map<String, String> context = this.getAndFillContext(event.getSession());
                if (currentCallDepth == 0) {
                    for (Map.Entry<String, String> e : context.entrySet()) {
                        MDC.put(e.getKey(), e.getValue());
                    }
                }
                try {
                    event.fire();
                    if (currentCallDepth != 0) break block8;
                }
                catch (Throwable throwable) {
                    if (currentCallDepth == 0) {
                        for (String key : context.keySet()) {
                            MDC.remove(key);
                        }
                        this.callDepth.remove();
                    } else {
                        this.callDepth.set(currentCallDepth);
                    }
                    throw throwable;
                }
                for (String key : context.keySet()) {
                    MDC.remove(key);
                }
                this.callDepth.remove();
                break block9;
            }
            this.callDepth.set(currentCallDepth);
        }
    }

    private Map<String, String> getAndFillContext(IoSession session) {
        Map<String, String> context = MdcInjectionFilter.getContext(session);
        if (context.isEmpty()) {
            this.fillContext(session, context);
        }
        return context;
    }

    private static Map<String, String> getContext(IoSession session) {
        ConcurrentHashMap context = (ConcurrentHashMap)session.getAttribute(CONTEXT_KEY);
        if (context == null) {
            context = new ConcurrentHashMap();
            session.setAttribute(CONTEXT_KEY, context);
        }
        return context;
    }

    protected void fillContext(IoSession session, Map<String, String> context) {
        if (this.mdcKeys.contains((Object)MdcKey.handlerClass)) {
            context.put(MdcKey.handlerClass.name(), session.getHandler().getClass().getName());
        }
        if (this.mdcKeys.contains((Object)MdcKey.remoteAddress)) {
            context.put(MdcKey.remoteAddress.name(), session.getRemoteAddress().toString());
        }
        if (this.mdcKeys.contains((Object)MdcKey.localAddress)) {
            context.put(MdcKey.localAddress.name(), session.getLocalAddress().toString());
        }
        if (session.getTransportMetadata().getAddressType() == InetSocketAddress.class) {
            InetSocketAddress remoteAddress = (InetSocketAddress)session.getRemoteAddress();
            InetSocketAddress localAddress = (InetSocketAddress)session.getLocalAddress();
            if (this.mdcKeys.contains((Object)MdcKey.remoteIp)) {
                context.put(MdcKey.remoteIp.name(), remoteAddress.getAddress().getHostAddress());
            }
            if (this.mdcKeys.contains((Object)MdcKey.remotePort)) {
                context.put(MdcKey.remotePort.name(), String.valueOf(remoteAddress.getPort()));
            }
            if (this.mdcKeys.contains((Object)MdcKey.localIp)) {
                context.put(MdcKey.localIp.name(), localAddress.getAddress().getHostAddress());
            }
            if (this.mdcKeys.contains((Object)MdcKey.localPort)) {
                context.put(MdcKey.localPort.name(), String.valueOf(localAddress.getPort()));
            }
        }
    }

    public static String getProperty(IoSession session, String key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }
        Map<String, String> context = MdcInjectionFilter.getContext(session);
        String answer = context.get(key);
        if (answer != null) {
            return answer;
        }
        return MDC.get(key);
    }

    public static void setProperty(IoSession session, String key, String value) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }
        if (value == null) {
            MdcInjectionFilter.removeProperty(session, key);
        }
        Map<String, String> context = MdcInjectionFilter.getContext(session);
        context.put(key, value);
        MDC.put(key, value);
    }

    public static void removeProperty(IoSession session, String key) {
        if (key == null) {
            throw new IllegalArgumentException("key should not be null");
        }
        Map<String, String> context = MdcInjectionFilter.getContext(session);
        context.remove(key);
        MDC.remove(key);
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

