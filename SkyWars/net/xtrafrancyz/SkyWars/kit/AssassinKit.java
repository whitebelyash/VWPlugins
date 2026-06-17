/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.Items
 *  org.bukkit.Material
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.PotionMeta
 *  org.bukkit.potion.Potion
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 *  org.bukkit.potion.PotionType
 */
package net.xtrafrancyz.SkyWars.kit;

import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.SkyWars.kit.Kit;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class AssassinKit
extends Kit {
    protected AssassinKit() {
        super("assassin", "\u0410\u0441\u0441\u0430\u0441\u0438\u043d", 15000);
    }

    @Override
    protected ItemStack getItem() {
        return Items.name((Material)Material.CARROT_STICK, (String)this.name, (String[])new String[]{"\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0432\u0435\u0449\u0438:", "&e\u0412\u0437\u0440\u044b\u0432\u043d\u044b\u0435 \u0437\u0435\u043b\u044c\u044f", "&9   2 x \u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c II", "&9   2 x \u041f\u0440\u044b\u0433\u0443\u0447\u0435\u0441\u0442\u044c I"});
    }

    @Override
    public void equip(PlayerInfo player) {
        Potion p = new Potion(PotionType.SPEED);
        p.setSplash(true);
        ItemStack speed = p.toItemStack(2);
        PotionMeta meta = (PotionMeta)speed.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1), true);
        speed.setItemMeta((ItemMeta)meta);
        p = new Potion(PotionType.SPEED);
        p.setSplash(true);
        ItemStack jump = p.toItemStack(2);
        meta = (PotionMeta)jump.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 1200, 0), true);
        jump.setItemMeta((ItemMeta)meta);
        player.player.getInventory().addItem(new ItemStack[]{speed, jump});
    }
}

