/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.NumberConversions
 *  org.bukkit.util.Vector
 */
package net.xtrafrancyz.ClashPoint.game;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.ClashPoint.ClashPoint;
import net.xtrafrancyz.ClashPoint.Config;
import net.xtrafrancyz.ClashPoint.game.CPTexteria;
import net.xtrafrancyz.ClashPoint.game.GameState;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class MapBorder
implements Runnable {
    private final ClashPoint plugin = ClashPoint.instance();
    private final Map<PlayerInfo, Integer> players = new HashMap<PlayerInfo, Integer>();
    private final double maxDistance = Math.pow(Config.teamDistance + 50.0, 2.0);

    @Override
    public void run() {
        if (this.plugin.game.getState() != GameState.GAME) {
            return;
        }
        for (PlayerInfo player : PlayerInfo.PLAYERS.values()) {
            if (player.team == null) continue;
            double distance = this.xzDistanceToMiddle(player.player.getLocation());
            Integer task = this.players.get(player);
            if (distance > this.maxDistance) {
                if (task != null) continue;
                int task0 = Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this.plugin, () -> {
                    player.player.setVelocity(new Vector(0, 0, 0));
                    player.player.teleport(player.team.getSpawnLocation());
                    this.players.remove(player);
                }, 200L);
                this.players.put(player, task0);
                CPTexteria.onMapLeave(player.player, 9999L);
                continue;
            }
            if (task == null) continue;
            Bukkit.getScheduler().cancelTask(task.intValue());
            this.players.remove(player);
            CPTexteria.removeMapLeave(player.player);
        }
    }

    private double xzDistanceToMiddle(Location loc) {
        return NumberConversions.square((double)(loc.getX() - Config.middle.getX())) + NumberConversions.square((double)(loc.getZ() - Config.middle.getZ()));
    }
}

