package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyHorse extends LBAction {
   public void onBreak(Block block, Player player) {
      Horse horse = (Horse)block.getWorld().spawnEntity(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), EntityType.HORSE);
      horse.setOwner(player);
      horse.setStyle((Horse.Style)Rand.of(Horse.Style.class));
      horse.setVariant((Horse.Variant)Rand.of(Horse.Variant.class));
      horse.setColor((Horse.Color)Rand.of(Horse.Color.class));
      horse.setTamed(true);
      horse.setOwner(player);
      horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
      horse.getInventory().setArmor(new ItemStack((Material)Rand.of((Object[])(Material.DIAMOND_BARDING, Material.GOLD_BARDING, Material.IRON_BARDING))));
   }
}
