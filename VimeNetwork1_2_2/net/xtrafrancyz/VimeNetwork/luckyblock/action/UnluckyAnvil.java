/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.util.Vector
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class UnluckyAnvil
extends LBAction {
    @Override
    public void onBreak(Block block, Player p) {
        Location loc = block.getLocation();
        loc.add(0.5, 0.5, 0.5);
        loc.getWorld().spawnFallingBlock(loc.clone().add(0.0, 4.5, 0.0), Material.ANVIL, (byte)0);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.lb.getPlugin(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Location loc2 = player.getLocation();
                double distanceSquared = loc2.distanceSquared(loc);
                if (!(distanceSquared <= 25.0)) continue;
                Vector velocity = new Vector();
                velocity.setX(loc2.getX() - loc.getX());
                velocity.setZ(loc2.getZ() - loc.getZ());
                velocity.normalize();
                velocity.setY(0.5);
                velocity.multiply(0.6 + (25.0 - distanceSquared) / 25.0);
                player.setVelocity(velocity);
                U.msg((CommandSender)player, T.system("LuckyBlock", "&c\u0411\u0410\u0411\u0410\u0425!"));
            }
        }, 18L);
    }
}

