/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.GameReloader.GameReloader
 *  net.xtrafrancyz.VimeNetwork.api.Lobby$State
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery
 *  net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType
 *  net.xtrafrancyz.VimeNetwork.api.score.Record
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
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockState
 *  org.bukkit.block.Chest
 *  org.bukkit.command.CommandSender
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.scheduler.BukkitScheduler
 */
package net.xtrafrancyz.SkyWars.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.xtrafrancyz.GameReloader.GameReloader;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.Island;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.SkyWars.game.GameState;
import net.xtrafrancyz.SkyWars.game.MysteryChest;
import net.xtrafrancyz.SkyWars.game.STexteria;
import net.xtrafrancyz.SkyWars.game.loot.LootGenerator;
import net.xtrafrancyz.SkyWars.game.loot.StandardLootGenerator;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.score.Record;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

public class Game {
    public static final int START_DELAY_TICKS = (VimeNetwork.isDev() ? 5 : 20) * 20;
    public static final int INVUL_DURATION_TICKS = 200;
    public static final int GAME_DURATION_TICKS = 18000;
    public static final int DEATHMATCH_DURATION_TICKS = 6000;
    private final SkyWars plugin;
    private GameState state = GameState.WAITING;
    public long startTime = 0L;
    private SubState subState = SubState.INVUL;
    public long substateStartTime = 0L;
    public long substateDuration = 0L;
    private int startTask = -1;
    private int chunkLoadTask = -1;
    private List<Integer> deathmatchTasks = new LinkedList<Integer>();
    public SideScoreboard scoreboard;
    private final Record rAlive;
    private final Record rSpectators;
    public List<Integer> gameTasks = new LinkedList<Integer>();
    public MysteryChest mysteryChest;
    public LootGenerator lootGenerator;

    public Game(SkyWars plugin) {
        this.plugin = plugin;
        this.scoreboard = new SideScoreboard(U.colored((String)"&b&lSkyWars"));
        this.scoreboard.create("\u041a\u0430\u0440\u0442\u0430: " + ChatColor.BOLD + Config.mapName, 6).update();
        this.scoreboard.create(ChatColor.GREEN + "", 5).update();
        this.rAlive = this.scoreboard.create("alive", 4);
        this.rSpectators = this.scoreboard.create("spec", 3);
        this.scoreboard.create(ChatColor.BLUE + "", 2).update();
        this.scoreboard.create(U.colored((String)"&a&lVimeWorld.ru"), 1).update();
        this.setAliveRecord(0);
        this.setSpectatorsRecord(0);
        this.lootGenerator = new StandardLootGenerator();
        this.mysteryChest = new MysteryChest(plugin);
        this.setState(GameState.WAITING);
    }

    public void setAliveRecord(int val) {
        this.rAlive.setName(U.colored((String)("&l\u0412\u044b\u0436\u0438\u0432\u0448\u0438\u0445: &a" + val)));
    }

    public void setSpectatorsRecord(int val) {
        this.rSpectators.setName(U.colored((String)("&l\u0417\u0440\u0438\u0442\u0435\u043b\u0435\u0439: &a" + val)));
    }

    public void join(PlayerInfo player, Island island) {
        Island old = null;
        if (player.island != null) {
            old = player.island;
            this.leave(player);
        }
        player.island = island;
        island.players.add(player);
        this.plugin.spectatorMenu.update();
        this.plugin.islandSelectMenu.update(island);
        this.plugin.targetCompass.addUpdatePlayer(player.player);
        if (old != null) {
            old.players.forEach(p -> {
                player.player.hidePlayer(p.player);
                player.player.showPlayer(p.player);
            });
            island.players.forEach(p -> {
                player.player.hidePlayer(p.player);
                player.player.showPlayer(p.player);
            });
        } else {
            for (Island island0 : Config.islands) {
                island0.players.forEach(p -> {
                    player.player.hidePlayer(p.player);
                    player.player.showPlayer(p.player);
                });
            }
        }
        VimeNetwork.getPlayer((String)player.username).getTag().newPrefix().guildTag().space().text("@@").save();
        U.msg((CommandSender)player.player, (String[])new String[]{"&6\u0412\u044b \u0438\u0433\u0440\u0430\u0435\u0442\u0435 \u0437\u0430 \u041e\u0441\u0442\u0440\u043e\u0432 " + island.id});
    }

