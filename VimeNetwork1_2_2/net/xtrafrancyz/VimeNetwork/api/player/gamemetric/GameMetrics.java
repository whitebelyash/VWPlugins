/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.player.gamemetric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.xtrafrancyz.Commons.season.GameSeason;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.mysql.CoreMysqlCache;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricType;
import net.xtrafrancyz.VimeNetwork.api.player.gamemetric.GameMetricValue;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class GameMetrics {
    protected final NetworkPlayer player;
    private final String game;
    private List<GameMetricValue> metrics;
    private GameSeason season;
    private boolean readOnly = false;
    private AtomicInteger loadCounter = new AtomicInteger(-1);
    private List<Consumer<GameMetrics>> callbacks = new LinkedList<Consumer<GameMetrics>>();

    public GameMetrics(String game, NetworkPlayer player) {
        this.game = game;
        this.player = player;
        this.metrics = new ArrayList<GameMetricValue>();
    }

    public void addMetrics(GameMetricValue ... metrics) {
        this.metrics.addAll(Arrays.asList(metrics));
    }

    public void setReadOnly(boolean flag) {
        this.readOnly = flag;
    }

    public void setSeason(GameSeason season) {
        this.season = season;
    }

    public List<GameMetricValue> getMetrics() {
        return this.metrics;
    }

    public boolean isLoaded() {
        return this.loadCounter.get() == 0;
    }

    public void runWhenLoaded(Consumer<GameMetrics> callback) {
        if (this.isLoaded()) {
            callback.accept(this);
        } else {
            this.callbacks.add(callback);
        }
    }

    public void load() {
        for (GameMetricValue metric : this.metrics) {
            metric.reset();
        }
        if (VimeNetwork.core().isConnected()) {
            this.coreLoad();
        } else {
            this.mysqlLoad();
        }
    }

    public void save() {
        if (!this.isLoaded() || this.readOnly) {
            return;
        }
        this.beforeSave();
        if (VimeNetwork.core().isConnected()) {
            this.coreSave();
        } else {
            this.mysqlSave();
        }
        for (GameMetricValue metric : this.metrics) {
            metric.commitChanges();
        }
    }

    protected void afterLoad() {
    }

    protected void beforeSave() {
    }

    private void queryFinished() {
        if (this.loadCounter.decrementAndGet() == 0) {
            this.afterLoad();
            if (this.callbacks.isEmpty()) {
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> {
                for (Consumer<GameMetrics> callback : this.callbacks) {
                    callback.accept(this);
                }
                this.callbacks.clear();
            });
        }
    }

    private void coreLoad() {
        LinkedList<GameMetricValue> columns;
        if (this.season != null) {
            this.loadCounter.set(2);
            columns = new LinkedList<GameMetricValue>();
            for (GameMetricValue metric : this.metrics) {
                if (metric.getType() == GameMetricType.GLOBAL) continue;
                columns.add(metric);
            }
            this.coreRequestData(this.game + "_stats" + this.season.getTableSuffix(), columns, (m, v) -> m.seasonal().coreRead(v));
        } else {
            this.loadCounter.set(1);
        }
        columns = new LinkedList();
        for (GameMetricValue metric : this.metrics) {
            if (metric.getType() == GameMetricType.SEASONAL) continue;
            columns.add(metric);
        }
        this.coreRequestData(this.game + "_stats", columns, (m, v) -> m.global().coreRead(v));
    }

    private void coreRequestData(String table, List<GameMetricValue> metrics, BiConsumer<GameMetricValue, Object> reader) {
        if (metrics.isEmpty()) {
            this.queryFinished();
            return;
        }
        LinkedList<String> columns = new LinkedList<String>();
        for (GameMetricValue value : metrics) {
            columns.add(value.getName());
        }
        CoreMysqlCache.load(table, this.player.getId(), columns, values -> {
            if (values != null) {
                Iterator it = metrics.iterator();
                int i = 0;
                while (it.hasNext()) {
                    reader.accept((GameMetricValue)it.next(), values[i]);
                    ++i;
                }
            }
            this.queryFinished();
        });
    }

    private void coreSave() {
        LinkedList<String> columns = new LinkedList<String>();
        LinkedList<Object> values = new LinkedList<Object>();
        for (GameMetricValue metric : this.metrics) {
            if (metric.getType() == GameMetricType.SEASONAL || !metric.isChanged()) continue;
            columns.add(metric.getName());
            values.add(metric.global().coreWrite());
        }
        if (!columns.isEmpty()) {
            CoreMysqlCache.save(this.game + "_stats", (Object)this.player.getId(), columns, values);
        }
        columns.clear();
        values.clear();
        for (GameMetricValue metric : this.metrics) {
            if (metric.getType() == GameMetricType.GLOBAL || !metric.isChanged()) continue;
            columns.add(metric.getName());
            values.add(metric.seasonal().coreWrite());
        }
        if (!columns.isEmpty()) {
            CoreMysqlCache.save(this.game + "_stats" + this.season.getTableSuffix(), (Object)this.player.getId(), columns, values);
        }
    }

    private void mysqlLoad() {
        StringBuilder sb;
        if (this.season != null) {
            this.loadCounter.set(2);
            sb = new StringBuilder();
            for (GameMetricValue metric : this.metrics) {
                if (metric.getType() == GameMetricType.GLOBAL) continue;
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(metric.getName());
            }
            if (sb.length() == 0) {
                sb.append('1');
            }
            VimeNetwork.mysql().select("SELECT " + sb + " FROM " + this.game + "_stats" + this.season.getTableSuffix() + " WHERE userid = " + this.player.getId(), rs -> {
                if (rs.next()) {
                    for (GameMetricValue metric : this.metrics) {
                        if (metric.getType() == GameMetricType.GLOBAL) continue;
                        metric.seasonal().mysqlRead(rs);
                    }
                } else if (!this.readOnly) {
                    VimeNetwork.mysql().query("INSERT INTO " + this.game + "_stats" + this.season.getTableSuffix() + " (userid) VALUES (" + this.player.getId() + ")");
                }
                this.queryFinished();
            });
        } else {
            this.loadCounter.set(1);
        }
        sb = new StringBuilder();
        for (GameMetricValue metric : this.metrics) {
            if (metric.getType() == GameMetricType.SEASONAL) continue;
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(metric.getName());
        }
        if (sb.length() == 0) {
            sb.append('1');
        }
        VimeNetwork.mysql().select("SELECT " + sb + " FROM " + this.game + "_stats WHERE userid = " + this.player.getId(), rs -> {
            if (rs.next()) {
                for (GameMetricValue metric : this.metrics) {
                    if (metric.getType() == GameMetricType.SEASONAL) continue;
                    metric.global().mysqlRead(rs);
                }
            } else if (!this.readOnly) {
                VimeNetwork.mysql().query("INSERT INTO " + this.game + "_stats (userid) VALUES (" + this.player.getId() + ")");
            }
            this.queryFinished();
        });
    }

    private void mysqlSave() {
        StringBuilder sb = new StringBuilder();
        for (GameMetricValue metric : this.metrics) {
            if (metric.getType() == GameMetricType.SEASONAL || !metric.isChanged()) continue;
            if (sb.length() != 0) {
                sb.append(',');
            }
            sb.append(metric.getName()).append('=').append(metric.global().mysqlWrite());
        }
        if (sb.length() > 0) {
            VimeNetwork.mysql().query("UPDATE " + this.game + "_stats SET " + sb + " WHERE userid = " + this.player.getId());
        }
        if (this.season != null) {
            sb = new StringBuilder();
            for (GameMetricValue metric : this.metrics) {
                if (metric.getType() == GameMetricType.GLOBAL || !metric.isChanged()) continue;
                if (sb.length() != 0) {
                    sb.append(',');
                }
                sb.append(metric.getName()).append('=').append(metric.seasonal().mysqlWrite());
            }
            if (sb.length() > 0) {
                VimeNetwork.mysql().query("UPDATE " + this.game + "_stats" + this.season.getTableSuffix() + " SET " + sb + " WHERE userid = " + this.player.getId());
            }
        }
    }
}

