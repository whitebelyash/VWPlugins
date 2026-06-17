/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricType;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricValue;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.StringGameMetricEntry;

public class StringGameMetric
extends GameMetricValue {
    private StringGameMetricEntry global;
    private StringGameMetricEntry seasonal;

    public StringGameMetric(String name, GameMetricType type) {
        super(name, type);
        if (type != GameMetricType.SEASONAL) {
            this.global = new StringGameMetricEntry(this);
        }
        if (type != GameMetricType.GLOBAL) {
            this.seasonal = new StringGameMetricEntry(this);
        }
    }

    @Override
    public StringGameMetricEntry global() {
        return this.global;
    }

    @Override
    public StringGameMetricEntry seasonal() {
        return this.seasonal;
    }

    public void set(String val) {
        if (this.global != null) {
            this.global.set(val);
        }
        if (this.seasonal != null) {
            this.seasonal.set(val);
        }
    }

    public String get() {
        switch (this.type) {
            case SHARED: {
                throw new IllegalStateException("You can't use get() method on SHARED metric type");
            }
            case GLOBAL: {
                return this.global.get();
            }
            case SEASONAL: {
                return this.seasonal.get();
            }
        }
        throw new IllegalStateException("Illegal metric type = " + this.type.name());
    }
}

