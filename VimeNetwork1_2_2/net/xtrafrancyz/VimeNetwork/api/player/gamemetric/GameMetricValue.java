/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricEntry;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricType;

public abstract class GameMetricValue {
    protected final String name;
    protected GameMetricType type;

    public GameMetricValue(String name, GameMetricType type) {
        this.name = name;
        this.type = type;
    }

    abstract GameMetricEntry global();

    abstract GameMetricEntry seasonal();

    public String getName() {
        return this.name;
    }

    public GameMetricType getType() {
        return this.type;
    }

    void commitChanges() {
        GameMetricEntry seasonal;
        GameMetricEntry global = this.global();
        if (global != null) {
            global.commitChanges();
        }
        if ((seasonal = this.seasonal()) != null) {
            seasonal.commitChanges();
        }
    }

    public boolean isChanged() {
        GameMetricEntry global = this.global();
        if (global != null && global.isChanged()) {
            return true;
        }
        GameMetricEntry seasonal = this.seasonal();
        return seasonal != null && seasonal.isChanged();
    }

    public void reset() {
        GameMetricEntry seasonal;
        GameMetricEntry global = this.global();
        if (global != null) {
            global.reset();
        }
        if ((seasonal = this.seasonal()) != null) {
            seasonal.reset();
        }
    }
}

