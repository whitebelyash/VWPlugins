package net.xtrafrancyz.VimeNetwork.api.player.achievement;

public interface Achievements {
   boolean isCompleted(Achievement var1);

   CompletedAchievement getCompletedAchievement(Achievement var1);

   boolean complete(Achievement var1);

   int getCompletedCount();
}
