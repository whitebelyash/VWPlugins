/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ConcurrentSet
 *  net.minecraft.server.v1_6_R3.Scoreboard
 *  net.minecraft.server.v1_6_R3.ScoreboardObjective
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_6_R3.scoreboard.CraftScoreboard
 *  org.bukkit.entity.Player
 *  org.bukkit.scoreboard.DisplaySlot
 *  org.bukkit.scoreboard.Objective
 *  org.bukkit.scoreboard.Scoreboard
 */
package net.xtrafrancyz.VimeNetwork.api.score;

import io.netty.util.internal.ConcurrentSet;
import java.util.Set;
import net.minecraft.server.v1_6_R3.ScoreboardObjective;
import net.xtrafrancyz.VimeNetwork.api.score.Record;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SideScoreboard {
    private Set<Record> records = new ConcurrentSet();
    private Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private Objective objective = this.scoreboard.registerNewObjective("score", "dummy");
    private ScoreboardObjective nmsObjective;
    private net.minecraft.server.v1_6_R3.Scoreboard nmsScoreboard;

    public SideScoreboard(String name) {
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        this.nmsObjective = (ScoreboardObjective)Reflect.get(this.objective, "objective");
        this.nmsScoreboard = ((CraftScoreboard)this.scoreboard).getHandle();
        this.setDisplayName(name);
    }

    public void setDisplayName(String name) {
        this.objective.setDisplayName(name);
    }

    public void unbind(Player player) {
        try {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
        }
    }

    public void bind(Player player) {
        try {
            player.setScoreboard(this.scoreboard);
        }
        catch (IllegalStateException illegalStateException) {
            // empty catch block
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
        }
        Record rec = new Record(this, name);
        rec.value = value;
        this.records.add(rec);
        return rec;
    }

    public void remove(Record record) {
        this.records.remove(record);
        this.removeScore(record.name);
    }

    public void reset() {
        for (Record record : this.records) {
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

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }
}

