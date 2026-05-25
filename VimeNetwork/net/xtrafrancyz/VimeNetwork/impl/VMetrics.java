package net.xtrafrancyz.VimeNetwork.impl;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Metrics;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;

public class VMetrics implements Metrics {
   private static final int FLUSH_INTERVAL_TICKS = 24000;
   private final HashMap map = new HashMap();

   public VMetrics(VNPlugin plugin) {
      Value.class.getName();
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::flush, 24000L, 24000L);
   }

   public void add(String key, int amount) {
      Value var10000 = (Value)this.map.computeIfAbsent(key, (k) -> new Value());
      var10000.value += amount;
   }

   public void flush() {
      for(Map.Entry entry : this.map.entrySet()) {
         if (((Value)entry.getValue()).value != 0) {
            if (!((Value)entry.getValue()).inserted) {
               ((Value)entry.getValue()).inserted = true;
               VimeNetwork.mysql().query("INSERT INTO `metrics` (`id`, `value`) VALUES ('" + (String)entry.getKey() + "', " + ((Value)entry.getValue()).value + ") ON DUPLICATE KEY UPDATE `value` = `value` + " + ((Value)entry.getValue()).value);
            } else {
               VimeNetwork.mysql().query("UPDATE `metrics` SET `value` = `value` + " + ((Value)entry.getValue()).value + " WHERE id = '" + (String)entry.getKey() + "'");
            }

            ((Value)entry.getValue()).value = 0;
         }
      }

   }

   private static class Value {
      boolean inserted;
      int value;

      private Value() {
         this.inserted = false;
         this.value = 0;
      }
   }
}
