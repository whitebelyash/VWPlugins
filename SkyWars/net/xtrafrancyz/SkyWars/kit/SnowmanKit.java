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

public class SnowmanKit
extends Kit {
    protected SnowmanKit() {
        super("snowman", "\u0421\u043d\u0435\u0433\u043e\u0432\u0438\u043a", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.SNOW_BALL, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e16 \u0421\u043d\u0435\u0436\u043a\u043e\u0432", "&e2 \u0411\u043b\u043e\u043a\u0430 \u0441\u043d\u0435\u0433\u0430", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u0430\u044f \u043b\u043e\u043f\u0430\u0442\u0430", "&9   \u041f\u0440\u043e\u0447\u043d\u043e\u0441\u0442\u044c III", "&e\u0422\u044b\u043a\u0432\u0430"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setHelmet(new ItemStack(Material.JACK_O_LANTERN));
        player.player.getInventory().setItem(3, Items.enchant((ItemStack)new ItemStack(Material.IRON_SPADE), (Object[])new Object[]{Enchantment.DURABILITY, 3}));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.SNOW_BALL, 16), new ItemStack(Material.SNOW_BLOCK, 2), new ItemStack(Material.PUMPKIN)});
    }
}

