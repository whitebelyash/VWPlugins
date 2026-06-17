/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.Set;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;

public interface Guild {
    public int getId();

    public String getName();

    public String getTag();

    public String getColor();

    public Set<NetworkPlayer> getOnlinePlayers();
}

