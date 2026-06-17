package net.xtrafrancyz.VimeNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet4PlayerChangeServer;
import net.xtrafrancyz.VimeNetwork.api.Features;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerUnloadEvent;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.TexteriaCallbackEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

class Events implements Listener {
   private static final Pattern URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?");
   private VNPlugin plugin;
   private Features.AutoWindowTitleFeature autoWindowTitleFeature;
   private Features.Feature joinLeaveMessageFeature;
   private Set kicks;

   public Events(VNPlugin plugin) {
      this.autoWindowTitleFeature = VimeNetwork.features().AUTO_WINDOW_TITLE;
      this.joinLeaveMessageFeature = VimeNetwork.features().JOIN_LEAVE_MESSAGES;
      this.kicks = new HashSet();
      this.plugin = plugin;
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      event.setJoinMessage("");
      player.setGameMode(GameMode.ADVENTURE);
      VPlayer networkPlayer = VPlayer.get(player);
      if (this.plugin.core.isEnabled()) {
         if (this.plugin.core.isConnected()) {
            this.plugin.core.sendPacket(new Packet4PlayerChangeServer(event.getPlayer().getName()));
         }
      } else {
         this.plugin.mysql.addLoadPlayer(networkPlayer);
      }

      ((CraftPlayer)player).addChannel("Vime");
      ((CraftPlayer)player).addChannel("VimeBungee");
      ((CraftPlayer)player).addChannel("BungeeCord");
      if (this.autoWindowTitleFeature.isEnabled()) {
         VimeNetwork.setWindowTitle(player, this.autoWindowTitleFeature.getTitle());
      }

   }

