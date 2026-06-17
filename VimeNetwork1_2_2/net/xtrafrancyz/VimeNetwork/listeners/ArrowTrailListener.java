/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Arrow
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.entity.ProjectileLaunchEvent
 *  org.bukkit.plugin.Plugin
 */
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
import org.bukkit.plugin.Plugin;

public class ArrowTrailListener
implements Listener {
    @EventHandler
    public void onPlayerShootArrow(ProjectileLaunchEvent event) {
        NetworkPlayer player;
        if (event.getEntityType() == EntityType.ARROW && event.getEntity().getShooter().getType() == EntityType.PLAYER && (player = VimeNetwork.getPlayer((Player)event.getEntity().getShooter())).getArrowTrail() != null) {
            Trailer trailer = new Trailer((Arrow)event.getEntity(), player.getArrowTrail());
            trailer.task = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)VNPlugin.instance(), (Runnable)trailer, 3L, 1L);
        }
    }

    private static class Trailer
    implements Runnable {
        Arrow arrow;
        ArrowTrail trail;
        int task = -1;
        int tick = 0;

        public Trailer(Arrow arrow, ArrowTrail trail) {
            this.arrow = arrow;
            this.trail = trail;
        }

        @Override
        public void run() {
            switch (this.trail) {
                case HEARTS: {
                    Particles.HEART.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case ANGRY_VILLAGER: {
                    Particles.ANGRY_VILLAGER.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case HAPPY_VILLAGER: {
                    Particles.HAPPY_VILLAGER.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case FIREWORK: {
                    Particles.FIREWORKS_SPARK.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case MAGIC_CRIT: {
                    Particles.MAGIC_CRIT.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case SMOKE: {
                    Particles.SMOKE.play(this.arrow.getLocation(), 0.02f, 0.02f, 0.02f, 0.0f, 2, new Player[0]);
                    break;
                }
                case DRIP_LAVA: {
                    Particles.DRIP_LAVA.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case DRIP_WATER: {
                    Particles.DRIP_WATER.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 1, new Player[0]);
                    break;
                }
                case SNOWBALL_POOF: {
                    Particles.SNOWBALL_POOF.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 2, new Player[0]);
                    break;
                }
                case SLIME: {
                    Particles.SLIME.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 2, new Player[0]);
                    break;
                }
                case WITCH_MAGIC: {
                    Particles.WITCH_MAGIC.play(this.arrow.getLocation(), 0.0f, 0.0f, 0.0f, 0.0f, 2, new Player[0]);
                }
            }
            if (this.arrow.isDead() || this.arrow.isOnGround() || this.tick++ > 600) {
                Bukkit.getScheduler().cancelTask(this.task);
                this.task = -1;
            }
        }
    }
}

