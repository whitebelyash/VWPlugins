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

public class EcologKit
extends Kit {
    protected EcologKit() {
        super("ecolog", "\u042d\u043a\u043e\u043b\u043e\u0433", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.LOG, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e8 \u0434\u0443\u0431\u0430", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u0442\u043e\u043f\u043e\u0440", "   &9\u041e\u0441\u0442\u0440\u043e\u0442\u0430 I", "   &9\u042d\u0444\u0444\u0435\u043a\u0442\u0438\u0432\u043d\u043e\u0441\u0442\u044c I"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setItem(2, Items.enchant((ItemStack)new ItemStack(Material.IRON_AXE), (Object[])new Object[]{Enchantment.DAMAGE_ALL, 1, Enchantment.DIG_SPEED, 1}));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.LOG, 8)});
    }
}

