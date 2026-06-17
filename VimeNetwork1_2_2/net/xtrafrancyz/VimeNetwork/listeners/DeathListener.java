/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.LivingEntity
 *  org.bukkit.entity.Player
 *  org.bukkit.entity.Projectile
 *  org.bukkit.entity.TNTPrimed
 *  org.bukkit.entity.Tameable
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDamageEvent
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.entity.PlayerDeathEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.listeners;

import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Features;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerKillEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

public class DeathListener
implements Listener {
    private final VNPlugin plugin;

    public DeathListener(VNPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            VPlayer player = VPlayer.get((Player)event.getEntity());
            Player damager = null;
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent)event;
                player.lastDamagerEntity = event0.getDamager();
                switch (event0.getDamager().getType()) {
                    case PLAYER: {
                        damager = (Player)event0.getDamager();
                        break;
                    }
                    case PRIMED_TNT: {
                        Entity entity = ((TNTPrimed)event0.getDamager()).getSource();
                        if (!(entity instanceof Player)) break;
                        damager = (Player)entity;
                    }
                }
                if (damager == null) {
                    AnimalTamer tamer;
                    if (event0.getDamager() instanceof Projectile) {
                        LivingEntity shooter = ((Projectile)event0.getDamager()).getShooter();
                        if (shooter instanceof Player) {
                            damager = (Player)shooter;
                        }
                    } else if (event0.getDamager() instanceof Tameable && (tamer = ((Tameable)event0.getDamager()).getOwner()) instanceof Player) {
                        damager = (Player)tamer;
                    }
                }
            } else {
                player.lastDamagerEntity = null;
            }
            if (damager != null) {
                player.lastDamageFromPlayer = System.currentTimeMillis();
                player.lastDamager = damager;
                Bukkit.getScheduler().cancelTask(player.lastDamagerPurgeTask);
                player.lastDamagerPurgeTask = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                    player.lastDamager = null;
                    player.lastDamagerEntity = null;
                    player.lastDamagerPurgeTask = -1;
                }, 200L);
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player target = event.getEntity();
        VPlayer player = VPlayer.get(target);
        long time = System.currentTimeMillis();
        if (player.lastDeath > time - 1000L) {
            return;
        }
        player.lastDeath = time;
        if (player.lastDamager != null && VimeNetwork.isPlayerOnline(player.lastDamager)) {
            EntityDamageEvent lastEvent = target.getLastDamageCause();
            EntityDamageEvent.DamageCause cause = lastEvent == null ? EntityDamageEvent.DamageCause.CUSTOM : lastEvent.getCause();
            Bukkit.getPluginManager().callEvent((Event)new PlayerKillEvent(player.lastDamager, player.lastDamagerEntity, target, cause));
            if (player.lastDamager == player.player && player.lastDamagerEntity != null && player.lastDamagerEntity.getType() == EntityType.ARROW) {
                player.getAchievements().complete(Achievement.SECRET_SELF_KILL);
            }
            player.lastDamager = null;
            player.lastDamagerEntity = null;
            Bukkit.getScheduler().cancelTask(player.lastDamagerPurgeTask);
            player.lastDamagerPurgeTask = -1;
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerLeave(PlayerLeaveEvent event) {
        Features.AntiLeaveFeature antiLeave = VimeNetwork.features().ANTI_LEAVE;
        if (antiLeave.isEnabled()) {
            VPlayer player = VPlayer.get(event.getPlayer());
            if (player.lastDamageFromPlayer + (long)antiLeave.getDamageDelay() > System.currentTimeMillis()) {
                player.player.setHealth(0.0);
            }
        }
    }
}

