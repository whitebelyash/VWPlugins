package net.xtrafrancyz.VimeNetwork.tasks;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SpaceAchievementActivator implements Runnable {
   public void run() {
      for(Player player : Bukkit.getOnlinePlayers()) {
         if (((CraftPlayer)player).getHandle().locY > (double)2000.0F) {
            VimeNetwork.getPlayer(player).getAchievements().complete(Achievement.SECRET_SPACE);
         }
      }

   }
}
