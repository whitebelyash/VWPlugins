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

public class FarmerKit
extends Kit {
    protected FarmerKit() {
        super("farmer", "\u0424\u0435\u0440\u043c\u0435\u0440", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.EGG, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0410\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a", "&e64 \u042f\u0439\u0446\u0430", "&e\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 64)});
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.GOLDEN_APPLE)});
    }
}

