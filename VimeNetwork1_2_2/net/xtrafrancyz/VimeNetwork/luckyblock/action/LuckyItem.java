/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import java.util.function.Supplier;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyItem
extends LBActionItem {
    private ItemStack[] is = null;
    private Supplier<ItemStack>[] supplier = null;
    private Supplier<List<ItemStack>> supplier2 = null;

    public LuckyItem(ItemStack ... is) {
        this.is = is;
    }

    @SafeVarargs
    public LuckyItem(Supplier<ItemStack> ... supplier) {
        this.supplier = supplier;
    }

    public LuckyItem(Supplier<List<ItemStack>> supplier) {
        this.supplier2 = supplier;
    }

    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        if (this.is != null) {
            for (ItemStack is0 : this.is) {
                drop.add(is0.clone());
            }
        } else if (this.supplier != null) {
            for (Supplier<ItemStack> supplier0 : this.supplier) {
                drop.add(supplier0.get());
            }
        } else {
            for (ItemStack is : this.supplier2.get()) {
                drop.add(is);
            }
        }
    }
}

