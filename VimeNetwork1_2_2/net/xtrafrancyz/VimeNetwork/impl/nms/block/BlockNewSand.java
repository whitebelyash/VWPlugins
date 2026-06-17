/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.BlockSand
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.BlockSand;

public class BlockNewSand
extends BlockSand {
    public BlockNewSand(int i) {
        super(i);
        this.c(0.5f);
        this.a(o);
        this.c("sand");
        this.d("sand");
    }

    public int getDropData(int data) {
        return data;
    }
}

