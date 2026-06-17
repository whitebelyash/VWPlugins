/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyMathematicBook
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        drop.add(Items.enchant(Items.name(Material.BOOK, "&b\u0423\u0447\u0435\u0431\u043d\u0438\u043a \u043c\u0430\u0442\u0435\u043c\u0430\u0442\u0438\u043a\u0438", "&7\u0411\u044c\u0435\u0442 \u0433\u0440\u0430\u043d\u0438\u0442\u043e\u043c \u043d\u0430\u0443\u043a\u0438"), Enchantment.DAMAGE_ALL, 2));
    }
}

