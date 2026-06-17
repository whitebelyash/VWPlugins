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

public class WinsGoal
extends ExpCoinsGoal {
    public WinsGoal() {
        this(0, 0);
    }

    public WinsGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.DIAMOND);
    }

    @Override
    public List<String> getText(boolean addGame) {
        String text = "&f\u041f\u043e\u0431\u0435\u0434\u0438\u0442\u044c &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0440\u0430\u0437", " \u0440\u0430\u0437\u0430", " \u0440\u0430\u0437");
        if (addGame) {
            text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
        }
        return Collections.singletonList(text);
    }
}

