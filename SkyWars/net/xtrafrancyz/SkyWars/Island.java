/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 */
package net.xtrafrancyz.SkyWars;

import java.util.List;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import org.bukkit.Location;

public class Island {
    public int id;
    public List<Location> spawns;
    public List<Location> clests;
    public List<PlayerInfo> players;
    public int slot;
    public String tag;

    public boolean equals(Object obj) {
        return obj instanceof Island && ((Island)obj).id == this.id;
    }

    public int hashCode() {
        return this.id;
    }
}

