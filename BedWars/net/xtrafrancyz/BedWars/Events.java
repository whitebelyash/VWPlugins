package net.xtrafrancyz.BedWars;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import net.xtrafrancyz.BedWars.game.BTexteria;
import net.xtrafrancyz.BedWars.game.GameState;
import net.xtrafrancyz.BedWars.menu.HardShopMenu;
import net.xtrafrancyz.BedWars.menu.NormalShopMenu;
import net.xtrafrancyz.BedWars.menu.QuickShopMenu;
import net.xtrafrancyz.BedWars.menu.ShopMenu;
import net.xtrafrancyz.BedWars.menu.SpectatorMenu;
import net.xtrafrancyz.BedWars.menu.SpectatorSettingsMenu;
import net.xtrafrancyz.BedWars.util.CommonUtils;
import net.xtrafrancyz.BedWars.util.PrivateChests;
import net.xtrafrancyz.Commons.T;
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
import net.xtrafrancyz.VimeNetwork.api.util.GameSession;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
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
import org.bukkit.craftbukkit.v1_6_R3.block.CraftChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Villager;
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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Events implements Listener {
   private final BedWars plugin;
   private final PrivateChests privateChests;
   private final ShopMenu shop;
   public final Set userBlocks = new HashSet(128);
   private WorldGroup leaderboard = null;

   public Events(BedWars plugin) {
      this.plugin = plugin;
      this.privateChests = new PrivateChests();
      if (Config.type == Config.Type.NORMAL) {
         this.shop = new NormalShopMenu();
      } else if (Config.type == Config.Type.HARD) {
         this.shop = new HardShopMenu();
      } else {
         if (Config.type != Config.Type.QUICK) {
            throw new RuntimeException("What the fuck with game type?");
         }

         this.shop = new QuickShopMenu();
      }

      if (Config.leaderboardEnabled) {
         Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> plugin.repository.getLeaderboard((list) -> {
               WorldGroup group = new WorldGroup("leaders");
               group.setLocation(Config.leaderboardLocation.x, Config.leaderboardLocation.y, Config.leaderboardLocation.z);
               group.setRotation(Config.leaderboardRotation.x, Config.leaderboardRotation.y, Config.leaderboardRotation.z);
               group.setScale(Config.leaderboardScale);
               group.setCulling(true);
               group.setHoverable(true);
               group.setHoverRange(12);
               Table table = (new Table("0")).setTitle("&lТаблица лидеров BedWars").setDrawBack(true).setMaxRows(10).addColumn((new Table.Column("#", 15)).setCenter(true).setColor(-5317)).addColumn(new Table.Column("Ник игрока", 70)).addColumn((new Table.Column("Побед", 30)).setCenter(true).setColor(-16121)).addColumn((new Table.Column("Игр", 30)).setCenter(true)).addColumn((new Table.Column("Убито", 40)).setCenter(true)).addColumn((new Table.Column("Кроватей сломано", 80)).setCenter(true));
               table.setHoverable(true);
               list.forEach(table::addRow);
               group.add(table);
               this.leaderboard = group;
               Texteria3D.addGroup(this.leaderboard, Bukkit.getOnlinePlayers());
            }), 0L, 72000L);
      }

      for(Player player : Bukkit.getOnlinePlayers()) {
         this.onPlayerJoin(new PlayerJoinEvent(player, (String)null));
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onServerListPing(ServerListPingEvent event) {
      event.setMaxPlayers(Config.getMaxPlayers());
   }

   @EventHandler
   public void onPlayerLogin(PlayerLoginEvent event) {
      if (this.plugin.game.getState() == GameState.STARTING) {
         event.disallow(Result.KICK_OTHER, "Идет отсчет до начала игры");
      } else if (this.plugin.game.getState() == GameState.ENDING) {
         event.disallow(Result.KICK_OTHER, "Игра уже закончилась");
      } else if (this.plugin.game.getState() == GameState.WAITING) {
         if (!VimeNetwork.isTournament() && Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
            event.disallow(Result.KICK_FULL, "Сервер переполнен");
         }

      } else {
         if (this.plugin.game.getState() == GameState.GAME) {
            event.allow();
         }

      }
   }

   @EventHandler
   public void onPlayerJoin(PlayerJoinEvent event) {
      event.setJoinMessage((String)null);
      Player player = event.getPlayer();
      player.setGameMode(GameMode.SURVIVAL);
      player.teleport(Config.lobby);
      this.plugin.game.scoreboard.bind(player);
      if (!VimeNetwork.isTournament() && Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
         this.plugin.game.start();
      } else if (this.plugin.game.getState() == GameState.WAITING) {
         BTexteria.showPlayersToStart();
      }

      if (this.leaderboard != null) {
         Texteria3D.addGroup(this.leaderboard, new Player[]{event.getPlayer()});
      }

   }

   @EventHandler
   public void onPlayerLoaded(PlayerLoadedEvent event) {
      PlayerInfo player = PlayerInfo.get(event.getPlayer());
      this.equip(player);
      if (this.plugin.game.getState() != GameState.GAME) {
         U.bcast("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e => &fИгрок " + event.getPlayer().getDisplayName() + " подключился");
      } else {
         GameSession.Leaver leaver = this.plugin.game.session.getPlayer(event.getPlayer());
         if (leaver != null) {
            if (!((BWTeam)leaver.customData.get("team")).bedBreaked) {
               this.plugin.game.session.restorePlayer(event.getPlayer());
            } else {
               U.msg(player.player, new String[]{"&cВаша кровать была сломана, вы не можете появиться"});
               player.hyperSpectator = true;
               this.plugin.spectators.add(event.getPlayer());
            }
         } else {
            player.hyperSpectator = true;
            if (!VimeNetwork.isTournament() && !event.getNetworkPlayer().getRank().has(event.getPlayer(), Rank.PREMIUM)) {
               event.getPlayer().kickPlayer("Для просмотра игр необходим статус " + Rank.PREMIUM.getDisplayName());
               return;
            }

            this.plugin.spectators.add(event.getPlayer());
         }

         BTexteria.showPrimaryTopTimer(event.getPlayer());
      }

      this.plugin.repository.loadPlayer(player);
   }

   @EventHandler(
      priority = EventPriority.LOW
   )
   public void onPlayerLoadedLow(PlayerLoadedEvent event) {
      if (event.getSwitchData() != null && (this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING)) {
         event.getSwitchData().remove("teleportToPlayer");
      }

   }

   @EventHandler
   public void onPlayerLeave(PlayerLeaveEvent event) {
      event.setLeaveMessage((String)null);
      PlayerInfo leaver = PlayerInfo.get(event.getPlayer());
      if (!leaver.hyperSpectator) {
         String name;
         if (leaver.team != null) {
            name = leaver.team.chatColor + leaver.username + "&r";
         } else {
            name = leaver.player.getDisplayName();
         }

         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> U.bcast("[" + Bukkit.getOnlinePlayers().length + "/" + Config.getMaxPlayers() + "]&e <= &fИгрок " + name + " вышел"));
      }

      if (leaver.team != null) {
         if (this.plugin.game.getState() == GameState.GAME) {
            this.plugin.game.session.savePlayer(event.getPlayer());
         }

         this.plugin.game.leave(leaver);
      }

      this.plugin.repository.savePlayer(leaver);
      PlayerInfo.PLAYERS.remove(event.getPlayer().getName());
      switch (this.plugin.game.getState()) {
         case STARTING:
            this.plugin.game.cancelStartTask();
         case WAITING:
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, BTexteria::showPlayersToStart);
         default:
      }
   }

   @EventHandler
   public void onPlayerRespawn(PlayerRespawnEvent event) {
      event.setRespawnLocation(Config.lobby);
      PlayerInfo player = PlayerInfo.get(event.getPlayer());
      CommonUtils.resetPlayer(player.player);
      player.player.setNoDamageTicks(60);
      Invs.clear(player.player);
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
      if (event.hasItem() && E.isRightClick(event)) {
         int typeid = event.getItem().getTypeId();
         if ((this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING) && typeid == Def.ITEM_TEAM_SELECT.getTypeId()) {
            event.getPlayer().openInventory(this.plugin.teamSelectMenu.getInventory());
            event.setCancelled(true);
         } else if (typeid == SpectatorMenu.MENU_ITEM.getTypeId()) {
            this.plugin.spectatorMenu.show(event.getPlayer());
            event.setCancelled(true);
         } else if (typeid == SpectatorSettingsMenu.MENU_ITEM.getTypeId()) {
            (new SpectatorSettingsMenu(event.getPlayer())).show(event.getPlayer());
            event.setCancelled(true);
         }
      }

      if (Config.parkourEnabled && event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
         if (this.plugin.spectators.contains(event.getPlayer())) {
            return;
         }

         Location loc = event.getClickedBlock().getLocation();
         if (loc.getBlockX() == Config.parkourSign.x && loc.getBlockY() == Config.parkourSign.y && loc.getBlockZ() == Config.parkourSign.z) {
            VimeNetwork.getPlayer(event.getPlayer()).getAchievements().complete(Achievement.BW_LOBBY_PARKOUR);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onChestPlace(BlockPlaceEvent event) {
      this.userBlocks.add(new Vec3i(event.getBlock()));
      if (event.getBlock().getType() == Material.CHEST) {
         this.privateChests.addProtection(event.getBlock().getLocation(), PlayerInfo.get(event.getPlayer()).team);
         Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            CraftChest chest = (CraftChest)event.getBlock().getState();
            Reflect.set(Reflect.get(chest, "chest"), "s", (Object)null);
         });
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onChestBreak(BlockBreakEvent event) {
      if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
         if (!this.userBlocks.remove(new Vec3i(event.getBlock()))) {
            event.setCancelled(true);
         } else {
            switch (event.getBlock().getType()) {
               case CHEST:
                  this.privateChests.removeProtection(event.getBlock().getLocation());
                  break;
               case WEB:
                  event.setCancelled(true);
                  event.getBlock().setType(Material.AIR);
            }

         }
      }
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onBlockInteract(PlayerInteractEvent event) {
      if (event.hasBlock() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
         switch (event.getClickedBlock().getType()) {
            case CHEST:
               if (!this.privateChests.canOpen(PlayerInfo.get(event.getPlayer()), event.getClickedBlock().getLocation())) {
                  event.getPlayer().sendMessage(ChatColor.RED + "Сундук принадлежит другой команде");
                  event.setCancelled(true);
               }
            case WEB:
            default:
               break;
            case ENDER_CHEST:
               this.privateChests.openTeamEnderChest(PlayerInfo.get(event.getPlayer()));
               event.setCancelled(true);
               break;
            case BED_BLOCK:
               if (Config.type != Config.Type.QUICK) {
                  return;
               }

               PlayerInventory inv = event.getPlayer().getInventory();
               ItemStack used = inv.getItemInHand();
               if (used != null && used.getType() == Material.WATCH) {
                  PlayerInfo player = PlayerInfo.get(event.getPlayer());
                  if (player.team.bed.contains(event.getClickedBlock().getLocation())) {
                     BWTeam var10000 = player.team;
                     var10000.quickTTL += 10000L;
                     player.team.updateTimer();
                     var10000 = player.team;
                     var10000.gamePoints = (float)((double)var10000.gamePoints + 0.06);
                     U.msg(event.getPlayer(), new String[]{T.success("BedWars", "Вы продлили время жизни своей кровати на &a10 сек.")});
                     used.setAmount(used.getAmount() - 1);
                     inv.setItem(inv.getHeldItemSlot(), used.getAmount() == 0 ? null : used);
                     if (player.clockUses++ == 29) {
                        VimeNetwork.getPlayer(player.username).getAchievements().complete(Achievement.BW_CHSVOY);
                     }
                  } else {
                     U.msg(event.getPlayer(), new String[]{T.error("BedWars", "Вы не можете продлить время жизни чужой кровати")});
                  }
               }

               event.setCancelled(true);
               break;
            case BREWING_STAND:
            case ENCHANTMENT_TABLE:
            case ANVIL:
               event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
      if (event.getRightClicked() instanceof Villager) {
         event.getPlayer().openInventory(this.shop.getInventory());
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityExplode(EntityExplodeEvent event) {
      LinkedList<Block> toExplode = new LinkedList();

      for(Block block : event.blockList()) {
         Vec3i loc = new Vec3i(block);
         if (this.userBlocks.remove(loc)) {
            if (this.plugin.trapUsable.traps.remove(loc) != null) {
               block.setType(Material.AIR);
            } else {
               toExplode.add(block);
            }
         }
      }

      event.blockList().clear();
      event.blockList().addAll(toExplode);
   }

   @EventHandler
   public void onPlayerKill(PlayerKillEvent event) {
      PlayerInfo player = PlayerInfo.get(event.getPlayer());
      PlayerInfo target = PlayerInfo.get(event.getTarget());
      if (player.equals(target)) {
         U.bcast("Игрок &e" + target.player.getDisplayName() + "&f самоубился");
      } else {
         BTexteria.onPlayerKill(player, target.player.getDisplayName());
         U.bcast("Игрок " + target.team.chatColor + target.username + "&f убит игроком " + (player.team == null ? "" : player.team.chatColor) + player.username);
         if (target.lastDeath > System.currentTimeMillis() - 15000L) {
            player.player.sendMessage(ChatColor.RED + "Этот игрок был убит менее 15 секунд назад. Убийство не засчитано.");
         } else {
            ++player.stats.kills;
            if (player.team != null) {
               BWTeam var5 = player.team;
               var5.gamePoints = (float)((double)var5.gamePoints + 0.1);
            }

            NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.username);
            networkPlayer.addCoins(4);
            networkPlayer.giveExp(4);
            networkPlayer.getGoals().trigger("bw", GoalQuery.of("kill").put("weapon", event.getPlayer().getItemInHand()).put("target", target.player));
            if (event.getDamageCause() == DamageCause.VOID && player.thrownPlayers++ == 19) {
               networkPlayer.getAchievements().complete(Achievement.BW_KING_OF_THE_HILL);
            }
         }

         player.player.setLevel(player.player.getLevel() + 1);
      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      event.setDeathMessage((String)null);
      PlayerInfo player = PlayerInfo.get(event.getEntity());
      ++player.stats.deaths;
      player.lastDeath = System.currentTimeMillis();
      Location loc = player.player.getLocation();
      double y = loc.getY();
      player.deathLocation = loc;
      if (y < (double)5.0F) {
         player.deathLocation.setY(Config.respawnY);
      }

      if (player.team != null) {
         Beam beam = new Beam((String)null, -16777216 + player.team.color.asRGB());
         beam.setLocation((float)loc.getX(), (float)y, (float)loc.getZ());
         beam.setDuration(4000L);
         beam.setRenderDistance(128);
         beam.animation.setBoth((new Animation3D.Params()).setScale(-1.3F));
         Texteria3D.addGroup(beam, Bukkit.getOnlinePlayers());
      }

      if (player.team != null && player.team.bedBreaked) {
         this.plugin.game.leave(player);
         this.plugin.spectators.add(player.player);
         ++player.stats.games;
         NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.player);
         networkPlayer.addCoins(20);
         networkPlayer.giveExp(20);
         networkPlayer.getGoals().trigger("bw", GoalQuery.of("played"));
         this.plugin.game.session.removePlayer(networkPlayer.getId());
         TrailMenu.getPlayer(player.username).visible = false;
         BTexteria.onLoose(player);
      }

      U.respawnPlayer(event.getEntity());
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onBedBreak(BlockBreakEvent event) {
      if (this.plugin.game.getState() == GameState.GAME) {
         if (event.getBlock().getType() == Material.BED_BLOCK) {
            Location loc = event.getBlock().getLocation();

            for(BWTeam team : Config.teams) {
               if (team.bed.contains(loc)) {
                  PlayerInfo player = PlayerInfo.get(event.getPlayer());
                  if (team.bedBreaked || player.team == null || team.equals(player.team)) {
                     return;
                  }

                  event.setCancelled(false);
                  U.bcast("&fИгрок " + player.team.chatColor + player.player.getName() + "&f разрушил " + team.chatColor + team.names[1] + " кровать");
                  this.privateChests.removeProtection(team);
                  team.bedBreaked = true;
                  team.updateRecord();
                  if (Config.type == Config.Type.QUICK) {
                     if (!player.team.bedBreaked) {
                        BWTeam var10000 = player.team;
                        var10000.quickTTL += 120000L;
                     }

                     team.updateTimer();
                  }

                  ++player.stats.bedBreaked;
                  int alive = (int)Config.teams.stream().filter((t) -> !t.bedBreaked).count();
                  BWTeam var15 = player.team;
                  var15.gamePoints += (float)(2 + Math.min(2, alive));
                  NetworkPlayer networkPlayer = VimeNetwork.getPlayer(player.player);
                  networkPlayer.addCoins(20);
                  networkPlayer.giveExp(20);
                  networkPlayer.getGoals().trigger("bw", GoalQuery.of("bedbreak"));
                  boolean achievementCompleted = false;
                  if (player.stats.bedBreaked >= 1) {
                     achievementCompleted = networkPlayer.getAchievements().complete(Achievement.BW_BREAK_BED_1);
                  }

                  if (player.stats.bedBreaked >= 10) {
                     achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.BW_BREAK_BED_10);
                  }

                  if (player.stats.bedBreaked >= 100) {
                     achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.BW_BREAK_BED_100);
                  }

                  if (player.stats.bedBreaked >= 500) {
                     achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.BW_BREAK_BED_500);
                  }

                  long time = System.currentTimeMillis();
                  int recentlyBedBreaks = 1;

                  for(long entry : player.bedBreakTimes) {
                     if (time - entry < 240000L) {
                        ++recentlyBedBreaks;
                     }
                  }

                  player.bedBreakTimes.add(time);
                  if (recentlyBedBreaks >= 3) {
                     achievementCompleted |= networkPlayer.getAchievements().complete(Achievement.BW_RUSHER);
                  }

                  if (achievementCompleted) {
                     BTexteria.bedBreakMsgToTeam(team, player);
                  } else {
                     BTexteria.onBedBreak(team, player);
                  }
                  break;
               }
            }
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onEntityDamage(EntityDamageEvent event) {
      if (event.getEntity().getType() == EntityType.PLAYER) {
         if (event.getCause() == DamageCause.VOID) {
            event.setDamage((double)300.0F);
            return;
         }

         if (this.plugin.game.getState() != GameState.GAME) {
            event.setCancelled(true);
         }
      } else if (event.getEntity().getType() == EntityType.VILLAGER) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGH
   )
   public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
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
         Particles.playTileCrack(152, 0, (float)loc.getX(), (float)loc.getY() + 0.6F, (float)loc.getZ(), 0.3F, 0.5F, 0.3F, 0.076F, 35, new Player[0]);
      }

      if (event.getDamager() instanceof Projectile) {
         LivingEntity shooter = ((Projectile)event.getDamager()).getShooter();
         if (shooter.getType() == EntityType.PLAYER) {
            PlayerInfo damager = PlayerInfo.get((Player)shooter);
            PlayerInfo target = PlayerInfo.get((Player)event.getEntity());
            if (damager.team != null && damager.team.equals(target.team)) {
               event.setCancelled(true);
            }
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

   @EventHandler(
      ignoreCancelled = true
   )
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
            boolean isAdmin = VimeNetwork.hasRank(player.player, Rank.CHIEF, false);
            if (this.plugin.game.getState() == GameState.GAME && !isAdmin) {
               broadcast = false;
            }

            if (broadcast) {
               if (isAdmin) {
                  U.bcast(T.system(player.player.getDisplayName(), message));
               } else {
                  Bukkit.broadcastMessage(U.colored("&7(Всем) " + name + "&r&7:&f ") + message);
               }
            } else {
               message = U.colored("&7(Зрители) " + player.player.getDisplayName() + "&r&7:&f ") + message;

               for(PlayerInfo pi : PlayerInfo.PLAYERS.values()) {
                  if (pi.team == null) {
                     pi.player.sendMessage(message);
                  }
               }

               Bukkit.getLogger().info(message);
            }

            return;
         }

         broadcast = true;
      }

      if (broadcast) {
         Bukkit.broadcastMessage(U.colored("&7(Всем) " + name + "&r&7:&f ") + message);
      } else {
         message = U.colored(player.team.chatColor + "(Команда) " + name + "&r&7:&f ") + message;

         for(PlayerInfo pl : player.team.players) {
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
      Invs.clear(player.player);
      if (this.plugin.game.getState() != GameState.STARTING && this.plugin.game.getState() != GameState.WAITING) {
         if (player.team == null) {
            inv.setItem(7, Def.getSettingsItem(player.player));
            inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
         }
      } else {
         inv.setItem(0, Def.ITEM_TEAM_SELECT.clone());
         inv.setItem(1, Def.ITEM_TRAILS.clone());
         inv.setItem(7, Def.getSettingsItem(player.player));
         inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
      }

   }
}
