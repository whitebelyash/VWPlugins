package net.xtrafrancyz.VimeNetwork.api.score;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import net.minecraft.server.v1_6_R3.ScoreboardObjective;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SideScoreboard {
   private Set records = new ConcurrentSet();
   private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
   private Objective objective;
   private ScoreboardObjective nmsObjective;
   private net.minecraft.server.v1_6_R3.Scoreboard nmsScoreboard;

   public SideScoreboard(String name) {
      this.objective = this.scoreboard.registerNewObjective("score", "dummy");
      this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      this.nmsObjective = (ScoreboardObjective)Reflect.get((Object)this.objective, "objective");
      this.nmsScoreboard = ((CraftScoreboard)this.scoreboard).getHandle();
      this.setDisplayName(name);
   }

   public void setDisplayName(String name) {
      this.objective.setDisplayName(name);
   }

   public void unbind(Player player) {
      try {
         player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
      } catch (IllegalStateException var3) {
      }

   }

   public void bind(Player player) {
      try {
         player.setScoreboard(this.scoreboard);
      } catch (IllegalStateException var3) {
      }

   }

   public Record create() {
      return this.create("", 0);
   }

   public Record create(String name) {
      return this.create(name, 0);
   }

   public Record create(String name, int value) {
      if (name == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         Record rec = new Record(this, name);
         rec.value = value;
         this.records.add(rec);
         return rec;
      }
   }

   public void remove(Record record) {
      this.records.remove(record);
      this.removeScore(record.name);
   }

   public void reset() {
      for(Record record : this.records) {
         this.removeScore(record.name);
      }

      this.records.clear();
   }

   void setScore(String name, int score) {
      this.nmsScoreboard.getPlayerScoreForObjective(name, this.nmsObjective).setScore(score);
   }

   void removeScore(String name) {
      this.nmsScoreboard.resetPlayerScores(name);
   }
}
