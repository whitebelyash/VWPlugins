/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.treasure;

import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;

public interface Treasures {
    public int get(TreasureType var1);

    public void add(TreasureType var1, int var2);

    public void take(TreasureType var1, int var2);

    public void giveWithMessage(TreasureType var1, float var2);

    public void giveWithMessage(TreasureType var1);

    public boolean hasAny();
}

