package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LuckyGhastAxe extends LBActionItem {
   public void onBreak(Block block, Player player) {
      player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
      player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0F, 1.0F);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0F, 1.0F), 10L);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1.0F, 1.0F), 20L);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_DEATH, 1.0F, 1.0F), 30L);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0F, 1.0F), 40L);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         player.removePotionEffect(PotionEffectType.BLINDNESS);
         if (!Spectators.isEnabled() || !Spectators.instance().contains(player)) {
            ItemStack is = Items.name(Material.DIAMOND_AXE, "&eИспугался?");
            is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            this.giveItem(player, block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), is);
         }
      }, 60L);
   }

   protected void populateDrop(List drop, Block block, Player player) {
   }
}
