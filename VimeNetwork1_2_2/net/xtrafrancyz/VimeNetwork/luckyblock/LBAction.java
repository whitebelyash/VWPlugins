/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.luckyblock;

import net.xtrafrancyz.VimeNetwork.luckyblock.LuckyBlocks;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class LBAction {
    public int id;
    public Plugin plugin;
    public LuckyBlocks lb;

    public void register() {
    }

    public abstract void onBreak(Block var1, Player var2);
}

