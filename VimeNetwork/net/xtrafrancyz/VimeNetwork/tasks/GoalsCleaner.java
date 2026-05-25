package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Map;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;

public class GoalsCleaner implements Runnable {
   public void run() {
      long start = System.currentTimeMillis();
      long time = start / 1000L;

      for(VPlayer player : VPlayer.PLAYERS.values()) {
         for(Map.Entry entry : player.goals.getActiveGoals().entrySet()) {
            if (((Goal)entry.getValue()).finishTime < time) {
               player.goals.remove((String)entry.getKey());
               VTexteria.showGoalExpired(player, (Goal)entry.getValue());
            }
         }
      }

      Debug.GOALS.info("GoalCleaner finished: " + (System.currentTimeMillis() - start) + " ms.");
   }
}
