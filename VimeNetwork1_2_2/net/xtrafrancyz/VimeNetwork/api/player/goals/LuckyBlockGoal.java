/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.api.player.goals;

import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.player.goals.ExpCoinsGoal;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LuckyBlocks;
import org.bukkit.inventory.ItemStack;

public class LuckyBlockGoal
extends ExpCoinsGoal {
    public LuckyBlockGoal() {
        this(0, 0);
    }

    public LuckyBlockGoal(int rewardCoins, int rewardExp) {
        super(rewardCoins, rewardExp);
    }

    @Override
    public ItemStack getItem() {
        return LuckyBlocks.getLuckyBlock();
    }

    @Override
    public List<String> getText(boolean addGame) {
        String text = "&f\u0421\u043b\u043e\u043c\u0430\u0442\u044c &e" + this.getGoal() + U.plurals(this.getGoal(), " Lucky Block", " Lucky Block'a", " Lucky Block'\u043e\u0432");
        if (addGame) {
            text = text + "&f \u043d\u0430 " + ServerType.byId(this.game).getName();
        }
        return Collections.singletonList(text);
    }
}

