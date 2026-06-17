/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Commons.T
 *  net.xtrafrancyz.VimeNetwork.VNPlugin
 *  net.xtrafrancyz.VimeNetwork.api.Def
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerKillEvent
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.Rank
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.VimeNetwork.api.util.Particles
 *  net.xtrafrancyz.VimeNetwork.api.util.Reflect
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  net.xtrafrancyz.VimeNetwork.commands.VanishCommand$VanishData
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Table
 *  net.xtrafrancyz.bukkit.texteria.elements.Table$Column
 *  net.xtrafrancyz.bukkit.texteria.utils.Animation3D$Params
 *  net.xtrafrancyz.bukkit.texteria.world.Beam
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_6_R3.block.CraftChest
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.ItemDespawnEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.player.AsyncPlayerChatEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.server.ServerListPingEvent
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.ClashPoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.TournamentUI;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.menu.SpectatorMenu;
import net.xtrafrancyz.ClashPoint.menu.SpectatorSettingsMenu;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.ClashPoint.util.CommonUtils;
import net.xtrafrancyz.ClashPoint.util.PrivateChests;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Def;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerKillEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.commands.VanishCommand;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Table;
import net.xtrafrancyz.bukkit.texteria.utils.Animation3D;
import net.xtrafrancyz.bukkit.texteria.world.Beam;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.block.CraftChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

