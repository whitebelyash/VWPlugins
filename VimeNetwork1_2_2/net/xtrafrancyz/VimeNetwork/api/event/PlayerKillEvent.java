/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.HandlerList
 *  org.bukkit.event.entity.EntityDamageEvent$DamageCause
 *  org.bukkit.event.player.PlayerEvent
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKillEvent
extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player target;
    private final Entity realDamager;
    private final EntityDamageEvent.DamageCause cause;

    public PlayerKillEvent(Player player, Entity realDamager, Player target, EntityDamageEvent.DamageCause cause) {
        super(player);
        this.realDamager = realDamager;
        this.target = target;
        this.cause = cause;
    }

    public Player getTarget() {
        return this.target;
    }

    public Entity getRealDamager() {
        return this.realDamager;
    }

    public EntityDamageEvent.DamageCause getDamageCause() {
        return this.cause;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}

