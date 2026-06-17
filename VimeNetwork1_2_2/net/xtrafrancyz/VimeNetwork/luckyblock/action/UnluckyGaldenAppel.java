/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.player.PlayerItemConsumeEvent
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class UnluckyGaldenAppel
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(new ItemStack(Material.GOLDEN_APPLE, 1, 1), "&e\u0417\u0430\u043b\u0430\u0442\u043e\u0435 \u044f\u0431\u043b\u0430\u043a\u043e", "&7\u041a\u0430\u043a\u043e\u0435-\u0442\u043e \u043f\u043e\u0434\u043e\u0437\u0440\u0438\u0442\u0435\u043b\u044c\u043d\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435...");
        is = this.lb.controller.setConsumeCallback(this, is);
        drop.add(is);
    }

    @Override
    public void onItemConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
        double health = event.getPlayer().getHealth();
        if (health - 5.0 <= 1.0) {
            event.getPlayer().setHealth(1.0);
        } else {
            event.getPlayer().setHealth(health - 2.0);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            event.getPlayer().removePotionEffect(PotionEffectType.REGENERATION);
            event.getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            event.getPlayer().removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
            event.getPlayer().removePotionEffect(PotionEffectType.ABSORPTION);
        });
    }
}

