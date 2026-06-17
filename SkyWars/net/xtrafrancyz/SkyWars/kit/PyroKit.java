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

public class PyroKit
extends Kit {
    protected PyroKit() {
        super("pyro", "\u041f\u0438\u0440\u043e\u0442\u0435\u0445\u043d\u0438\u043a", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.FLINT_AND_STEEL, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0417\u0430\u0436\u0438\u0433\u0430\u043b\u043a\u0430", "&e\u0412\u0435\u0434\u0440\u043e \u043b\u0430\u0432\u044b", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a", "&9   \u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432 V", "&9   \u041e\u0433\u043d\u0435\u0443\u043f\u043e\u0440\u043d\u043e\u0441\u0442\u044c II"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setChestplate(Items.enchant((ItemStack)new ItemStack(Material.IRON_CHESTPLATE), (Object[])new Object[]{Enchantment.PROTECTION_PROJECTILE, 5, Enchantment.PROTECTION_FIRE, 2}));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.FLINT_AND_STEEL), new ItemStack(Material.LAVA_BUCKET)});
    }
}

