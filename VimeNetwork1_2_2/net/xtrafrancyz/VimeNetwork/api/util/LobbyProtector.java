/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.Action
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockFadeEvent
 *  org.bukkit.event.block.BlockFormEvent
 *  org.bukkit.event.block.BlockFromToEvent
 *  org.bukkit.event.block.BlockGrowEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.block.LeavesDecayEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public class LobbyProtector
implements Listener {
    public static final int LOBBY_SIZE = 75;
    private static LobbyProtector instance;
    private Plugin plugin;
    private Location lobby;
    private int size;

    private LobbyProtector(Plugin plugin, Location lobby, int radius) {
        this.plugin = plugin;
        this.lobby = lobby;
        this.size = radius * radius;
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin().equals(this.plugin)) {
            instance = null;
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (event.hasBlock() && event.getPlayer().getGameMode() != GameMode.CREATIVE && this.isNearLobby0((Entity)event.getPlayer())) {
            if (event.getAction() == Action.PHYSICAL) {
                event.setCancelled(true);
                return;
            }
            switch (event.getClickedBlock().getType()) {
                case BED: 
                case BED_BLOCK: 
                case TRAP_DOOR: 
                case WORKBENCH: 
                case CHEST: 
                case ENDER_CHEST: 
                case TRAPPED_CHEST: 
                case LOCKED_CHEST: 
                case FURNACE: 
                case BURNING_FURNACE: 
                case BREWING_STAND: 
                case BEACON: {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    private void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && this.isNearLobby0((Entity)event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    private void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.CREATIVE && this.isNearLobby0((Entity)event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && this.isNearLobby0(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodChance(FoodLevelChangeEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && this.isNearLobby0((Entity)event.getEntity())) {
            event.setFoodLevel(20);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (this.isLobby0(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onBlockGrow(BlockGrowEvent event) {
        if (this.isLobby0(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onBlockFade(BlockFadeEvent event) {
        if (this.isLobby0(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onLeavesDecay(LeavesDecayEvent event) {
        if (this.isLobby0(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onBlockForm(BlockFormEvent event) {
        if (this.isLobby0(event.getBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
    public void onPickup(PlayerPickupItemEvent event) {
        if (this.isNearLobby0((Entity)event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean isNearLobby0(Entity player) {
        if (this.lobby.getWorld() != player.getWorld()) {
            return false;
        }
        return player.getLocation().distanceSquared(this.lobby) < (double)this.size;
    }

    private boolean isLobby0(Location loc) {
        if (this.lobby.getWorld() != loc.getWorld()) {
            return false;
        }
        return loc.distanceSquared(this.lobby) < (double)this.size;
    }

    public static void init(Plugin plugin, Location lobby) {
        LobbyProtector.init(plugin, lobby, 75);
    }

    public static void init(Plugin plugin, Location lobby, int radius) {
        if (instance != null) {
            throw new IllegalStateException("LobbyProtector already inited");
        }
        instance = new LobbyProtector(plugin, lobby, radius);
        Bukkit.getPluginManager().registerEvents((Listener)instance, plugin);
    }

    public static void dispose() {
        instance = null;
    }

    public static boolean isNearLobby(Entity entity) {
        return instance.isNearLobby0(entity);
    }

    public static boolean isLobby(Location loc) {
        return instance.isLobby0(loc);
    }
}

