/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.goals.ExpCoinsGoal;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NexusGoal
extends ExpCoinsGoal {
    public NexusGoal() {
        this(0, 0);
    }

    public NexusGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.BED);
    }

    @Override
    public List<String> getText(boolean addGame) {
        if (addGame) {
            return Arrays.asList("&f\u041d\u0430\u043d\u0435\u0441\u0442\u0438 \u0431\u0430\u0437\u0435 &e" + this.getGoal() + " \u0443\u0440\u043e\u043d\u0430", "&f\u043d\u0430 " + ServerType.byId(this.game).getName());
        }
        return Collections.singletonList("&f\u041d\u0430\u043d\u0435\u0441\u0442\u0438 \u0431\u0430\u0437\u0435 &e" + this.getGoal() + " \u0443\u0440\u043e\u043d\u0430");
    }
}

