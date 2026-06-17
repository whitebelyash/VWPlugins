/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.event.Event
 *  org.bukkit.event.HandlerList
 */
package net.xtrafrancyz.VimeNetwork.api.event;

import net.xtrafrancyz.VimeNetwork.api.updater.WatchedDir;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedEntry;
import net.xtrafrancyz.VimeNetwork.api.updater.WatchedFile;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FileUpdateEvent
extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private Action action = Action.IGNORE;
    private WatchedEntry old;
    private WatchedEntry curr;

    public FileUpdateEvent(WatchedEntry old, WatchedEntry curr) {
        this.old = old;
        this.curr = curr;
    }

    public boolean isDir() {
        return this.old instanceof WatchedDir;
    }

    public boolean isFile() {
        return this.old instanceof WatchedFile;
    }

    public WatchedEntry getOld() {
        return this.old;
    }

    public WatchedEntry getCurrent() {
        return this.curr;
    }

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public static enum Action {
        RESTART,
        IGNORE;

    }
}

