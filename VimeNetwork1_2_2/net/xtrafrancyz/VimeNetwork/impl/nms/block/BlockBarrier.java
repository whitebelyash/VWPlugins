/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Material
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.Material;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.VBlock;

public class BlockBarrier
extends VBlock {
    public BlockBarrier(int i) {
        super(i, Material.SHATTERABLE);
        this.r();
        this.setResistance(6000000.0f);
        this.a(k);
        this.C();
    }

    public boolean c() {
        return false;
    }
}

