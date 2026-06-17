/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.World
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerChangedWorldEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package net.xtrafrancyz.VimeNetwork.listeners;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PerWorldTablist
implements Listener {
    @EventHandler
    private void onPlayreChangeWorld(PlayerChangedWorldEvent event) {
        this.updatePlayer(event.getPlayer());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        this.updatePlayer(event.getPlayer());
    }

    private void updatePlayer(Player player) {
        if (!VimeNetwork.features().PER_WORLD_TABLIST.isEnabled()) {
            return;
        }
        boolean spectator = PerWorldTablist.isSpectator(player);
        World world = player.getWorld();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (other == player) continue;
            if (other.getWorld() != world) {
                if (!spectator) {
                    other.hidePlayer(player);
                }
                player.hidePlayer(other);
                continue;
            }
            if (!PerWorldTablist.isSpectator(other)) {
                player.showPlayer(other);
            }
            if (spectator) continue;
            other.showPlayer(player);
        }
    }

    private static boolean isSpectator(Player player) {
        return Spectators.isEnabled() && Spectators.instance().contains(player);
    }
}

