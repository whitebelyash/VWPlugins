/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.Sound
 *  org.bukkit.block.Block
 *  org.bukkit.enchantments.Enchantment
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.potion.PotionEffect
 *  org.bukkit.potion.PotionEffectType
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class LuckyGhastAxe
extends LBActionItem {
    @Override
    public void onBreak(Block block, Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 1));
        player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 1.0f, 1.0f);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0f, 1.0f), 10L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM2, 1.0f, 1.0f), 20L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_DEATH, 1.0f, 1.0f), 30L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 1.0f, 1.0f), 40L);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            if (Spectators.isEnabled() && Spectators.instance().contains(player)) {
                return;
            }
            ItemStack is = Items.name(Material.DIAMOND_AXE, "&e\u0418\u0441\u043f\u0443\u0433\u0430\u043b\u0441\u044f?", new String[0]);
            is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            is.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
            is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
            this.giveItem(player, block.getLocation().add(0.5, 0.5, 0.5), is);
        }, 60L);
    }

    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
    }
}

