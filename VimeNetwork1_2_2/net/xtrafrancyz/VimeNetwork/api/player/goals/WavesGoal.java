/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.craftbukkit.libs.com.google.gson.JsonElement
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
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonElement;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.inventory.ItemStack;

public class WavesGoal
extends ExpCoinsGoal {
    public int streak = 0;

    public WavesGoal() {
        this(0, 0);
    }

    public WavesGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public void write(JsonObject json) {
        super.write(json);
        if (this.streak > 0) {
            json.addProperty("s", (Number)this.streak);
        }
    }

    @Override
    public void read(JsonObject json) {
        super.read(json);
        JsonElement elem = json.get("s");
        if (elem != null) {
            this.streak = elem.getAsInt();
        }
    }

    @Override
    public boolean isApplicable(NetworkPlayer player, GoalQuery query) {
        Object streak0 = query.data.get("streak");
        return this.streak == 0 || streak0 != null && (Integer)streak0 == this.streak;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.NETHER_STAR);
    }

    @Override
    public List<String> getText(boolean addGame) {
        if (this.streak > 0) {
            if (addGame) {
                return Arrays.asList("&f\u0414\u043e\u0441\u0442\u0438\u0433\u043d\u0443\u0442\u044c &e" + this.streak + "-\u0439 \u0432\u043e\u043b\u043d\u044b", "&f\u043d\u0430 " + ServerType.byId(this.game).getName());
            }
            return Collections.singletonList("&f\u0414\u043e\u0441\u0442\u0438\u0433\u043d\u0443\u0442\u044c &e" + this.streak + "-\u0439 \u0432\u043e\u043b\u043d\u044b");
        }
        if (addGame) {
            return Arrays.asList("&f\u041f\u0440\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c\u0441\u044f &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0432\u043e\u043b\u043d\u0443", " \u0432\u043e\u043b\u043d\u044b", " \u0432\u043e\u043b\u043d"), "&f\u043d\u0430 " + ServerType.byId(this.game).getName());
        }
        return Collections.singletonList("&f\u041f\u0440\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c\u0441\u044f &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0432\u043e\u043b\u043d\u0443", " \u0432\u043e\u043b\u043d\u044b", " \u0432\u043e\u043b\u043d"));
    }
}

