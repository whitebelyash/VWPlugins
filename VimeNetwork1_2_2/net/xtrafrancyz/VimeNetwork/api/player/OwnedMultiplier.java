/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player;

import net.xtrafrancyz.VimeNetwork.api.player.Multiplier;

public class OwnedMultiplier {
    private final Multiplier multiplier;
    private final int amount;

    public OwnedMultiplier(Multiplier multiplier, int amount) {
        this.multiplier = multiplier;
        this.amount = amount;
    }

    public Multiplier getMultiplier() {
        return this.multiplier;
    }

    public int getAmount() {
        return this.amount;
    }
}

