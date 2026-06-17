/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricEntry;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.StringGameMetric;

public class StringGameMetricEntry
extends GameMetricEntry {
    private String initial;
    private String value;

    StringGameMetricEntry(StringGameMetric parent) {
        super(parent);
    }

    public void set(String val) {
        this.value = val;
    }

    public String get() {
        return this.value;
    }

    @Override
    String mysqlWrite() {
        return this.value == null ? "NULL" : "'" + this.value.replaceAll("'", "''") + "'";
    }

    @Override
    void mysqlRead(ResultSet rs) throws SQLException {
        this.initial = this.value = rs.getString(this.parent.name);
    }

    @Override
    Object coreWrite() {
        return this.value;
    }

    @Override
    void coreRead(Object val) {
        this.initial = this.value = (String)val;
    }

    @Override
    public boolean isChanged() {
        return !Objects.equals(this.initial, this.value);
    }

    @Override
    void commitChanges() {
        this.initial = this.value;
    }

    @Override
    void reset() {
        this.value = null;
        this.initial = null;
    }
}

