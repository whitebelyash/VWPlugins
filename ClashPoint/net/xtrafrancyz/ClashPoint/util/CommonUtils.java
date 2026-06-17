/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.util.U
 *  org.bukkit.Color
 *  org.bukkit.GameMode
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.inventory.meta.ItemMeta
 *  org.bukkit.inventory.meta.LeatherArmorMeta
 *  org.bukkit.potion.PotionEffect
 */
package net.xtrafrancyz.ClashPoint.util;

import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

public class CommonUtils {
    public static void resetPlayer(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.setFlying(false);
        player.setAllowFlight(false);
        player.setWalkSpeed(0.2f);
        player.setExp(0.0f);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setSaturation(20.0f);
        player.setFireTicks(0);
        player.setNoDamageTicks(0);
        if (!player.isDead()) {
            player.setMaxHealth(20.0);
            player.setHealth(20.0);
        }
        for (PotionEffect pe : player.getActivePotionEffects()) {
            if (pe.getType() == null) continue;
            player.removePotionEffect(pe.getType());
        }
        U.removeArrows((Player)player);
    }

    public static ItemStack paint(ItemStack is, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)is.getItemMeta();
        meta.setColor(color);
        is.setItemMeta((ItemMeta)meta);
        return is;
    }

    public static boolean isSameBlock(Location loc1, Location loc2) {
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
}

