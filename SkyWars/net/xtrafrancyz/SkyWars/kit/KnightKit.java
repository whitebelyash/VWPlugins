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

public class KnightKit
extends Kit {
    protected KnightKit() {
        super("knight", "\u0420\u044b\u0446\u0430\u0440\u044c", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.IRON_SWORD, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0410\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u0448\u043b\u0435\u043c", "&9   \u0417\u0430\u0449\u0438\u0442\u0430 \u043e\u0442 \u0441\u043d\u0430\u0440\u044f\u0434\u043e\u0432 I", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u043c\u0435\u0447", "&9   \u041e\u0441\u0442\u0440\u043e\u0442\u0430 I"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setHelmet(Items.enchant((ItemStack)new ItemStack(Material.DIAMOND_HELMET), (Object[])new Object[]{Enchantment.PROTECTION_PROJECTILE, 1}));
        player.player.getInventory().setItem(0, Items.enchant((ItemStack)new ItemStack(Material.IRON_SWORD), (Object[])new Object[]{Enchantment.DAMAGE_ALL, 1}));
    }
}