    public void leave(PlayerInfo player) {
        if (player.island == null) {
            return;
        }
        player.island.players.remove(player);
        this.plugin.islandSelectMenu.update(player.island);
        player.island = null;
        this.plugin.spectatorMenu.update();
        this.plugin.targetCompass.removeUpdatePlayer(player.player);
        if (this.getState() == GameState.GAME) {
            this.setAliveRecord((int)PlayerInfo.PLAYERS.values().stream().filter(pi -> pi.island != null).count());
            int aliveIslands = this.getAliveIslands();
            if (aliveIslands <= 1) {
                this.end();
            } else if (this.subState == SubState.GAME && aliveIslands == 3) {
                if (this.substateStartTime + this.substateDuration - System.currentTimeMillis() < 120000L) {
                    return;
                }
                this.substateStartTime = System.currentTimeMillis();
                this.substateDuration = 120000L;
                STexteria.showPrimaryTopTimer("\u0414\u0435\u0442\u043c\u0430\u0442\u0447 \u0431\u0443\u0434\u0435\u0442 \u0447\u0435\u0440\u0435\u0437: &e{M}:{SS}", this.substateStartTime, this.substateDuration);
                this.deathmatchTasks.forEach(arg_0 -> ((BukkitScheduler)Bukkit.getScheduler()).cancelTask(arg_0));
                this.deathmatchTasks.clear();
                this.scheduleDeathmatch(2400L);
            }
        }
    }

