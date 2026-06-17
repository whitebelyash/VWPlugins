/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.achievement;

import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;

public class CompletedAchievement {
    private Achievement achievement;
    private int timestamp;

    public CompletedAchievement(Achievement achievement, int timestamp) {
        this.achievement = achievement;
        this.timestamp = timestamp;
    }

    public Achievement getAchievement() {
        return this.achievement;
    }

    public int getTimestamp() {
        return this.timestamp;
    }
}

