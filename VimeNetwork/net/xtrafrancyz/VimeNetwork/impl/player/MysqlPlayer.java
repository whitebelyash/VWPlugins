package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public class MysqlPlayer extends VPlayer {
   public Set ignored = new HashSet();
   public boolean ignoreAll = false;
   public String lastWriter = null;
   public Map meta = new ConcurrentHashMap();

   public MysqlPlayer(Player player) {
      super(player);
   }

   public String getMeta(String key) {
      MetaValue value = (MetaValue)this.meta.get(key);
      return value == null ? null : value.value;
   }

   public void setMeta(String key, String value) {
      if (value == null) {
         this.removeMeta(key);
      } else {
         MetaValue metaValue = (MetaValue)this.meta.computeIfAbsent(key, (k) -> new MetaValue(value));
         metaValue.changed = System.currentTimeMillis();
         metaValue.value = value;
         metaValue.saved = false;
      }
   }

   public boolean hasMeta(String key) {
      return this.meta.containsKey(key);
   }

   public String removeMeta(String key) {
      MetaValue prev = (MetaValue)this.meta.get(key);
      if (prev != null) {
         prev.value = null;
         prev.saved = false;
         prev.changed = System.currentTimeMillis();
      }

      return prev == null ? null : prev.value;
   }

   public Map getMetaMap() {
      HashMap<String, String> newMap = new HashMap(this.meta.size());

      for(Map.Entry entry : this.meta.entrySet()) {
         newMap.put(entry.getKey(), ((MetaValue)entry.getValue()).value);
      }

      return newMap;
   }

   public void dispose() {
      this.meta.clear();
      this.lastWriter = null;
      this.ignored.clear();
      super.dispose();
   }

   public static class MetaValue {
      public String value;
      public String prev;
      public long changed;
      public boolean saved = false;

      public MetaValue(String value) {
         this.value = value;
      }
   }
}
