package net.xtrafrancyz.VimeNetwork.listeners;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

public class TeleportFix implements Listener {
   private static final int TELEPORT_FIX_DELAY = 15;
   private Server server;
   private Plugin plugin;

   public TeleportFix(Plugin plugin) {
      this.plugin = plugin;
      this.server = plugin.getServer();
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlayerTeleport(PlayerTeleportEvent event) {
      if (VimeNetwork.features().TELEPORT_FIX.isEnabled()) {
         Player player = event.getPlayer();
         if (!Spectators.isEnabled() || !Spectators.instance().contains(player)) {
            int visibleDistance = this.server.getViewDistance() * 16;
            this.server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
               List<Player> nearby = this.getPlayersWithin(player, visibleDistance);
               this.updateEntities(player, nearby, false);
               this.server.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.updateEntities(player, nearby, true), 1L);
            }, 15L);
         }
      }
   }

   private void updateEntities(Player tpedPlayer, List players, boolean visible) {
      for(Player player : players) {
         if (visible) {
            tpedPlayer.showPlayer(player);
            player.showPlayer(tpedPlayer);
         } else {
            tpedPlayer.hidePlayer(player);
            player.hidePlayer(tpedPlayer);
         }
      }

   }

   private List getPlayersWithin(Player player, int distance) {
      List<Player> res = new ArrayList();
      int d2 = distance * distance;

      for(Player p : this.server.getOnlinePlayers()) {
         if (p != player && p.getWorld() == player.getWorld() && p.getLocation().distanceSquared(player.getLocation()) <= (double)d2 && player.canSee(p)) {
            res.add(p);
         }
      }

      return res;
   }
}
