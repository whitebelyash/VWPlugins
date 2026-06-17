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

public class CheckpointGoal
extends ExpCoinsGoal {
    public CheckpointGoal() {
        this(0, 0);
    }

    public CheckpointGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Material.GOLD_NUGGET);
    }

    @Override
    public List<String> getText(boolean addGame) {
        String text = "&f\u041f\u0440\u043e\u0439\u0442\u0438 &e" + this.getGoal() + U.plurals(this.getGoal(), " \u0447\u0435\u043a\u043f\u043e\u0438\u043d\u0442", " \u0447\u0435\u043a\u043f\u043e\u0438\u043d\u0442\u0430", " \u0447\u0435\u043a\u043f\u043e\u0438\u043d\u0442\u043e\u0432");
        if (addGame) {
            text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
        }
        return Collections.singletonList(text);
    }
}

