package net.xtrafrancyz.VimeNetwork.impl.player;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.Treasures;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;

public class VTreasures implements Treasures {
   public static final String META_KEY = "trsr.count";
   private final VPlayer player;
   private TreasuresEntry entry;

   public VTreasures(VPlayer player) {
      this.player = player;
      this.entry = new TreasuresEntry();
   }

   public int get(TreasureType type) {
      switch (type) {
         case BASIC:
            return this.entry.basic;
         case ANCIENT:
            return this.entry.ancient;
         case MYTHICAL:
            return this.entry.mythical;
         default:
            return 0;
      }
   }

   public void add(TreasureType type, int amount) {
      switch (type) {
         case BASIC:
            TreasuresEntry var4 = this.entry;
            var4.basic += amount;
            if (this.entry.basic < 0) {
               this.entry.basic = 0;
            }
            break;
         case ANCIENT:
            TreasuresEntry var3 = this.entry;
            var3.ancient += amount;
            if (this.entry.ancient < 0) {
               this.entry.ancient = 0;
            }
            break;
         case MYTHICAL:
            TreasuresEntry var10000 = this.entry;
            var10000.mythical += amount;
            if (this.entry.mythical < 0) {
               this.entry.mythical = 0;
            }
      }

      this.save();
   }

   public void take(TreasureType type, int amount) {
      this.add(type, -amount);
   }

   public boolean hasAny() {
      return this.entry.basic + this.entry.ancient + this.entry.mythical > 0;
   }

   public void giveWithMessage(TreasureType type, float chance) {
      Rank rank = this.player.getRank();
      if (rank.has(Rank.IMMORTAL)) {
         chance *= 3.0F;
      } else if (rank.has(Rank.HOLY)) {
         chance *= 2.3F;
      } else if (rank.has(Rank.PREMIUM)) {
         chance *= 1.8F;
      } else if (rank.has(Rank.VIP)) {
         chance *= 1.35F;
      }

      if (Rand.nextFloat() <= chance) {
         this.giveWithMessage(type);
      }

   }

   public void giveWithMessage(TreasureType type) {
      this.add(type, 1);
      U.msg(this.player.player, (String[])("&aВы получили " + type.name));
      U.bcast(T.system("&rСокровищница", "Игроку &e" + this.player.player.getDisplayName() + " &fвыпал: " + type.name));
      if (VimeNetwork.lobby().getServerType() != ServerType.LOBBY) {
         this.player.getAchievements().complete(Achievement.GLOBAL_LOOT_CHEST);
      }

   }

   public void load() {
      String meta = this.player.getMeta("trsr.count");
      if (meta == null) {
         this.entry = new TreasuresEntry();
      } else {
         this.entry = (TreasuresEntry)VimeNetwork.gson.fromJson(meta, TreasuresEntry.class);
      }

   }

   private void save() {
      if (this.hasAny()) {
         this.player.setMeta("trsr.count", VimeNetwork.gson.toJson(this.entry));
      } else {
         this.player.removeMeta("trsr.count");
      }

   }

   public static class TreasuresEntry {
      public int basic = 0;
      public int ancient = 0;
      public int mythical = 0;
   }
}
