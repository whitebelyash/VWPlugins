/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Cancellable
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 *  org.bukkit.potion.PotionEffect
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

public class PlayerEffectAddEvent
extends Event
implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private PotionEffect effect;
    private boolean cancelled;

    public PlayerEffectAddEvent(Player player, PotionEffect effect) {
        super(false);
        this.player = player;
        this.effect = effect;
        this.cancelled = false;
    }

    public void setCancelled(boolean flag) {
        this.cancelled = flag;
    }

    public void setEffect(PotionEffect effect) {
        this.effect = effect;
    }

    public PotionEffect getEffect() {
        return this.effect;
    }

    public Player getEntity() {
        return this.player;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

