package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UnluckyAnvil extends LBAction {
   public void onBreak(Block block, Player p) {
      Location loc = block.getLocation();
      loc.add((double)0.5F, (double)0.5F, (double)0.5F);
      loc.getWorld().spawnFallingBlock(loc.clone().add((double)0.0F, (double)4.5F, (double)0.0F), Material.ANVIL, (byte)0);
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.lb.getPlugin(), () -> {
         for(Player player : Bukkit.getOnlinePlayers()) {
            Location loc2 = player.getLocation();
            double distanceSquared = loc2.distanceSquared(loc);
            if (distanceSquared <= (double)25.0F) {
               Vector velocity = new Vector();
               velocity.setX(loc2.getX() - loc.getX());
               velocity.setZ(loc2.getZ() - loc.getZ());
               velocity.normalize();
               velocity.setY((double)0.5F);
               velocity.multiply(0.6 + ((double)25.0F - distanceSquared) / (double)25.0F);
               player.setVelocity(velocity);
               U.msg(player, (String[])(T.system("LuckyBlock", "&cБАБАХ!")));
            }
         }

      }, 18L);
   }
}
