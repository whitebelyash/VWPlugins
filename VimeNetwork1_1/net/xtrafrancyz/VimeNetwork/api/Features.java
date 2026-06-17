package net.xtrafrancyz.VimeNetwork.api;

import net.xtrafrancyz.VimeNetwork.VNPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Features {
   static final Features inst = new Features();
   public final Feature POTION_EFFECT_EVENTS = new Feature("Potion Effect Events", false);
   public final Feature CANCEL_DROP_ITEM_FIX = new Feature("Cancel Drop Item Fix", true);
   public final Feature TELEPORT_FIX = new Feature("Teleport Fix", false);
   public final AlwaysDayFeature ALWAYS_DAY = new AlwaysDayFeature();
   public final Feature ALWAYS_SUN = new AlwaysSunFeature();
   public final Feature DISABLE_FOOD = new Feature("Disable Food", false);
   public final Feature CHANGE_TAGS = new Feature("Change Visible Names", false);
   public final Feature CHANGE_PLAYER_LIST_NAMES = new Feature("Change Player List Names", false);
   public final Feature CHANGE_CHAT = new Feature("Change Chat", false);
   public final Feature JOIN_LEAVE_MESSAGES = new Feature("Login & Leave Messages", false);
   public final AntiLeaveFeature ANTI_LEAVE = new AntiLeaveFeature();
   public final AutoWindowTitleFeature AUTO_WINDOW_TITLE = new AutoWindowTitleFeature();

   private Features() {
   }

   public static class Feature {
      private final String name;
      private boolean enabled;

      Feature(String name, boolean enabled) {
         this.name = name;
         this.enabled = enabled;
      }

      public void setEnabled(boolean enabled) {
         if (this.enabled != enabled) {
            this.enabled = enabled;
            VNPlugin.instance().getLogger().info("Feature [" + this.name + "] is " + (enabled ? "enabled" : "disabled"));
         }

      }

      public boolean isEnabled() {
         return this.enabled;
      }

      public String getName() {
         return this.name;
      }
   }

   public static class AlwaysDayFeature extends Feature {
      private long time = 8000L;

      AlwaysDayFeature() {
         super("Always Day", false);
      }

      public void setTime(long time) {
         long old = this.time;
         this.time = time;
         if (time != old) {
            this.update();
         }

      }

      public long getTime() {
         return this.time;
      }

      public void setEnabled(boolean enabled) {
         super.setEnabled(enabled);
         this.update();
      }

      private void update() {
         for(World world : Bukkit.getWorlds()) {
            if (this.isEnabled()) {
               world.setGameRuleValue("doDaylightCycle", "false");
               world.setFullTime(this.time);
            } else {
               world.setGameRuleValue("doDaylightCycle", "true");
            }
         }

      }
   }

   static class AlwaysSunFeature extends Feature {
      AlwaysSunFeature() {
         super("Always Sun", false);
      }

      public void setEnabled(boolean enabled) {
         super.setEnabled(enabled);
         if (enabled) {
            for(World world : Bukkit.getWorlds()) {
               world.setStorm(false);
            }
         }

      }
   }

   public static class AutoWindowTitleFeature extends Feature {
      private String title = "VimeWorld.ru";

      AutoWindowTitleFeature() {
         super("Auto Window Title", true);
         this.title = ChatColor.stripColor(Bukkit.getMotd());
      }

      public void setEnabled(boolean enabled) {
         super.setEnabled(enabled);
         if (enabled) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               VimeNetwork.setWindowTitle(player, this.title);
            }
         }

      }

      public void setTitle(String title) {
         if (!title.equals(this.title)) {
            for(Player player : Bukkit.getOnlinePlayers()) {
               VimeNetwork.setWindowTitle(player, title);
            }
         }

         this.title = title;
      }

      public String getTitle() {
         return this.title;
      }
   }

   public static class AntiLeaveFeature extends Feature {
      private int damageDelayMillis = 10000;

      AntiLeaveFeature() {
         super("AntiLeave", false);
      }

      public void setDamageDelay(int delay) {
         this.damageDelayMillis = delay;
      }

      public int getDamageDelay() {
         return this.damageDelayMillis;
      }
   }
}
