/*
 * Decompiled with CFR 0.152.
 */
package org.apache.mina.util;

public abstract class LazyInitializer<V> {
    private V value;

    public abstract V init();

    public V get() {
        if (this.value == null) {
            this.value = this.init();
        }
        return this.value;
    }
}

