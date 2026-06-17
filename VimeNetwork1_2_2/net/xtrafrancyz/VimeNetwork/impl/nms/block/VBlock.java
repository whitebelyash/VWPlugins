/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Block
 *  net.minecraft.server.v1_6_R3.Material
 *  net.minecraft.server.v1_6_R3.StepSound
 */
package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.Material;
import net.minecraft.server.v1_6_R3.StepSound;

public class VBlock
extends Block {
    private boolean dropExactMeta = false;

    public VBlock(int i, Material material) {
        super(i, material);
    }

    public int getDropData(int i) {
        return this.dropExactMeta ? i : 0;
    }

    public VBlock dropExactMeta() {
        this.dropExactMeta = true;
        return this;
    }

    public VBlock setStepSound(StepSound sound) {
        this.a(sound);
        return this;
    }

    public VBlock setHardness(float val) {
        this.c(val);
        return this;
    }

    public VBlock setResistance(float val) {
        this.b(val);
        return this;
    }

    public VBlock setLightValue(float val) {
        this.a(val);
        return this;
    }
}

