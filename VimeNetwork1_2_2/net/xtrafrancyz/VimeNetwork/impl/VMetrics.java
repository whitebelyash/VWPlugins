/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl;

import java.lang.invoke.LambdaMetafactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Metrics;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class VMetrics
implements Metrics {
    private static final int FLUSH_INTERVAL_TICKS = 24000;
    private final HashMap<String, Value> map = new HashMap();

    public VMetrics(VNPlugin plugin) {
        Value.class.getName();
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, this::flush, 24000L, 24000L);
    }

    @Override
    public void add(String key, int amount) {
        this.map.computeIfAbsent((String)key, (Function<String, Value>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, lambda$add$0(java.lang.String ), (Ljava/lang/String;)Lnet/xtrafrancyz/VimeNetwork/impl/VMetrics$Value;)()).value += amount;
    }

    public void flush() {
        for (Map.Entry<String, Value> entry : this.map.entrySet()) {
            if (entry.getValue().value == 0) continue;
            if (!entry.getValue().inserted) {
                entry.getValue().inserted = true;
                VimeNetwork.mysql().query("INSERT INTO `metrics` (`id`, `value`) VALUES ('" + entry.getKey() + "', " + entry.getValue().value + ") ON DUPLICATE KEY UPDATE `value` = `value` + " + entry.getValue().value);
            } else {
                VimeNetwork.mysql().query("UPDATE `metrics` SET `value` = `value` + " + entry.getValue().value + " WHERE id = '" + entry.getKey() + "'");
            }
            entry.getValue().value = 0;
        }
    }

    private static /* synthetic */ Value lambda$add$0(String k) {
        return new Value();
    }

    private static class Value {
        boolean inserted = false;
        int value = 0;

        private Value() {
        }
    }
}

