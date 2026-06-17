/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableBiMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonParser
 */
package net.xtrafrancyz.VimeNetwork.impl.player;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
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
import net.xtrafrancyz.VimeNetwork.api.player.goals.PhaseGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.PlayedGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.PointGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.WavesGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.WinsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.GoalsInventory;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonParser;

public class VGoals
implements Goals {
    public static BiMap<String, Class<? extends Goal>> REGISTRY = ImmutableBiMap.builder().put((Object)"kill", KillsGoal.class).put((Object)"win", WinsGoal.class).put((Object)"played", PlayedGoal.class).put((Object)"level", LevelsGoal.class).put((Object)"wave", WavesGoal.class).put((Object)"bedbreak", BedBreakGoal.class).put((Object)"nexus", NexusGoal.class).put((Object)"checkpoint", CheckpointGoal.class).put((Object)"luckyblock", LuckyBlockGoal.class).put((Object)"point", PointGoal.class).put((Object)"phase", PhaseGoal.class).build();
    public final VPlayer player;
    public final Map<String, Goal> goals;
    public final Map<String, Goal> custom;

    public VGoals(VPlayer player) {
        this.player = player;
        this.goals = new ConcurrentHashMap<String, Goal>();
        this.custom = new HashMap<String, Goal>();
    }

    @Override
    public void add(String id, Goal goal) {
        this.player.getAchievements().complete(Achievement.GLOBAL_FIRST_GOAL);
        VTexteria.showGoalAdded(this.player, goal);
        this.goals.put(id, goal);
        this.save(id, goal);
        VimeNetwork.metrics().add("goals.taken");
    }

    @Override
    public void addCustom(String id, Goal goal) {
        this.custom.put(id, goal);
    }

    @Override
    public boolean remove(String id) {
        if (this.goals.remove(id) != null) {
            this.player.removeMeta("goal." + id);
            return true;
        }
        return false;
    }

    @Override
    public boolean contains(String id) {
        return this.goals.containsKey(id);
    }

    @Override
    public void trigger(String game, GoalQuery query) {
        this.triggerAmount(game, 1, query);
    }

    @Override
    public void triggerAmount(String game, int amount, GoalQuery query) {
        Class clazz = (Class)REGISTRY.get((Object)query.type);
        if (clazz == null) {
            new IllegalArgumentException("ID '" + query.type + "' not registered").printStackTrace();
            return;
        }
        for (Map.Entry<String, Goal> entry : this.goals.entrySet()) {
            Goal goal = entry.getValue();
            if (goal.getClass() != clazz || goal.game != null && !goal.game.equals(game) || !goal.isApplicable(this.player, query)) continue;
            goal.setProgress(goal.getProgress() + amount);
            goal.needSave = true;
            if (goal.getProgress() < goal.getGoal()) continue;
            VNPlugin.instance().getLogger().info("[Goals] Player '" + this.player.username + "' completed goal: " + entry.getKey());
            try {
                goal.complete(this.player);
                this.player.getStats().increment(Stat.GOAL_COMPLETE);
                VTexteria.showGoalComplete(this.player, goal);
                VimeNetwork.metrics().add("goals.complete");
                if (Rand.nextFloat() < 0.25f) {
                    this.player.treasures.add(TreasureType.BASIC, 1);
                    U.msg((CommandSender)this.player.player, "&a\u0412\u044b \u043f\u043e\u043b\u0443\u0447\u0438\u043b\u0438 " + TreasureType.BASIC.name);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            this.remove(entry.getKey());
        }
    }

    @Override
    public void openInventory() {
        new GoalsInventory(this.player).show(this.player.player);
    }

    @Override
    public Map<String, Goal> getActiveGoals() {
        return Collections.unmodifiableMap(this.goals);
    }

    @Override
    public Map<String, Goal> getCustomGoals() {
        return Collections.unmodifiableMap(this.custom);
    }

    public void load() {
        this.goals.clear();
        long time = System.currentTimeMillis() / 1000L;
        for (Map.Entry<String, String> entry : this.player.getMetaMap().entrySet()) {
            if (!entry.getKey().startsWith("goal.")) continue;
            Goal goal = this.load(entry.getValue());
            if (goal == null || goal.finishTime < time) {
                this.player.removeMeta(entry.getKey());
                continue;
            }
            this.goals.put(entry.getKey().substring(5), goal);
        }
    }

    public void save() {
        this.goals.entrySet().stream().filter(e -> ((Goal)e.getValue()).needSave).forEach(e -> this.save((String)e.getKey(), (Goal)e.getValue()));
    }

    private void save(String id, Goal goal) {
        JsonObject json = new JsonObject();
        json.addProperty(".id", (String)REGISTRY.inverse().get(goal.getClass()));
        json.addProperty(".to", (Number)goal.finishTime);
        goal.write(json);
        this.player.setMeta("goal." + id, json.toString());
    }

    private Goal load(String str) {
        try {
            JsonObject json = new JsonParser().parse(str).getAsJsonObject();
            Class clazz = (Class)REGISTRY.get((Object)json.get(".id").getAsString());
            Goal goal = (Goal)clazz.newInstance();
            goal.read(json);
            goal.finishTime = json.get(".to").getAsLong();
            return goal;
        }
        catch (Exception e) {
            VNPlugin.instance().getLogger().log(Level.WARNING, str, e);
            return null;
        }
    }
}

