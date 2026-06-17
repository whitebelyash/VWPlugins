/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.OfflinePlayer
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.api.util;

import java.util.Map;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FakeOfflinePlayer
implements OfflinePlayer {
    private final String name;

    public FakeOfflinePlayer(String name) {
        this.name = name;
    }

    public boolean isOnline() {
        return false;
    }

    public String getName() {
        return this.name;
    }

    public boolean isBanned() {
        return false;
    }

    public void setBanned(boolean b) {
    }

    public boolean isWhitelisted() {
        return false;
    }

    public void setWhitelisted(boolean b) {
    }

    public Player getPlayer() {
        return null;
    }

    public long getFirstPlayed() {
        return 0L;
    }

    public long getLastPlayed() {
        return 0L;
    }

    public boolean hasPlayedBefore() {
        return false;
    }

    public Location getBedSpawnLocation() {
        return null;
    }

    public Map<String, Object> serialize() {
        return null;
    }

    public boolean isOp() {
        return false;
    }

    public void setOp(boolean b) {
    }
}

