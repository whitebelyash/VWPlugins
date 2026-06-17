/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Material
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.Material;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.VBlock;

public class BlockNewStone
extends VBlock {
    public BlockNewStone(int id) {
        super(id, Material.STONE);
        this.c(1.5f);
        this.b(10.0f);
        this.a(k);
        this.c("stone");
        this.d("stone");
        this.dropExactMeta();
    }

    public int getDropType(int meta, Random random, int j) {
        if (meta == 0) {
            return BlockNewStone.COBBLESTONE.id;
        }
        return this.id;
    }
}

