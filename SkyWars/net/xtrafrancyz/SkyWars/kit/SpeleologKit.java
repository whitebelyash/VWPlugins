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

public class SpeleologKit
extends Kit {
    protected SpeleologKit() {
        super("speleolog", "\u0421\u043f\u0435\u043b\u0435\u043e\u043b\u043e\u0433", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.IRON_PICKAXE, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e16 \u0411\u043b\u043e\u043a\u043e\u0432 \u043a\u0430\u043c\u043d\u044f", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u0430\u044f \u043a\u0438\u0440\u043a\u0430", "   &9\u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c III", "   &9\u041e\u0441\u0442\u0440\u043e\u0442\u0430 I", "   &9\u042d\u0444\u0444\u0435\u043a\u0442\u0438\u0432\u043d\u043e\u0441\u0442\u044c III"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setItem(1, Items.enchant((ItemStack)new ItemStack(Material.IRON_PICKAXE), (Object[])new Object[]{Enchantment.DIG_SPEED, 3, Enchantment.DAMAGE_ALL, 1, Enchantment.DURABILITY, 3}));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STONE, 16)});
    }
}

