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
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DeathListener implements Listener {
   private final VNPlugin plugin;

   public DeathListener(VNPlugin plugin) {
      this.plugin = plugin;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onPlayerDamage(EntityDamageEvent event) {
      if (event.getEntityType() == EntityType.PLAYER) {
         VPlayer player = VPlayer.get((Player)event.getEntity());
         Player damager = null;
         if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event0 = (EntityDamageByEntityEvent)event;
            player.lastDamagerEntity = event0.getDamager();
            switch (event0.getDamager().getType()) {
               case PLAYER:
                  damager = (Player)event0.getDamager();
                  break;
               case PRIMED_TNT:
                  Entity entity = ((TNTPrimed)event0.getDamager()).getSource();
                  if (entity instanceof Player) {
                     damager = (Player)entity;
                  }
            }

            if (damager == null) {
               if (event0.getDamager() instanceof Projectile) {
                  Entity shooter = ((Projectile)event0.getDamager()).getShooter();
                  if (shooter instanceof Player) {
                     damager = (Player)shooter;
                  }
               } else if (event0.getDamager() instanceof Tameable) {
                  AnimalTamer tamer = ((Tameable)event0.getDamager()).getOwner();
                  if (tamer instanceof Player) {
                     damager = (Player)tamer;
                  }
               }
            }
         } else {
            player.lastDamagerEntity = null;
         }

         if (damager != null) {
            player.lastDamageFromPlayer = System.currentTimeMillis();
            player.lastDamager = damager;
            Bukkit.getScheduler().cancelTask(player.lastDamagerPurgeTask);
            player.lastDamagerPurgeTask = Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
               player.lastDamager = null;
               player.lastDamagerEntity = null;
               player.lastDamagerPurgeTask = -1;
            }, 200L);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerDeath(PlayerDeathEvent event) {
      Player target = event.getEntity();
      VPlayer player = VPlayer.get(target);
      long time = System.currentTimeMillis();
      if (player.lastDeath <= time - 1000L) {
         player.lastDeath = time;
         if (player.lastDamager != null && VimeNetwork.isPlayerOnline(player.lastDamager)) {
            EntityDamageEvent lastEvent = target.getLastDamageCause();
            EntityDamageEvent.DamageCause cause = lastEvent == null ? DamageCause.CUSTOM : lastEvent.getCause();
            Bukkit.getPluginManager().callEvent(new PlayerKillEvent(player.lastDamager, player.lastDamagerEntity, target, cause));
            if (player.lastDamager == player.player && player.lastDamagerEntity != null && player.lastDamagerEntity.getType() == EntityType.ARROW) {
               player.getAchievements().complete(Achievement.SECRET_SELF_KILL);
            }

            player.lastDamager = null;
            player.lastDamagerEntity = null;
            Bukkit.getScheduler().cancelTask(player.lastDamagerPurgeTask);
            player.lastDamagerPurgeTask = -1;
         }

      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlayerLeave(PlayerLeaveEvent event) {
      Features.AntiLeaveFeature antiLeave = VimeNetwork.features().ANTI_LEAVE;
      if (antiLeave.isEnabled()) {
         VPlayer player = VPlayer.get(event.getPlayer());
         if (player.lastDamageFromPlayer + (long)antiLeave.getDamageDelay() > System.currentTimeMillis()) {
            player.player.setHealth((double)0.0F);
         }
      }

   }
}
