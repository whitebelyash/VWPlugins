/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonObject
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.ExpCoinsGoal;
import net.xtrafrancyz.VimeNetwork.api.player.goals.GoalQuery;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class PhaseGoal
extends ExpCoinsGoal {
    public int phase = 0;

    public PhaseGoal() {
        this(0, 0);
    }

    public PhaseGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public void write(JsonObject json) {
        super.write(json);
        json.addProperty("p", (Number)this.phase);
    }

    @Override
    public void read(JsonObject json) {
        super.read(json);
        this.phase = json.get("p").getAsInt();
    }

    @Override
    public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
        Object currentPhase = query.data.get("phase");
        Object playerPhase = query.data.get("playerJoinPhase");
        return (Integer)currentPhase == this.phase && (Integer)playerPhase == 1;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.NETHER_STAR);
    }

    @Override
    public List<String> getText(boolean addGame) {
        if (addGame) {
            return Arrays.asList("&f\u041f\u0440\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c\u0441\u044f \u0434\u043e &e" + this.phase + "-\u0439 \u0444\u0430\u0437\u044b", "&f\u043d\u0430 " + ServerType.byId(this.game).getName());
        }
        return Collections.singletonList("&f\u041f\u0440\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c\u0441\u044f \u0434\u043e &e" + this.phase + "-\u0439 \u0444\u0430\u0437\u044b");
    }
}

