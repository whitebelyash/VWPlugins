/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public abstract class Goal {
    private int progress = 0;
    private int goal = 1;
    public String game = null;
    public long finishTime = 0L;
    public boolean needSave = false;

    public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
        return true;
    }

    public void write(JsonObject json) {
        json.addProperty(".p", (Number)this.progress);
        json.addProperty(".g", (Number)this.goal);
        if (this.game != null) {
            json.addProperty(".e", this.game);
        }
    }

    public void read(JsonObject json) {
        this.progress = json.get(".p").getAsInt();
        this.goal = json.get(".g").getAsInt();
        this.game = json.get(".e").getAsString();
    }

    public void setDuration(int seconds) {
        this.finishTime = System.currentTimeMillis() / 1000L + (long)seconds;
    }

    public abstract void complete(NetworkPlayer var1);

    public abstract ItemStack getItem();

    public List<String> getText(boolean addGame) {
        return new ArrayList<String>();
    }

    public List<String> getRewardText() {
        return null;
    }

    public int getGoal() {
        return this.goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}

