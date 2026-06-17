/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityCombustEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.EntityTargetLivingEntityEvent
 *  org.bukkit.event.entity.FoodLevelChangeEvent
 *  org.bukkit.event.entity.PotionSplashEvent
 *  org.bukkit.event.player.PlayerDropItemEvent
 *  org.bukkit.event.player.PlayerInteractEntityEvent
 *  org.bukkit.event.player.PlayerInteractEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.event.player.PlayerPickupItemEvent
 *  org.bukkit.event.player.PlayerRespawnEvent
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerUnloadEvent;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Spectators
implements Listener {
    private static Spectators inst = null;
    private final Set<Player> set = new HashSet<Player>();
    private final List<ListenerInfo> listeners = new LinkedList<ListenerInfo>();

    private Spectators(Plugin plugin) {
        this.addListener(plugin, (player, spectator) -> {
            TrailMenu.TrailPlayer trailPlayer;
            if (spectator) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 0)), 3L);
            } else {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.removePotionEffect(PotionEffectType.INVISIBILITY), 3L);
            }
            player.setAllowFlight(spectator);
            player.setFlying(spectator);
            if (TrailMenu.isEnabled() && (trailPlayer = TrailMenu.getPlayer(player.getName())) != null) {
                trailPlayer.visible = !spectator;
            }
        });
        Bukkit.getPluginManager().registerEvents((Listener)this, plugin);
    }

    public List<SpecListener> getListeners() {
        return this.listeners.stream().map(l -> l.listener).collect(Collectors.toList());
    }

    public void removeListener(SpecListener listener) {
        this.listeners.removeIf(info -> info.listener == listener);
    }

    public void addListener(Plugin plugin, SpecListener listener) {
        this.removeListener(listener);
        this.listeners.add(new ListenerInfo(plugin, listener));
    }

    public boolean contains(Player player) {
        return this.set.contains(player);
    }

    public void add(Player player) {
        if (this.contains(player)) {
            return;
        }
        this.set.add(player);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            otherPlayer.hidePlayer(player);
        }
        this.setStats(player, true);
    }

    public void remove(Player player) {
        if (!this.contains(player)) {
            return;
        }
        this.set.remove(player);
        this.setStats(player, false);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            otherPlayer.showPlayer(player);
        }
    }

    public Set<Player> getSpectators() {
        return Collections.unmodifiableSet(this.set);
    }

    private void setStats(Player player, boolean spectator) {
        if (spectator) {
            this.disableCollision(player);
        } else {
            this.enableCollision(player);
        }
        for (ListenerInfo listener : this.listeners) {
            try {
                listener.listener.equip(player, spectator);
            }
            catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, null, e);
            }
        }
    }

    private void disableCollision(Player player) {
        player.spigot().setCollidesWithEntities(false);
    }

    private void enableCollision(Player player) {
        player.spigot().setCollidesWithEntities(true);
    }

    @EventHandler(priority=EventPriority.HIGH)
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        if (this.contains(event.getPlayer())) {
            this.setStats(event.getPlayer(), true);
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        this.set.forEach(arg_0 -> ((Player)player).hidePlayer(arg_0));
    }

    @EventHandler
    private void onPlayerleave(PlayerLeaveEvent event) {
        this.set.remove(event.getPlayer());
        this.setStats(event.getPlayer(), false);
    }

    @EventHandler
    private void onPlayerUnload(PlayerUnloadEvent event) {
        this.enableCollision(event.getPlayer());
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onPickup(PlayerPickupItemEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onBreak(BlockBreakEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onPlace(BlockPlaceEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onDrop(PlayerDropItemEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onInteract(PlayerInteractEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onInteractEntity(PlayerInteractEntityEvent event) {
        if (this.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onDamageBySpectator(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER && this.contains((Player)event.getDamager())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && this.contains((Player)event.getEntity())) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                event.getEntity().teleport(event.getEntity().getLocation().add(0.0, 60.0, 0.0));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    private void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && this.contains((Player)event.getEntity())) {
            ((Player)event.getEntity()).setFoodLevel(20);
            ((Player)event.getEntity()).setSaturation(20.0f);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onCombust(EntityCombustEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && this.contains((Player)event.getEntity())) {
            event.setCancelled(true);
            event.setDuration(1);
        }
    }

    @EventHandler
    private void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget().getType() == EntityType.PLAYER && this.contains((Player)event.getTarget())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity entity : event.getAffectedEntities()) {
            if (entity.getType() != EntityType.PLAYER || !this.contains((Player)entity)) continue;
            event.setIntensity(entity, 0.0);
        }
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        this.listeners.removeIf(info -> info.plugin.equals(event.getPlugin()));
    }

    public static Spectators instance() {
        if (inst == null) {
            inst = new Spectators((Plugin)VNPlugin.instance());
        }
        return inst;
    }

    public static boolean isEnabled() {
        return inst != null;
    }

    private static class ListenerInfo {
        public final Plugin plugin;
        public final SpecListener listener;

        public ListenerInfo(Plugin plugin, SpecListener listener) {
            this.plugin = plugin;
            this.listener = listener;
        }
    }

    public static interface SpecListener {
        public void equip(Player var1, boolean var2);
    }
}

