/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.mina.util.LazyInitializer;

public class LazyInitializedCacheMap<K, V>
implements Map<K, V> {
    private ConcurrentMap<K, LazyInitializer<V>> cache;

    public LazyInitializedCacheMap() {
        this.cache = new ConcurrentHashMap<K, LazyInitializer<V>>();
    }

    public LazyInitializedCacheMap(ConcurrentHashMap<K, LazyInitializer<V>> map) {
        this.cache = map;
    }

    @Override
    public V get(Object key) {
        LazyInitializer c = (LazyInitializer)this.cache.get(key);
        if (c != null) {
            return c.get();
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        LazyInitializer c = (LazyInitializer)this.cache.remove(key);
        if (c != null) {
            return c.get();
        }
        return null;
    }

    @Override
    public V putIfAbsent(K key, LazyInitializer<V> value) {
        LazyInitializer<V> v = (LazyInitializer<V>)this.cache.get(key);
        if (v == null && (v = this.cache.putIfAbsent(key, value)) == null) {
            return value.get();
        }
        return v.get();
    }

    @Override
    public V put(K key, V value) {
        LazyInitializer c = this.cache.put(key, new NoopInitializer(value));
        if (c != null) {
            return c.get();
        }
        return null;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<K, V> e : m.entrySet()) {
            this.cache.put(e.getKey(), new NoopInitializer(e.getValue()));
        }
    }

    public Collection<LazyInitializer<V>> getValues() {
        return this.cache.values();
    }

    @Override
    public void clear() {
        this.cache.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.cache.containsKey(key);
    }

    @Override
    public boolean isEmpty() {
        return this.cache.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return this.cache.keySet();
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    public class NoopInitializer
    extends LazyInitializer<V> {
        private V value;

        public NoopInitializer(V value) {
            this.value = value;
        }

        @Override
        public V init() {
            return this.value;
        }
    }
}

