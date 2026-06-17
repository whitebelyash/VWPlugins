/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Fireworks
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.FireworkMeta
 *  org.bukkit.inventory.meta.ItemMeta
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.util.Fireworks;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class TrollKit
extends Kit {
    protected TrollKit() {
        super("troll", "\u0422\u0440\u043e\u043b\u043b\u044c", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.FIREWORK, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0410\u043b\u043c\u0430\u0437\u043d\u044b\u0439 \u0448\u043b\u0435\u043c", "&e\u041a\u043e\u043b\u044c\u0447\u0443\u0436\u043d\u044b\u0439 \u043d\u0430\u0433\u0440\u0443\u0434\u043d\u0438\u043a", "&e\u0417\u043e\u043b\u043e\u0442\u044b\u0435 \u0448\u0442\u0430\u043d\u044b", "&e\u0416\u0435\u043b\u0435\u0437\u043d\u044b\u0435 \u0431\u043e\u0442\u0438\u043d\u043a\u0438", "&e5 \u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u043e\u0432", "&e16 \u041f\u0430\u0443\u0442\u0438\u043d\u044b"});
    }

    @Override
    public void equip(PlayerInfo player) {
        ItemStack firework = new ItemStack(Material.FIREWORK, 5);
        FireworkMeta meta = (FireworkMeta)firework.getItemMeta();
        meta.addEffect(Fireworks.getRandomEffect());
        firework.setItemMeta((ItemMeta)meta);
        player.player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.WEB, 16), firework});
        player.player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.GOLD_LEGGINGS), new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.DIAMOND_HELMET)});
    }
}

