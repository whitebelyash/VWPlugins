/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.xtrafrancyz.GameReloader.GameReloader
 *  net.xtrafrancyz.VimeNetwork.api.Lobby$State
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery
 *  net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType
 *  net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.InventoryHolder
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package net.xtrafrancyz.ClashPoint.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.game.MapBorder;
import net.xtrafrancyz.ClashPoint.game.entity.TraderEntity;
import net.xtrafrancyz.ClashPoint.menu.UpgradeVillagerMenu;
import net.xtrafrancyz.ClashPoint.menu.shop.NormalShopMenu;
import net.xtrafrancyz.ClashPoint.menu.shop.ShopMenu;
import net.xtrafrancyz.ClashPoint.object.CPTeam;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.ClashPoint.task.TeamTicker;
import net.xtrafrancyz.ClashPoint.util.CommonUtils;
import net.xtrafrancyz.GameReloader.GameReloader;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Game {
    private static final int START_COUNTDOWN = VimeNetwork.isDev() ? 2000 : 20000;
    private final ClashPoint plugin;
    private ShopMenu shop;
    private GameState state = GameState.WAITING;
    public SideScoreboard scoreboard;
    public int startTask = -1;
    public int chunkLoadTask = -1;
    public long startTime = 0L;
    public long endTime = 0L;
    public Set<Integer> gameTasks = new HashSet<Integer>();

    public Game(ClashPoint plugin) {
        this.plugin = plugin;
        this.shop = new NormalShopMenu();
        this.scoreboard = new SideScoreboard(U.colored((String)"&b&lClashPoint"));
        this.scoreboard.create("\u041a\u0430\u0440\u0442\u0430: " + ChatColor.BOLD + Config.mapName, 99).update();
        this.scoreboard.create(ChatColor.RED + "", 98).update();
        for (CPTeam team : Config.teams) {
            team.createRecord(this.scoreboard);
        }
        this.scoreboard.create(ChatColor.BLACK + "", -1).update();
        this.scoreboard.create(U.colored((String)"&a&lVimeWorld.ru"), -2).update();
        this.setState(GameState.WAITING);
    }

    public void join(PlayerInfo player, CPTeam team, boolean force) {
        if (!force && this.getState() == GameState.GAME) {
            return;
        }
        if (player.team != null) {
            this.leave(player);
        }
        player.personalInventory = Bukkit.createInventory((InventoryHolder)player.player, (int)9, (String)"\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
        player.team = team;
        team.players.add(player);
        U.msg((CommandSender)player.player, (String[])new String[]{"&f\u0412\u044b \u0438\u0433\u0440\u0430\u0435\u0442\u0435 \u0437\u0430 " + team.chatColor + team.names[1] + " \u043a\u043e\u043c\u0430\u043d\u0434\u0443"});
        VimeNetwork.getPlayer((String)player.username).setTag(team.chatColor + player.username);
        String name = team.chatColor + player.username;
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        player.player.setPlayerListName(name);
        team.updateRecord();
        this.plugin.teamSelectMenu.update(team);
        this.plugin.spectatorMenu.update();
    }

    public void leave(PlayerInfo player) {
        CPTeam team = player.team;
        player.team = null;
        team.players.remove(player);
        VimeNetwork.getPlayer((String)player.username).removeTag();
        player.player.setPlayerListName(player.username);
        team.updateRecord();
        this.plugin.teamSelectMenu.update(team);
        this.plugin.spectatorMenu.update();
        this.plugin.targetCompass.removeUpdatePlayer(player.player);
        if (team.players.isEmpty()) {
            ClashPoint.instance().events.privateChests.removeProtection(team);
        }
        if (this.getState() == GameState.GAME) {
            this.checkEnd();
        }
    }

    public void cancelStartTask() {
        Bukkit.getScheduler().cancelTask(this.startTask);
        this.startTask = -1;
        Bukkit.getScheduler().cancelTask(this.chunkLoadTask);
        this.chunkLoadTask = -1;
        this.setState(GameState.WAITING);
    }

    public void start() {
        if (this.getState() != GameState.WAITING) {
            return;
        }
        this.setState(GameState.STARTING);
        CPTexteria.showTimer("\u041e\u0442\u0441\u0447\u0435\u0442 \u0434\u043e \u043d\u0430\u0447\u0430\u043b\u0430 \u0438\u0433\u0440\u044b", START_COUNTDOWN);
        this.chunkLoadTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ClashPoint.instance(), () -> {
            for (CPTeam team : Config.teams) {
                team.getSpawnLocation().getChunk().load();
            }
        }, (long)(START_COUNTDOWN / 50 - 60));
        this.startTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)ClashPoint.instance(), () -> {
            this.startTime = System.currentTimeMillis();
            this.endTime = this.startTime + 2700000L;
            if (!VimeNetwork.isTournament()) {
                this.spreadInTeams();
            }
            for (PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
                if (pi.team == null) {
                    if (VimeNetwork.isTournament()) {
                        U.msg((CommandSender)pi.player, (String[])new String[]{"&a\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u0437\u0440\u0438\u0442\u0435\u043b\u0435\u043c"});
                        pi.hyperSpectator = true;
                        Invs.clear((HumanEntity)pi.player);
                        this.plugin.spectators.add(pi.player);
                        continue;
                    }
                    U.msg((CommandSender)pi.player, (String[])new String[]{"&c\u041f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u0432\u043d\u0443\u0442\u0440\u0435\u043d\u043d\u044f\u044f \u043e\u0448\u0438\u0431\u043a\u0430 \u043f\u0440\u0438 \u0440\u0430\u0441\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u0438\u0438 \u043a\u043e\u043c\u0430\u043d\u0434 [" + VimeNetwork.lobby().getServerId() + "]"});
                    VimeNetwork.toLobby((Player[])new Player[]{pi.player});
                    continue;
                }
                pi.personalInventory = Bukkit.createInventory((InventoryHolder)pi.player, (int)9, (String)"\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a");
                CommonUtils.resetPlayer(pi.player);
                pi.player.closeInventory();
                pi.player.setItemOnCursor(null);
                Invs.clear((HumanEntity)pi.player);
                pi.player.teleport(this.spawnPlayer(pi));
                pi.player.setFallDistance(0.0f);
                pi.player.setSaturation(10.0f);
                pi.player.setFoodLevel(20);
                pi.player.setMaxHealth(20.0);
                pi.player.setHealth(20.0);
                pi.player.setGameMode(GameMode.SURVIVAL);
                pi.updateResourceBar();
                this.plugin.targetCompass.addUpdatePlayer(pi.player);
            }
            for (CPTeam team : Config.teams) {
                if (team.players.isEmpty()) {
                    team.wipe();
                    team.record.remove();
                } else {
                    team.init();
                    team.getResourcePoints().forEach(ResourcePoint::prepareForGame);
                }
                this.removeEntitiesInChunks(team.traders);
                this.removeEntitiesInChunks(team.upgraders);
                this.removeEntitiesInChunks(team.getResourcePoints().stream().map(ResourcePoint::getLocation).collect(Collectors.toList()));
                for (Location loc : team.personalChests) {
                    VimeNetwork.holograms().createText(new Vec3f(loc).add(0.5f, 1.5f, 0.5f), new String[]{"&d&l\u041f\u0435\u0440\u0441\u043e\u043d\u0430\u043b\u044c\u043d\u044b\u0439 \u0441\u0443\u043d\u0434\u0443\u043a"});
                }
                if (team.players.isEmpty()) continue;
                this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), (Runnable)new TeamTicker(team), 20L, 20L));
            }
            this.setState(GameState.GAME);
            this.plugin.getLogger().info("Game started");
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), () -> Config.goldSpawns.forEach(loc -> this.spawnItem((Location)loc, Config.GOLD)), (long)Config.goldFrequency, (long)Config.goldFrequency));
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), () -> this.spawnItem(Config.diamondSpawns, Config.DIAMOND), (long)Config.diamondFrequency, (long)Config.diamondFrequency));
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), () -> {
                AtomicBoolean any = new AtomicBoolean(false);
                Config.teams.forEach(team -> {
                    if (team.getResourcePoints().isEmpty()) {
                        return;
                    }
                    any.set(true);
                    team.getResourcePoints().forEach(ResourcePoint::deactivate);
                    ResourcePoint activated = (ResourcePoint)Rand.of(team.getResourcePoints());
                    activated.activate();
                });
                if (any.get()) {
                    U.bcast((String)"&e&l\u0423 \u043a\u0430\u0436\u0434\u043e\u0439 \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u0430\u043a\u0442\u0438\u0432\u0438\u0440\u043e\u0432\u0430\u043d\u043e \u043f\u043e \u043e\u0434\u043d\u043e\u0439 \u0442\u043e\u0447\u043a\u0435 \u0441\u043f\u0430\u0432\u043d\u0430 \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432!");
                }
                CPTexteria.showSecondatyTopTimer("\u0421\u043c\u0435\u043d\u0430 \u0442\u043e\u0447\u0435\u043a \u0440\u0435\u0441\u0443\u0440\u0441\u043e\u0432 \u0447\u0435\u0440\u0435\u0437: &e{M}:{SS}", Config.resourcePointsActivationFrequency * 50);
            }, 0L, (long)Config.resourcePointsActivationFrequency));
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), () -> {
                long remaining = this.endTime - System.currentTimeMillis();
                if (remaining < 0L) {
                    this.end();
                }
            }, 20L, 20L));
            CPTexteria.showPrimaryTopTimer("\u0414\u043e \u043a\u043e\u043d\u0446\u0430 \u0438\u0433\u0440\u044b: &e{M}:{SS}", this.startTime, this.endTime - this.startTime);
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)ClashPoint.instance(), (Runnable)new MapBorder(), 100L, 50L));
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                for (CPTeam team : Config.teams) {
                    for (Location loc : team.traders) {
                        NMSEntityUtils.spawn((net.minecraft.server.v1_6_R3.Entity)new TraderEntity(NMSEntityUtils.getNMSWorld((World)Config.world), "&e&l\u0422\u043e\u0440\u0433\u043e\u0432\u0435\u0446", arg_0 -> ((ShopMenu)this.shop).show(arg_0)), (Location)loc);
                    }
                    for (Location loc : team.upgraders) {
                        NMSEntityUtils.spawn((net.minecraft.server.v1_6_R3.Entity)new TraderEntity(NMSEntityUtils.getNMSWorld((World)Config.world), "&b&l\u041c\u0430\u0441\u0442\u0435\u0440 \u043f\u0440\u043e\u043a\u0430\u0447\u043a\u0438", player -> new UpgradeVillagerMenu(PlayerInfo.get(player)).show((Player)player)), (Location)loc);
                    }
                }
            }, 30L);
        }, (long)(START_COUNTDOWN / 50));
    }

    private void spreadInTeams() {
        ArrayList<List> freeParties = new ArrayList<List>();
        block0: for (PlayerInfo playerInfo : PlayerInfo.PLAYERS.values()) {
            NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)playerInfo.username);
            if (!networkPlayer.isInParty()) continue;
            for (List list : freeParties) {
                for (PlayerInfo playerInfo2 : list) {
                    if (playerInfo2 != playerInfo) continue;
                    continue block0;
                }
            }
            ArrayList<CPTeam> teams = new ArrayList<CPTeam>();
            LinkedList<PlayerInfo> linkedList = new LinkedList<PlayerInfo>();
            linkedList.add(playerInfo);
            for (String string : networkPlayer.getParty().getPlayers()) {
                PlayerInfo ppi = PlayerInfo.PLAYERS.get(string);
                if (ppi == null || ppi == playerInfo) continue;
                if (ppi.team == null) {
                    linkedList.add(ppi);
                    continue;
                }
                if (ppi.team.players.size() >= Config.teamPlayers) continue;
                teams.add(ppi.team);
            }
            if (!teams.isEmpty()) {
                Map<CPTeam, Long> map = teams.stream().collect(Collectors.groupingBy(t -> t, Collectors.counting()));
                long l = 0L;
                CPTeam bestTeam = null;
                for (Map.Entry<CPTeam, Long> entry : map.entrySet()) {
                    if (l >= entry.getValue()) continue;
                    l = entry.getValue();
                    bestTeam = entry.getKey();
                }
                if (bestTeam != null) {
                    this.join(playerInfo, bestTeam, false);
                    continue;
                }
            }
            if (linkedList.size() <= 1) continue;
            freeParties.add(linkedList);
        }
        ArrayList<CPTeam> freeTeams = new ArrayList<CPTeam>();
        for (CPTeam team : Config.teams) {
            if (Config.teamPlayers - team.players.size() <= 1) continue;
            freeTeams.add(team);
        }
        freeParties.sort(Collections.reverseOrder(Comparator.comparingInt(List::size)));
        freeTeams.sort(Collections.reverseOrder(Comparator.comparingInt(t -> t.players.size())));
        while (!freeParties.isEmpty() && !freeTeams.isEmpty()) {
            int slots;
            int teamIndex;
            List list = (List)freeParties.get(0);
            for (teamIndex = 0; teamIndex < freeTeams.size() && (slots = Config.teamPlayers - ((CPTeam)freeTeams.get((int)teamIndex)).players.size()) != list.size(); ++teamIndex) {
                if (slots >= list.size()) continue;
                if (teamIndex <= 0) break;
                --teamIndex;
                break;
            }
            if (teamIndex >= freeTeams.size()) {
                teamIndex = freeTeams.size() - 1;
            }
            CPTeam team = (CPTeam)freeTeams.get(teamIndex);
            Iterator iterator = list.iterator();
            while (iterator.hasNext() && team.players.size() < Config.teamPlayers) {
                this.join((PlayerInfo)iterator.next(), team, false);
                iterator.remove();
            }
            if (Config.teamPlayers - team.players.size() < 2) {
                freeTeams.remove(teamIndex);
            } else {
                freeTeams.sort(Collections.reverseOrder(Comparator.comparingInt(t -> t.players.size())));
            }
            if (list.size() < 2) {
                freeParties.remove(0);
                continue;
            }
            freeParties.sort(Collections.reverseOrder(Comparator.comparingInt(List::size)));
        }
        block9: for (PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
            if (pi.team != null) continue;
            for (CPTeam cPTeam : Config.teams) {
                if (cPTeam.players.size() >= Config.teamPlayers) continue;
                this.join(pi, cPTeam, false);
                continue block9;
            }
        }
    }

    private int getAliveTeams() {
        int alive = 0;
        for (CPTeam team : Config.teams) {
            if (team.players.size() <= 0) continue;
            ++alive;
        }
        return alive;
    }

    public void checkEnd() {
        if (this.getAliveTeams() <= 1) {
            this.end();
        }
    }

    public void end() {
        if (this.getState() != GameState.GAME) {
            return;
        }
        this.setState(GameState.ENDING);
        VimeNetwork.metrics().add("cp.games");
        this.gameTasks.forEach(arg_0 -> ((BukkitScheduler)Bukkit.getScheduler()).cancelTask(arg_0));
        CPTeam winners = null;
        int leavers = 0;
        if (this.getAliveTeams() == 1) {
            for (CPTeam team : Config.teams) {
                if (!team.players.isEmpty()) {
                    winners = team;
                    continue;
                }
                leavers += team.leavers;
            }
        } else {
            for (CPTeam team : Config.teams) {
                if (team.players.isEmpty()) continue;
                team.gamePoints += (float)team.getResourcePoints().size();
                team.gamePoints += (float)team.players.size() * 0.5f;
            }
            winners = Config.teams.stream().filter(t -> !t.players.isEmpty()).sorted((t1, t2) -> {
                if (t1.gamePoints < t2.gamePoints) {
                    return 1;
                }
                if (t1.gamePoints > t2.gamePoints) {
                    return -1;
                }
                return 0;
            }).findFirst().orElse(null);
        }
        U.bcast((String)"&7####################################");
        if (winners == null) {
            U.bcast((String)"&7# &f\u041d\u0438\u0447\u044c\u044f");
            for (CPTeam team : Config.teams) {
                for (PlayerInfo player : team.players) {
                    NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player.player);
                    networkPlayer.addCoins(10);
                    networkPlayer.giveExp(10);
                    networkPlayer.getGoals().trigger("cp", GoalQuery.of((String)"played"));
                }
            }
        } else {
            U.bcast((String)("&7# &f\u041f\u043e\u0431\u0435\u0434\u0438\u043b\u0430 " + winners.chatColor + winners.names[0] + " \u043a\u043e\u043c\u0430\u043d\u0434\u0430&f!"));
            if (leavers == Config.teamPlayers * (Config.teams.size() - 1)) {
                U.bcast((String)"&7# &f\u0412\u0441\u0435 \u0438\u0445 \u043f\u0440\u043e\u0442\u0438\u0432\u043d\u0438\u043a\u0438 \u0443\u0431\u0435\u0436\u0430\u043b\u0438 \u0432 \u0443\u0436\u0430\u0441\u0435, \u0442\u0430\u043a \u0438 \u043d\u0435 \u0434\u043e\u0436\u0434\u0430\u0432\u0448\u0438\u0441\u044c \u043a\u043e\u043d\u0446\u0430");
                U.bcast((String)"&7# &f\u0417\u0430\u043f\u0443\u0433\u0438\u0432\u0430\u0442\u044c \u0432\u0440\u0430\u0433\u043e\u0432 \u0443 \u043d\u0430\u0441 \u043d\u0435 \u043f\u0440\u0438\u043d\u044f\u0442\u043e, \u043f\u043e\u044d\u0442\u043e\u043c\u0443 \u0438 \u0438\u0433\u0440\u0430 \u0437\u0430\u0441\u0447\u0438\u0442\u0430\u043d\u0430 \u043d\u0435 \u0431\u0443\u0434\u0435\u0442...");
            } else {
                for (PlayerInfo winner : winners.players) {
                    U.bcast((String)("&7#     " + winners.chatColor + winner.player.getDisplayName()));
                    ++winner.stats.wins;
                    ++winner.stats.games;
                    NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)winner.player);
                    networkPlayer.addCoins(30);
                    networkPlayer.giveExp(30);
                    networkPlayer.getGoals().trigger("cp", GoalQuery.of((String)"played"));
                    networkPlayer.getGoals().trigger("cp", GoalQuery.of((String)"win"));
                    networkPlayer.getTreasures().giveWithMessage(TreasureType.BASIC, 0.11f);
                    networkPlayer.getTreasures().giveWithMessage(TreasureType.ANCIENT, 0.01f);
                    if (winner.stats.wins >= 1) {
                        networkPlayer.getAchievements().complete(Achievement.CP_WIN_1);
                    }
                    if (winner.stats.wins >= 10) {
                        networkPlayer.getAchievements().complete(Achievement.CP_WIN_10);
                    }
                    if (winner.stats.wins >= 100) {
                        networkPlayer.getAchievements().complete(Achievement.CP_WIN_100);
                    }
                    if (winner.stats.wins >= 1000) {
                        networkPlayer.getAchievements().complete(Achievement.CP_WIN_1000);
                    }
                    if (winner.stats.wins < 10000) continue;
                    networkPlayer.getAchievements().complete(Achievement.CP_WIN_10000);
                }
            }
        }
        U.bcast((String)"&7####################################");
        CPTexteria.onGameEnd(winners);
        CPTexteria.showTimer("\u041e\u0442\u0441\u0447\u0435\u0442 \u0434\u043e \u043a\u043e\u043d\u0446\u0430 \u0438\u0433\u0440\u044b", 10000L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> GameReloader.reload((JavaPlugin)this.plugin), 200L);
    }

    public Location spawnPlayer(PlayerInfo player) {
        PlayerInventory inv = player.player.getInventory();
        inv.setHelmet(Items.appendLore((ItemStack)CommonUtils.paint(new ItemStack(Material.LEATHER_HELMET), player.team.color), (String[])new String[]{"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442"}));
        inv.setChestplate(Items.appendLore((ItemStack)CommonUtils.paint(new ItemStack(Material.LEATHER_CHESTPLATE), player.team.color), (String[])new String[]{"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442"}));
        inv.setLeggings(Items.appendLore((ItemStack)CommonUtils.paint(new ItemStack(Material.LEATHER_LEGGINGS), player.team.color), (String[])new String[]{"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442"}));
        inv.setBoots(Items.appendLore((ItemStack)CommonUtils.paint(new ItemStack(Material.LEATHER_BOOTS), player.team.color), (String[])new String[]{"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442"}));
        inv.setItem(0, Items.appendLore((ItemStack)new ItemStack(Material.WOOD_SWORD), (String[])new String[]{"\u041d\u0430\u0447\u0430\u043b\u044c\u043d\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442"}));
        return player.team.getSpawnLocation();
    }

    private void spawnItem(List<Location> locs, ItemStack is) {
        this.spawnItem((Location)Rand.of(locs), is);
    }

    private void spawnItem(Location loc, ItemStack is) {
        Config.world.dropItem(loc, is);
    }

    private void removeEntitiesInChunks(Collection<Location> locs) {
        for (Location loc : locs) {
            for (Entity entity : Config.world.getChunkAt(loc).getEntities()) {
                if (entity.getType() == EntityType.PLAYER) continue;
                entity.remove();
            }
        }
    }

    public void setState(GameState state) {
        this.state = state;
        ArrayList<String> menuText = new ArrayList<String>(5);
        menuText.add("&f\u041a\u0430\u0440\u0442\u0430: &e" + Config.mapName);
        menuText.add("&f\u0424\u043e\u0440\u043c\u0430\u0442: &e" + Config.teamPlayers + "x" + Config.teams.size());
        menuText.add("");
        if (state == GameState.WAITING) {
            menuText.add("&a\u041d\u0430\u0431\u043e\u0440 \u0438\u0433\u0440\u043e\u043a\u043e\u0432");
            VimeNetwork.lobby().setConnectableState(Lobby.State.ALLOW_ALL);
        } else if (state == GameState.STARTING) {
            menuText.add("&e\u041d\u0430\u0447\u0430\u043b\u043e \u0438\u0433\u0440\u044b");
            VimeNetwork.lobby().setConnectableState(Lobby.State.DENY_ALL);
        } else if (state == GameState.GAME) {
            menuText.add("%GameStarted-" + this.startTime / 1000L);
            VimeNetwork.lobby().setConnectableState(Lobby.State.ALLOW_SPECTATORS);
        } else if (state == GameState.ENDING) {
            menuText.add("&6\u041e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u0438\u0433\u0440\u044b");
            VimeNetwork.lobby().setConnectableState(Lobby.State.DENY_ALL);
        }
        VimeNetwork.lobby().setMenuText(menuText);
    }

    public GameState getState() {
        return this.state;
    }
}

