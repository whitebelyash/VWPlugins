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

public class UnluckyBowNoChars
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.BOW, "\u041b\u0443\u043a \u0447\u0438\u0442\u0435\u0440\u0430", new String[0]);
        is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 0);
        is.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 0);
        is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
        is.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 0);
        is.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        is.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 0);
        is.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 0);
        is.addUnsafeEnchantment(Enchantment.DIG_SPEED, 0);
        is.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 0);
        is.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 0);
        drop.add(is);
        drop.add(new ItemStack(Material.ARROW, 10));
    }
}

