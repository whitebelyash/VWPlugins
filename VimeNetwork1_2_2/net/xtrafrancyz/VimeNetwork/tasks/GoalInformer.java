/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;

public class GoalInformer
implements Runnable {
    @Override
    public void run() {
        for (VPlayer player : VPlayer.PLAYERS.values()) {
            if (player.goals.getActiveGoals().size() <= 0 || !player.settings.get(2)) continue;
            VTexteria.showGoalMessage(player);
        }
    }
}

