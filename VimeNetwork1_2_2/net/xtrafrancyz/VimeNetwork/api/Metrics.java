/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api;

public interface Metrics {
    default public void add(String key) {
        this.add(key, 1);
    }

    public void add(String var1, int var2);
}

