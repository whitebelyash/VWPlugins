/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricType;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricValue;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.IntGameMetricEntry;

public class IntGameMetric
extends GameMetricValue {
    private IntGameMetricEntry global;
    private IntGameMetricEntry seasonal;

    public IntGameMetric(String name, GameMetricType type) {
        super(name, type);
        if (type != GameMetricType.SEASONAL) {
            this.global = new IntGameMetricEntry(this);
        }
        if (type != GameMetricType.GLOBAL) {
            this.seasonal = new IntGameMetricEntry(this);
        }
    }

    @Override
    public IntGameMetricEntry global() {
        return this.global;
    }

    @Override
    public IntGameMetricEntry seasonal() {
        return this.seasonal;
    }

    public void add(int val) {
        if (this.global != null) {
            this.global.add(val);
        }
        if (this.seasonal != null) {
            this.seasonal.add(val);
        }
    }

    public void set(int val) {
        if (this.global != null) {
            this.global.set(val);
        }
        if (this.seasonal != null) {
            this.seasonal.set(val);
        }
    }

    public int get() {
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

