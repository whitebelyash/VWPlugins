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

public class BlockPackedIce
extends VBlock {
    public BlockPackedIce(int i) {
        super(i, Material.ICE);
        this.frictionFactor = 0.98f;
        this.setHardness(0.5f);
        this.a(m);
    }

    public int getDropCount(int i, Random random) {
        return 0;
    }
}

