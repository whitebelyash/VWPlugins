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

public class LevelsGoal
extends ExpCoinsGoal {
    public LevelsGoal() {
        this(0, 0);
    }

    public LevelsGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.EXP_BOTTLE);
    }

    @Override
    public List<String> getText(boolean addGame) {
        String text = "&f\u041f\u043e\u043b\u0443\u0447\u0438\u0442\u044c &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0443\u0440\u043e\u0432\u0435\u043d\u044c", " \u0443\u0440\u043e\u0432\u043d\u044f", " \u0443\u0440\u043e\u0432\u043d\u0435\u0439");
        if (addGame) {
            text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
        }
        return Collections.singletonList(text);
    }
}