    public int getAliveIslands() {
        int alive = 0;
        for (Island island : Config.islands) {
            if (island.players.isEmpty()) continue;
            ++alive;
        }
        return alive;
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
        this.substateStartTime = System.currentTimeMillis();
        this.substateDuration = START_DELAY_TICKS * 50;
        STexteria.showBaseTimer("\u041e\u0442\u0441\u0447\u0435\u0442 \u0434\u043e \u043d\u0430\u0447\u0430\u043b\u0430 \u0438\u0433\u0440\u044b", this.substateDuration);
        this.chunkLoadTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            for (Island island : Config.islands) {
                for (Location spawn : island.spawns) {
                    spawn.getWorld().getChunkAt(spawn).load();
                }
            }
        }, (long)(START_DELAY_TICKS - 40));
        this.startTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            this.startTime = System.currentTimeMillis();
            this.setState(GameState.GAME);
            this.substateStartTime = this.startTime;
            this.substateDuration = 10000L;
            this.subState = SubState.INVUL;
            this.plugin.getLogger().info("Game started");
            this.spreadInTeams();
            int alive = 0;
            for (Island island : Config.islands) {
                int spawnIndex = 0;
                for (PlayerInfo player : island.players) {
                    player.player.closeInventory();
                    player.player.setItemOnCursor(null);
                    player.player.setNoDamageTicks(400);
                    player.player.setFallDistance(0.0f);
                    player.player.teleport(island.spawns.get(spawnIndex % island.spawns.size()));
                    player.player.setSaturation(10.0f);
                    player.player.setFoodLevel(20);
                    player.player.setMaxHealth(20.0);
                    player.player.setHealth(20.0);
                    player.player.setFireTicks(0);
                    player.player.setGameMode(GameMode.SURVIVAL);
                    player.chestOpened = false;
                    Invs.clear((HumanEntity)player.player);
                    PlayerInventory inv = player.player.getInventory();
                    inv.addItem(new ItemStack[]{new ItemStack(Material.WOOD_SWORD)});
                    inv.addItem(new ItemStack[]{Items.enchant((ItemStack)new ItemStack(Material.STONE_PICKAXE), (Object[])new Object[]{Enchantment.DIG_SPEED, 2})});
                    inv.addItem(new ItemStack[]{new ItemStack(Material.STONE_AXE)});
                    inv.addItem(new ItemStack[]{new ItemStack(Material.STONE_SPADE)});
                    if (player.kit != null) {
                        this.plugin.kits.kits.get(player.kit).equip(player);
                    }
                    if (player.upgrades.speedBoost > 0) {
                        player.player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, player.upgrades.speedBoost * 20, 0));
                    }
                    ++alive;
                    ++spawnIndex;
                }
            }
            this.setAliveRecord(alive);
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                List players = Config.islands.stream().flatMap(i -> i.players.stream()).map(p -> p.player).collect(Collectors.toList());
                for (Player player : players) {
                    for (Player player2 : players) {
                        if (!player.canSee(player2)) continue;
                        player.hidePlayer(player2);
                        player.showPlayer(player2);
                    }
                }
            }, 40L);
            STexteria.showPrimaryTopTimer("\u041d\u0430\u0447\u0430\u043b\u043e \u0431\u0438\u0442\u0432\u044b: &e{SS} \u0441.", this.substateStartTime, this.substateDuration);
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                for (PlayerInfo player : PlayerInfo.PLAYERS.values()) {
                    if (player.island == null || player.upgrades.resistance <= 0) continue;
                    player.player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, player.upgrades.resistance * 20, 0));
                }
                this.substateDuration = 890000L;
                this.substateStartTime = System.currentTimeMillis();
                this.subState = SubState.GAME;
                STexteria.showPrimaryTopTimer("\u0414\u0435\u0442\u043c\u0430\u0442\u0447 \u0431\u0443\u0434\u0435\u0442 \u0447\u0435\u0440\u0435\u0437: &e{M}:{SS}", this.substateStartTime, this.substateDuration);
            }, 200L));
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> VimeNetwork.texteria().showCountdown(5, "\u0423\u0434\u0430\u0447\u0438!", Bukkit::getOnlinePlayers), 80L);
            this.scheduleDeathmatch(18000L);
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this.plugin, () -> {
                List<Object> items;
                for (Island island : Config.islands) {
                    Inventory[] chests;
                    items = new LinkedList<ItemStack>();
                    if (Config.islandPlayers == 1) {
                        items.addAll(this.lootGenerator.basic());
                    } else {
                        for (int i = 0; i < Config.islandPlayers; ++i) {
                            items.addAll(Game.thinLoot(this.lootGenerator.basic(), 0.9f));
                        }
                    }
                    for (Inventory inv : chests = (Inventory[])island.clests.stream().map(loc -> loc.getBlock().getState()).filter(state -> state instanceof Chest).map(state -> ((Chest)state).getInventory()).toArray(Inventory[]::new)) {
                        inv.clear();
                    }
                    int i = 0;
                    for (ItemStack item : items) {
                        chests[i % chests.length].setItem(i / chests.length, item);
                        ++i;
                    }
                    for (Inventory inv : chests) {
                        ItemStack[] contents = inv.getContents();
                        Game.shuffleArray(contents, Rand.getRandom());
                        inv.setContents(contents);
                    }
                }
                for (Location loc2 : Config.basicChests) {
                    items = this.lootGenerator.basic();
                    BlockState state2 = loc2.getBlock().getState();
                    if (!(state2 instanceof Chest)) continue;
                    Inventory inv = ((Chest)state2).getInventory();
                    ItemStack[] contents = new ItemStack[inv.getSize()];
                    int i = 0;
                    for (ItemStack item : items) {
                        if (!Rand.nextBoolean()) continue;
                        contents[i++] = item;
                    }
                    Game.shuffleArray(contents, Rand.getRandom());
                    inv.setContents(contents);
                }
                for (Location loc2 : Config.middleChests) {
                    Game.randomFillChest(loc2, this.lootGenerator.middle());
                }
                if (this.lootGenerator.rotation > 0) {
                    STexteria.showCustomMessage(Bukkit.getOnlinePlayers(), "\u0421\u0443\u043d\u0434\u0443\u043a\u0438 \u043f\u0435\u0440\u0435\u0437\u0430\u043f\u043e\u043b\u043d\u0435\u043d\u044b", -1, 3000L);
                }
                ++this.lootGenerator.rotation;
            }, 0L, 7400L));
            this.mysteryChest.start();
        }, (long)START_DELAY_TICKS);
    }

    private void spreadInTeams() {
        ArrayList<List> freeParties = new ArrayList<List>();
        block0: for (PlayerInfo playerInfo : PlayerInfo.PLAYERS.values()) {
            NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)playerInfo.username);
            if (!networkPlayer.hasParty()) continue;
            for (List list : freeParties) {
                for (PlayerInfo playerInfo2 : list) {
                    if (playerInfo2 != playerInfo) continue;
                    continue block0;
                }
            }
            ArrayList<Island> teams = new ArrayList<Island>();
            LinkedList<PlayerInfo> linkedList = new LinkedList<PlayerInfo>();
            linkedList.add(playerInfo);
            for (String string : networkPlayer.getParty().getPlayers()) {
                PlayerInfo ppi = PlayerInfo.PLAYERS.get(string);
                if (ppi == null || ppi == playerInfo) continue;
                if (ppi.island == null) {
                    linkedList.add(ppi);
                    continue;
                }
                if (ppi.island.players.size() >= Config.islandPlayers) continue;
                teams.add(ppi.island);
            }
            if (!teams.isEmpty()) {
                Map<Island, Long> map = teams.stream().collect(Collectors.groupingBy(t -> t, Collectors.counting()));
                long l = 0L;
                Island bestIsland = null;
                for (Map.Entry<Island, Long> entry : map.entrySet()) {
                    if (l >= entry.getValue()) continue;
                    l = entry.getValue();
                    bestIsland = entry.getKey();
                }
                if (bestIsland != null) {
                    this.join(playerInfo, bestIsland);
                    continue;
                }
            }
            if (linkedList.size() <= 1) continue;
            freeParties.add(linkedList);
        }
        ArrayList<Island> freeTeams = new ArrayList<Island>();
        for (Island island : Config.islands) {
            if (Config.islandPlayers - island.players.size() <= 1) continue;
            freeTeams.add(island);
        }
        freeParties.sort(Collections.reverseOrder(Comparator.comparingInt(List::size)));
        freeTeams.sort(Collections.reverseOrder(Comparator.comparingInt(t -> t.players.size())));
        while (!freeParties.isEmpty() && !freeTeams.isEmpty()) {
            int slots;
            int teamIndex;
            List list = (List)freeParties.get(0);
            for (teamIndex = 0; teamIndex < freeTeams.size() && (slots = Config.islandPlayers - ((Island)freeTeams.get((int)teamIndex)).players.size()) != list.size(); ++teamIndex) {
                if (slots >= list.size()) continue;
                if (teamIndex <= 0) break;
                --teamIndex;
                break;
            }
            if (teamIndex >= freeTeams.size()) {
                teamIndex = freeTeams.size() - 1;
            }
            Island island = (Island)freeTeams.get(teamIndex);
            Iterator iterator = list.iterator();
            while (iterator.hasNext() && island.players.size() < Config.islandPlayers) {
                this.join((PlayerInfo)iterator.next(), island);
                iterator.remove();
            }
            if (Config.islandPlayers - island.players.size() < 2) {
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
            if (pi.island != null) continue;
            for (Island island : Config.islands) {
                if (island.players.size() >= Config.islandPlayers) continue;
                this.join(pi, island);
                continue block9;
            }
        }
    }

    private void scheduleDeathmatch(long delayTicks) {
        int task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this::startDeathmatch, delayTicks);
        this.deathmatchTasks.add(task);
        this.gameTasks.add(task);
        task = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> VimeNetwork.texteria().showCountdown(5, "\u0412 \u0431\u043e\u0439!!", Bukkit::getOnlinePlayers), delayTicks - 120L);
        this.deathmatchTasks.add(task);
        this.gameTasks.add(task);
    }

    private void startDeathmatch() {
        if (this.subState != SubState.GAME) {
            return;
        }
        this.subState = SubState.DEATHMATCH;
        this.substateDuration = 300000L;
        this.substateStartTime = System.currentTimeMillis();
        this.plugin.getLogger().info("Deathmatch started");
        int i = 0;
        for (Island island : Config.islands) {
            for (PlayerInfo player : island.players) {
                player.player.leaveVehicle();
                player.player.setNoDamageTicks(100);
                Location loc = Config.deathmatchSpawns.get(i);
                player.player.teleport(loc);
                Block floor = loc.getBlock().getRelative(0, -3, 0);
                this.setBlockIfAir(floor, Material.GLASS);
                this.setBlockIfAir(floor.getRelative(0, 0, 1), Material.GLASS);
                this.setBlockIfAir(floor.getRelative(0, 0, -1), Material.GLASS);
                this.setBlockIfAir(floor.getRelative(1, 0, 0), Material.GLASS);
                this.setBlockIfAir(floor.getRelative(-1, 0, 0), Material.GLASS);
            }
            ++i;
        }
        STexteria.showPrimaryTopTimer("\u041e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u0438\u0433\u0440\u044b \u0447\u0435\u0440\u0435\u0437: &e{M}:{SS}", this.substateStartTime, this.substateDuration);
        this.gameTasks.add(Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, this::end, 6000L));
    }

    private void setBlockIfAir(Block block, Material type) {
        if (block.getType() == Material.AIR) {
            block.setType(type);
        }
    }

    public void end() {
        if (this.getState() != GameState.GAME) {
            return;
        }
        this.setState(GameState.ENDING);
        VimeNetwork.metrics().add(Config.TYPE.getId() + ".games");
        this.gameTasks.forEach(arg_0 -> ((BukkitScheduler)Bukkit.getScheduler()).cancelTask(arg_0));
        this.mysteryChest.stop();
        Island winners = null;
        if (this.getAliveIslands() == 1) {
            for (Island island : Config.islands) {
                if (island.players.size() <= 0) continue;
                winners = island;
                for (PlayerInfo player : island.players) {
                    ++player.stats.games;
                }
            }
        }
        HashSet<String> texteriaWhitelistedPlayers = new HashSet<String>();
        U.bcast((String)"&7####################################");
        if (winners == null) {
            U.bcast((String)"&7# &f\u041d\u0438\u0447\u044c\u044f");
        } else {
            if (winners.players.size() == 1) {
                U.bcast((String)"&7# &f\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u044c: ");
            } else {
                U.bcast((String)"&7# &f\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u0435\u043b\u0438: ");
            }
            for (PlayerInfo winner : winners.players) {
                U.bcast((String)("&7#     " + winner.player.getDisplayName()));
                ++winner.stats.wins;
                ++winner.stats.winStreak;
                if (winner.stats.winStreak > winner.stats.highestWinStreak) {
                    winner.stats.highestWinStreak = winner.stats.winStreak;
                }
                NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)winner.player);
                networkPlayer.addCoins(50 + winner.kills * 5);
                networkPlayer.giveExp(50 + winner.kills * 5);
                networkPlayer.getGoals().trigger("sw", GoalQuery.of((String)"played").put("mode", (Object)Config.TYPE.getId()));
                networkPlayer.getGoals().trigger("sw", GoalQuery.of((String)"win").put("mode", (Object)Config.TYPE.getId()));
                networkPlayer.getTreasures().giveWithMessage(TreasureType.BASIC, 0.14f);
                networkPlayer.getTreasures().giveWithMessage(TreasureType.ANCIENT, 0.01f);
                this.plugin.getLogger().info("Winner: " + winner.username);
                boolean completedAchievement = false;
                if (!winner.chestOpened) {
                    completedAchievement = networkPlayer.getAchievements().complete(Achievement.SW_IS_IT_REAL);
                }
                if (winner.stats.wins >= 1) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_1);
                }
                if (winner.stats.wins >= 10) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_10);
                }
                if (winner.stats.wins >= 100) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_100);
                }
                if (winner.stats.wins >= 1000) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_1000);
                }
                if (winner.stats.wins >= 10000) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_10000);
                }
                if (winner.stats.winStreak >= 5) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_WIN_STREAK_5);
                }
                if (winner.kills == 0) {
                    completedAchievement |= networkPlayer.getAchievements().complete(Achievement.SW_PACIFIST);
                }
                if (!completedAchievement) continue;
                texteriaWhitelistedPlayers.add(winner.username);
            }
        }
        U.bcast((String)"&7####################################");
        STexteria.onGameEnd(winners, texteriaWhitelistedPlayers);
        STexteria.showBaseTimer("\u041e\u0442\u0441\u0447\u0435\u0442 \u0434\u043e \u043a\u043e\u043d\u0446\u0430 \u0438\u0433\u0440\u044b", 10000L);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> GameReloader.reload((JavaPlugin)this.plugin), 200L);
    }

    static void randomFillChest(Location chest, List<ItemStack> items) {
        BlockState state = chest.getBlock().getState();
        if (state instanceof Chest) {
            Game.randomFillInventory(((Chest)state).getInventory(), items);
        }
    }

    static void randomFillInventory(Inventory inv, List<ItemStack> items) {
        ItemStack[] contents = new ItemStack[inv.getSize()];
        int i = 0;
        for (ItemStack item : items) {
            contents[i++] = item;
        }
        Game.shuffleArray(contents, Rand.getRandom());
        inv.setContents(contents);
    }

    static <T> List<T> thinLoot(List<T> list, float keepChance) {
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            it.next();
            if (!(Rand.nextFloat() > keepChance)) continue;
            it.remove();
        }
        return list;
    }

    private static <T> void shuffleArray(T[] arr, Random rnd) {
        for (int i = arr.length - 1; i > 0; --i) {
            int index = rnd.nextInt(i + 1);
            T a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
    }

    public GameState getState() {
        return this.state;
    }

    public void setState(GameState state) {
        this.state = state;
        ArrayList<String> menuText = new ArrayList<String>(5);
        menuText.add("&f\u041a\u0430\u0440\u0442\u0430: &e" + Config.mapName);
        menuText.add("&f\u0424\u043e\u0440\u043c\u0430\u0442: &e" + Config.islandPlayers + "x" + Config.islands.size());
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

    private static enum SubState {
        INVUL,
        GAME,
        DEATHMATCH;

    }
}

