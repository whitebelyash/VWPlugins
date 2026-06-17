/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.luckyblock;

import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;

class LBActionEntry {
    LBAction action;
    int weight;

    public LBActionEntry(int weight, LBAction action) {
        this.weight = weight;
        this.action = action;
    }
}

