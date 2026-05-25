package net.xtrafrancyz.BedWars.util;

import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

public class CommonUtils {
   public static void resetPlayer(Player player) {
      player.setGameMode(GameMode.SURVIVAL);
      player.setFlying(false);
      player.setAllowFlight(false);
      player.setWalkSpeed(0.2F);
      player.setExp(0.0F);
      player.setLevel(0);
      player.setFoodLevel(20);
      player.setSaturation(20.0F);
      player.setFireTicks(0);
      player.setNoDamageTicks(0);
      if (!player.isDead()) {
         player.setMaxHealth((double)20.0F);
         player.setHealth((double)20.0F);
      }

      for(PotionEffect pe : player.getActivePotionEffects()) {
         if (pe.getType() != null) {
            player.removePotionEffect(pe.getType());
         }
      }

      U.removeArrows(player);
   }

   public static ItemStack paint(ItemStack is, Color color) {
      LeatherArmorMeta meta = (LeatherArmorMeta)is.getItemMeta();
      meta.setColor(color);
      is.setItemMeta(meta);
      return is;
   }

   public static ItemStack startLore(ItemStack is) {
      return Items.appendLore(is, new String[]{"Начальный предмет"});
   }

   public static boolean isSameBlock(Location loc1, Location loc2) {
      return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
   }
}
