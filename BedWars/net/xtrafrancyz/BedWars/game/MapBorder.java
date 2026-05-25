package net.xtrafrancyz.BedWars.game;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class MapBorder implements Runnable {
   private final BedWars plugin = BedWars.instance();
   private final Map players = new HashMap();
   private final double maxDistance;

   public MapBorder() {
      this.maxDistance = Math.pow(Config.teamDistance + (double)50.0F, (double)2.0F);
   }

   public void run() {
      if (this.plugin.game.getState() == GameState.GAME) {
         for(PlayerInfo player : PlayerInfo.PLAYERS.values()) {
            if (player.team != null) {
               double distance = this.xzDistanceToMiddle(player.player.getLocation());
               Integer task = (Integer)this.players.get(player);
               if (distance > this.maxDistance) {
                  if (task == null) {
                     int task0 = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                        if (player.team != null) {
                           player.player.setVelocity(new Vector(0, 0, 0));
                           player.player.teleport(player.team.getSpawnLocation());
                           this.players.remove(player);
                        }
                     }, 200L);
                     this.players.put(player, task0);
                     BTexteria.onMapLeave(player.player, 9999L);
                  }
               } else if (task != null) {
                  Bukkit.getScheduler().cancelTask(task);
                  this.players.remove(player);
                  BTexteria.removeMapLeave(player.player);
               }
            }
         }

      }
   }

   private double xzDistanceToMiddle(Location loc) {
      return NumberConversions.square(loc.getX() - Config.middle.getX()) + NumberConversions.square(loc.getZ() - Config.middle.getZ());
   }
}
