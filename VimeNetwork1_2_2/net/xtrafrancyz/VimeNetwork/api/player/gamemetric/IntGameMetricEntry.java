/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricEntry;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.IntGameMetric;

public class IntGameMetricEntry
extends GameMetricEntry {
    private int initial;
    private int value;

    IntGameMetricEntry(IntGameMetric parent) {
        super(parent);
    }

    public int get() {
        return this.value;
    }

    public void set(int val) {
        this.value = val;
    }

    public void add(int val) {
        this.value += val;
    }

    @Override
    String mysqlWrite() {
        return Integer.toString(this.value);
    }

    @Override
    void mysqlRead(ResultSet rs) throws SQLException {
        this.initial = this.value = rs.getInt(this.parent.name);
    }

    @Override
    Object coreWrite() {
        return this.value;
    }

    @Override
    void coreRead(Object val) {
        this.initial = this.value = ((Integer)val).intValue();
    }

    @Override
    public boolean isChanged() {
        return this.initial != this.value;
    }

    @Override
    void commitChanges() {
        this.initial = this.value;
    }

    @Override
    void reset() {
        this.value = 0;
        this.initial = 0;
    }
}

