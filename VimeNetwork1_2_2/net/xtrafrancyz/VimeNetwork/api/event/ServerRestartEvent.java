/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerRestartEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private State state;
    private boolean forced;

    public ServerRestartEvent(State state, boolean forced) {
        this.state = state;
        this.forced = forced;
    }

    public boolean isForced() {
        return this.forced;
    }

    public State getState() {
        return this.state;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static enum State {
        SCHEDULED,
        COUNTDOWN,
        RESTART;

    }
}

