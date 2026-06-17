/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.BlockStepAbstract
 *  net.minecraft.server.v1_6_R3.ItemStack
 *  net.minecraft.server.v1_6_R3.Material
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.BlockStepAbstract;
import net.minecraft.server.v1_6_R3.ItemStack;
import net.minecraft.server.v1_6_R3.Material;

public class BlockPurpurStep
extends BlockStepAbstract {
    public BlockPurpurStep(int i, boolean b) {
        super(i, b, Material.STONE);
        this.c(2.0f);
        this.b(10.0f);
        BlockPurpurStep.x[i] = true;
    }

    protected ItemStack d_(int i) {
        return new ItemStack(205, 2, 0);
    }

    public int getDropType(int i, Random random, int j) {
        return 205;
    }

    public String c(int i) {
        return "\u041f\u0443\u0440\u043f\u0443\u0440\u043e\u0432\u0430\u044f \u043f\u043b\u0438\u0442\u0430";
    }
}