public class Events
implements Listener {
    private final ClashPoint plugin;
    private final Map<String, Leaver> leavers;
    public final PrivateChests privateChests;
    public final Set<Vec3i> userBlocks = new HashSet<Vec3i>(256);
    private WorldGroup leaderboard = null;

    public Events(ClashPoint plugin) {
        this.plugin = plugin;
        this.privateChests = new PrivateChests();
        this.leavers = new HashMap<String, Leaver>();
        if (Config.leaderboardEnabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, () -> plugin.repository.getLeaderboard(list -> {
                WorldGroup group = new WorldGroup("leaders");
                group.setLocation(Config.leaderboardLocation.x, Config.leaderboardLocation.y, Config.leaderboardLocation.z);
                group.setRotation(Config.leaderboardRotation.x, Config.leaderboardRotation.y, Config.leaderboardRotation.z);
                group.setScale(Config.leaderboardScale);
                group.setCulling(true);
                group.setHoverable(true);
                group.setHoverRange(12);
                Table table = new Table("0").setTitle("&l\u0422\u0430\u0431\u043b\u0438\u0446\u0430 \u043b\u0438\u0434\u0435\u0440\u043e\u0432 ClashPoint").setDrawBack(true).setMaxRows(10).addColumn(new Table.Column("#", 15).setCenter(true).setColor(-5317)).addColumn(new Table.Column("\u041d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430", 70)).addColumn(new Table.Column("\u041f\u043e\u0431\u0435\u0434", 30).setCenter(true).setColor(-16121)).addColumn(new Table.Column("\u0418\u0433\u0440", 30).setCenter(true)).addColumn(new Table.Column("\u0423\u0431\u0438\u0442\u043e", 40).setCenter(true)).addColumn(new Table.Column("\u0422\u043e\u0447\u0435\u043a \u0441\u043b\u043e\u043c\u0430\u043d\u043e", 80).setCenter(true));
                table.setHoverable(true);
                list.forEach(arg_0 -> ((Table)table).addRow(arg_0));
                group.add((Element)table);
                this.leaderboard = group;
                Texteria3D.addGroup((WorldGroup)this.leaderboard, (Player[])Bukkit.getOnlinePlayers());
            }), 0L, 72000L);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.onPlayerJoin(new PlayerJoinEvent(player, null));
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onServerListPing(ServerListPingEvent event) {
        event.setMaxPlayers(Config.getMaxPlayers());
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (this.plugin.game.getState() == GameState.STARTING) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\u0418\u0434\u0435\u0442 \u043e\u0442\u0441\u0447\u0435\u0442 \u0434\u043e \u043d\u0430\u0447\u0430\u043b\u0430 \u0438\u0433\u0440\u044b");
            return;
        }
        if (this.plugin.game.getState() == GameState.ENDING) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "\u0418\u0433\u0440\u0430 \u0443\u0436\u0435 \u0437\u0430\u043a\u043e\u043d\u0447\u0438\u043b\u0430\u0441\u044c");
            return;
        }
        if (this.plugin.game.getState() == GameState.WAITING) {
            if (!VimeNetwork.isTournament() && Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, "\u0421\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d");
            }
            return;
        }
        if (this.plugin.game.getState() == GameState.GAME) {
            event.allow();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SURVIVAL);
        player.teleport(Config.lobby);
        this.plugin.game.scoreboard.bind(player);
        if (!VimeNetwork.isTournament() && Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
            this.plugin.game.start();
        } else if (this.plugin.game.getState() == GameState.WAITING) {
            CPTexteria.showPlayersToStart();
        }
        if (this.leaderboard != null) {
            Texteria3D.addGroup((WorldGroup)this.leaderboard, (Player[])new Player[]{event.getPlayer()});
        }
    }

    @EventHandler
    public void onPlayerLoaded(PlayerLoadedEvent event) {
        PlayerInfo player = PlayerInfo.get(event.getPlayer());
        this.equip(player);
        if (this.plugin.game.getState() != GameState.GAME) {
            U.bcast((String)("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e => &f\u0418\u0433\u0440\u043e\u043a " + event.getPlayer().getDisplayName() + " \u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0438\u043b\u0441\u044f"));
        } else {
            Leaver leaver = this.leavers.remove(player.username);
            if (leaver != null && !leaver.team.getResourcePoints().isEmpty()) {
                this.plugin.game.join(player, leaver.team, true);
                --leaver.team.leavers;
                PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player.player, Config.lobby, false);
                this.onPlayerRespawn(respawnEvent);
                player.player.teleport(respawnEvent.getRespawnLocation());
                player.player.getInventory().setArmorContents(leaver.armor);
                player.player.getInventory().setContents(leaver.inventory);
                U.bcast((String)("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e => &f\u0418\u0433\u0440\u043e\u043a " + leaver.team.chatColor + event.getPlayer().getName() + "&r \u0432\u0435\u0440\u043d\u0443\u043b\u0441\u044f \u0432 \u0438\u0433\u0440\u0443"));
                if (TournamentUI.instance != null) {
                    TournamentUI.instance.showUI(TournamentUI.instance.getWatchers());
                }
            } else {
                player.hyperSpectator = true;
                if (!VimeNetwork.isTournament() && !event.getNetworkPlayer().getRank().has((CommandSender)event.getPlayer(), Rank.PREMIUM)) {
                    event.getPlayer().kickPlayer("\u0414\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 \u0438\u0433\u0440 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u0441\u0442\u0430\u0442\u0443\u0441 " + Rank.PREMIUM.getDisplayName());
                    return;
                }
                this.plugin.spectators.add(event.getPlayer());
                TrailMenu.getPlayer((String)event.getPlayer().getName()).visible = false;
                CPTexteria.showPrimaryTopTimer(event.getPlayer());
            }
        }
        this.plugin.repository.loadPlayer(player);
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerLoadedLow(PlayerLoadedEvent event) {
        if (event.getSwitchData() != null && (this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING)) {
            event.getSwitchData().remove((Object)"teleportToPlayer");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        event.setLeaveMessage(null);
        PlayerInfo leaver = PlayerInfo.PLAYERS.remove(event.getPlayer().getName());
        if (!leaver.hyperSpectator) {
            String name = leaver.team != null ? leaver.team.chatColor + leaver.username + "&r" : leaver.player.getDisplayName();
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> U.bcast((String)("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e <= &f\u0418\u0433\u0440\u043e\u043a " + name + " \u0432\u044b\u0448\u0435\u043b")));
        }
        if (leaver.team != null) {
            ++leaver.team.leavers;
            if (this.plugin.game.getState() == GameState.GAME) {
                this.leavers.put(leaver.username, new Leaver(leaver));
            }
            this.plugin.game.leave(leaver);
        }
        this.plugin.repository.savePlayer(leaver);
        switch (this.plugin.game.getState()) {
            case STARTING: {
                this.plugin.game.cancelStartTask();
            }
            case WAITING: {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, CPTexteria::showPlayersToStart);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Config.lobby);
        PlayerInfo player = PlayerInfo.get(event.getPlayer());
        CommonUtils.resetPlayer(player.player);
        player.player.setNoDamageTicks(60);
        Invs.clear((HumanEntity)player.player);
        this.equip(player);
        if (this.plugin.game.getState() == GameState.GAME) {
            if (player.team == null) {
                if (player.deathLocation != null) {
                    event.setRespawnLocation(player.deathLocation);
                }
            } else {
                event.setRespawnLocation(this.plugin.game.spawnPlayer(player));
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem() && E.isRightClick((PlayerInteractEvent)event)) {
            int typeid = event.getItem().getTypeId();
            if ((this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING) && typeid == Def.ITEM_TEAM_SELECT.getTypeId()) {
                event.getPlayer().openInventory(this.plugin.teamSelectMenu.getInventory());
                event.setCancelled(true);
            } else if (typeid == SpectatorMenu.MENU_ITEM.getTypeId()) {
                this.plugin.spectatorMenu.show(event.getPlayer());
                event.setCancelled(true);
            } else if (typeid == SpectatorSettingsMenu.MENU_ITEM.getTypeId()) {
                new SpectatorSettingsMenu(event.getPlayer()).show(event.getPlayer());
                event.setCancelled(true);
            }
        }
        if (Config.parkourEnabled && event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
            if (this.plugin.spectators.contains(event.getPlayer())) {
                return;
            }
            Location loc = event.getClickedBlock().getLocation();
            if (loc.getBlockX() == Config.parkourSign.x && loc.getBlockY() == Config.parkourSign.y && loc.getBlockZ() == Config.parkourSign.z) {
                VimeNetwork.getPlayer((Player)event.getPlayer()).getAchievements().complete(Achievement.CP_LOBBY_PARKOUR);
            }
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        Location loc = event.getBlock().getLocation();
        block0: for (CPTeam team : Config.teams) {
            for (ResourcePoint rp : team.getResourcePoints()) {
                if (rp.destroyed || !(loc.distanceSquared(rp.getLocation()) < 9.0)) continue;
                event.setCancelled(true);
                U.msg((CommandSender)event.getPlayer(), (String[])new String[]{"&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0431\u043b\u043e\u043a\u0438 \u0442\u0430\u043a \u0431\u043b\u0438\u0437\u043a\u043e \u043a \u0442\u043e\u0447\u043a\u0435 \u0441\u043f\u0430\u0432\u043d\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432"});
                continue block0;
            }
        }
        this.userBlocks.add(new Vec3i(event.getBlock()));
        if (event.getBlock().getType() == Material.CHEST) {
            this.privateChests.addProtection(loc, PlayerInfo.get((Player)event.getPlayer()).team);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                CraftChest chest = (CraftChest)event.getBlock().getState();
                Reflect.set((Object)Reflect.get((Object)chest, (String)"chest"), (String)"s", null);
            });
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onChestBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if (!this.userBlocks.remove(new Vec3i(event.getBlock()))) {
            event.setCancelled(true);
            return;
        }
        switch (event.getBlock().getType()) {
            case CHEST: {
                this.privateChests.removeProtection(event.getBlock().getLocation());
                break;
            }
            case WEB: {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            switch (event.getClickedBlock().getType()) {
                case CHEST: {
                    if (this.privateChests.canOpen(PlayerInfo.get(event.getPlayer()), event.getClickedBlock().getLocation())) break;
                    event.getPlayer().sendMessage(ChatColor.RED + "\u0421\u0443\u043d\u0434\u0443\u043a \u043f\u0440\u0438\u043d\u0430\u0434\u043b\u0435\u0436\u0438\u0442 \u0434\u0440\u0443\u0433\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u0435");
                    event.setCancelled(true);
                    break;
                }
                case ENDER_CHEST: {
                    event.setCancelled(true);
                    Location loc = event.getClickedBlock().getLocation();
                    for (CPTeam team : Config.teams) {
                        if (!team.personalChests.contains(loc)) continue;
                        Invs.forceOpen((HumanEntity)event.getPlayer(), (Inventory)PlayerInfo.get((Player)event.getPlayer()).personalInventory);
                        return;
                    }
                    this.privateChests.openTeamEnderChest(PlayerInfo.get(event.getPlayer()));
                    break;
                }
                case BREWING_STAND: 
                case ENCHANTMENT_TABLE: 
                case ANVIL: 
                case BEACON: {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        LinkedList<Block> toExplode = new LinkedList<Block>();
        for (Block block : event.blockList()) {
            Vec3i loc = new Vec3i(block);
            if (!this.userBlocks.remove(loc)) continue;
            if (this.plugin.trapUsable.traps.remove(loc) != null) {
                block.setType(Material.AIR);
                continue;
            }
            toExplode.add(block);
        }
        event.blockList().clear();
        event.blockList().addAll(toExplode);
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        PlayerInfo target;
        PlayerInfo player = PlayerInfo.get(event.getPlayer());
        if (player.equals(target = PlayerInfo.get(event.getTarget()))) {
            U.bcast((String)("\u0418\u0433\u0440\u043e\u043a &e" + target.player.getDisplayName() + "&f \u0441\u0430\u043c\u043e\u0443\u0431\u0438\u043b\u0441\u044f"));
            return;
        }
        CPTexteria.onPlayerKill(player, target);
        U.bcast((String)("\u0418\u0433\u0440\u043e\u043a " + target.team.chatColor + target.username + "&f \u0443\u0431\u0438\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u043c " + (player.team == null ? "" : player.team.chatColor) + player.username));
        if (target.lastDeath > System.currentTimeMillis() - 15000L) {
            player.player.sendMessage(ChatColor.RED + "\u042d\u0442\u043e\u0442 \u0438\u0433\u0440\u043e\u043a \u0431\u044b\u043b \u0443\u0431\u0438\u0442 \u043c\u0435\u043d\u0435\u0435 15 \u0441\u0435\u043a\u0443\u043d\u0434 \u043d\u0430\u0437\u0430\u0434. \u0423\u0431\u0438\u0439\u0441\u0442\u0432\u043e \u043d\u0435 \u0437\u0430\u0441\u0447\u0438\u0442\u0430\u043d\u043e.");
        } else {
            ++player.stats.kills;
            if (player.team != null) {
                player.team.gamePoints = (float)((double)player.team.gamePoints + 0.1);
            }
            NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)player.username);
            networkPlayer.addCoins(4);
            networkPlayer.giveExp(4);
            networkPlayer.getGoals().trigger("cp", GoalQuery.of((String)"kill").put("weapon", (Object)event.getPlayer().getItemInHand()).put("target", (Object)target.player));
            if (event.getDamageCause() == EntityDamageEvent.DamageCause.VOID && player.thrownPlayers++ == 19) {
                networkPlayer.getAchievements().complete(Achievement.CP_KING_OF_THE_HILL);
            }
        }
        player.player.setLevel(player.player.getLevel() + 1);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PlayerInfo player = PlayerInfo.get(event.getEntity());
        ++player.stats.deaths;
        player.lastDeath = System.currentTimeMillis();
        Location loc = player.player.getLocation();
        double y = loc.getY();
        player.deathLocation = loc;
        if (y < 5.0) {
            player.deathLocation.setY(Config.respawnY);
        }
        if (player.team != null && this.plugin.game.getState() == GameState.GAME) {
            Beam beam = new Beam(null, -16777216 + player.team.color.asRGB());
            beam.setLocation((float)loc.getX(), (float)y, (float)loc.getZ());
            beam.setDuration(4000L);
            beam.setRenderDistance(128);
            beam.animation.setBoth(new Animation3D.Params().setScale(-1.3f));
            Texteria3D.addGroup((WorldGroup)beam, (Player[])Bukkit.getOnlinePlayers());
        }
        if (player.team != null && player.team.getResourcePoints().isEmpty()) {
            this.plugin.game.leave(player);
            this.plugin.spectators.add(player.player);
            ++player.stats.games;
            NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player.player);
            networkPlayer.addCoins(20);
            networkPlayer.giveExp(20);
            networkPlayer.getGoals().trigger("cp", GoalQuery.of((String)"played"));
            TrailMenu.getPlayer((String)player.username).visible = false;
            CPTexteria.onLoose(player);
        }
        U.respawnPlayer((Player)event.getEntity());
    }

    @EventHandler(ignoreCancelled=true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.setDamage(300.0);
                return;
            }
            if (this.plugin.game.getState() != GameState.GAME) {
                event.setCancelled(true);
            }
        } else if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        LivingEntity shooter;
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (this.plugin.game.getState() != GameState.GAME) {
                event.setCancelled(true);
                return;
            }
            if (event.getEntity().getType() == EntityType.PLAYER) {
                PlayerInfo damager = PlayerInfo.get((Player)event.getDamager());
                PlayerInfo target = PlayerInfo.get((Player)event.getEntity());
                if (damager.team != null && damager.team.equals(target.team)) {
                    event.setCancelled(true);
                    return;
                }
            }
            Location loc = event.getEntity().getLocation();
            float x = (float)loc.getX();
            float y = (float)loc.getY() + 0.6f;
            float z = (float)loc.getZ();
            event.getEntity().getNearbyEntities(20.0, 20.0, 20.0).stream().filter(e -> e instanceof Player).forEach(e -> Particles.playTileCrack((int)152, (int)0, (float)x, (float)y, (float)z, (float)0.3f, (float)0.5f, (float)0.3f, (float)0.076f, (int)35, (Player[])new Player[]{(Player)e}));
        }
        if (event.getDamager() instanceof Projectile && (shooter = ((Projectile)event.getDamager()).getShooter()).getType() == EntityType.PLAYER) {
            PlayerInfo damager = PlayerInfo.get((Player)shooter);
            PlayerInfo target = PlayerInfo.get((Player)event.getEntity());
            if (damager.team != null && damager.team.equals(target.team)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (this.plugin.game.getState() != GameState.GAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && this.plugin.spectators.contains((Player)event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        PlayerInfo player = PlayerInfo.get(event.getPlayer());
        String name = player.username;
        String message = event.getMessage();
        if (player.team != null) {
            name = player.team.chatColor + name;
        }
        boolean broadcast = false;
        if (message.startsWith("!")) {
            broadcast = true;
            message = message.substring(1).trim();
        }
        if (player.team == null) {
            if (VimeNetwork.isTournament()) {
                boolean isAdmin = VimeNetwork.hasRank((CommandSender)player.player, (Rank)Rank.CHIEF, (boolean)false);
                if (this.plugin.game.getState() == GameState.GAME && !isAdmin) {
                    broadcast = false;
                }
                if (broadcast) {
                    if (isAdmin) {
                        U.bcast((String)T.system((String)player.player.getDisplayName(), (String)message));
                    } else {
                        Bukkit.broadcastMessage((String)(U.colored((String)("&7(\u0412\u0441\u0435\u043c) " + name + "&r&7:&f ")) + message));
                    }
                } else {
                    message = U.colored((String)("&7(\u0417\u0440\u0438\u0442\u0435\u043b\u0438) " + player.player.getDisplayName() + "&r&7:&f ")) + message;
                    for (PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
                        if (pi.team != null) continue;
                        pi.player.sendMessage(message);
                    }
                    Bukkit.getLogger().info(message);
                }
                return;
            }
            broadcast = true;
        }
        if (broadcast) {
            Bukkit.broadcastMessage((String)(U.colored((String)("&7(\u0412\u0441\u0435\u043c) " + name + "&r&7:&f ")) + message));
        } else {
            message = U.colored((String)(player.team.chatColor + "(\u041a\u043e\u043c\u0430\u043d\u0434\u0430) " + name + "&r&7:&f ")) + message;
            for (PlayerInfo pl : player.team.players) {
                pl.player.sendMessage(message);
            }
            Bukkit.getLogger().info(message);
        }
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        if (event.getEntity().getPickupDelay() > 0) {
            event.setCancelled(true);
        }
    }

    public void equip(PlayerInfo player) {
        PlayerInventory inv = player.player.getInventory();
        Invs.clear((HumanEntity)player.player);
        if (this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING) {
            inv.setItem(0, Def.ITEM_TEAM_SELECT.clone());
            inv.setItem(1, Def.ITEM_TRAILS.clone());
            inv.setItem(7, Def.getSettingsItem((Player)player.player));
            inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
        } else if (player.team == null) {
            inv.setItem(7, Def.getSettingsItem((Player)player.player));
            inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
        }
    }

    private static class Leaver {
        public CPTeam team;
        public ItemStack[] armor;
        public ItemStack[] inventory;

        public Leaver(PlayerInfo player) {
            VanishCommand.VanishData data;
            this.team = player.team;
            if (Spectators.instance().contains(player.player) && (data = (VanishCommand.VanishData)VNPlugin.instance().vanishCommand.data.get(player.username)) != null) {
                this.armor = data.armor;
                this.inventory = data.inventory;
                return;
            }
            this.armor = player.player.getInventory().getArmorContents();
            this.inventory = player.player.getInventory().getContents();
        }
    }
}

