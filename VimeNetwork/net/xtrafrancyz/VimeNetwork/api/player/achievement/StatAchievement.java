package net.xtrafrancyz.VimeNetwork.api.player.achievement;

import net.xtrafrancyz.VimeNetwork.api.player.Stat;

public class StatAchievement extends Achievement {
   private Stat stat;
   private int neeeded;

   public StatAchievement(int id, String name, int reward, Achievement.Group group, Stat stat, int neeeded, String... description) {
      super(id, name, reward, group, description);
      this.stat = stat;
      this.neeeded = neeeded;
      this.stat.getAchievements().add(this);
   }

   public Stat getStat() {
      return this.stat;
   }

   public int getNeeded() {
      return this.neeeded;
   }
}
