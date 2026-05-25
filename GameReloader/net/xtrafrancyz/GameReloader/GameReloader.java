package net.xtrafrancyz.GameReloader;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.Lobby.State;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameReloader extends JavaPlugin implements Listener {
   static final String DUMMY_WORLD = "____dummy";
   private static GameReloader instance;
   static boolean reloadInProgress = false;
   static int reloadsPerformed = 1;
   static int maxReloads = 40;

   public void onLoad() {
      instance = this;
   }

   public void onEnable() {
      Bukkit.getPluginManager().registerEvents(this, this);
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.ADMIN, true)) {
         return true;
      } else {
         U.msg(sender, new String[]{"Reloads: &a" + reloadsPerformed});
         return true;
      }
   }

   public static void setMaxReloads(int maxReloads) {
      GameReloader.maxReloads = maxReloads;
   }

   public static void reload(JavaPlugin plugin) {
      if (plugin.isEnabled()) {
         instance.getLogger().info("Initiated GameReloader by " + plugin.getName());
         instance.getLogger().info("Reload # " + reloadsPerformed);
         reloadInProgress = true;
         VimeNetwork.lobby().setConnectableState(State.DENY_ALL);
         VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
         ++reloadsPerformed;
         if (reloadsPerformed <= maxReloads) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new ReloadTask(plugin, instance.getLogger()), 60L);
         } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, Bukkit::shutdown, 60L);
         }

      }
   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent event) {
      if (reloadInProgress) {
         event.disallow(Result.KICK_OTHER, "Сервер перезагружается");
      }

   }

   @EventHandler
   public void onPlayerLogin(PlayerJoinEvent event) {
      if (reloadInProgress) {
         event.getPlayer().kickPlayer("Сервер перезагружается");
      }

   }

   @EventHandler
   public void onWorldInit(WorldInitEvent event) {
      if (event.getWorld().getName().equals("____dummy")) {
         event.getWorld().setKeepSpawnInMemory(false);
      }

   }
}
