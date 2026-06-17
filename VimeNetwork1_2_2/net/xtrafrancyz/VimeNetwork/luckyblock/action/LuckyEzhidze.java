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

public class LuckyEzhidze
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.enchant(Items.name(Material.CHAINMAIL_CHESTPLATE, "&b\u0415\u0436\u0438\u0434\u0437\u0435", "&7\u0423\u043b\u044c\u0442\u0440\u0430 \u043e\u0434\u043d\u043e\u0440\u0430\u0437\u043e\u0432\u0430\u044f \u043e\u0442\u0434\u0430\u0447\u0430"), Enchantment.THORNS, 40);
        is.setDurability((short)(Material.CHAINMAIL_CHESTPLATE.getMaxDurability() - 1));
        drop.add(is);
    }
}

