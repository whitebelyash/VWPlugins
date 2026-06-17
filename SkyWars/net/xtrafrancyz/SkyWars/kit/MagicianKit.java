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

public class MagicianKit
extends Kit {
    protected MagicianKit() {
        super("magician", "\u0427\u0430\u0440\u043e\u0434\u0435\u0439", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.ENCHANTMENT_TABLE, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0421\u0442\u043e\u043b \u0437\u0430\u0447\u0430\u0440\u043e\u0432\u0430\u043d\u0438\u0439", "&e64 \u041f\u0443\u0437\u044b\u0440\u044c\u043a\u0430 \u043e\u043f\u044b\u0442\u0430", "&e8 \u041a\u043d\u0438\u0436\u043d\u044b\u0445 \u043f\u043e\u043b\u043e\u043a"});
    }

    @Override
    public void equip(PlayerInfo player) {
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ENCHANTMENT_TABLE), new ItemStack(Material.EXP_BOTTLE, 64), new ItemStack(Material.BOOKSHELF, 8)});
    }
}

