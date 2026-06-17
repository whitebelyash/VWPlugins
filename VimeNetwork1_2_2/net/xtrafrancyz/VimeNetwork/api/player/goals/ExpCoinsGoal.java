/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;

public abstract class ExpCoinsGoal
extends Goal {
    public int rewardCoins;
    public int rewardExp;

    public ExpCoinsGoal(int rewardCoins, int rewardExp) {
        this.rewardCoins = rewardCoins;
        this.rewardExp = rewardExp;
    }

    @Override
    public void write(JsonObject json) {
        super.write(json);
        json.addProperty("c", (Number)this.rewardCoins);
        json.addProperty("e", (Number)this.rewardExp);
    }

    @Override
    public void read(JsonObject json) {
        super.read(json);
        this.rewardCoins = json.get("c").getAsInt();
        this.rewardExp = json.get("e").getAsInt();
    }

    @Override
    public void complete(NetworkPlayer player) {
        player.addCoinsExact(this.rewardCoins);
        player.giveExpExact(this.rewardExp);
    }

    @Override
    public List<String> getRewardText() {
        return Arrays.asList("&7+ &e" + U.pluralsCoins(this.rewardCoins), "&7+ &9" + this.rewardExp + " \u043e\u043f\u044b\u0442\u0430");
    }
}

