/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.EnchantmentStorageMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class BlacksmithKit
extends Kit {
    protected BlacksmithKit() {
        super("blacksmith", "\u041a\u0443\u0437\u043d\u0435\u0446", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.ANVIL, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u041d\u0430\u043a\u043e\u0432\u0430\u043b\u044c\u043d\u044f", "&e\u0417\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0435 \u043a\u043d\u0438\u0433\u0438", "&9   \u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432 III", "&9   \u0417\u0430\u0449\u0438\u0442\u0430 II", "&9   \u041e\u0441\u0442\u0440\u043e\u0442\u0430 I"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ANVIL), this.getBook(Enchantment.PROTECTION_PROJECTILE, 3), this.getBook(Enchantment.PROTECTION_ENVIRONMENTAL, 2), this.getBook(Enchantment.DAMAGE_ALL, 1)});
    }

    private ItemStack getBook(Enchantment ench, int level) {
        ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta im = (EnchantmentStorageMeta)is.getItemMeta();
        im.addStoredEnchant(ench, level, true);
        is.setItemMeta((ItemMeta)im);
        return is;
    }
}

