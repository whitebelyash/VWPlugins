package net.xtrafrancyz.VimeNetwork.impl.player;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.logging.Level;
import net.xtrafrancyz.Core.network.packet.Packet5PlayerCoinsChange;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class VCoins {
   private final VNPlugin plugin;
   private volatile boolean waiting = false;

   public VCoins(VNPlugin plugin) {
      this.plugin = plugin;
      this.waiting = true;
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::flush, 200L, 200L);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
         long time = System.currentTimeMillis();

         for(VPlayer pi : VPlayer.PLAYERS.values()) {
            if (pi.multipliers.isActivated() && pi.multipliers.getExtraEndTime() < time) {
               pi.multipliers.deactivate();
               VTexteria.showCoins(pi);
            }
         }

      }, 100L, 100L);
   }

   private void flush() {
      this.waiting = false;
      int total = 0;
      TIntObjectMap<TIntLinkedList> map = new TIntObjectHashMap();

      for(VPlayer player : VPlayer.PLAYERS.values()) {
         if (player.coinsAddBuffer != 0) {
            total += player.coinsAddBuffer;
            TIntLinkedList list = (TIntLinkedList)map.get(player.coinsAddBuffer);
            if (list == null) {
               map.put(player.coinsAddBuffer, list = new TIntLinkedList());
            }

            list.add(player.id);
            player.coinsAddBuffer = 0;
         }
      }

      VimeNetwork.metrics().add("coins.added", total);
      this.waiting = true;
      TIntObjectIterator<TIntLinkedList> it = map.iterator();

      while(it.hasNext()) {
         it.advance();
         if (this.plugin.core.isEnabled()) {
            this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(((TIntLinkedList)it.value()).toArray(), it.key()));
         } else {
            String ids = ((TIntLinkedList)it.value()).toString();
            this.plugin.mysql.query("UPDATE users SET coins=coins+" + it.key() + " WHERE id IN (" + ids.substring(1, ids.length() - 1) + ")");
         }
      }

      map.clear();
   }

   public void saveNow(VPlayer player) {
      if (this.waiting) {
         if (player.coinsAddBuffer > 0) {
            int amount = player.coinsAddBuffer;
            player.coinsAddBuffer = 0;
            if (this.plugin.core.isEnabled()) {
               this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, amount));
            } else {
               this.plugin.mysql.query("UPDATE users SET coins=coins+" + amount + " WHERE id = " + player.id);
            }
         }

      }
   }

   public int addCoins(VPlayer player, int amount) {
      if (player != null && amount >= 1) {
         try {
            if (this.waiting) {
               player.coinsAddBuffer += amount;
            } else if (this.plugin.core.isEnabled()) {
               this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, amount));
            } else {
               this.plugin.mysql.query("UPDATE users SET coins=coins+" + amount + " WHERE id = " + player.id);
            }

            player.coins += amount;
            player.player.sendMessage(ChatColor.YELLOW + "Добавлено коинов: " + amount);
            if (player.coins >= 20000) {
               player.getAchievements().complete(Achievement.GLOBAL_COINS_20000);
            }

            if (player.coins >= 100000) {
               player.getAchievements().complete(Achievement.GLOBAL_COINS_100000);
            }

            VTexteria.showCoins(player);
            VTexteria.showCoinsChange(player, amount);
            return amount;
         } catch (Exception e) {
            this.plugin.getLogger().log(Level.WARNING, (String)null, e);
            return -1;
         }
      } else {
         return -1;
      }
   }

   public void takeCoins(VPlayer player, int amount) {
      if (amount >= 1) {
         player.coins -= amount;
         VTexteria.showCoins(player);
         VTexteria.showCoinsChange(player, -amount);
         if (this.plugin.core.isEnabled()) {
            this.plugin.core.sendPacket(new Packet5PlayerCoinsChange(player.id, -amount));
         } else {
            this.plugin.mysql.query("UPDATE users SET coins=coins-" + amount + " WHERE id = " + player.id);
         }

      }
   }

   public void finish() {
      this.flush();
      this.waiting = false;
   }
}
