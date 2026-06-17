/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.player.PlayerEvent
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerTagChangeEvent
extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private NetworkPlayer player;
    private String oldTag;

    public PlayerTagChangeEvent(NetworkPlayer player, String oldTag) {
        super(player.getBukkitPlayer());
        this.player = player;
        this.oldTag = oldTag;
    }

    public NetworkPlayer getNetworkPlayer() {
        return this.player;
    }

    public String getOldTag() {
        return this.oldTag;
    }

    public String getNewTag() {
        return this.player.getTag().getVisibleName();
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

