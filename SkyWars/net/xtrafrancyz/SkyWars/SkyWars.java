/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Commons.player.Rank
 *  net.xtrafrancyz.Core.network.packet.Packet59Party
 *  net.xtrafrancyz.GameReloader.GameReloader
 *  net.xtrafrancyz.VimeNetwork.VNPlugin
 *  net.xtrafrancyz.VimeNetwork.api.PlayerTargetCompass
 *  net.xtrafrancyz.VimeNetwork.api.VSpigot
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3f
 *  net.xtrafrancyz.VimeNetwork.api.geom.Vec3i
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.util.LobbyProtector
 *  net.xtrafrancyz.VimeNetwork.api.util.Spectators
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Chest
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.inventory.Recipe
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.java.JavaPlugin
 */
package net.xtrafrancyz.SkyWars;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.GameReloader.GameReloader;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.DataRepository;
import net.xtrafrancyz.SkyWars.Events;
import net.xtrafrancyz.SkyWars.Island;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.SWTagManager;
import net.xtrafrancyz.SkyWars.game.Game;
import net.xtrafrancyz.SkyWars.game.GameState;
import net.xtrafrancyz.SkyWars.kit.Kits;
import net.xtrafrancyz.SkyWars.menu.IslandSelectMenu;
import net.xtrafrancyz.SkyWars.menu.SpectatorMenu;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.PlayerTargetCompass;
import net.xtrafrancyz.VimeNetwork.api.VSpigot;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.LobbyProtector;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyWars
extends JavaPlugin {
    private static SkyWars instance;
    public DataRepository repository;
    public Game game;
    public Spectators spectators;
    public SpectatorMenu spectatorMenu;
    public IslandSelectMenu islandSelectMenu;
    public Kits kits;
    public PlayerTargetCompass targetCompass;

    public void onEnable() {
        instance = this;
        Config.load();
        this.spectators = Spectators.instance();
        this.repository = new DataRepository();
        this.game = new Game(this);
        this.spectatorMenu = new SpectatorMenu();
        this.islandSelectMenu = new IslandSelectMenu(this);
        this.kits = new Kits();
        this.targetCompass = new PlayerTargetCompass((Plugin)this, player -> {
            Island island = PlayerInfo.get((Player)player).island;
            return PlayerInfo.PLAYERS.values().stream().filter(p -> p.island != null && p.island != island).map(p -> p.player).collect(Collectors.toList());
        });
        TrailMenu.init((JavaPlugin)this, (String)"sw");
        LobbyProtector.init((Plugin)this, (Location)Config.lobby, (int)125);
        Bukkit.getPluginManager().registerEvents((Listener)new Events(this), (Plugin)this);
        VimeNetwork.addCommandHelp((String)"stats", (String)"\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430");
        VimeNetwork.addCommandHelp((String)"skywars", (String)"\u0410\u0434\u043c\u0438\u043d\u0441\u043a\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b", (Rank)Rank.ADMIN);
        VSpigot.setDeathFromHunger((World)Config.world, (boolean)true);
        VSpigot.setChestCatDetection((World)Config.world, (boolean)false);
        VimeNetwork.features().ALWAYS_SUN.setEnabled(true);
        VimeNetwork.features().ALWAYS_DAY.setEnabled(true);
        VimeNetwork.features().CHANGE_PLAYER_LIST_NAMES.setEnabled(true);
        VimeNetwork.features().CHANGE_CHAT.setEnabled(true);
        VimeNetwork.features().ANTI_LEAVE.setEnabled(true);
        VimeNetwork.features().SAVE_PLAYER_DATA.setEnabled(false);
        VimeNetwork.features().CHANGE_TAGS.setEnabled(true);
        VimeNetwork.features().ADD_GUILD_TAGS.setEnabled(true);
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)this, this::savePlayers, 12000L, 12000L);
        VimeNetwork.lobby().setMaxPlayers(Config.getMaxPlayers());
        VimeNetwork.core().addHandler(Packet59Party.class, this::onPartyPacket);
        this.spectators.addListener((Plugin)this, (player, spectator) -> {
            if (spectator) {
                player.getInventory().setItem(0, SpectatorMenu.MENU_ITEM.clone());
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
            this.game.setSpectatorsRecord(this.spectators.getSpectators().size());
        });
        VNPlugin.instance().packets.removeOutgoingListener((Consumer)VNPlugin.instance().tags);
        VNPlugin.instance().packets.addOutgoingListener((Plugin)this, (Consumer)new SWTagManager());
        Iterator it = this.getServer().recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = (Recipe)it.next();
            if (recipe.getResult().getType() != Material.GOLDEN_APPLE || recipe.getResult().getDurability() != 1) continue;
            it.remove();
        }
        this.getLogger().info("Map: " + Config.mapName);
    }

    public void onDisable() {
        this.savePlayers();
        VNPlugin.instance().packets.addOutgoingListener((Plugin)VNPlugin.instance(), (Consumer)VNPlugin.instance().tags);
        VimeNetwork.holograms().reset();
        VimeNetwork.core().removeHandler(Packet59Party.class, this::onPartyPacket);
        PlayerInfo.PLAYERS.clear();
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
                if (System.currentTimeMillis() - networkPlayer.getLoginTime() < 1000L && (leader = PlayerInfo.PLAYERS.get(packet.leader.username)) != null && leader != player && leader.island != null && leader.island.players.size() < Config.islandPlayers) {
                    this.game.join(player, leader.island);
                }
            }
        });
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("skywars")) {
            if (!VimeNetwork.hasRank((CommandSender)sender, (Rank)Rank.ADMIN, (boolean)true)) {
                return true;
            }
            if (args.length == 0) {
                args = new String[]{"help"};
            }
            switch (args[0].toLowerCase()) {
                case "reload": {
                    GameReloader.reload((JavaPlugin)this);
                    break;
                }
                case "start": {
                    this.game.start();
                    U.msg((CommandSender)sender, (String[])new String[]{"\u0418\u0433\u0440\u0430 \u0437\u0430\u043f\u0443\u0449\u0435\u043d\u0430"});
                    break;
                }
                case "stop": {
                    this.game.end();
                    U.msg((CommandSender)sender, (String[])new String[]{"\u0418\u0433\u0440\u0430 \u043e\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430"});
                    break;
                }
                case "testconfig": {
                    AtomicBoolean ok = new AtomicBoolean(true);
                    HashSet chests = new HashSet();
                    HashSet<Location> spawns = new HashSet<Location>();
                    ArrayList<String> messages = new ArrayList<String>();
                    Consumer<Location> typeChecker = loc -> {
                        Material type = loc.getBlock().getType();
                        switch (type) {
                            case CHEST: 
                            case ENDER_CHEST: 
                            case LOCKED_CHEST: 
                            case TRAPPED_CHEST: {
                                if (chests.add(loc)) break;
                                messages.add("&c\u0421\u0443\u043d\u0434\u0443\u043a \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u0435\u0442\u0441\u044f " + new Vec3i(loc) + " " + type);
                                ok.set(false);
                                break;
                            }
                            default: {
                                messages.add("&c" + new Vec3i(loc) + " " + type);
                                ok.set(false);
                            }
                        }
                    };
                    typeChecker.accept(Config.mysteryChest);
                    sender.sendMessage("Middle:");
                    Config.middleChests.forEach(typeChecker);
                    sender.sendMessage("Basic:");
                    Config.basicChests.forEach(typeChecker);
                    for (Island island : Config.islands) {
                        if (island.clests.isEmpty()) {
                            messages.add("&c\u041d\u0435\u0442 \u043d\u0438 \u043e\u0434\u043d\u043e\u0433\u043e \u0441\u0443\u043d\u0434\u0443\u043a\u0430");
                            ok.set(false);
                        } else {
                            island.clests.forEach(loc -> {
                                typeChecker.accept((Location)loc);
                                if (loc.distance(island.spawns.get(0)) > 20.0) {
                                    messages.add("&c\u0421\u0443\u043d\u0434\u0443\u043a \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u0434\u0430\u043b\u0435\u043a\u043e \u043e\u0442 \u0441\u043f\u0430\u0432\u043d\u0430 \u0438\u0433\u0440\u043e\u043a\u0430 " + new Vec3i(loc));
                                    ok.set(false);
                                }
                            });
                        }
                        for (Location spawn : island.spawns) {
                            Location loc2 = spawn.clone();
                            while (loc2.getY() > 0.0) {
                                loc2.add(0.0, -1.0, 0.0);
                                if (loc2.getBlock().isEmpty()) continue;
                            }
                            if (loc2.getY() <= 0.0) {
                                ok.set(false);
                                messages.add("&c\u0421\u043f\u0430\u0432\u043d \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435 " + new Vec3f(loc2));
                                continue;
                            }
                            if (spawns.add(loc2)) continue;
                            messages.add("&a\u0422\u043e\u0447\u043a\u0430 \u0441\u043f\u0430\u0432\u043d\u0430 \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u0435\u0442\u0441\u044f " + new Vec3f(loc2));
                            ok.set(false);
                        }
                        if (messages.isEmpty()) continue;
                        sender.sendMessage("\u041e\u0441\u0442\u0440\u043e\u0432 " + island.id);
                        messages.forEach(msg -> U.msg((CommandSender)sender, (String[])new String[]{msg}));
                        messages.clear();
                    }
                    if (!ok.get()) {
                        U.msg((CommandSender)sender, (String[])new String[]{"", "      &c&l\u041f\u0438\u0437\u0434\u0430&r", ""});
                        break;
                    }
                    U.msg((CommandSender)sender, (String[])new String[]{"", "      &a&l\u0412\u0441\u0451 \u043e\u043a&r", ""});
                    break;
                }
                case "clearchests": {
                    Config.protectedChests.forEach(l -> ((Chest)l.getBlock().getState()).getInventory().clear());
                    break;
                }
                default: {
                    U.msg((CommandSender)sender, (String[])new String[]{"&e=========== &fSkyWars &e===========", "&e/sw start&f \u0441\u0442\u0430\u0440\u0442 \u0438\u0433\u0440\u044b", "&e/sw stop&f \u043e\u043a\u043e\u043d\u0447\u0430\u043d\u0438\u0435 \u0438\u0433\u0440\u044b", "&e/sw testconfig&f \u0432\u0430\u043b\u0438\u0434\u0430\u0446\u0438\u044f \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a", "&e/sw clearchests&f \u043e\u0447\u0438\u0441\u0442\u043a\u0430 \u0432\u0441\u0435\u0445 \u0441\u0443\u043d\u0434\u0443\u043a\u043e\u0432"});
                    break;
                }
            }
        } else if (command.getName().equals("stats")) {
            PlayerInfo player = PlayerInfo.get((Player)sender);
            U.msg((CommandSender)sender, (String[])new String[]{"&e ---- &f\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430 &aSkyWars &e----", "&e\u0418\u0433\u0440 \u0441\u044b\u0433\u0440\u0430\u043d\u043e: &f" + player.stats.games, "&e\u041f\u043e\u0431\u0435\u0434: &f" + player.stats.wins, "&e\u0423\u0431\u0438\u0439\u0441\u0442\u0432: &f" + player.stats.kills, "&e\u0421\u043c\u0435\u0440\u0442\u0435\u0439: &f" + player.stats.deaths, "&e\u0412\u044b\u0441\u0442\u0440\u0435\u043b\u043e\u0432 \u0438\u0437 \u043b\u0443\u043a\u0430: &f" + player.stats.arrowsFired, "&e\u0411\u043b\u043e\u043a\u043e\u0432 \u0441\u043b\u043e\u043c\u0430\u043d\u043e: &f" + player.stats.blocksBroken, "&e\u0411\u043b\u043e\u043a\u043e\u0432 \u043f\u043e\u0441\u0442\u0430\u0432\u043b\u0435\u043d\u043e: &f" + player.stats.blocksPlaced, "&e\u041b\u0443\u0447\u0448\u0430\u044f \u0441\u0435\u0440\u0438\u044f \u043f\u043e\u0431\u0435\u0434: &f" + player.stats.highestWinStreak, "&e\u0422\u0435\u043a\u0443\u0449\u0430\u044f \u0441\u0435\u0440\u0438\u044f \u043f\u043e\u0431\u0435\u0434: &f" + player.stats.winStreak});
        }
        return true;
    }

    public static SkyWars instance() {
        return instance;
    }
}

