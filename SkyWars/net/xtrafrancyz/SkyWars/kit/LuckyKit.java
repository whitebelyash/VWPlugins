/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LuckyKit
extends Kit {
    protected LuckyKit() {
        super("lucky", "\u0412\u0435\u0437\u0443\u043d\u0447\u0438\u043a", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.WOOD_SWORD, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u041b\u0443\u043a", "&e16 \u0441\u0442\u0440\u0435\u043b", "&e5 \u0437\u043e\u043b\u043e\u0442\u044b\u0445 \u044f\u0431\u043b\u043e\u043a"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.BOW)});
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLDEN_APPLE, 5)});
        player.player.getInventory().setItem(8, new ItemStack(Material.ARROW, 16));
    }
}

