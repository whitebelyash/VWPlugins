/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.filter.util;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

public class SessionAttributeInitializingFilter
extends IoFilterAdapter {
    private final Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public SessionAttributeInitializingFilter() {
    }

    public SessionAttributeInitializingFilter(Map<String, ? extends Object> attributes) {
        this.setAttributes(attributes);
    }

    public Object getAttribute(String key) {
        return this.attributes.get(key);
    }

    public Object setAttribute(String key, Object value) {
        if (value == null) {
            return this.removeAttribute(key);
        }
        return this.attributes.put(key, value);
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

    public Set<String> getAttributeKeys() {
        return this.attributes.keySet();
    }

    public void setAttributes(Map<String, ? extends Object> attributes) {
        if (attributes == null) {
            attributes = new ConcurrentHashMap<String, Object>();
        }
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }

    @Override
    public void sessionCreated(IoFilter.NextFilter nextFilter, IoSession session) throws Exception {
        for (Map.Entry<String, Object> e : this.attributes.entrySet()) {
            session.setAttribute(e.getKey(), e.getValue());
        }
        nextFilter.sessionCreated(session);
    }
}

