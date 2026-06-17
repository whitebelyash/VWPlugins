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

public class BlockRedSandstoneStep
extends BlockStepAbstract {
    public BlockRedSandstoneStep(int i, boolean b) {
        super(i, b, Material.STONE);
        this.c(2.0f);
        this.b(10.0f);
        BlockRedSandstoneStep.x[i] = true;
    }

    protected ItemStack d_(int i) {
        return new ItemStack(182, 2, 0);
    }

    public int getDropType(int i, Random random, int j) {
        return 182;
    }

    public String c(int i) {
        return "\u041f\u043b\u0438\u0442\u0430 \u0438\u0437 \u043a\u0440\u0430\u0441\u043d\u043e\u0433\u043e \u043f\u0435\u0441\u0447\u0430\u043d\u0438\u043a\u0430";
    }
}

