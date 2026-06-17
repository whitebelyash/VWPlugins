/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import net.xtrafrancyz.VimeNetwork.api.player.Stat;

public interface Stats {
    public int get(Stat var1);

    public int increment(Stat var1);

    public int increment(Stat var1, int var2);
}

