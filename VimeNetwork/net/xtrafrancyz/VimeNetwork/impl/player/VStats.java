package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.EnumMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet9PlayerStatChange;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.Stats;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;

public class VStats implements Stats {
   private final EnumMap stats = new EnumMap(Stat.class);
   private final VPlayer player;

   public VStats(VPlayer player) {
      this.player = player;
   }

   public void load(Packet1PlayerInfo packet) {
      for(int[] stat : packet.stats) {
         this.stats.put(Stat.byId(stat[0]), stat[1]);
      }

   }

   public int get(Stat stat) {
      return (Integer)this.stats.getOrDefault(stat, 0);
   }

   public int increment(Stat stat) {
      return this.increment(stat, 1);
   }

   public int increment(Stat stat, int amount) {
      if (amount < 0) {
         throw new IllegalArgumentException("amount < 0");
      } else {
         int val = this.get(stat);
         val += amount;
         this.stats.put(stat, val);

         for(StatAchievement achievement : stat.getAchievements()) {
            if (achievement.getNeeded() <= val) {
               this.player.getAchievements().complete(achievement);
            }
         }

         VimeNetwork.core().sendPacket(new Packet9PlayerStatChange(this.player.id, stat.getId(), amount));
         return val;
      }
   }
}
