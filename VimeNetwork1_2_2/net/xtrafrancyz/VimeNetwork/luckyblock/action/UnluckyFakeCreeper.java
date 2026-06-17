/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftCreeper
 *  org.bukkit.entity.Creeper
 *  org.bukkit.entity.EntityType
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftCreeper;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UnluckyFakeCreeper
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        Location loc = block.getLocation();
        Creeper creeper = (Creeper)loc.getWorld().spawnEntity(loc.add(0.5, 0.5, 0.5), EntityType.CREEPER);
        Reflect.set(((CraftCreeper)creeper).getHandle(), "maxFuseTicks", (Object)100);
    }
}

