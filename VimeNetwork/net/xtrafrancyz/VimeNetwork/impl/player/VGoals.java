package net.xtrafrancyz.VimeNetwork.impl.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Stat;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.goals.BedBreakGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.CheckpointGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goals;
import net.xtrafrancyz.VimeNetwork.api.player.goals.KillsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.LevelsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.LuckyBlockGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.NexusGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.PlayedGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.PointGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.WavesGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.WinsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

public class VGoals implements Goals {
   public static BiMap REGISTRY = ImmutableBiMap.builder().put("kill", KillsGoal.class).put("win", WinsGoal.class).put("played", PlayedGoal.class).put("level", LevelsGoal.class).put("wave", WavesGoal.class).put("bedbreak", BedBreakGoal.class).put("nexus", NexusGoal.class).put("checkpoint", CheckpointGoal.class).put("luckyblock", LuckyBlockGoal.class).put("point", PointGoal.class).build();
   public final VPlayer player;
   public final Map goals;
   public final Map custom;

   public VGoals(VPlayer player) {
      this.player = player;
      this.goals = new ConcurrentHashMap();
      this.custom = new ConcurrentHashMap();
   }

   public void add(String id, Goal goal) {
      this.player.getAchievements().complete(Achievement.GLOBAL_FIRST_GOAL);
      VTexteria.showGoalAdded(this.player, goal);
      this.goals.put(id, goal);
      this.save(id, goal);
      VimeNetwork.metrics().add("goals.taken");
   }

   public void addCustom(String id, Goal goal) {
      this.custom.put(id, goal);
   }

   public boolean remove(String id) {
      if (this.goals.remove(id) != null) {
         this.player.removeMeta("goal." + id);
         return true;
      } else {
         return false;
      }
   }

   public boolean contains(String id) {
      return this.goals.containsKey(id);
   }

   public void trigger(String game, GoalQuery query) {
      this.triggerAmount(game, 1, query);
   }

   public void triggerAmount(String game, int amount, GoalQuery query) {
      Class clazz = (Class)REGISTRY.get(query.type);
      if (clazz == null) {
         (new IllegalArgumentException("ID '" + query.type + "' not registered")).printStackTrace();
      } else {
         for(Map.Entry entry : this.goals.entrySet()) {
            Goal goal = (Goal)entry.getValue();
            if (goal.getClass() == clazz && (goal.game == null || goal.game.equals(game)) && goal.isApplicable(this.player, query)) {
               goal.setProgress(goal.getProgress() + amount);
               goal.needSave = true;
               if (goal.getProgress() >= goal.getGoal()) {
                  VNPlugin.instance().getLogger().info("[Goals] Player '" + this.player.username + "' completed goal: " + (String)entry.getKey());

                  try {
                     goal.complete(this.player);
                     this.player.getStats().increment(Stat.GOAL_COMPLETE);
                     VTexteria.showGoalComplete(this.player, goal);
                     VimeNetwork.metrics().add("goals.complete");
                     if (Rand.nextFloat() < 0.25F) {
                        this.player.treasures.add(TreasureType.BASIC, 1);
                        U.msg(this.player.player, (String[])("&aВы получили " + TreasureType.BASIC.name));
                     }
                  } catch (Exception e) {
                     e.printStackTrace();
                  }

                  this.remove((String)entry.getKey());
               }
            }
         }

      }
   }

   public void openInventory() {
      (new GoalsInventory(this.player)).show(this.player.player);
   }

   public Map getActiveGoals() {
      return Collections.unmodifiableMap(this.goals);
   }

   public Map getCustomGoals() {
      return Collections.unmodifiableMap(this.custom);
   }

   public void load() {
      long start = System.currentTimeMillis();
      long time = start / 1000L;

      for(Map.Entry entry : this.player.getMetaMap().entrySet()) {
         if (((String)entry.getKey()).startsWith("goal.")) {
            Goal goal = this.load((String)entry.getValue());
            if (goal != null && goal.finishTime >= time) {
               this.goals.put(((String)entry.getKey()).substring(5), goal);
            } else {
               this.player.removeMeta((String)entry.getKey());
            }
         }
      }

      Debug.GOALS.info("Goals for player '" + this.player.username + "' loaded: " + (System.currentTimeMillis() - start) + " ms.");
   }

   public void save() {
      this.goals.entrySet().stream().filter((e) -> ((Goal)e.getValue()).needSave).forEach((e) -> this.save((String)e.getKey(), (Goal)e.getValue()));
   }

   private void save(String id, Goal goal) {
      JsonObject json = new JsonObject();
      json.addProperty(".id", (String)REGISTRY.inverse().get(goal.getClass()));
      json.addProperty(".to", goal.finishTime);
      goal.write(json);
      this.player.setMeta("goal." + id, json.toString());
   }

   private Goal load(String str) {
      try {
         JsonObject json = (new JsonParser()).parse(str).getAsJsonObject();
         Class<? extends Goal> clazz = (Class)REGISTRY.get(json.get(".id").getAsString());
         Goal goal = (Goal)clazz.newInstance();
         goal.read(json);
         goal.finishTime = json.get(".to").getAsLong();
         return goal;
      } catch (Exception e) {
         VNPlugin.instance().getLogger().log(Level.WARNING, str, e);
         return null;
      }
   }
}
