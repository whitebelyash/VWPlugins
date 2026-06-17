/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Core.network.packet.Packet59Party
 *  net.xtrafrancyz.VimeNetwork.api.Material2
 *  net.xtrafrancyz.VimeNetwork.api.PlayerTargetCompass
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.Rank
 *  net.xtrafrancyz.VimeNetwork.api.util.LobbyProtector
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  net.xtrafrancyz.bukkit.texteria.utils.ParsedTime
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package net.xtrafrancyz.ClashPoint;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.DataRepository;
import net.xtrafrancyz.ClashPoint.Events;
import net.xtrafrancyz.ClashPoint.TotalDisabler;
import net.xtrafrancyz.ClashPoint.TournamentUI;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.Game;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.game.usables.RescuePlatform;
import net.xtrafrancyz.ClashPoint.game.usables.Thor;
import net.xtrafrancyz.ClashPoint.game.usables.Trap;
import net.xtrafrancyz.ClashPoint.game.usables.WarpPowder;
import net.xtrafrancyz.ClashPoint.menu.SpectatorMenu;
import net.xtrafrancyz.ClashPoint.menu.SpectatorSettingsMenu;
import net.xtrafrancyz.ClashPoint.menu.TeamSelectMenu;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.ClashPoint.util.ForcedChunks;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.VimeNetwork.api.Material2;
import net.xtrafrancyz.VimeNetwork.api.PlayerTargetCompass;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.LobbyProtector;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ClashPoint
extends JavaPlugin {
    private static ClashPoint instance;
    public Spectators spectators;
    public DataRepository repository;
    public Game game;
    public Events events;
    public TeamSelectMenu teamSelectMenu;
    public SpectatorMenu spectatorMenu;
    public PlayerTargetCompass targetCompass;
    public Trap trapUsable;
    public ForcedChunks forcedChunks;

    public void onEnable() {
        instance = this;
        Config.load();
        this.forcedChunks = new ForcedChunks();
        this.game = new Game(this);
        this.repository = new DataRepository();
        this.teamSelectMenu = new TeamSelectMenu(this);
        this.spectatorMenu = new SpectatorMenu();
        this.spectators = Spectators.instance();
        this.targetCompass = new PlayerTargetCompass((Plugin)this, player -> {
            PlayerInfo pi = PlayerInfo.get(player);
            LinkedList<Player> players = new LinkedList<Player>();
            if (pi.team == null) {
                return players;
            }
            for (CPTeam team : Config.teams) {
                if (pi.team.equals(team)) continue;
                for (PlayerInfo pl : team.players) {
                    players.add(pl.player);
                }
            }
            return players;
        });
        this.targetCompass.setPlayerNameProvider(player -> PlayerInfo.get((Player)player).team.chatColor + player.getName());
        TrailMenu.init((JavaPlugin)this, (String)"cp");
        LobbyProtector.init((Plugin)this, (Location)Config.lobby, (int)100);
        this.events = new Events(this);
        Bukkit.getPluginManager().registerEvents((Listener)this.events, (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new TotalDisabler(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new WarpPowder(this), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new RescuePlatform(this), (Plugin)this);
        this.trapUsable = new Trap(this);
        Bukkit.getPluginManager().registerEvents((Listener)this.trapUsable, (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)new Thor(), (Plugin)this);
        Bukkit.getPluginManager().registerEvents((Listener)this.forcedChunks, (Plugin)this);
        VimeNetwork.addCommandHelp((String)"     ", (String)"\u041e\u0431\u0449\u0438\u0439 \u0447\u0430\u0442: \"&a!&f\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435\"");
        VimeNetwork.addCommandHelp((String)"stats", (String)"\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430");
        VimeNetwork.addCommandHelp((String)"clashpoint", (String)"\u0410\u0434\u043c\u0438\u043d\u0441\u043a\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", (Rank)Rank.ADMIN);
        VimeNetwork.features().ALWAYS_SUN.setEnabled(true);
        VimeNetwork.features().ALWAYS_DAY.setEnabled(true);
        VimeNetwork.features().CANCEL_DROP_ITEM_FIX.setEnabled(true);
        VimeNetwork.features().ANTI_LEAVE.setEnabled(true);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, this::savePlayers, 12000L, 12000L);
        VimeNetwork.lobby().setMaxPlayers(Config.getMaxPlayers());
        this.spectators.addListener((Plugin)this, (player, spectator) -> {
            if (spectator) {
                player.getInventory().setItem(0, SpectatorMenu.MENU_ITEM.clone());
                player.getInventory().setItem(1, SpectatorSettingsMenu.MENU_ITEM.clone());
            }
        });
        VimeNetwork.core().addHandler(Packet59Party.class, this::onPartyPacket);
        if (VimeNetwork.isTournament()) {
            Bukkit.getPluginManager().registerEvents((Listener)new TournamentUI(this), (Plugin)this);
        }
        for (CPTeam team : Config.teams) {
            for (ResourcePoint rp : team.originalResourcePoints) {
                this.forcedChunks.addAndLoadChunk(rp.getLocation().getChunk());
            }
            for (Location loc : team.traders) {
                this.forcedChunks.addAndLoadChunk(loc.getChunk());
            }
            for (Location loc : team.upgraders) {
                this.forcedChunks.addAndLoadChunk(loc.getChunk());
            }
        }
    }

    public void onDisable() {
        this.savePlayers();
        this.forcedChunks.clear();
        VimeNetwork.holograms().reset();
        PlayerInfo.PLAYERS.clear();
        VimeNetwork.core().removeHandler(Packet59Party.class, this::onPartyPacket);
    }

    public void savePlayers() {
        PlayerInfo.PLAYERS.values().forEach(this.repository::savePlayer);
    }

    private void onPartyPacket(Packet59Party packet) {
        if (this.game.getState() != GameState.WAITING) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
            if (packet.leader == null) {
                return;
            }
            PlayerInfo player = PlayerInfo.PLAYERS.get(packet.username);
            if (player != null) {
                PlayerInfo leader;
                NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)player.username);
                if (System.currentTimeMillis() - networkPlayer.getLoginTime() < 1000L && (leader = PlayerInfo.PLAYERS.get(packet.leader.username)) != null && leader != player && leader.team != null && leader.team.players.size() < Config.teamPlayers) {
                    this.game.join(player, leader.team, false);
                }
            }
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        block36: {
            block35: {
                if (!command.getName().equals("clashpoint")) break block35;
                if (!VimeNetwork.hasRank((CommandSender)sender, (Rank)Rank.CHIEF, (boolean)true)) {
                    return true;
                }
                if (args.length == 0) {
                    args = new String[]{"help"};
                }
                switch (args[0]) {
                    case "player": {
                        if (args.length != 2) {
                            sender.sendMessage(ChatColor.RED + "/" + label + " player <player>");
                            break;
                        }
                        PlayerInfo player = PlayerInfo.PLAYERS.get(args[1]);
                        if (player == null) {
                            sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found");
                            break;
                        }
                        U.msg((CommandSender)sender, (String[])new String[]{"&e ---- &f" + player.player.getDisplayName() + " &e---"});
                        U.msg((CommandSender)sender, (String[])new String[]{"&eWins: &f" + player.stats.wins});
                        U.msg((CommandSender)sender, (String[])new String[]{"&eGames: &f" + player.stats.games});
                        U.msg((CommandSender)sender, (String[])new String[]{"&eKills: &f" + player.stats.kills});
                        U.msg((CommandSender)sender, (String[])new String[]{"&eDeaths: &f" + player.stats.deaths});
                        break;
                    }
                    case "stats": {
                        U.msg((CommandSender)sender, (String[])new String[]{"&e\u0421\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435: &f" + (Object)((Object)this.game.getState())});
                        for (CPTeam team : Config.teams) {
                            int alive = team.getResourcePoints().size();
                            String msg = team.chatColor + team.names[2] + "" + (alive == 0 ? " \u0442\u043e\u0447\u0435\u043a \u043d\u0435\u0442" : alive + " \u0442\u043e\u0447\u0435\u043a") + " (\u043e\u0447\u043a\u043e\u0432: " + team.gamePoints + ")&r:";
                            for (PlayerInfo pl1 : team.players) {
                                msg = msg + " " + pl1.username;
                            }
                            U.msg((CommandSender)sender, (String[])new String[]{msg});
                        }
                        if (this.game.getState() == GameState.GAME) {
                            int secs = (int)((System.currentTimeMillis() - this.game.startTime) / 1000L);
                            U.msg((CommandSender)sender, (String[])new String[]{"\u0412\u0440\u0435\u043c\u044f: " + secs / 60 + ":" + ParsedTime.numToString((int)(secs % 60), (int)2) + " \u043c\u0438\u043d."});
                        }
                        U.msg((CommandSender)sender, (String[])new String[]{"\u0417\u0440\u0438\u0442\u0435\u043b\u0438 (" + this.spectators.getSpectators().size() + "): " + this.spectators.getSpectators().stream().map(CommandSender::getName).collect(Collectors.joining(", "))});
                        break;
                    }
                    case "testconfig": {
                        AtomicLong errors = new AtomicLong(0L);
                        Config.teams.forEach(t -> {
                            errors.addAndGet(t.spawns.stream().filter(loc -> {
                                Location loc2 = loc.clone();
                                while (loc2.getY() > 0.0) {
                                    loc2.add(0.0, -1.0, 0.0);
                                    if (loc2.getBlock().isEmpty()) continue;
                                }
                                if (loc2.getY() <= 0.0) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &f\u0421\u043f\u0430\u0432\u043d \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435"});
                                    return true;
                                }
                                return false;
                            }).count());
                            errors.addAndGet(t.traders.stream().filter(loc -> {
                                Location loc2 = loc.clone();
                                while (loc2.getY() > 0.0) {
                                    loc2.add(0.0, -1.0, 0.0);
                                    if (loc2.getBlock().isEmpty()) continue;
                                }
                                if (loc2.getY() <= 0.0) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &f\u0421\u043f\u0430\u0432\u043d \u0442\u0440\u0435\u0439\u0434\u0435\u0440\u0430 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435"});
                                    return true;
                                }
                                return false;
                            }).count());
                            errors.addAndGet(t.upgraders.stream().filter(loc -> {
                                Location loc2 = loc.clone();
                                while (loc2.getY() > 0.0) {
                                    loc2.add(0.0, -1.0, 0.0);
                                    if (loc2.getBlock().isEmpty()) continue;
                                }
                                if (loc2.getY() <= 0.0) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &f\u0421\u043f\u0430\u0432\u043d \u0430\u043f\u0433\u0440\u0435\u0439\u0434\u0435\u0440\u0430 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435"});
                                    return true;
                                }
                                return false;
                            }).count());
                            errors.addAndGet(t.originalResourcePoints.stream().filter(rp -> {
                                if (rp.getLocation().getX() % 1.0 != 0.0 || rp.getLocation().getY() % 1.0 != 0.0 || rp.getLocation().getZ() % 1.0 != 0.0) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + new Vec3f(rp.getLocation()) + " &f\u0423 \u0442\u043e\u0447\u043a\u0438 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u043d\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c 0.5"});
                                    return true;
                                }
                                if (rp.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.BEDROCK) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + new Vec3f(rp.getLocation()) + " &f\u041f\u043e\u0434 \u0442\u043e\u0447\u043a\u043e\u0439 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u043d\u0435\u0442 \u0431\u0435\u0434\u0440\u043e\u043a\u0430"});
                                    return true;
                                }
                                if (rp.getLocation().getBlock().getType() != Material.AIR) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + new Vec3f(rp.getLocation()) + " &f\u041d\u0430 \u043c\u0435\u0441\u0442\u0435 \u0442\u043e\u0447\u043a\u0438 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c"});
                                    return true;
                                }
                                return false;
                            }).count());
                            errors.addAndGet(t.personalChests.stream().filter(loc -> {
                                if (loc.getX() % 1.0 != 0.0 || loc.getY() % 1.0 != 0.0 || loc.getZ() % 1.0 != 0.0) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + new Vec3f(loc) + " &f\u0423 \u0442\u043e\u0447\u043a\u0438 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u043d\u0435 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c 0.5"});
                                    return true;
                                }
                                if (loc.getBlock().getType() != Material.ENDER_CHEST) {
                                    U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + new Vec3f(loc) + " &f\u041d\u0430 \u043c\u0435\u0441\u0442\u0435 \u043f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430 " + loc.getBlock().getType()});
                                    return true;
                                }
                                return false;
                            }).count());
                        });
                        errors.addAndGet(this.checkResourceSpawn(sender, "", Config.goldSpawns, 3, "\u0437\u043e\u043b\u043e\u0442\u0430", Material.GOLD_BLOCK));
                        errors.addAndGet(this.checkResourceSpawn(sender, "", Config.diamondSpawns, 3, "\u0430\u043b\u043c\u0430\u0437\u0430", Material.DIAMOND_BLOCK));
                        if (errors.get() == 0L) {
                            U.msg((CommandSender)sender, (String[])new String[]{"&a\u0412\u0441\u0451 \u043e\u043a"});
                            break;
                        }
                        break block36;
                    }
                    case "setstate": {
                        this.game.setState(GameState.valueOf(args[1].toUpperCase()));
                        break;
                    }
                    case "start": {
                        this.game.start();
                        break;
                    }
                    case "stop": {
                        if (this.game.getState() == GameState.STARTING) {
                            this.game.cancelStartTask();
                            if (VimeNetwork.isTournament()) {
                                Texteria2D.removeGroup((String)"bw.lobby", (Player[])Bukkit.getOnlinePlayers());
                                break;
                            }
                            CPTexteria.showPlayersToStart();
                            break;
                        }
                        this.game.end();
                        break;
                    }
                    case "spectoteam": {
                        Player bukkitPlayer = Bukkit.getPlayerExact((String)args[1]);
                        if (bukkitPlayer == null) {
                            U.msg((CommandSender)sender, (String[])new String[]{"\u0418\u0433\u0440\u043e\u043a \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d"});
                            return false;
                        }
                        PlayerInfo player = PlayerInfo.get(bukkitPlayer);
                        String color = Character.toUpperCase(args[2].charAt(0)) + args[2].substring(1).toLowerCase();
                        CPTeam team = Config.teams.stream().filter(t -> Arrays.asList(t.names).contains(color)).findAny().orElse(null);
                        if (team == null) {
                            U.msg((CommandSender)sender, (String[])new String[]{"\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d\u0430"});
                            return false;
                        }
                        this.spectators.remove(player.player);
                        this.game.join(player, team, true);
                        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player.player, Config.lobby, false);
                        this.events.onPlayerRespawn(respawnEvent);
                        player.player.teleport(respawnEvent.getRespawnLocation());
                        if (TournamentUI.instance != null) {
                            TournamentUI.instance.showUI(TournamentUI.instance.getWatchers());
                            break;
                        }
                        break block36;
                    }
                    case "etui": {
                        if (TournamentUI.instance == null) {
                            Bukkit.getPluginManager().registerEvents((Listener)new TournamentUI(this), (Plugin)this);
                            Player[] watchers = TournamentUI.instance.getWatchers();
                            TournamentUI.instance.showUI(watchers);
                            Texteria2D.remove((String)"bw.pt.t", (Player[])watchers);
                            Texteria2D.remove((String)"bw.pt.t.t", (Player[])watchers);
                            break;
                        }
                        break block36;
                    }
                    default: {
                        U.msg((CommandSender)sender, (String[])new String[]{"&e=========== &fClashPoint &e===========", "&e/cp start&f \u0441\u0442\u0430\u0440\u0442 \u0438\u0433\u0440\u044b", "&e/cp stop&f \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u0438\u0433\u0440\u044b", "&e/cp testconfig&f \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u044f \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a", "&e/cp setstate <state>&f", "&e/cp stats&f \u0441\u043e\u0441\u0442\u043e\u044f\u043d\u0438\u0435 \u0438\u0433\u0440\u044b", "&e/cp player <\u0438\u0433\u0440\u043e\u043a>&f \u0438\u043d\u0444\u0430 \u043e\u0431 \u0438\u0433\u0440\u043e\u043a\u0435", "&e/cp spectoteam <\u0438\u0433\u0440\u043e\u043a> <\u0446\u0432\u0435\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u044b>&f \u043f\u0435\u0440\u0435\u0432\u0435\u0441\u0442\u0438 \u0438\u0433\u0440\u043e\u043a\u0430 \u0438\u0437 \u0441\u043f\u0435\u043a\u0442\u043e\u0440\u0430\u0441\u043e\u0432 \u0432 \u043d\u0443\u0436\u043d\u0443\u044e \u043a\u043e\u043c\u0430\u043d\u0434\u0443", "&e/cp etui&f \u0432\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0442\u0443\u0440\u043d\u0438\u0440\u043d\u044b\u0439 \u0438\u043d\u0442\u0435\u0440\u0444\u0435\u0439\u0441 \u0443 \u0437\u0440\u0438\u0442\u0435\u043b\u0435\u0439"});
                        break;
                    }
                }
                break block36;
            }
            if (command.getName().equals("stats")) {
                PlayerInfo player = PlayerInfo.get((Player)sender);
                U.msg((CommandSender)sender, (String[])new String[]{"&e ---- &f\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430 &e----", "&e\u0418\u0433\u0440 \u0441\u044b\u0433\u0440\u0430\u043d\u043e: &f" + player.stats.games, "&e\u041f\u043e\u0431\u0435\u0434: &f" + player.stats.wins, "&e\u0422\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0441\u043b\u043e\u043c\u0430\u043d\u043e: &f" + player.stats.resourcePointsBreaked, "&e\u0423\u0431\u0438\u0439\u0441\u0442\u0432: &f" + player.stats.kills, "&e\u0421\u043c\u0435\u0440\u0442\u0435\u0439: &f" + player.stats.deaths});
            }
        }
        return true;
    }

    private long checkResourceSpawn(CommandSender sender, String team, List<Location> locs, int radius, String name, Material type) {
        return locs.stream().filter(loc -> this.checkResourceSpawn(sender, team, (Location)loc, radius, name, type)).count();
    }

    private boolean checkResourceSpawn(CommandSender sender, String team, Location loc, int radius, String name, Material type) {
        Block middle = loc.getBlock();
        if (Material2.isSolid((Material)middle.getType())) {
            U.msg((CommandSender)sender, (String[])new String[]{"&6WARNING: " + team + " " + new Vec3i(loc) + " &f\u0421\u043f\u0430\u0432\u043d " + name + " \u0432 \u0431\u043b\u043e\u043a\u0435"});
        }
        if (Math.abs(loc.getX() % 1.0) != 0.5 || Math.abs(loc.getZ() % 1.0) != 0.5) {
            U.msg((CommandSender)sender, (String[])new String[]{"&6WARNING: " + team + " " + new Vec3i(loc) + " &f\u0421\u043f\u0430\u0432\u043d " + name + " \u0431\u0435\u0437 0.5"});
        }
        boolean status = false;
        block0: for (int x = -radius; x <= radius; ++x) {
            for (int y = -radius; y <= radius; ++y) {
                for (int z = -radius; z <= radius; ++z) {
                    Block relative = middle.getRelative(x, y, z);
                    if (relative.getType() != type) continue;
                    status = true;
                    break block0;
                }
            }
        }
        if (!status) {
            U.msg((CommandSender)sender, (String[])new String[]{"&cFAIL: " + team + " " + new Vec3i(loc) + " &f\u0412\u043e\u0437\u043b\u0435 \u0441\u043f\u0430\u0432\u043d\u0430 " + name + " \u043d\u0435\u0442 \u0431\u043b\u043e\u043a\u0430"});
        }
        return !status;
    }

    public static ClashPoint instance() {
        return instance;
    }
}

