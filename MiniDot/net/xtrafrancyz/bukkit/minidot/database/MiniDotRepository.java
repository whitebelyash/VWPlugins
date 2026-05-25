package net.xtrafrancyz.bukkit.minidot.database;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet7MiniDot;
import net.xtrafrancyz.Core.network.packet.Packet7MiniDot.Action;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.bukkit.minidot.Messenger;
import net.xtrafrancyz.bukkit.minidot.MiniDot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MiniDotRepository {
   public Map players = new ConcurrentHashMap();
   private TIntObjectHashMap items = new TIntObjectHashMap();
   private TIntList free = new TIntArrayList();
   private TIntIntMap discounts = new TIntIntHashMap();

   public MiniDotRepository() {
      Bukkit.getScheduler().scheduleSyncRepeatingTask(MiniDot.instance(), this::loadItems, 0L, 6000L);
      VimeNetwork.core().addHandler(Packet1PlayerInfo.class, (packet) -> {
         if ((packet.queryFlags & 2) == 2) {
            Player player = Bukkit.getPlayerExact(packet.username);
            if (player == null) {
               return;
            }

            MiniDotPlayer info = new MiniDotPlayer(player);

            for(int id : packet.minidotItems) {
               info.available.add(id);
            }

            info.dressed = packet.minidotDressed;
            TIntHashSet var10001 = info.available;
            this.free.forEach(var10001::add);
            this.players.put(player.getName(), info);
            Messenger.sendPlayerInfoToSelf(info);
            if (!info.dressed.isEmpty()) {
               Messenger.sendPlayerInfoToAll(info);
            }
         }

      });
   }

   public void loadPlayer(Player player) {
      if (!VimeNetwork.core().isEnabled()) {
         int id = VimeNetwork.getPlayer(player).getId();
         VimeNetwork.mysql().select("SELECT item FROM minidot_buys WHERE userid = " + id, (rs) -> {
            MiniDotPlayer pi = new MiniDotPlayer(player);

            while(rs.next()) {
               pi.available.add(rs.getInt("item"));
            }

            TIntHashSet var10001 = pi.available;
            this.free.forEach(var10001::add);
            this.players.put(player.getName(), pi);
            VimeNetwork.mysql().select("SELECT * FROM minidot_dressed WHERE userid = " + id, (rs1) -> {
               if (rs1.next()) {
                  for(MiniDotItem.Slot slot : MiniDotItem.Slot.values()) {
                     int item = rs1.getInt(slot.getId());
                     if (item != -1) {
                        pi.dressed.put(slot.getId(), item);
                     }
                  }
               } else {
                  VimeNetwork.mysql().query("INSERT INTO minidot_dressed (userid) VALUES (" + id + ")");
               }

               MiniDot.debug("Player " + player.getName() + " loaded from database");
               Messenger.sendPlayerInfoToSelf(pi);
               if (!pi.dressed.isEmpty()) {
                  Messenger.sendPlayerInfoToAll(pi);
               }

            });
         });
      }

   }

   public void unloadPlayer(String player) {
      MiniDotPlayer pi = (MiniDotPlayer)this.players.remove(player);
      if (pi != null) {
         if (!VimeNetwork.core().isEnabled()) {
            MiniDot.instance().playerSaver.saveNow(pi);
         }

         Messenger.removePlayer(pi);
      }

   }

   public MiniDotPlayer getPlayer(String player) {
      return (MiniDotPlayer)this.players.get(player);
   }

   public MiniDotItem getItem(int id) {
      return (MiniDotItem)this.items.get(id);
   }

   public Collection getItems() {
      return this.items.valueCollection();
   }

   public TIntIntMap getDiscountedItems() {
      return this.discounts;
   }

   public TIntList getFreeItems() {
      return this.free;
   }

   public void unlockItem(Player player, int id) {
      MiniDotPlayer pi = this.getPlayer(player.getName());
      if (!pi.available.contains(id)) {
         pi.available.add(id);
         Messenger.sendPlayerInfoToSelf(pi);
         Achievements achievements = VimeNetwork.getPlayer(player).getAchievements();
         if (!achievements.isCompleted(Achievement.GLOBAL_FACELESS)) {
            int masks = 0;

            for(int i : pi.available.toArray()) {
               if (this.getItem(i).isMask()) {
                  ++masks;
               }
            }

            if (masks >= 5) {
               achievements.complete(Achievement.GLOBAL_FACELESS);
            }
         }

         if (VimeNetwork.core().isEnabled()) {
            VimeNetwork.core().sendPacket(new Packet7MiniDot(pi.getId(), id, Action.UNLOCK));
         } else {
            VimeNetwork.mysql().query("INSERT INTO minidot_buys (userid, item) VALUES (" + pi.getId() + ", " + id + ")");
         }

      }
   }

   private void loadItems() {
      VimeNetwork.mysql().select("SELECT * FROM minidot_config", (rs) -> {
         TIntObjectHashMap<MiniDotItem> items0 = new TIntObjectHashMap();
         TIntList free0 = new TIntArrayList();
         TIntIntMap discounts0 = new TIntIntHashMap();

         while(rs.next()) {
            MiniDotItem item = new MiniDotItem(rs.getInt("id"), rs.getInt("price"), MiniDotItem.Slot.valueOf(rs.getString("slot").toUpperCase()), rs.getString("name"), rs.getInt("discount"));
            items0.put(item.id, item);
            if (item.isFree()) {
               free0.add(item.id);
            }

            if (item.hasDiscount()) {
               discounts0.put(item.id, item.discount);
            }
         }

         if (items0.size() != this.items.size()) {
            MiniDot.instance().getLogger().info("Loaded " + items0.size() + " items");
         }

         this.items = items0;
         this.free = free0;
         if (!this.discounts.equals(discounts0)) {
            this.discounts = discounts0;
            Messenger.sendDiscountsToAll();
         }

      });
   }
}
