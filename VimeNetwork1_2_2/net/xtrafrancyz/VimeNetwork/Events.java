/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  net.xtrafrancyz.bukkit.texteria.TexteriaCallbackEvent
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.SignChangeEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerKickEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerQuitEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.event.weather.WeatherChangeEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet4PlayerChangeServer;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.Features;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerUnloadEvent;
import net.xtrafrancyz.VimeNetwork.api.player.Guild;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
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
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.Plugin;

class Events
implements Listener {
    private static final Pattern URL_PATTERN = Pattern.compile("(?:(https?)://)?([-\\w_.]{2,}\\.[a-z]{2,4})(/\\S*)?");
    private VNPlugin plugin;
    private Features.AutoWindowTitleFeature autoWindowTitleFeature;
    private Features.Feature joinLeaveMessageFeature;
    private Set<String> kicks;

    public Events(VNPlugin plugin) {
        this.autoWindowTitleFeature = VimeNetwork.features().AUTO_WINDOW_TITLE;
        this.joinLeaveMessageFeature = VimeNetwork.features().JOIN_LEAVE_MESSAGES;
        this.kicks = new HashSet<String>();
        this.plugin = plugin;
    }

    @EventHandler(priority=EventPriority.LOW)
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
        String target;
        if (event.getNetworkPlayer().has(Rank.VIP)) {
            event.getNetworkPlayer().getAchievements().complete(Achievement.GLOBAL_VIP);
        }
        VTexteria.showUsername((VPlayer)event.getNetworkPlayer());
        CoreByteMap switchData = event.getSwitchData();
        if (this.joinLeaveMessageFeature.isEnabled() && (switchData == null || !switchData.containsKey("silentJoin"))) {
            U.bcast("&7\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u043b\u0441\u044f => " + event.getPlayer().getDisplayName());
        }
        if (switchData != null && event.getNetworkPlayer().has(Rank.MODER) && (target = switchData.getString("teleportToPlayer")) != null) {
            Player targetPlayer = Bukkit.getPlayerExact((String)target);
            if (targetPlayer == null) {
                U.msg((CommandSender)event.getPlayer(), "&c\u0418\u0433\u0440\u043e\u043a \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0442\u043e \u0431\u044b\u043b \u0442\u0443\u0442, \u043d\u043e \u043e\u043d \u043f\u0440\u043e\u043f\u0430\u043b, \u043f\u043e\u0438\u0449\u0438 \u0441\u043d\u043e\u0432\u0430...");
            } else {
                U.msg((CommandSender)event.getPlayer(), "&a\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0443 &f" + targetPlayer.getDisplayName());
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> this.plugin.vanishCommand.enableVanish(event.getPlayer()), 1L);
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> event.getPlayer().teleport((Entity)targetPlayer), 5L);
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
            player.setSaturation(20.0f);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
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
        BlockState state;
        if (event.hasBlock() && E.isRightClick(event) && (state = event.getClickedBlock().getState()) instanceof Sign && ((Sign)state).getLine(0).contains(ChatColor.GREEN + "Lobby")) {
            event.setCancelled(true);
            VimeNetwork.toLobby(event.getPlayer());
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onChatLowest(AsyncPlayerChatEvent event) {
        Matcher matcher;
        Rank rank = VimeNetwork.getPlayer(event.getPlayer()).getRank();
        if (rank == Rank.PLAYER && (matcher = URL_PATTERN.matcher(event.getMessage())).find()) {
            boolean replaced = false;
            StringBuffer sb = new StringBuffer();
            do {
                String host;
                if ((host = matcher.group(2)) != null && host.endsWith("vimeworld.ru")) continue;
                matcher.appendReplacement(sb, "<\u0441\u0441\u044b\u043b\u043a\u0430 \u0443\u0434\u0430\u043b\u0435\u043d\u0430>");
                replaced = true;
            } while (matcher.find());
            if (replaced) {
                matcher.appendTail(sb);
                event.setMessage(sb.toString());
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> U.msg((CommandSender)event.getPlayer(), T.error("VimeWorld", "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u044f\u0442\u044c \u0441\u0442\u043e\u0440\u043e\u043d\u043d\u0438\u0435 \u0441\u0441\u044b\u043b\u043a\u0438 \u0432 \u0447\u0430\u0442")));
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onChatHigh(AsyncPlayerChatEvent event) {
        NetworkPlayer player = VimeNetwork.getPlayer(event.getPlayer());
        if (VimeNetwork.features().CHANGE_CHAT.isEnabled()) {
            Guild guild;
            String msgColor = "&f";
            if (player.has(Rank.CHIEF)) {
                msgColor = "&a";
                event.setMessage(U.colored(event.getMessage()));
            } else if (player.getRank() == Rank.IMMORTAL) {
                event.setMessage(U.colored(event.getMessage()));
            }
            if (player.hasGuild() && (guild = player.getGuild()).getTag() != null) {
                event.setFormat(U.colored("&7<" + guild.getColor() + guild.getTag() + "&7> &7%1$s&r&7: " + msgColor) + "%2$s");
            } else {
                event.setFormat(U.colored("&7%1$s&r&7: " + msgColor) + "%2$s");
            }
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (VimeNetwork.getPlayer(event.getPlayer()).has(Rank.ADMIN)) {
            for (int i = 0; i < 4; ++i) {
                event.setLine(i, U.colored(event.getLine(i)));
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (this.plugin.config.bungeeEnable && !this.plugin.config.bungeeIps.contains(event.getAddress().getHostAddress())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "\u0417\u0430\u0439\u0442\u0438 \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u043e \u0442\u043e\u043b\u044c\u043a\u043e \u0447\u0435\u0440\u0435\u0437 IP: vimeworld.net");
        }
    }

    @EventHandler
    public void onTexteriaCallback(TexteriaCallbackEvent event) {
        String module = event.getData().getString("module");
        if (module != null && module.equals("VimeNetwork")) {
            switch (event.getData().getString("action")) {
                case "goals-inv": {
                    VimeNetwork.getPlayer(event.getPlayer()).getGoals().openInventory();
                    break;
                }
                case "streams-inv": {
                    this.plugin.streamMenu.show(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (this.kicks.remove(event.getPlayer().getName())) {
            return;
        }
        event.setQuitMessage(this.fireLeaveEvent(VPlayer.get(event.getPlayer()), event.getQuitMessage(), false));
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        this.kicks.add(event.getPlayer().getName());
        event.setLeaveMessage(this.fireLeaveEvent(VPlayer.get(event.getPlayer()), event.getLeaveMessage(), true));
    }

    @EventHandler
    public void onLeave(PlayerLeaveEvent event) {
        event.setLeaveMessage(null);
        if (this.joinLeaveMessageFeature.isEnabled()) {
            U.bcast("&7\u0412\u044b\u0448\u0435\u043b => " + event.getPlayer().getDisplayName());
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
        if (player.has(Rank.WARDEN)) {
            this.plugin.vanishCommand.purge(player.getBukkitPlayer());
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().getName().equals(this.plugin.getName())) {
            for (VPlayer player : VPlayer.PLAYERS.values()) {
                Bukkit.getPluginManager().callEvent((Event)new PlayerUnloadEvent(player));
            }
        }
    }

    private String fireLeaveEvent(VPlayer player, String message, boolean isKick) {
        PlayerLeaveEvent event = new PlayerLeaveEvent(player, message, isKick);
        Bukkit.getPluginManager().callEvent((Event)event);
        Bukkit.getPluginManager().callEvent((Event)new PlayerUnloadEvent(player));
        VPlayer.PLAYERS.remove(player.getName());
        VPlayer.IDS.remove(player.getId());
        player.dispose();
        return event.getLeaveMessage();
    }
}

