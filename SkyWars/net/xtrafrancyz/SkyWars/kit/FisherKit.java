/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class FisherKit
extends Kit {
    protected FisherKit() {
        super("fisher", "\u0420\u044b\u0431\u0430\u043a", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.FISHING_ROD, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0423\u0434\u043e\u0447\u043a\u0430", "   &9\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c III", "   &9\u041e\u0442\u0434\u0430\u0447\u0430 I", "&e16 \u0416\u0430\u0440\u0435\u043d\u043e\u0439 \u0440\u044b\u0431\u044b"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().addItem(new ItemStack[]{Items.enchant((ItemStack)new ItemStack(Material.FISHING_ROD), (Object[])new Object[]{Enchantment.DURABILITY, 3, Enchantment.KNOCKBACK, 1}), new ItemStack(Material.COOKED_FISH, 16)});
    }
}

