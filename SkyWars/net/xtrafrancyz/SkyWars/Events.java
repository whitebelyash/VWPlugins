/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  net.xtrafrancyz.Commons.F
 *  net.xtrafrancyz.Commons.player.Rank
 *  net.xtrafrancyz.VimeNetwork.api.Def
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerKillEvent
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent
 *  net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent
 *  net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.Stat
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 *  net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery
 *  net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType
 *  net.xtrafrancyz.VimeNetwork.api.util.E
 *  net.xtrafrancyz.VimeNetwork.api.util.Fireworks
 *  net.xtrafrancyz.VimeNetwork.api.util.Invs
 *  net.xtrafrancyz.VimeNetwork.api.util.Particles
 *  net.xtrafrancyz.VimeNetwork.api.util.Rand
 *  net.xtrafrancyz.VimeNetwork.api.util.Reflect
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Table
 *  net.xtrafrancyz.bukkit.texteria.elements.Table$Column
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.World
 *  org.bukkit.block.Block
 *  org.bukkit.block.Chest
 *  org.bukkit.block.Sign
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftTNTPrimed
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.HumanEntity
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.entity.EntityExplodeEvent
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.event.entity.EntitySpawnEvent
 *  org.bukkit.event.entity.ExplosionPrimeEvent
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.event.inventory.InventoryClickEvent
 *  org.bukkit.event.inventory.InventoryOpenEvent
 *  org.bukkit.event.inventory.InventoryType$SlotType
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerLoginEvent
 *  org.bukkit.event.player.PlayerLoginEvent$Result
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.player.PlayerTeleportEvent
 *  org.bukkit.event.player.PlayerTeleportEvent$TeleportCause
 *  org.bukkit.event.server.ServerListPingEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.PlayerInventory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.SkyWars;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.SkyWars.Config;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.Registry;
