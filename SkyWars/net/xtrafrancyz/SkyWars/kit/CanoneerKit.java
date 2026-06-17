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

public class CanoneerKit
extends Kit {
    protected CanoneerKit() {
        super("canoneer", "\u041a\u0430\u043d\u043e\u043d\u0438\u0440", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.TNT, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0435 \u0448\u0442\u0430\u043d\u044b", "&9   \u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432 III", "&e16 TNT", "&e32 \u0420\u0435\u0434\u0441\u0442\u043e\u0443\u043d \u043f\u044b\u043b\u0438", "&e\u0412\u0435\u0434\u0440\u043e \u0441 \u0432\u043e\u0434\u043e\u0439"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setLeggings(Items.enchant((ItemStack)new ItemStack(Material.IRON_LEGGINGS), (Object[])new Object[]{Enchantment.PROTECTION_PROJECTILE, 3}));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.TNT, 16), new ItemStack(Material.REDSTONE, 32), new ItemStack(Material.WATER_BUCKET)});
    }
}

