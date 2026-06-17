/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.player.achievement;

import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.CompletedAchievement;

public interface Achievements {
    public boolean isCompleted(Achievement var1);

    public CompletedAchievement getCompletedAchievement(Achievement var1);

    public boolean complete(Achievement var1);

    public int getCompletedCount();
}

