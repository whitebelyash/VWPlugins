/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.AnimalTamer
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Horse
 *  org.bukkit.entity.Horse$Color
 *  org.bukkit.entity.Horse$Style
 *  org.bukkit.entity.Horse$Variant
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.api.util.Rand;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyHorse
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        Horse horse = (Horse)block.getWorld().spawnEntity(block.getLocation().add(0.5, 0.5, 0.5), EntityType.HORSE);
        horse.setOwner((AnimalTamer)player);
        horse.setStyle(Rand.of(Horse.Style.class));
        horse.setVariant(Rand.of(Horse.Variant.class));
        horse.setColor(Rand.of(Horse.Color.class));
        horse.setTamed(true);
        horse.setOwner((AnimalTamer)player);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.getInventory().setArmor(new ItemStack(Rand.of(Material.DIAMOND_BARDING, Material.GOLD_BARDING, Material.IRON_BARDING)));
    }
}

