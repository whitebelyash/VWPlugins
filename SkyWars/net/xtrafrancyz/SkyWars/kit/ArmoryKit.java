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

public class ArmoryKit
extends Kit {
    protected ArmoryKit() {
        super("armory", "\u0411\u0440\u043e\u043d\u0435\u043d\u043e\u0441\u0435\u0446", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.GOLD_CHESTPLATE, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0439 \u0441\u0435\u0442 \u0431\u0440\u043e\u043d\u0438"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.IRON_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)});
    }
}

