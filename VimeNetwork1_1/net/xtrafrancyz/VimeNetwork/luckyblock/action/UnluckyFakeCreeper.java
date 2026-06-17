package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftCreeper;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyFakeCreeper extends LBAction {
   public void onBreak(Block block, Player player) {
      Location loc = block.getLocation();
      Creeper creeper = (Creeper)loc.getWorld().spawnEntity(loc.add((double)0.5F, (double)0.5F, (double)0.5F), EntityType.CREEPER);
      Reflect.set((Object)((CraftCreeper)creeper).getHandle(), "maxFuseTicks", 100);
   }
}
