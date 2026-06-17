/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.VimeNetwork.api.util.E;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnluckyPeppaChest
extends LBAction {
    @Override
    public void onBreak(Block block, Player player) {
        int rotation = MathHelper.floor((double)((double)(player.getPlayer().getLocation().getYaw() * 4.0f / 360.0f) + 0.5)) & 3;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
            int data = 0;
            if (rotation == 0) {
                data = 2;
            } else if (rotation == 1) {
                data = 5;
            } else if (rotation == 2) {
                data = 3;
            } else if (rotation == 3) {
                data = 4;
            }
            block.setTypeIdAndData(Material.CHEST.getId(), (byte)data, true);
            int holoId = VimeNetwork.holograms().createImage(new Vec3f(block.getLocation()).add(0.5f, 1.5f, 0.5f), 64, "file:texteria/peppa64.png").getId();
            this.lb.controller.setBlockInteractCallback(block, event -> {
                if (E.isRightClick(event)) {
                    U.msg((CommandSender)event.getPlayer(), T.system("LuckyBlock", "&d\u0425\u0440\u044e-\u0445\u0440\u044e!"));
                    event.setCancelled(true);
                }
            });
            this.lb.controller.setBlockBreakCallback(block, event -> {
                Hologram hologram = VimeNetwork.holograms().get(holoId);
                if (hologram != null) {
                    hologram.remove();
                }
                this.lb.controller.removeBlockBreakCallback(block);
                this.lb.controller.removeBlockInteractCallback(block);
            });
        });
    }
}

