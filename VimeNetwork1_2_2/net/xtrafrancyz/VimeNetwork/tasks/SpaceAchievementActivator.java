/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.tasks;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class SpaceAchievementActivator
implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!(((CraftPlayer)player).getHandle().locY > 2000.0)) continue;
            VimeNetwork.getPlayer(player).getAchievements().complete(Achievement.SECRET_SPACE);
        }
    }
}

