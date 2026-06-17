/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyGhast
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        Location loc = block.getLocation();
        loc.getWorld().spawnEntity(loc.add(0.5, 1.0, 0.5), EntityType.GHAST);
    }
}

