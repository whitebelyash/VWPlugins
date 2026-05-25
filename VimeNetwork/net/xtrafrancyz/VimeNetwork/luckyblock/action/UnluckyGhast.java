package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyGhast extends LBAction {
   public void onBreak(Block block, Player player) {
      Location loc = block.getLocation();
      loc.getWorld().spawnEntity(loc.add((double)0.5F, (double)1.0F, (double)0.5F), EntityType.GHAST);
   }
}
