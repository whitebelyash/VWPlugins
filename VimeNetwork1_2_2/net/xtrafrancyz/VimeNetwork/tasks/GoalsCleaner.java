/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Map;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;

public class GoalsCleaner
implements Runnable {
    @Override
    public void run() {
        long start = System.currentTimeMillis();
        long time = start / 1000L;
        for (VPlayer player : VPlayer.PLAYERS.values()) {
            for (Map.Entry<String, Goal> entry : player.goals.getActiveGoals().entrySet()) {
                if (entry.getValue().finishTime >= time) continue;
                player.goals.remove(entry.getKey());
                VTexteria.showGoalExpired(player, entry.getValue());
            }
        }
    }
}

