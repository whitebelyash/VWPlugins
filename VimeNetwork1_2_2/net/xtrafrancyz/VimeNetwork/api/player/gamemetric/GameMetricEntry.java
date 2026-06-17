/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricValue;

public abstract class GameMetricEntry {
    protected final GameMetricValue parent;

    GameMetricEntry(GameMetricValue parent) {
        this.parent = parent;
    }

    abstract String mysqlWrite();

    abstract void mysqlRead(ResultSet var1) throws SQLException;

    abstract Object coreWrite();

    abstract void coreRead(Object var1);

    public abstract boolean isChanged();

    abstract void commitChanges();

    abstract void reset();
}

