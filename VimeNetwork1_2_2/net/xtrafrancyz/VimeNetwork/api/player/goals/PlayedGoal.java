/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.goals.ExpCoinsGoal;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PlayedGoal
extends ExpCoinsGoal {
    public PlayedGoal() {
        this(0, 0);
    }

    public PlayedGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.EMERALD);
    }

    @Override
    public List<String> getText(boolean addGame) {
        String text = "&f\u0421\u044b\u0433\u0440\u0430\u0442\u044c &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0438\u0433\u0440\u0443", " \u0438\u0433\u0440\u044b", " \u0438\u0433\u0440");
        if (addGame) {
            text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
        }
        return Collections.singletonList(text);
    }
}