   @EventHandler
   public void onPlayerLoaded(PlayerLoadedEvent event) {
      if (event.getNetworkPlayer().getRank().has(Rank.VIP)) {
         event.getNetworkPlayer().getAchievements().complete(Achievement.GLOBAL_VIP);
      }

      VTexteria.showUsername((VPlayer)event.getNetworkPlayer());
      CoreByteMap switchData = event.getSwitchData();
      if (this.joinLeaveMessageFeature.isEnabled() && (switchData == null || !switchData.containsKey("silentJoin"))) {
         U.bcast("&7Подключился => " + event.getPlayer().getDisplayName());
      }

      if (switchData != null && event.getNetworkPlayer().getRank().has(Rank.MODER)) {
         String target = switchData.getString("teleportToPlayer");
         if (target != null) {
            Player targetPlayer = Bukkit.getPlayerExact(target);
            if (targetPlayer == null) {
               U.msg(event.getPlayer(), (String[])("&cИгрок только что был тут, но он пропал, поищи снова..."));
            } else {
               Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                  this.plugin.vanishCommand.enableVanish(event.getPlayer());
                  event.getPlayer().teleport(targetPlayer);
               }, 5L);
            }
         }
      }

   }

   @EventHandler
   public void onJoinNormal(PlayerJoinEvent event) {
      VTexteria.showServerId(event.getPlayer());
   }

   @EventHandler
   public void onFoodChange(FoodLevelChangeEvent event) {
      if (VimeNetwork.features().DISABLE_FOOD.isEnabled() && event.getEntityType() == EntityType.PLAYER) {
         Player player = (Player)event.getEntity();
         player.setFoodLevel(20);
         player.setSaturation(20.0F);
         event.setCancelled(true);
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onWeatherChange(WeatherChangeEvent event) {
      if (VimeNetwork.features().ALWAYS_SUN.isEnabled()) {
         event.setCancelled(true);
         WorldServer world = ((CraftWorld)event.getWorld()).getHandle();
         world.worldData.setStorm(false);
         world.worldData.setThundering(false);
         world.worldData.setThunderDuration(0);
      }

   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      U.removeArrows(event.getPlayer());
   }

   @EventHandler
   public void onInteract(PlayerInteractEvent event) {
      if (event.hasBlock() && E.isRightClick(event)) {
         BlockState state = event.getClickedBlock().getState();
         if (state instanceof Sign && ((Sign)state).getLine(0).contains(ChatColor.GREEN + "Lobby")) {
            event.setCancelled(true);
            VimeNetwork.toLobby(event.getPlayer());
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onChatLowest(AsyncPlayerChatEvent event) {
      Rank rank = VimeNetwork.getPlayer(event.getPlayer()).getRank();
      if (rank == Rank.PLAYER) {
         Matcher matcher = URL_PATTERN.matcher(event.getMessage());
         if (matcher.find()) {
            boolean replaced = false;
            StringBuffer sb = new StringBuffer();

            do {
               String host = matcher.group(2);
               if (host == null || !host.endsWith("vimeworld.ru")) {
                  matcher.appendReplacement(sb, "<ссылка удалена>");
                  replaced = true;
               }
            } while(matcher.find());

            if (replaced) {
               matcher.appendTail(sb);
               event.setMessage(sb.toString());
               Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> U.msg(event.getPlayer(), (String[])(T.error("VimeWorld", "Вы не можете отправлять сторонние ссылки в чат"))));
            }
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onChatHigh(AsyncPlayerChatEvent event) {
      Rank rank = VimeNetwork.getPlayer(event.getPlayer()).getRank();
      if (VimeNetwork.features().CHANGE_CHAT.isEnabled()) {
         String msgColor = "&f";
         if (rank.has(Rank.CHIEF)) {
            msgColor = "&a";
            event.setMessage(U.colored(event.getMessage()));
         } else if (rank == Rank.IMMORTAL) {
            event.setMessage(U.colored(event.getMessage()));
         }

         event.setFormat(U.colored("&7%1$s&r&7: " + msgColor) + "%2$s");
      }

   }

   @EventHandler
   public void onSignChange(SignChangeEvent event) {
      if (VimeNetwork.getPlayer(event.getPlayer()).getRank().has(Rank.ADMIN)) {
         for(int i = 0; i < 4; ++i) {
            event.setLine(i, U.colored(event.getLine(i)));
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerLogin(PlayerLoginEvent event) {
      if (this.plugin.config.bungeeEnable && !this.plugin.config.bungeeIps.contains(event.getAddress().getHostAddress())) {
         event.disallow(Result.KICK_WHITELIST, "Зайти на сервер возможно только через IP: vimeworld.net");
      }

   }

   @EventHandler
   public void onTexteriaCallback(TexteriaCallbackEvent event) {
      String module = event.getData().getString("module");
      if (module != null && module.equals("VimeNetwork")) {
         switch (event.getData().getString("action")) {
            case "goals-inv":
               VimeNetwork.getPlayer(event.getPlayer()).getGoals().openInventory();
               break;
            case "streams-inv":
               this.plugin.streamMenu.show(event.getPlayer());
         }
      }

   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      if (!this.kicks.remove(event.getPlayer().getName())) {
         event.setQuitMessage(this.fireLeaveEvent(VPlayer.get(event.getPlayer()), event.getQuitMessage(), false));
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onKick(PlayerKickEvent event) {
      this.kicks.add(event.getPlayer().getName());
      event.setLeaveMessage(this.fireLeaveEvent(VPlayer.get(event.getPlayer()), event.getLeaveMessage(), true));
   }

   @EventHandler
   public void onLeave(PlayerLeaveEvent event) {
      event.setLeaveMessage((String)null);
      if (this.joinLeaveMessageFeature.isEnabled()) {
         U.bcast("&7Вышел => " + event.getPlayer().getDisplayName());
      }

   }

   @EventHandler
   public void onUnload(PlayerUnloadEvent event) {
      VPlayer player = (VPlayer)event.getNetworkPlayer();
      player.goals.save();
      this.plugin.coins.saveNow(player);
      this.plugin.expBuffer.saveNow(player);
      if (this.plugin.metaSaver != null) {
         this.plugin.metaSaver.saveNow((MysqlPlayer)player);
      }

      if (player.getRank().has(Rank.WARDEN)) {
         this.plugin.vanishCommand.purge(player.getBukkitPlayer());
      }

   }

   @EventHandler
   public void onPluginDisable(PluginDisableEvent event) {
      if (event.getPlugin().getName().equals(this.plugin.getName())) {
         for(VPlayer player : VPlayer.PLAYERS.values()) {
            Bukkit.getPluginManager().callEvent(new PlayerUnloadEvent(player));
         }
      }

   }

   private String fireLeaveEvent(VPlayer player, String message, boolean isKick) {
      PlayerLeaveEvent event = new PlayerLeaveEvent(player, message, isKick);
      Bukkit.getPluginManager().callEvent(event);
      Bukkit.getPluginManager().callEvent(new PlayerUnloadEvent(player));
      VPlayer.PLAYERS.remove(player.getName());
      VPlayer.IDS.remove(player.getId());
      player.dispose();
      return event.getLeaveMessage();
   }
}
