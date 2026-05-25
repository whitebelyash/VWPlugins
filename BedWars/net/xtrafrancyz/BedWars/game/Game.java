package net.xtrafrancyz.BedWars.game;

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
import java.util.stream.Collectors;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.BedWars;
import net.xtrafrancyz.BedWars.Config;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.BedWars.TournamentUI;
import net.xtrafrancyz.BedWars.game.entity.TraderEntity;
import net.xtrafrancyz.BedWars.util.CommonUtils;
import net.xtrafrancyz.GameReloader.GameReloader;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.Lobby.State;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.score.SideScoreboard;
import net.xtrafrancyz.VimeNetwork.api.util.GameSession;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

public class Game {
   private static final int START_COUNTDOWN = VimeNetwork.isDev() ? 5000 : 20000;
   private final BedWars plugin;
   private GameState state;
   public SideScoreboard scoreboard;
   public int startTask;
   public int chunkLoadTask;
   public long startTime;
   public long endTime;
   public Set gameTasks;
   public GameSession session;

   public Game(BedWars plugin) {
      this.state = GameState.WAITING;
      this.startTask = -1;
      this.chunkLoadTask = -1;
      this.startTime = 0L;
      this.endTime = 0L;
      this.gameTasks = new HashSet();
      this.plugin = plugin;
      this.scoreboard = new SideScoreboard(U.colored("&b&lBedWars"));
      this.scoreboard.create("Карта: " + ChatColor.BOLD + Config.mapName, 99).update();
      this.scoreboard.create(ChatColor.RED + "", 98).update();

      for(BWTeam team : Config.teams) {
         team.createRecord(this.scoreboard);
      }

      this.scoreboard.create(ChatColor.BLACK + "", -1).update();
      this.scoreboard.create(U.colored("&a&lVimeWorld.ru"), -2).update();
      this.setState(GameState.WAITING);
      this.session = new GameSession(plugin);
      this.session.setPlayerNameTransformer((player) -> PlayerInfo.get(player).team.chatColor + player.getName());
      this.session.setPlayerSaver((player, leaver) -> {
         PlayerInfo info = PlayerInfo.get(player);
         leaver.customData.put("team", info.team);
      });
      this.session.setPlayerRestorer((player, leaver) -> {
         PlayerInfo info = PlayerInfo.get(player);
         this.join(info, (BWTeam)leaver.customData.get("team"), true);
         PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, Config.lobby, false);
         plugin.events.onPlayerRespawn(respawnEvent);
         if (leaver.location == null) {
            leaver.location = respawnEvent.getRespawnLocation();
         }

         U.bcast("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e => &fИгрок " + info.team.chatColor + player.getName() + "&r вернулся в игру");
         if (leaver.killed) {
            BTexteria.showCustomMessage(player, "Пока вас не было, вас убили", -1, 4000L);
         }

         if (TournamentUI.instance != null) {
            TournamentUI.instance.showUI(TournamentUI.instance.getWatchers());
         }

      });
      this.session.setLeaverKillListener((killer, leaver) -> {
         BWTeam team = (BWTeam)leaver.customData.get("team");
         String name = team.chatColor + leaver.username;
         PlayerInfo damager = PlayerInfo.get(killer);
         BTexteria.onPlayerKill(damager, name);
         U.bcast("&c&lАФК &fИгрок " + name + "&f убит игроком " + (damager.team == null ? "" : damager.team.chatColor) + damager.username);
      });
      this.session.setDamageFilter((player, leaver) -> PlayerInfo.get(player).team != leaver.customData.get("team"));
   }

   public void join(PlayerInfo player, BWTeam team, boolean force) {
      if (force || this.getState() != GameState.GAME) {
         if (player.team != null) {
            this.leave(player);
         }

         player.team = team;
         team.players.add(player);
         U.msg(player.player, new String[]{"&fВы играете за " + team.chatColor + team.names[1] + " команду"});
         VimeNetwork.getPlayer(player.username).setTag(team.chatColor + player.username);
         String name = team.chatColor + player.username;
         if (name.length() > 16) {
            name = name.substring(0, 16);
         }

         player.player.setPlayerListName(name);
         team.updateRecord();
         this.plugin.teamSelectMenu.update(team);
         this.plugin.spectatorMenu.update();
         if (this.state == GameState.GAME) {
            this.plugin.targetCompass.addUpdatePlayer(player.player);
         }

      }
   }

   public void leave(PlayerInfo player) {
      BWTeam team = player.team;
      player.team = null;
      team.players.remove(player);
      VimeNetwork.getPlayer(player.username).removeTag();

      try {
         player.player.setPlayerListName(player.username);
      } catch (Exception var4) {
      }

      team.updateRecord();
      this.plugin.teamSelectMenu.update(team);
      this.plugin.spectatorMenu.update();
      this.plugin.targetCompass.removeUpdatePlayer(player.player);
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
      if (this.getState() == GameState.WAITING) {
         this.setState(GameState.STARTING);
         BTexteria.showTimer("Отсчет до начала игры", (long)START_COUNTDOWN);
         this.chunkLoadTask = Bukkit.getScheduler().scheduleSyncDelayedTask(BedWars.instance(), () -> {
            for(BWTeam team : Config.teams) {
               team.getSpawnLocation().getChunk().load();
            }

         }, (long)(START_COUNTDOWN / 50 - 60));
         this.startTask = Bukkit.getScheduler().scheduleSyncDelayedTask(BedWars.instance(), () -> {
            this.startTime = System.currentTimeMillis();
            if (Config.type == Config.Type.HARD) {
               this.endTime = this.startTime + 3600000L;
            } else {
               this.endTime = this.startTime + 2700000L;
            }

            if (!VimeNetwork.isTournament()) {
               this.spreadInTeams();
            }

            for(PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
               if (pi.team == null) {
                  if (VimeNetwork.isTournament()) {
                     U.msg(pi.player, new String[]{"&aВы стали зрителем"});
                     pi.hyperSpectator = true;
                     Invs.clear(pi.player);
                     this.plugin.spectators.add(pi.player);
                  } else {
                     U.msg(pi.player, new String[]{"&cПроизошла внутренняя ошибка при распределении команд [" + VimeNetwork.lobby().getServerId() + "]"});
                     VimeNetwork.toLobby(new Player[]{pi.player});
                  }
               } else {
                  CommonUtils.resetPlayer(pi.player);
                  pi.player.closeInventory();
                  pi.player.setItemOnCursor((ItemStack)null);
                  Invs.clear(pi.player);
                  pi.player.teleport(this.spawnPlayer(pi));
                  pi.player.setFallDistance(0.0F);
                  pi.player.setSaturation(10.0F);
                  pi.player.setFoodLevel(20);
                  pi.player.setMaxHealth((double)20.0F);
                  pi.player.setHealth((double)20.0F);
                  pi.player.setFireTicks(0);
                  pi.player.setGameMode(GameMode.SURVIVAL);
                  this.plugin.targetCompass.addUpdatePlayer(pi.player);
               }
            }

            this.session.create((Collection)PlayerInfo.PLAYERS.values().stream().filter((p) -> p.team != null).map((p) -> p.player).collect(Collectors.toList()));

            for(BWTeam team : Config.teams) {
               if (team.players.isEmpty()) {
                  team.bedBreaked = true;
                  team.record.remove();
                  team.updateTimer();

                  for(Location loc : team.bed) {
                     loc.getBlock().setType(Material.AIR);
                  }
               }
            }

            this.setState(GameState.GAME);
            this.plugin.getLogger().info("Game started");
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), () -> Config.teams.stream().filter((team) -> !team.bedBreaked || !team.players.isEmpty()).forEach((team) -> this.spawnItem(team.bronzeSpawns, Config.BRONZE)), (long)Config.bronzeFrequency, (long)Config.bronzeFrequency));
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), () -> Config.teams.stream().filter((team) -> !team.bedBreaked || !team.players.isEmpty()).forEach((team) -> this.spawnItem(team.ironSpawns, Config.IRON)), (long)Config.ironFrequency, (long)Config.ironFrequency));
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), () -> Config.goldSpawns.forEach((loc) -> this.spawnItem(loc, Config.GOLD)), (long)Config.goldFrequency, (long)Config.goldFrequency));
            if (Config.type == Config.Type.QUICK) {
               this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), () -> this.spawnItem(Config.watchSpawns, Config.WATCH), (long)Config.watchFrequency, (long)Config.watchFrequency));
            }

            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), () -> {
               if (Config.type == Config.Type.QUICK) {
                  long time = System.currentTimeMillis();

                  for(BWTeam team : Config.teams) {
                     if (team.quickTTL <= time && !team.bedBreaked) {
                        team.bedBreaked = true;
                        ((Location)team.bed.get(0)).getBlock().setType(Material.AIR);
                        U.bcast(team.chatColor + team.names[1] + " команду &fпостигла неудача. У них закончилось время...");
                        BTexteria.bedBreakMsgToTeam(team, (PlayerInfo)null);
                     }

                     team.updateRecord();
                  }
               } else {
                  long remaining = this.endTime - System.currentTimeMillis();
                  if (remaining < 0L) {
                     this.end();
                  }
               }

            }, 0L, 20L));
            BTexteria.showPrimaryTopTimer("До конца игры: &e{M}:{SS}", this.startTime, this.endTime - this.startTime);
            this.gameTasks.add(Bukkit.getScheduler().scheduleSyncRepeatingTask(BedWars.instance(), new MapBorder(), 100L, 50L));

            for(BWTeam team : Config.teams) {
               for(Location loc : team.villagers) {
                  for(Entity entity : Config.world.getChunkAt(loc).getEntities()) {
                     if (entity.getType() != EntityType.PLAYER) {
                        entity.remove();
                     }
                  }
               }

               if (Config.type == Config.Type.QUICK) {
                  team.quickTTL = this.startTime + 300000L;
                  team.updateTimer();
               }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
               for(BWTeam team : Config.teams) {
                  for(Location loc : team.villagers) {
                     NMSEntityUtils.spawn(new TraderEntity(NMSEntityUtils.getNMSWorld(Config.world)), loc);
                  }
               }

            }, 30L);
         }, (long)(START_COUNTDOWN / 50));
      }
   }

   private void spreadInTeams() {
      List<List<PlayerInfo>> freeParties = new ArrayList();

      label172:
      for(PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
         NetworkPlayer networkPlayer = VimeNetwork.getPlayer(pi.username);
         if (networkPlayer.isInParty()) {
            for(List party : freeParties) {
               for(PlayerInfo pp : party) {
                  if (pp == pi) {
                     continue label172;
                  }
               }
            }

            List<BWTeam> teams = new ArrayList();
            List<PlayerInfo> party = new LinkedList();
            party.add(pi);

            for(String username : networkPlayer.getParty().getPlayers()) {
               PlayerInfo ppi = (PlayerInfo)PlayerInfo.PLAYERS.get(username);
               if (ppi != null && ppi != pi) {
                  if (ppi.team == null) {
                     party.add(ppi);
                  } else if (ppi.team.players.size() < Config.teamPlayers) {
                     teams.add(ppi.team);
                  }
               }
            }

            if (!teams.isEmpty()) {
               Map<BWTeam, Long> map = (Map)teams.stream().collect(Collectors.groupingBy((t) -> t, Collectors.counting()));
               long max = 0L;
               BWTeam bestTeam = null;

               for(Map.Entry entry : map.entrySet()) {
                  if (max < (Long)entry.getValue()) {
                     max = (Long)entry.getValue();
                     bestTeam = (BWTeam)entry.getKey();
                  }
               }

               if (bestTeam != null) {
                  this.join(pi, bestTeam, false);
                  continue;
               }
            }

            if (party.size() > 1) {
               freeParties.add(party);
            }
         }
      }

      List<BWTeam> freeTeams = new ArrayList();

      for(BWTeam team : Config.teams) {
         if (Config.teamPlayers - team.players.size() > 1) {
            freeTeams.add(team);
         }
      }

      freeParties.sort(Collections.reverseOrder(Comparator.comparingInt(List::size)));
      freeTeams.sort(Collections.reverseOrder(Comparator.comparingInt((t) -> t.players.size())));

      while(!freeParties.isEmpty() && !freeTeams.isEmpty()) {
         List<PlayerInfo> party = (List)freeParties.get(0);

         int teamIndex;
         for(teamIndex = 0; teamIndex < freeTeams.size(); ++teamIndex) {
            int slots = Config.teamPlayers - ((BWTeam)freeTeams.get(teamIndex)).players.size();
            if (slots == party.size()) {
               break;
            }

            if (slots < party.size()) {
               if (teamIndex > 0) {
                  --teamIndex;
               }
               break;
            }
         }

         if (teamIndex >= freeTeams.size()) {
            teamIndex = freeTeams.size() - 1;
         }

         BWTeam team = (BWTeam)freeTeams.get(teamIndex);
         Iterator<PlayerInfo> it = party.iterator();

         while(it.hasNext() && team.players.size() < Config.teamPlayers) {
            this.join((PlayerInfo)it.next(), team, false);
            it.remove();
         }

         if (Config.teamPlayers - team.players.size() < 2) {
            freeTeams.remove(teamIndex);
         } else {
            freeTeams.sort(Collections.reverseOrder(Comparator.comparingInt((t) -> t.players.size())));
         }

         if (party.size() < 2) {
            freeParties.remove(0);
         } else {
            freeParties.sort(Collections.reverseOrder(Comparator.comparingInt(List::size)));
         }
      }

      for(PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
         if (pi.team == null) {
            for(BWTeam team : Config.teams) {
               if (team.players.size() < Config.teamPlayers) {
                  this.join(pi, team, false);
                  break;
               }
            }
         }
      }

   }

   private int getAliveTeams() {
      int alive = 0;

      for(BWTeam team : Config.teams) {
         if (team.players.size() > 0) {
            ++alive;
         }
      }

      return alive;
   }

   public void checkEnd() {
      if (this.getAliveTeams() <= 1) {
         this.end();
      }

   }

   public void end() {
      if (this.getState() == GameState.GAME) {
         this.setState(GameState.ENDING);
         VimeNetwork.metrics().add(Config.type.getId() + ".games");
         Set var10000 = this.gameTasks;
         BukkitScheduler var10001 = Bukkit.getScheduler();
         var10000.forEach(var10001::cancelTask);
         BWTeam winners = null;
         if (this.getAliveTeams() == 1) {
            for(BWTeam team : Config.teams) {
               if (team.players.size() > 0) {
                  winners = team;

                  for(PlayerInfo player : team.players) {
                     ++player.stats.games;
                  }
                  break;
               }
            }
         } else {
            for(BWTeam team : Config.teams) {
               if (!team.players.isEmpty()) {
                  if (!team.bedBreaked) {
                     team.gamePoints += 2.0F;
                  }

                  team.gamePoints += (float)team.players.size() * 0.5F;
               }
            }

            winners = (BWTeam)Config.teams.stream().filter((t) -> !t.players.isEmpty()).sorted((t1, t2) -> {
               if (t1.gamePoints < t2.gamePoints) {
                  return 1;
               } else {
                  return t1.gamePoints > t2.gamePoints ? -1 : 0;
               }
            }).findFirst().orElse((Object)null);
         }

         U.bcast("&7####################################");
         if (winners == null) {
            U.bcast("&7# &fНичья");

            for(BWTeam team : Config.teams) {
               for(PlayerInfo player : team.players) {
                  NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.player);
                  networkPlayer.addCoins(10);
                  networkPlayer.giveExp(10);
                  networkPlayer.getGoals().trigger("bw", GoalQuery.of("played"));
               }
            }
         } else {
            U.bcast("&7# &fПобедила " + winners.chatColor + winners.names[0] + " команда&f!");

            for(PlayerInfo winner : winners.players) {
               U.bcast("&7#     " + winners.chatColor + winner.player.getDisplayName());
               ++winner.stats.wins;
               NetworkPlayer networkPlayer = VimeNetwork.getPlayer(winner.player);
               networkPlayer.addCoins(30);
               networkPlayer.giveExp(30);
               networkPlayer.getGoals().trigger("bw", GoalQuery.of("played"));
               networkPlayer.getGoals().trigger("bw", GoalQuery.of("win"));
               networkPlayer.getTreasures().giveWithMessage(TreasureType.BASIC, 0.11F);
               networkPlayer.getTreasures().giveWithMessage(TreasureType.ANCIENT, 0.01F);
            }
         }

         U.bcast("&7####################################");
         BTexteria.onGameEnd(winners);
         BTexteria.showTimer("Отсчет до конца игры", 10000L);
         boolean sessionExists = this.session.endSession();
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            if (sessionExists) {
               GameReloader.reload(this.plugin);
            } else {
               this.plugin.savePlayers();
               VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
               VimeNetwork.lobby().shutdown();
               Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, Bukkit::shutdown, 80L);
            }

         }, 200L);
      }
   }

   public Location spawnPlayer(PlayerInfo player) {
      if (Config.type == Config.Type.HARD) {
         PlayerInventory inv = player.player.getInventory();
         inv.setHelmet(CommonUtils.startLore(CommonUtils.paint(new ItemStack(Material.LEATHER_HELMET), player.team.color)));
         inv.setChestplate(CommonUtils.startLore(CommonUtils.paint(new ItemStack(Material.LEATHER_CHESTPLATE), player.team.color)));
         inv.setLeggings(CommonUtils.startLore(CommonUtils.paint(new ItemStack(Material.LEATHER_LEGGINGS), player.team.color)));
         inv.setBoots(CommonUtils.startLore(CommonUtils.paint(new ItemStack(Material.LEATHER_BOOTS), player.team.color)));
         inv.setItem(0, CommonUtils.startLore(new ItemStack(Material.STONE_SWORD)));
      }

      return player.team.getSpawnLocation();
   }

   private void spawnItem(List locs, ItemStack is) {
      this.spawnItem((Location)Rand.of(locs), is);
   }

   private void spawnItem(Location loc, ItemStack is) {
      Config.world.dropItem(loc, is);
   }

   public void setState(GameState state) {
      this.state = state;
      List<String> menuText = new ArrayList(5);
      menuText.add("&fКарта: &e" + Config.mapName);
      menuText.add("&fФормат: &e" + Config.teamPlayers + "x" + Config.teams.size());
      menuText.add("");
      if (state == GameState.WAITING) {
         menuText.add("&aНабор игроков");
         VimeNetwork.lobby().setConnectableState(State.ALLOW_ALL);
      } else if (state == GameState.STARTING) {
         menuText.add("&eНачало игры");
         VimeNetwork.lobby().setConnectableState(State.DENY_ALL);
      } else if (state == GameState.GAME) {
         menuText.add("%GameStarted-" + this.startTime / 1000L);
         VimeNetwork.lobby().setConnectableState(State.ALLOW_SPECTATORS);
      } else if (state == GameState.ENDING) {
         menuText.add("&6Окончание игры");
         VimeNetwork.lobby().setConnectableState(State.DENY_ALL);
      }

      VimeNetwork.lobby().setMenuText(menuText);
   }

   public GameState getState() {
      return this.state;
   }
}
