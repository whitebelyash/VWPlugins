package net.xtrafrancyz.BedWars;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.xtrafrancyz.BedWars.game.BTexteria;
import net.xtrafrancyz.BedWars.game.Game;
import net.xtrafrancyz.BedWars.game.GameState;
import net.xtrafrancyz.BedWars.game.usables.BridgeBuilder;
import net.xtrafrancyz.BedWars.game.usables.ItemPackage;
import net.xtrafrancyz.BedWars.game.usables.RescuePlatform;
import net.xtrafrancyz.BedWars.game.usables.Thor;
import net.xtrafrancyz.BedWars.game.usables.Trap;
import net.xtrafrancyz.BedWars.game.usables.WarpPowder;
import net.xtrafrancyz.BedWars.menu.SpectatorMenu;
import net.xtrafrancyz.BedWars.menu.SpectatorSettingsMenu;
import net.xtrafrancyz.BedWars.menu.TeamSelectMenu;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class BedWars extends JavaPlugin {
   private static BedWars instance;
   public Spectators spectators;
   public DataRepository repository;
   public Game game;
   public Events events;
   public TeamSelectMenu teamSelectMenu;
   public SpectatorMenu spectatorMenu;
   public PlayerTargetCompass targetCompass;
   public Trap trapUsable;

   public void onEnable() {
      instance = this;
      Config.load();
      this.game = new Game(this);
      this.repository = new DataRepository();
      this.teamSelectMenu = new TeamSelectMenu(this);
      this.spectatorMenu = new SpectatorMenu();
      this.spectators = Spectators.instance();
      this.targetCompass = new PlayerTargetCompass(this, (player) -> {
         PlayerInfo pi = PlayerInfo.get(player);
         List<Player> players = new LinkedList();
         if (pi.team == null) {
            return players;
         } else {
            for(BWTeam team : Config.teams) {
               if (!pi.team.equals(team)) {
                  for(PlayerInfo pl : team.players) {
                     players.add(pl.player);
                  }
               }
            }

            return players;
         }
      });
      this.targetCompass.setPlayerNameProvider((player) -> PlayerInfo.get(player).team.chatColor + player.getName());
      TrailMenu.init(this, "bw");
      LobbyProtector.init(this, Config.lobby);
      Bukkit.getPluginManager().registerEvents(this.events = new Events(this), this);
      Bukkit.getPluginManager().registerEvents(new TotalDisabler(), this);
      Bukkit.getPluginManager().registerEvents(new WarpPowder(this), this);
      Bukkit.getPluginManager().registerEvents(new RescuePlatform(this), this);
      Bukkit.getPluginManager().registerEvents(this.trapUsable = new Trap(this), this);
      Bukkit.getPluginManager().registerEvents(new Thor(), this);
      if (Config.type == Config.Type.QUICK) {
         Bukkit.getPluginManager().registerEvents(new BridgeBuilder(this), this);
         Bukkit.getPluginManager().registerEvents(new ItemPackage(), this);
      }

      VimeNetwork.addCommandHelp("     ", "Общий чат: \"&a!&fсообщение\"");
      VimeNetwork.addCommandHelp("stats", "Статистика");
      VimeNetwork.addCommandHelp("bedwars", "Админские команды", Rank.ADMIN);
      VimeNetwork.features().ALWAYS_SUN.setEnabled(true);
      VimeNetwork.features().ALWAYS_DAY.setEnabled(true);
      VimeNetwork.features().CANCEL_DROP_ITEM_FIX.setEnabled(true);
      VimeNetwork.features().ANTI_LEAVE.setEnabled(true);
      Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this::savePlayers, 12000L, 12000L);
      VimeNetwork.lobby().setMaxPlayers(Config.getMaxPlayers());
      this.spectators.addListener(this, (player, spectator) -> {
         if (spectator) {
            player.getInventory().setItem(0, SpectatorMenu.MENU_ITEM.clone());
            player.getInventory().setItem(1, SpectatorSettingsMenu.MENU_ITEM.clone());
         }

      });
      VimeNetwork.core().addHandler(Packet59Party.class, this::onPartyPacket);
      if (VimeNetwork.isTournament()) {
         Bukkit.getPluginManager().registerEvents(new TournamentUI(this), this);
      }

      Config.world.getEntities().stream().filter((e) -> e instanceof Item).forEach(Entity::remove);
      Item item = Config.world.dropItem(Config.dropBronze, Config.GOLD.clone());
      item.setVelocity(new Vector());
      item.setPickupDelay(Integer.MAX_VALUE);
      VimeNetwork.holograms().createText((new Vec3f(Config.dropBronze)).add(0.0F, 0.5F, 0.0F), new String[]{"&e&lЗолото"});
      item = Config.world.dropItem(Config.dropIron, Config.IRON.clone());
      item.setVelocity(new Vector());
      item.setPickupDelay(Integer.MAX_VALUE);
      VimeNetwork.holograms().createText((new Vec3f(Config.dropIron)).add(0.0F, 0.5F, 0.0F), new String[]{"&lЖелезо"});
      item = Config.world.dropItem(Config.dropGold, Config.BRONZE.clone());
      item.setVelocity(new Vector());
      item.setPickupDelay(Integer.MAX_VALUE);
      VimeNetwork.holograms().createText((new Vec3f(Config.dropGold)).add(0.0F, 0.5F, 0.0F), new String[]{"&6&lБронза"});
   }

   public void onDisable() {
      this.savePlayers();
      VimeNetwork.holograms().reset();
      VimeNetwork.npcs().reset();
      PlayerInfo.PLAYERS.clear();
      VimeNetwork.core().removeHandler(Packet59Party.class, this::onPartyPacket);
   }

   public void savePlayers() {
      Collection var10000 = PlayerInfo.PLAYERS.values();
      DataRepository var10001 = this.repository;
      var10000.forEach(var10001::savePlayer);
   }

   private void onPartyPacket(Packet59Party packet) {
      if (this.game.getState() == GameState.WAITING) {
         Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            if (packet.leader != null) {
               PlayerInfo player = (PlayerInfo)PlayerInfo.PLAYERS.get(packet.username);
               if (player != null) {
                  NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.username);
                  if (System.currentTimeMillis() - networkPlayer.getLoginTime() < 1000L) {
                     PlayerInfo leader = (PlayerInfo)PlayerInfo.PLAYERS.get(packet.leader.username);
                     if (leader != null && leader != player && leader.team != null && leader.team.players.size() < Config.teamPlayers) {
                        this.game.join(player, leader.team, false);
                     }
                  }
               }

            }
         });
      }
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (command.getName().equals("bedwars")) {
         if (!VimeNetwork.hasRank(sender, Rank.CHIEF, true)) {
            return true;
         }

         if (args.length == 0) {
            args = new String[]{"help"};
         }

         switch (args[0]) {
            case "player":
               if (args.length != 2) {
                  sender.sendMessage(ChatColor.RED + "/" + label + " player <player>");
               } else {
                  PlayerInfo player = (PlayerInfo)PlayerInfo.PLAYERS.get(args[1]);
                  if (player == null) {
                     sender.sendMessage(ChatColor.RED + "Player '" + args[1] + "' not found");
                  } else {
                     U.msg(sender, new String[]{"&e ---- &f" + player.player.getDisplayName() + " &e---"});
                     U.msg(sender, new String[]{"&eWins: &f" + player.stats.wins});
                     U.msg(sender, new String[]{"&eGames: &f" + player.stats.games});
                     U.msg(sender, new String[]{"&eKills: &f" + player.stats.kills});
                     U.msg(sender, new String[]{"&eDeaths: &f" + player.stats.deaths});
                  }
               }
               break;
            case "stats":
               U.msg(sender, new String[]{"&eСостояние: &f" + this.game.getState()});

               for(BWTeam team : Config.teams) {
                  String msg = team.chatColor + team.names[2] + "" + (team.bedBreaked ? " сломана" : "") + " (очков: " + team.gamePoints + ")&r:";

                  for(PlayerInfo pl1 : team.players) {
                     msg = msg + " " + pl1.username;
                  }

                  U.msg(sender, new String[]{msg});
               }

               if (this.game.getState() == GameState.GAME) {
                  int secs = (int)((System.currentTimeMillis() - this.game.startTime) / 1000L);
                  U.msg(sender, new String[]{"Время: " + secs / 60 + ":" + ParsedTime.numToString(secs % 60, 2) + " мин."});
               }

               U.msg(sender, new String[]{"Зрители (" + this.spectators.getSpectators().size() + "): " + (String)this.spectators.getSpectators().stream().map(CommandSender::getName).collect(Collectors.joining(", "))});
               break;
            case "testconfig":
               AtomicLong errors = new AtomicLong(0L);
               Config.teams.forEach((t) -> {
                  errors.addAndGet(t.bed.stream().filter((loc) -> {
                     if (loc.getX() % (double)1.0F != (double)0.5F && loc.getY() % (double)1.0F != (double)0.5F && loc.getZ() % (double)1.0F != (double)0.5F) {
                        if (loc.getBlock().getType() != Material.BED_BLOCK) {
                           U.msg(sender, new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &f" + loc.getBlock().getType() + " вместо кровати"});
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        U.msg(sender, new String[]{"&cFAIL: " + t.id + " " + new Vec3f(loc) + " &f координаты кровати должны быть без 0.5"});
                        return true;
                     }
                  }).count());
                  errors.addAndGet(t.spawns.stream().filter((loc) -> {
                     Location loc2 = loc.clone();

                     while(loc2.getY() > (double)0.0F) {
                        loc2.add((double)0.0F, (double)-1.0F, (double)0.0F);
                        if (!loc2.getBlock().isEmpty()) {
                           break;
                        }
                     }

                     if (loc2.getY() <= (double)0.0F) {
                        U.msg(sender, new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &fСпавн в воздухе"});
                        return true;
                     } else {
                        return false;
                     }
                  }).count());
                  errors.addAndGet(t.villagers.stream().filter((loc) -> {
                     Location loc2 = loc.clone();

                     while(loc2.getY() > (double)0.0F) {
                        loc2.add((double)0.0F, (double)-1.0F, (double)0.0F);
                        if (!loc2.getBlock().isEmpty()) {
                           break;
                        }
                     }

                     if (loc2.getY() <= (double)0.0F) {
                        U.msg(sender, new String[]{"&cFAIL: " + t.id + " " + new Vec3i(loc) + " &fСпавн жителя в воздухе"});
                        return true;
                     } else {
                        return false;
                     }
                  }).count());
                  errors.addAndGet(this.checkResourceSpawn(sender, t.id, (List)t.bronzeSpawns, 3, "бронзы", Material.HARD_CLAY));
                  errors.addAndGet(this.checkResourceSpawn(sender, t.id, (List)t.ironSpawns, 3, "железа", Material.IRON_BLOCK));
               });
               errors.addAndGet(this.checkResourceSpawn(sender, "", (List)Config.goldSpawns, 3, "золота", Material.GOLD_BLOCK));
               errors.addAndGet(this.checkResourceSpawn(sender, "", (List)Config.watchSpawns, 3, "часиков", Material.REDSTONE_BLOCK));
               if (errors.get() == 0L) {
                  U.msg(sender, new String[]{"&aВсё ок"});
               }
               break;
            case "setstate":
               this.game.setState(GameState.valueOf(args[1].toUpperCase()));
               break;
            case "start":
               this.game.start();
               break;
            case "stop":
               if (this.game.getState() == GameState.STARTING) {
                  this.game.cancelStartTask();
                  if (VimeNetwork.isTournament()) {
                     Texteria2D.removeGroup("bw.lobby", Bukkit.getOnlinePlayers());
                  } else {
                     BTexteria.showPlayersToStart();
                  }
               } else {
                  this.game.end();
               }
               break;
            case "spectoteam":
               Player bukkitPlayer = Bukkit.getPlayerExact(args[1]);
               if (bukkitPlayer == null) {
                  U.msg(sender, new String[]{"Игрок не найден"});
                  return false;
               }

               PlayerInfo player = PlayerInfo.get(bukkitPlayer);
               String color = Character.toUpperCase(args[2].charAt(0)) + args[2].substring(1).toLowerCase();
               BWTeam team = (BWTeam)Config.teams.stream().filter((t) -> Arrays.asList(t.names).contains(color)).findAny().orElse((Object)null);
               if (team == null) {
                  U.msg(sender, new String[]{"Команда не найдена"});
                  return false;
               }

               this.spectators.remove(player.player);
               this.game.join(player, team, true);
               PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player.player, Config.lobby, false);
               this.events.onPlayerRespawn(respawnEvent);
               player.player.teleport(respawnEvent.getRespawnLocation());
               if (TournamentUI.instance != null) {
                  TournamentUI.instance.showUI(TournamentUI.instance.getWatchers());
               }
               break;
            case "etui":
               if (TournamentUI.instance == null) {
                  Bukkit.getPluginManager().registerEvents(new TournamentUI(this), this);
                  Player[] watchers = TournamentUI.instance.getWatchers();
                  TournamentUI.instance.showUI(watchers);
                  Texteria2D.remove("bw.pt.t", watchers);
                  Texteria2D.remove("bw.pt.t.t", watchers);
               }
               break;
            default:
               U.msg(sender, new String[]{"&e=========== &fBedWars &e===========", "&e/bw start&f старт игры", "&e/bw stop&f окончание игры", "&e/bw testconfig&f валидация настроек", "&e/bw setstate <state>&f", "&e/bw stats&f состояние игры", "&e/bw player <игрок>&f инфа об игроке", "&e/bw spectoteam <игрок> <цвет команды>&f перевести игрока из спекторасов в нужную команду", "&e/bw etui&f включает турнирный интерфейс у зрителей"});
         }
      } else if (command.getName().equals("stats")) {
         PlayerInfo player = PlayerInfo.get((Player)sender);
         U.msg(sender, new String[]{"&e ---- &fСтатистика &e----", "&eИгр сыграно: &f" + player.stats.games, "&eПобед: &f" + player.stats.wins, "&eКроватей сломано: &f" + player.stats.bedBreaked, "&eУбийств: &f" + player.stats.kills, "&eСмертей: &f" + player.stats.deaths});
      }

      return true;
   }

   private long checkResourceSpawn(CommandSender sender, String team, List locs, int radius, String name, Material type) {
      return locs.stream().filter((loc) -> this.checkResourceSpawn(sender, team, loc, radius, name, type)).count();
   }

   private boolean checkResourceSpawn(CommandSender sender, String team, Location loc, int radius, String name, Material type) {
      Block middle = loc.getBlock();
      if (Material2.isSolid(middle.getType())) {
         U.msg(sender, new String[]{"&6WARNING: " + team + " " + new Vec3i(loc) + " &fСпавн " + name + " в блоке"});
      }

      if (Math.abs(loc.getX() % (double)1.0F) != (double)0.5F || Math.abs(loc.getZ() % (double)1.0F) != (double)0.5F) {
         U.msg(sender, new String[]{"&6WARNING: " + team + " " + new Vec3i(loc) + " &fСпавн " + name + " без 0.5"});
      }

      int status = 0;

      label46:
      for(int x = -radius; x <= radius; ++x) {
         for(int y = -radius; y <= radius; ++y) {
            for(int z = -radius; z <= radius; ++z) {
               Block relative = middle.getRelative(x, y, z);
               if (relative.getType() == type) {
                  status = 1;
                  break label46;
               }
            }
         }
      }

      if (status == 0) {
         U.msg(sender, new String[]{"&cFAIL: " + team + " " + new Vec3i(loc) + " &fВозле спавна " + name + " нет блока"});
      }

      return status != 1;
   }

   public static BedWars instance() {
      return instance;
   }
}
