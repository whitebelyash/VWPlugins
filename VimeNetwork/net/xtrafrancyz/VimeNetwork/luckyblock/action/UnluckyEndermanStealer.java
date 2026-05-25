package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyEndermanStealer extends LBAction {
   public void onBreak(Block block, Player player) {
      Enderman enderman = (Enderman)block.getWorld().spawnEntity(block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F), EntityType.ENDERMAN);
      enderman.setCarriedMaterial(Material.SPONGE.getNewData((byte)1));
      enderman.setCustomName(ChatColor.BOLD + "Воришка");
      enderman.setCustomNameVisible(true);
   }
}