import net.xtrafrancyz.SkyWars.SkyWars;
import net.xtrafrancyz.SkyWars.game.GameState;
import net.xtrafrancyz.SkyWars.game.STexteria;
import net.xtrafrancyz.SkyWars.menu.KitSelectMenu;
import net.xtrafrancyz.SkyWars.menu.MicroUpgradesMenu;
import net.xtrafrancyz.SkyWars.menu.SpectatorMenu;
import net.xtrafrancyz.VimeNetwork.api.Def;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerKillEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.event.ServiceItemClickedEvent;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Table;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Events
implements Listener {
    private final SkyWars plugin;
    private final Random rand = new Random();
    private WorldGroup leaderboard = null;
    private TIntObjectMap<Player> creeperSource = new TIntObjectHashMap();

    public Events(SkyWars plugin) {
        this.plugin = plugin;
        if (Config.leaderboardEnabled) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, () -> plugin.repository.getLeaderboard(list -> {
                WorldGroup group = new WorldGroup("leaders");
                group.setLocation(Config.leaderboardLocation.x, Config.leaderboardLocation.y, Config.leaderboardLocation.z);
                group.setRotation(Config.leaderboardRotation.x, Config.leaderboardRotation.y, Config.leaderboardRotation.z);
                group.setScale(Config.leaderboardScale);
                group.setCulling(true);
                group.setHoverable(true);
                group.setHoverRange(12);
                Table table = new Table("0").setTitle("&l\u0422\u0430\u0431\u043b\u0438\u0446\u0430 \u043b\u0438\u0434\u0435\u0440\u043e\u0432 SkyWars").setDrawBack(true).setMaxRows(10).addColumn(new Table.Column("#", 15).setCenter(true).setColor(-5317)).addColumn(new Table.Column("\u041d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430", 70)).addColumn(new Table.Column("\u041f\u043e\u0431\u0435\u0434", 30).setCenter(true).setColor(-16121)).addColumn(new Table.Column("\u0418\u0433\u0440", 30).setCenter(true)).addColumn(new Table.Column("\u0423\u0431\u0438\u0439\u0441\u0442\u0432", 40).setCenter(true));
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
        if (this.plugin.game.getState() == GameState.WAITING && Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
            event.disallow(PlayerLoginEvent.Result.KICK_FULL, "\u0421\u0435\u0440\u0432\u0435\u0440 \u043f\u0435\u0440\u0435\u043f\u043e\u043b\u043d\u0435\u043d");
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
        player.teleport(Config.lobby);
        this.plugin.game.scoreboard.bind(player);
        if (Bukkit.getOnlinePlayers().length >= Config.getMaxPlayers()) {
            this.plugin.game.start();
        } else if (this.plugin.game.getState() == GameState.WAITING) {
            STexteria.showPlayersToStart();
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
            if (Config.islandPlayers == 1) {
                U.msg((CommandSender)player.player, (String[])new String[]{"&6\u041e\u0431\u0440\u0430\u0442\u0438\u0442\u0435 \u0432\u043d\u0438\u043c\u0430\u043d\u0438\u0435! \u0417\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043e \u043e\u0431\u044a\u0435\u0434\u0438\u043d\u044f\u0442\u044c\u0441\u044f \u0441\u043e \u0441\u0432\u043e\u0438\u043c\u0438 \u0434\u0440\u0443\u0437\u044c\u044f\u043c\u0438 \u0438\u043b\u0438 \u0434\u0440\u0443\u0433\u0438\u043c\u0438 \u0438\u0433\u0440\u043e\u043a\u0430\u043c\u0438, \u0434\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0435\u0441\u0442\u044c &cSkyWars Team!"});
            }
        } else {
            if (!event.getNetworkPlayer().has(Rank.PREMIUM)) {
                player.hyperSpectator = true;
                event.getPlayer().kickPlayer("\u0414\u043b\u044f \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0430 \u0438\u0433\u0440 \u043d\u0435\u043e\u0431\u0445\u043e\u0434\u0438\u043c \u0441\u0442\u0430\u0442\u0443\u0441 " + Rank.PREMIUM.getDisplayName());
                return;
            }
            this.plugin.spectators.add(event.getPlayer());
            TrailMenu.getPlayer((String)event.getPlayer().getName()).visible = false;
            STexteria.showPrimaryTopTimer(event.getPlayer());
            player.hyperSpectator = true;
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
    public void onServiceItemClicked(ServiceItemClickedEvent event) {
        if (E.isRightClick((ServiceItemClickedEvent)event)) {
            int typeid = event.getItem().getTypeId();
            if (typeid == Def.ITEM_TEAM_SELECT.getTypeId()) {
                event.getPlayer().openInventory(this.plugin.islandSelectMenu.getInventory());
            } else if (typeid == SpectatorMenu.MENU_ITEM.getTypeId()) {
                event.getPlayer().openInventory(this.plugin.spectatorMenu.getInventory());
            } else if (typeid == Def.ITEM_MICRO_UPGRADES.getTypeId()) {
                new MicroUpgradesMenu(PlayerInfo.get(event.getPlayer())).show(event.getPlayer());
            } else if (typeid == KitSelectMenu.MENU_ITEM.getTypeId()) {
                event.getPlayer().openInventory(new KitSelectMenu(event.getPlayer()).getInventory());
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        if (this.plugin.spectators.contains((Player)event.getWhoClicked())) {
            event.setCancelled(true);
            return;
        }
        if (!event.isShiftClick() && event.getSlotType() == InventoryType.SlotType.ARMOR && this.isDiamondArmor(event.getCursor()) || event.isShiftClick() && (event.getSlotType() == InventoryType.SlotType.CONTAINER || event.getSlotType() == InventoryType.SlotType.QUICKBAR) && this.isDiamondArmor(event.getCurrentItem())) {
            this.checkDiamondSetAchievement((Player)event.getWhoClicked());
        }
    }

    private boolean isDiamondArmor(ItemStack is) {
        if (is == null) {
            return false;
        }
        switch (is.getType()) {
            case DIAMOND_HELMET: 
            case DIAMOND_BOOTS: 
            case DIAMOND_LEGGINGS: 
            case DIAMOND_CHESTPLATE: {
                return true;
            }
        }
        return false;
    }

    private void checkDiamondSetAchievement(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
            PlayerInventory inv = player.getInventory();
            if (inv.getHelmet() != null && inv.getHelmet().getType() == Material.DIAMOND_HELMET && inv.getChestplate() != null && inv.getChestplate().getType() == Material.DIAMOND_CHESTPLATE && inv.getLeggings() != null && inv.getLeggings().getType() == Material.DIAMOND_LEGGINGS && inv.getBoots() != null && inv.getBoots().getType() == Material.DIAMOND_BOOTS) {
                VimeNetwork.getPlayer((String)player.getName()).getAchievements().complete(Achievement.SW_DIAMOND_SET);
            }
        });
    }

    @EventHandler(ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (this.plugin.game.getState() != GameState.GAME) {
            return;
        }
        if (this.tryBreakBlock(event)) {
            ++PlayerInfo.get((Player)event.getPlayer()).stats.blocksBroken;
        }
    }

    private boolean tryBreakBlock(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        Player player = event.getPlayer();
        switch (type) {
            case GOLD_ORE: {
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                if (player != null) {
                    Particles.FLAME.play(loc, 0.4f, 0.4f, 0.4f, 0.0f, 8, new Player[]{player});
                }
                if (this.rand.nextFloat() < 0.1f) {
                    Entity entity = loc.getWorld().spawnEntity(loc, EntityType.CREEPER);
                    this.creeperSource.put(entity.getEntityId(), (Object)player);
                    event.setCancelled(true);
                    event.getBlock().setType(Material.AIR);
                    return true;
                }
                if (this.rand.nextBoolean()) {
                    loc.getWorld().dropItem(loc, (ItemStack)Rand.of((List[])new List[]{Registry.HELMETS, Registry.CHESTPLATES, Registry.LEGGINGS, Registry.BOOTS, Registry.PICKAXES, Registry.AXES, Registry.SWORDS, Registry.SPADES}));
                } else {
                    ArrayList<Supplier> items = new ArrayList<Supplier>();
                    items.addAll(Registry.FOOD);
                    items.addAll(Registry.IRON_ORE_ITEMS);
                    items.addAll(Registry.BLOCKS);
                    items.addAll(Arrays.asList(() -> new ItemStack(Material.ARROW, 16 + this.rand.nextInt(15)), () -> new ItemStack(Material.EGG, 2 + this.rand.nextInt(3)), () -> new ItemStack(Material.TNT, 5 + this.rand.nextInt(11)), () -> new ItemStack(Material.WATER_BUCKET), () -> new ItemStack(Material.LAVA_BUCKET), () -> new ItemStack(Material.GOLDEN_APPLE, 1 + this.rand.nextInt(1)), () -> new ItemStack(Material.ENDER_PEARL), () -> new ItemStack(Material.FISHING_ROD)));
                    Supplier<ItemStack> itemSupplier = () -> {
                        ItemStack is = (ItemStack)((Supplier)Rand.of((List)items)).get();
                        if (is.getType() == Material.ENDER_PEARL) {
                            VimeNetwork.getPlayer((Player)player).getAchievements().complete(Achievement.SW_DIAMOND_FROM_ORE);
                        }
                        return is;
                    };
                    loc.getWorld().dropItem(loc, itemSupplier.get());
                    if (this.rand.nextBoolean()) {
                        loc.getWorld().dropItem(loc, itemSupplier.get());
                    }
                    if (this.rand.nextBoolean()) {
                        loc.getWorld().dropItem(loc, itemSupplier.get());
                    }
                }
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case IRON_ORE: {
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                if (player != null) {
                    Particles.FLAME.play(loc, 0.4f, 0.4f, 0.4f, 0.0f, 8, new Player[]{player});
                }
                LinkedList<Supplier<ItemStack>> list = new LinkedList<Supplier<ItemStack>>(Registry.IRON_ORE_ITEMS);
                loc.getWorld().dropItem(loc, list.remove(this.rand.nextInt(list.size())).get());
                loc.getWorld().dropItem(loc, list.remove(this.rand.nextInt(list.size())).get());
                if (this.rand.nextBoolean()) {
                    loc.getWorld().dropItem(loc, list.get(this.rand.nextInt(list.size())).get());
                }
                list.clear();
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case LAPIS_ORE: {
                if (player == null) {
                    return true;
                }
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                Particles.FLAME.play(loc, 0.4f, 0.4f, 0.4f, 0.0f, 8, new Player[]{player});
                PlayerInfo pi = PlayerInfo.get(player);
                if (pi.blockEffect != null) {
                    player.removePotionEffect(pi.blockEffect);
                    Bukkit.getScheduler().cancelTask(pi.blockEffectTask);
                }
                PotionEffectType effect = (PotionEffectType)Rand.of(Registry.EFFECT_TYPES);
                if (!Objects.equals(pi.kit, "assassin") && effect != PotionEffectType.JUMP && effect != PotionEffectType.SPEED) {
                    int duration = Rand.intRange((int)40, (int)60) * 20;
                    player.addPotionEffect(effect.createEffect(duration, 0));
                    pi.blockEffect = effect;
                    pi.blockEffectTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                        pi.blockEffect = null;
                        pi.blockEffectTask = -1;
                    }, (long)duration);
                }
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case DIAMOND_ORE: {
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                if (player != null) {
                    Particles.FLAME.play(loc, 0.4f, 0.4f, 0.4f, 0.0f, 8, new Player[]{player});
                }
                if (this.rand.nextFloat() < 0.05f) {
                    loc.getWorld().dropItem(loc, new ItemStack(Material.ENDER_PEARL));
                    VimeNetwork.getPlayer((Player)player).getAchievements().complete(Achievement.SW_DIAMOND_FROM_ORE);
                } else if (this.rand.nextFloat() < 0.05f) {
                    loc.getWorld().dropItem(loc, new ItemStack(Material.DIAMOND));
                } else {
                    loc.getWorld().dropItem(loc, ((ItemStack)Rand.of(Registry.DIAMOND_ITEMS)).clone());
                }
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case REDSTONE_ORE: 
            case GLOWING_REDSTONE_ORE: {
                if (player == null) {
                    return true;
                }
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                if (this.rand.nextInt(100) <= PlayerInfo.get((Player)player).upgrades.redstoneHeart) {
                    Particles.HEART.play(loc, 0.3f, 0.3f, 0.3f, 0.0f, 3, new Player[]{player});
                    if (player.getMaxHealth() < 40.0) {
                        player.setMaxHealth(player.getMaxHealth() + 2.0);
                        player.setHealth(player.getHealth() + 2.0);
                    }
                } else {
                    player.setHealth(Math.max(1.0, player.getHealth() - 2.0));
                    player.setMaxHealth(Math.max(10.0, player.getMaxHealth() - 2.0));
                    int nodamageticks = player.getNoDamageTicks();
                    player.damage(0.0);
                    player.setNoDamageTicks(nodamageticks);
                }
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case EMERALD_ORE: {
                if (player == null) {
                    return true;
                }
                Location loc = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
                Particles.FLAME.play(loc, 0.4f, 0.4f, 0.4f, 0.0f, 8, new Player[]{player});
                player.setLevel(player.getLevel() + (this.rand.nextBoolean() ? 1 : 2));
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return true;
            }
            case CHEST: {
                if (Config.protectedChests.contains(event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
                return false;
            }
            case ENDER_CHEST: {
                if (Config.mysteryChest.equals((Object)event.getBlock().getLocation())) {
                    event.setCancelled(true);
                }
                return false;
            }
        }
        return true;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlaceMonitor(BlockPlaceEvent event) {
        ItemStack is;
        PlayerInfo pi = PlayerInfo.get(event.getPlayer());
        ++pi.stats.blocksPlaced;
        if (Rand.nextInt((int)100) < pi.upgrades.builder && (is = event.getItemInHand()) != null && is.getType() != Material.AIR) {
            is = new ItemStack(is);
            is.setAmount(1);
            event.getPlayer().getInventory().addItem(new ItemStack[]{is});
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        event.setLeaveMessage(null);
        PlayerInfo leaver = PlayerInfo.PLAYERS.remove(event.getPlayer().getName());
        if (leaver == null) {
            return;
        }
        if (leaver.island != null) {
            this.plugin.game.leave(leaver);
        }
        this.plugin.repository.savePlayer(leaver);
        if (!leaver.hyperSpectator) {
            U.bcast((String)("[" + (Bukkit.getOnlinePlayers().length - 1) + "/" + Config.getMaxPlayers() + "]&e <= &f\u0418\u0433\u0440\u043e\u043a " + event.getPlayer().getDisplayName() + " \u0432\u044b\u0448\u0435\u043b"));
        }
        switch (this.plugin.game.getState()) {
            case STARTING: {
                this.plugin.game.cancelStartTask();
            }
            case WAITING: {
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, STexteria::showPlayersToStart);
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            if (this.plugin.game.getState() == GameState.STARTING) {
                if (this.plugin.game.substateStartTime + this.plugin.game.substateDuration - System.currentTimeMillis() < 2000L) {
                    event.setCancelled(true);
                    return;
                }
            } else if (this.plugin.game.getState() == GameState.GAME && System.currentTimeMillis() - this.plugin.game.startTime < 2000L) {
                event.setCancelled(true);
                return;
            }
            event.setDamage(999.0);
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (this.plugin.game.getState() == GameState.GAME && event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            PlayerInfo pi = PlayerInfo.get(event.getPlayer());
            if (pi.island == null) {
                return;
            }
            event.setCancelled(true);
            event.getPlayer().teleport(event.getTo());
            event.getPlayer().setFallDistance(0.0f);
            event.getPlayer().damage(5.0 * (double)(1.0f - (float)pi.upgrades.enderman / 100.0f));
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerKillEvent event) {
        PlayerInfo target;
        PlayerInfo killer = PlayerInfo.get(event.getPlayer());
        if (killer.equals(target = PlayerInfo.get(event.getTarget()))) {
            U.bcast((String)("\u0418\u0433\u0440\u043e\u043a &e" + target.player.getDisplayName() + "&f \u0441\u0430\u043c\u043e\u0443\u0431\u0438\u043b\u0441\u044f"));
            return;
        }
        STexteria.onPlayerKill(killer, target);
        U.bcast((String)("\u0418\u0433\u0440\u043e\u043a &e" + target.player.getDisplayName() + "&f \u0443\u0431\u0438\u0442 \u0438\u0433\u0440\u043e\u043a\u043e\u043c &e" + killer.player.getDisplayName()));
        ++killer.kills;
        ++killer.stats.kills;
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)killer.username);
        networkPlayer.addCoins(5);
        networkPlayer.giveExp(5);
        networkPlayer.getGoals().trigger("sw", GoalQuery.of((String)"kill").put("weapon", (Object)event.getPlayer().getItemInHand()).put("target", (Object)target.player).put("streak", (Object)killer.kills).put("mode", (Object)Config.TYPE.getId()));
        if (event.getDamageCause() == EntityDamageEvent.DamageCause.VOID) {
            networkPlayer.getStats().increment(Stat.SW_THROWN_PLAYERS);
        }
        if (killer.stats.kills >= 100) {
            networkPlayer.getAchievements().complete(Achievement.SW_KILL_100);
        }
        if (killer.player.getHealth() <= 2.0) {
            networkPlayer.getAchievements().complete(Achievement.SW_LAST_EFFORT);
        }
        networkPlayer.getTreasures().giveWithMessage(TreasureType.BASIC, 0.0252f);
        killer.player.setLevel(killer.player.getLevel() + 1);
        U.msg((CommandSender)target.player, (String[])new String[]{"\u0412\u0430\u0441 \u0443\u0431\u0438\u043b \u0438\u0433\u0440\u043e\u043a &c" + killer.username + "&f \u0438 \u0443 \u043d\u0435\u0433\u043e \u043e\u0441\u0442\u0430\u043b\u043e\u0441\u044c &c" + F.formatFloat((float)((float)(killer.player.getHealth() / 2.0)), (int)1) + "\u2764"});
        if (killer.island != null) {
            if (killer.upgrades.juggernaut > 0) {
                killer.player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, killer.upgrades.juggernaut * 20, 1));
            }
            if (killer.upgrades.zombie > 0) {
                killer.player.setSaturation(20.0f);
                killer.player.setFoodLevel(20);
            }
            if (killer.upgrades.enchanter > 0) {
                killer.player.giveExpLevels(killer.upgrades.enchanter);
            }
            if (this.rand.nextInt(100) < killer.upgrades.goldenApple) {
                killer.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLDEN_APPLE)});
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        PlayerInfo player = PlayerInfo.get(event.getEntity());
        if (player.island != null && this.plugin.game.getState() == GameState.GAME) {
            this.plugin.game.leave(player);
            this.plugin.spectators.add(player.player);
            ++player.stats.games;
            ++player.stats.deaths;
            player.stats.winStreak = 0;
            NetworkPlayer networkPlayer = VimeNetwork.getPlayer((Player)player.player);
            networkPlayer.addCoins(5 + player.kills * 4);
            networkPlayer.giveExp(5 + player.kills * 4);
            networkPlayer.getGoals().trigger("sw", GoalQuery.of((String)"played").put("mode", (Object)Config.TYPE.getId()));
            TrailMenu.getPlayer((String)player.username).visible = false;
            boolean completedAchievement = false;
            if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.STARVATION) {
                completedAchievement = networkPlayer.getAchievements().complete(Achievement.SW_HUNGER);
            }
            if (!completedAchievement) {
                STexteria.onDefeat(player);
            }
        }
        player.deathLocation = player.player.getLocation();
        if (player.deathLocation.getY() < 5.0) {
            player.deathLocation.setY(Config.respawnY);
        }
        U.respawnPlayer((Player)event.getEntity());
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Config.lobby);
        PlayerInfo player = PlayerInfo.get(event.getPlayer());
        PlayerInventory inv = player.player.getInventory();
        inv.setArmorContents(null);
        player.player.setMaxHealth(20.0);
        player.player.setHealth(20.0);
        player.player.setFoodLevel(20);
        player.player.setSaturation(10.0f);
        this.equip(player);
        if ((this.plugin.game.getState() == GameState.GAME || this.plugin.game.getState() == GameState.ENDING) && player.deathLocation != null) {
            event.setRespawnLocation(player.deathLocation);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.CHICKEN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onExplosionPrime(ExplosionPrimeEvent event) {
        if (event.getEntityType() == EntityType.CREEPER && this.rand.nextBoolean()) {
            event.setCancelled(true);
            Fireworks.playRandom((Location)event.getEntity().getLocation().add(0.5, 0.5, 0.5));
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> Fireworks.playRandom((Location)event.getEntity().getLocation().add(0.5, 1.0, 0.5)), 3L);
            event.getEntity().remove();
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        Player player = null;
        if (event.getEntity() instanceof TNTPrimed) {
            player = (Player)((TNTPrimed)event.getEntity()).getSource();
        } else if (event.getEntity() instanceof Creeper) {
            player = (Player)this.creeperSource.remove(event.getEntity().getEntityId());
        }
        for (Block block : event.blockList()) {
            BlockBreakEvent event0 = new BlockBreakEvent(block, player);
            this.tryBreakBlock(event0);
            if (event0.isCancelled()) continue;
            if (this.rand.nextFloat() < 0.3f) {
                block.breakNaturally();
                continue;
            }
            if (block.getType() == Material.TNT) {
                Location loc = block.getLocation();
                TNTPrimed tnt = (TNTPrimed)loc.getWorld().spawnEntity(loc.add(0.0, 1.0, 0.0), EntityType.PRIMED_TNT);
                tnt.setFuseTicks(Rand.nextInt((int)(tnt.getFuseTicks() / 4)) + tnt.getFuseTicks() / 8);
                if (player != null) {
                    Reflect.set((Object)((CraftTNTPrimed)tnt).getHandle(), (String)"source", (Object)((CraftPlayer)player).getHandle());
                }
            }
            block.setType(Material.AIR);
        }
        event.blockList().clear();
    }

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            PlayerInfo player = PlayerInfo.get((Player)event.getEntity());
            if ((float)player.upgrades.arrow / 100.0f > this.rand.nextFloat()) {
                player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ARROW)});
            }
            if ((float)player.upgrades.blazeArrow / 100.0f > this.rand.nextFloat()) {
                event.getProjectile().setFireTicks(9999);
            }
            ++player.stats.arrowsFired;
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        LivingEntity shooter;
        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (this.plugin.game.getState() != GameState.GAME) {
                event.setCancelled(true);
                return;
            }
            if (event.getEntity().getType() == EntityType.PLAYER) {
                PlayerInfo damager = PlayerInfo.get((Player)event.getDamager());
                PlayerInfo target = PlayerInfo.get((Player)event.getEntity());
                if (damager.island != null && damager.island.equals(target.island)) {
                    event.setCancelled(true);
                    return;
                }
            }
            Location loc = event.getEntity().getLocation();
            float x = (float)loc.getX();
            float y = (float)loc.getY() + 0.6f;
            float z = (float)loc.getZ();
            event.getEntity().getNearbyEntities(20.0, 20.0, 20.0).stream().filter(e -> e instanceof Player).forEach(e -> Particles.playTileCrack((World)e.getWorld(), (int)152, (int)0, (float)x, (float)y, (float)z, (float)0.3f, (float)0.5f, (float)0.3f, (float)0.076f, (int)35, (Player[])new Player[]{(Player)e}));
        }
        if (event.getDamager() instanceof Projectile && (shooter = ((Projectile)event.getDamager()).getShooter()).getType() == EntityType.PLAYER) {
            PlayerInfo damager = PlayerInfo.get((Player)shooter);
            PlayerInfo target = PlayerInfo.get((Player)event.getEntity());
            if (damager.island != null && damager.island.equals(target.island)) {
                event.setCancelled(true);
            } else if (event.getDamager().getType() == EntityType.ARROW) {
                double initialHP = target.player.getHealth();
                Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> STexteria.showDamage(damager.player, initialHP - target.player.getHealth()), 1L);
            }
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        switch (event.getBlock().getType()) {
            case ICE: 
            case SNOW: 
            case SNOW_BLOCK: {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.GOLDEN_APPLE) {
            VimeNetwork.getPlayer((Player)event.getPlayer()).getAchievements().complete(Achievement.SW_GAPPLE);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        PlayerInfo info;
        if (event.getInventory().getHolder() instanceof Chest && (info = PlayerInfo.PLAYERS.get(event.getPlayer().getName())) != null) {
            info.chestOpened = true;
        }
    }

    public void equip(PlayerInfo player) {
        Invs.clear((HumanEntity)player.player);
        PlayerInventory inv = player.player.getInventory();
        if (this.plugin.game.getState() == GameState.STARTING || this.plugin.game.getState() == GameState.WAITING) {
            inv.setItem(0, Def.ITEM_TEAM_SELECT.clone());
            inv.setItem(1, Def.ITEM_MICRO_UPGRADES.clone());
            inv.setItem(2, KitSelectMenu.MENU_ITEM.clone());
            inv.setItem(3, Def.ITEM_TRAILS.clone());
            inv.setItem(7, Def.getSettingsItem((Player)player.player));
            inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
        } else if (player.island == null) {
            inv.setItem(7, Def.getSettingsItem((Player)player.player));
            inv.setItem(8, Def.ITEM_TO_LOBBY.clone());
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (Config.parkourEnabled && event.hasBlock() && event.getClickedBlock().getState() instanceof Sign) {
            if (this.plugin.spectators.contains(event.getPlayer())) {
                return;
            }
            Location loc = event.getClickedBlock().getLocation();
            if (loc.getBlockX() == Config.parkourSign.x && loc.getBlockY() == Config.parkourSign.y && loc.getBlockZ() == Config.parkourSign.z) {
                VimeNetwork.getPlayer((Player)event.getPlayer()).getAchievements().complete(Achievement.SW_LOBBY_PARKOUR);
            }
        }
        if (event.hasItem() && E.isRightClick((PlayerInteractEvent)event) && this.isDiamondArmor(event.getItem())) {
            this.checkDiamondSetAchievement(event.getPlayer());
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.CREEPER) {
            this.creeperSource.remove(event.getEntity().getEntityId());
        }
    }
}

