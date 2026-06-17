/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerUnloadEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private NetworkPlayer player;

    public PlayerUnloadEvent(NetworkPlayer player) {
        super(false);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player.getBukkitPlayer();
    }

    public NetworkPlayer getNetworkPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

