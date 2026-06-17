package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.Core.network.packet.Packet6PlayerMetaChange;
import org.bukkit.entity.Player;

public class CorePlayer extends VPlayer {
   public Map meta = new ConcurrentHashMap();

   public CorePlayer(Player player) {
      super(player);
   }

   public String getMeta(String key) {
      return (String)this.meta.get(key);
   }

   public void setMeta(String key, String value) {
      if (value == null) {
         this.removeMeta(key);
      } else {
         String prev = (String)this.meta.put(key, value);
         if (!value.equals(prev)) {
            this.plugin.core.sendPacket(new Packet6PlayerMetaChange(this.id, key, value));
         }
      }

   }

   public boolean hasMeta(String key) {
      return this.meta.containsKey(key);
   }

   public String removeMeta(String key) {
      String prev = (String)this.meta.remove(key);
      if (prev != null) {
         this.plugin.core.sendPacket(new Packet6PlayerMetaChange(this.id, key, (String)null));
      }

      return prev;
   }

   public Map getMetaMap() {
      return this.meta;
   }

   public void dispose() {
      this.meta.clear();
      super.dispose();
   }
}
