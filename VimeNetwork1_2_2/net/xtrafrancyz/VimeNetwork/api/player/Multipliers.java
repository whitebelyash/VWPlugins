/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;
import net.xtrafrancyz.VimeNetwork.api.player.OwnedMultiplier;

public interface Multipliers {
    public float getCurrentMultiplier();

    public int getRankMultiplier();

    public int getExtraMultiplier();

    public float getGuildMultiplier();

    public String getFormattedMultiplier();

    public long getExtraEndTime();

    default public void add(Multiplier mult) {
        this.add(mult, 1);
    }

    public void add(Multiplier var1, int var2);

    default public boolean isActivated() {
        return this.getExtraMultiplier() > 0;
    }

    public void activate(Multiplier var1);

    public void deactivate();

    default public void take(Multiplier mult) {
        this.take(mult, 1);
    }

    public void take(Multiplier var1, int var2);

    public int getAmount(Multiplier var1);

    public List<OwnedMultiplier> list();
}

