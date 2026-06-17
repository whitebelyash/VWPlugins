/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Block
 *  net.minecraft.server.v1_6_R3.BlockStairs
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.BlockStairs;

public class BlockNewStairs
extends BlockStairs {
    public BlockNewStairs(int id, Block block, int metadata) {
        super(id, block, metadata);
        BlockNewStairs.x[id] = true;
    }
}

