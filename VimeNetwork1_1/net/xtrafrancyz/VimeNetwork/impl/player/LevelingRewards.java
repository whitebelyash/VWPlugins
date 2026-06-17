package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;

public class LevelingRewards {
   public static List REWARDS = new ArrayList();

   static {
      REWARDS.add((Object)null);
      REWARDS.add(new CoinsReward(4000));
      REWARDS.add(new TreasureReward(TreasureType.BASIC, 3));
      REWARDS.add(new CoinsReward(5000));
      REWARDS.add(new MultiplierReward(2, 360, "6 часов"));
      REWARDS.add(new CoinsReward(6000));
      REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 1));
      REWARDS.add(new MultiplierReward(2, 360, "6 часов"));
      REWARDS.add(new CoinsReward(8000));
      REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 2));
      REWARDS.add(new CoinsReward(10000));
      REWARDS.add(new MultiplierReward(2, 720, "12 часов"));
      REWARDS.add(new CoinsReward(12000));
      REWARDS.add(new TreasureReward(TreasureType.ANCIENT, 3));
      REWARDS.add(new MultiplierReward(2, 1440, "1 день"));
      REWARDS.add(new CoinsReward(14000));
      REWARDS.add(new CoinsReward(16000));
      REWARDS.add(new CoinsReward(18000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 1));
      REWARDS.add(new CoinsReward(20000));
      REWARDS.add(new MultiplierReward(3, 360, "6 часов"));
      REWARDS.add(new CoinsReward(22000));
      REWARDS.add(new MultiplierReward(3, 360, "6 часов"));
      REWARDS.add(new CoinsReward(24000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов"));
      REWARDS.add(new CoinsReward(26000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
      REWARDS.add(new CoinsReward(28000));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов"));
      REWARDS.add(new CoinsReward(30000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов", 2));
      REWARDS.add(new CoinsReward(32000));
      REWARDS.add(new CoinsReward(34000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов", 2));
      REWARDS.add(new CoinsReward(36000));
      REWARDS.add(new CoinsReward(38000));
      REWARDS.add(new CoinsReward(40000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 2));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов", 2));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
      REWARDS.add(new CoinsReward(42000));
      REWARDS.add(new MultiplierReward(3, 720, "12 часов", 3));
      REWARDS.add(new MultiplierReward(3, 720, "1 день", 3));
      REWARDS.add(new CoinsReward(44000));
      REWARDS.add(new CoinsReward(46000));
      REWARDS.add(new CoinsReward(48000));
      REWARDS.add(new CoinsReward(50000));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 3));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
      REWARDS.add(new TreasureReward(TreasureType.MYTHICAL, 4));
      REWARDS.add(new CoinsReward(52000));
      REWARDS.add(new MultiplierReward(3, 720, "1 день", 4));
      REWARDS.add(new CoinsReward(54000));
      REWARDS.add(new CoinsReward(56000));
      REWARDS.add(new CoinsReward(58000));
   }

   public abstract static class LevelingReward {
      protected final List text;

      protected LevelingReward() {
         this.text = new ArrayList();
      }

      protected LevelingReward(String name) {
         this.text = Collections.singletonList(name);
      }

      public List getText() {
         return this.text;
      }

      public abstract void accept(VPlayer var1);
   }

   private static class CoinsReward extends LevelingReward {
      private final int coins;

      public CoinsReward(int coins) {
         super("&7+ &e" + U.pluralsCoins(coins));
         this.coins = coins;
      }

      public void accept(VPlayer player) {
         player.addCoinsExact(this.coins);
      }
   }

   private static class TreasureReward extends LevelingReward {
      private final TreasureType type;
      private final int amount;

      public TreasureReward(TreasureType type, int amount) {
         super("&7+ " + type.name + (amount > 1 ? " " + amount + " шт." : ""));
         this.type = type;
         this.amount = amount;
      }

      public void accept(VPlayer player) {
         player.getTreasures().add(this.type, this.amount);
         String message = "&aВы получили " + this.type.name;
         if (this.amount > 1) {
            message = message + " " + this.amount + " шт.";
         }

         U.msg(player.player, (String[])(message));
      }
   }

   private static class MultiplierReward extends LevelingReward {
      private Multiplier multiplier;
      private int amount;
      private String timeStr;

      public MultiplierReward(int multiplier, int minutes, String timeStr) {
         this(multiplier, minutes, timeStr, 1);
      }

      public MultiplierReward(int multiplier, int minutes, String timeStr, int amount) {
         if (amount > 1) {
            timeStr = timeStr + "&7 (x" + amount + ")";
         }

         this.timeStr = timeStr;
         this.text.add("&7+ Множитель коинов &fx" + multiplier + " &7на &f" + timeStr);
         this.text.add("");
         this.text.add("&8Для активации множителя коинов");
         this.text.add("&8зайдите в меню &f/me&8 и нажмите");
         this.text.add("&8на золотой слиток.");
         this.multiplier = new Multiplier(multiplier, minutes);
         this.amount = amount;
      }

      public void accept(VPlayer player) {
         player.getMultipliers().add(this.multiplier, this.amount);
         U.msg(player.player, (String[])("&aВы получили множитель коинов &fx" + this.multiplier.getMultiplier() + "&a на &f" + this.timeStr));
      }
   }

   private static class MiniDotReward extends LevelingReward {
      private int itemId;

      public MiniDotReward(String name, int itemId) {
         super(name);
         this.itemId = itemId;
      }

      public void accept(VPlayer player) {
         Class MiniDot = Reflect.findClass("net.xtrafrancyz.bukkit.minidot.MiniDot");
         Object plugin = Reflect.invoke(MiniDot, "instance");
         Object database = Reflect.get(plugin, "database");
         Reflect.invoke(database, "unlockItem", player.player, this.itemId);
      }
   }
}
