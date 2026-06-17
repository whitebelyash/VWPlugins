package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class UnluckyGaldenAppel extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(new ItemStack(Material.GOLDEN_APPLE, 1, (short)1), "&eЗалатое яблако", "&7Какое-то подозрительное название...");
      is = this.lb.controller.setConsumeCallback(this, is);
      drop.add(is);
   }

   public void onItemConsume(PlayerItemConsumeEvent event) {
      event.setCancelled(true);
      double health = event.getPlayer().getHealth();
      if (health - (double)5.0F <= (double)1.0F) {
         event.getPlayer().setHealth((double)1.0F);
      } else {
         event.getPlayer().setHealth(health - (double)2.0F);
      }

      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
         event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
         event.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
         event.getPlayer().removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
         event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
      });
   }
}
