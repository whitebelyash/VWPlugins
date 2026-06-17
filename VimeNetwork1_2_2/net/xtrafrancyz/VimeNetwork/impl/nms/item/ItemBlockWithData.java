/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.ItemBlock
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.item;

import net.minecraft.server.v1_6_R3.ItemBlock;

public class ItemBlockWithData
extends ItemBlock {
    public ItemBlockWithData(int i) {
        super(i);
        this.a(true);
    }

    public int filterData(int i) {
        return i;
    }
}

