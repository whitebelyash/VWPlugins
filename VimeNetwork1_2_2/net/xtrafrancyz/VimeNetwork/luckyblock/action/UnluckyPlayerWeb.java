/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.block.BlockFace
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.function.Consumer;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.Material2;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnluckyPlayerWeb
extends LBAction {
    @Override
    public void onBreak(Block a, Player player) {
        U.msg((CommandSender)player, T.system("LuckyBlock", "\u041f\u043e\u043f\u0430\u043b\u0441\u044f!"));
        Location location = player.getLocation();
        Block block = location.getBlock();
        block.setType(Material.WEB);
        Consumer<BlockFace> setIfAir = face -> {
            Block b = block.getRelative(face);
            if (!Material2.isSolid(b.getType())) {
                b.setType(Material.WEB);
            }
        };
        setIfAir.accept(BlockFace.EAST);
        setIfAir.accept(BlockFace.NORTH);
        setIfAir.accept(BlockFace.NORTH_WEST);
        setIfAir.accept(BlockFace.NORTH_EAST);
        setIfAir.accept(BlockFace.WEST);
        setIfAir.accept(BlockFace.SOUTH);
        setIfAir.accept(BlockFace.SOUTH_EAST);
        setIfAir.accept(BlockFace.SOUTH_WEST);
    }
}

