package net.xtrafrancyz.VimeNetwork.listeners;

import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Particles;
import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ArrowTrailListener implements Listener {
   @EventHandler
   public void onPlayerShootArrow(ProjectileLaunchEvent event) {
      if (event.getEntityType() == EntityType.ARROW && event.getEntity().getShooter().getType() == EntityType.PLAYER) {
         NetworkPlayer player = VimeNetwork.getPlayer((Player)event.getEntity().getShooter());
         if (player.getArrowTrail() != null) {
            Trailer trailer = new Trailer((Arrow)event.getEntity(), player.getArrowTrail());
            trailer.task = Bukkit.getScheduler().scheduleSyncRepeatingTask(VNPlugin.instance(), trailer, 3L, 1L);
         }
      }

   }

   private static class Trailer implements Runnable {
      Arrow arrow;
      ArrowTrail trail;
      int task = -1;

      public Trailer(Arrow arrow, ArrowTrail trail) {
         this.arrow = arrow;
         this.trail = trail;
      }

      public void run() {
         switch (this.trail) {
            case HEARTS:
               Particles.HEART.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case ANGRY_VILLAGER:
               Particles.ANGRY_VILLAGER.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case HAPPY_VILLAGER:
               Particles.HAPPY_VILLAGER.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case FIREWORK:
               Particles.FIREWORKS_SPARK.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case MAGIC_CRIT:
               Particles.MAGIC_CRIT.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case SMOKE:
               Particles.SMOKE.play(this.arrow.getLocation(), 0.02F, 0.02F, 0.02F, 0.0F, 2);
               break;
            case DRIP_LAVA:
               Particles.DRIP_LAVA.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case DRIP_WATER:
               Particles.DRIP_WATER.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 1);
               break;
            case SNOWBALL_POOF:
               Particles.SNOWBALL_POOF.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 2);
               break;
            case SLIME:
               Particles.SLIME.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 2);
               break;
            case WITCH_MAGIC:
               Particles.WITCH_MAGIC.play(this.arrow.getLocation(), 0.0F, 0.0F, 0.0F, 0.0F, 2);
         }

         if (this.arrow.isDead() || this.arrow.isOnGround()) {
            Bukkit.getScheduler().cancelTask(this.task);
            this.task = -1;
         }

      }
   }
}
