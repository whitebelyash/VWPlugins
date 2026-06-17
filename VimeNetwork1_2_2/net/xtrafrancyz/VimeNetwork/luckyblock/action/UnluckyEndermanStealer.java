/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Enderman
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyEndermanStealer
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        Enderman enderman = (Enderman)block.getWorld().spawnEntity(block.getLocation().add(0.5, 0.5, 0.5), EntityType.ENDERMAN);
        enderman.setCarriedMaterial(Material.SPONGE.getNewData((byte)1));
        enderman.setCustomName(ChatColor.BOLD + "\u0412\u043e\u0440\u0438\u0448\u043a\u0430");
        enderman.setCustomNameVisible(true);
    }
}

