package net.xtrafrancyz.VimeNetwork.commands;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VanishCommand implements CommandExecutor {
   public Map data = new HashMap();

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasPermission(sender, Permission.VANISH, true)) {
         return true;
      } else {
         Player player = (Player)sender;
         if (Spectators.instance().contains(player)) {
            this.disableVanish(player);
         } else {
            this.enableVanish(player);
         }

         return true;
      }
   }

   public void purge(Player player) {
      this.data.remove(player.getName());
   }

   public void disableVanish(Player player) {
      if (Spectators.instance().contains(player)) {
         VanishData vanishData = (VanishData)this.data.remove(player.getName());
         if (vanishData == null) {
            U.msg(player, (String[])(T.error("Режим наблюдателя", "Какая-то хуйня, извините, я не понимаю куда делись все ваши вещи")));
            return;
         }

         Spectators.instance().remove(player);
         player.getInventory().setContents(vanishData.inventory);
         player.getInventory().setArmorContents(vanishData.armor);
         player.teleport(vanishData.lastLoc);
         player.setAllowFlight(vanishData.allowFlight);
         player.setFlying(vanishData.flying);
         player.setWalkSpeed(vanishData.walkspeed);
         player.setFlySpeed(vanishData.flyspeed);
         player.setMaxHealth(vanishData.maxHealth);
         player.setHealth(vanishData.health);
         U.msg(player, (String[])(T.warning("Режим наблюдателя", "Деактивирован")));
      }

   }

   public void enableVanish(Player player) {
      if (!Spectators.instance().contains(player)) {
         this.data.put(player.getName(), new VanishData(player));
         Spectators.instance().add(player);
         Invs.clear(player);
         player.setAllowFlight(true);
         player.setFlying(true);
         U.msg(player, (String[])(T.success("Режим наблюдателя", "Активирован")));
      }

   }

   public static class VanishData {
      public ItemStack[] inventory;
      public ItemStack[] armor;
      public Location lastLoc;
      public boolean allowFlight;
      public boolean flying;
      public float flyspeed;
      public float walkspeed;
      public double health;
      public double maxHealth;

      public VanishData(Player player) {
         this.inventory = player.getInventory().getContents();
         this.armor = player.getInventory().getArmorContents();
         this.lastLoc = player.getLocation();
         this.allowFlight = player.getAllowFlight();
         this.flying = player.isFlying();
         this.flyspeed = player.getFlySpeed();
         this.walkspeed = player.getWalkSpeed();
         this.health = player.getHealth();
         this.maxHealth = player.getMaxHealth();
      }
   }
}
