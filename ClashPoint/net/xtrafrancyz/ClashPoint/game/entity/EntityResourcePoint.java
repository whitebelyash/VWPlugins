/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.DamageSource
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityEnderCrystal
 *  net.minecraft.server.v1_6_R3.EntityPlayer
 *  net.minecraft.server.v1_6_R3.World
 *  net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.ClashPoint.game.entity;

import net.minecraft.server.v1_6_R3.DamageSource;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityEnderCrystal;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.ClashPoint.object.ResourcePoint;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import org.bukkit.entity.Player;

public class EntityResourcePoint
extends EntityEnderCrystal {
    public final ResourcePoint rp;

    public EntityResourcePoint(World world) {
        this(world, null);
        this.die();
    }

    public EntityResourcePoint(World world, ResourcePoint rp) {
        super(world);
        this.rp = rp;
        this.m = false;
    }

    public void l_() {
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();
        if (entity instanceof EntityPlayer) {
            this.rp.onDamage((Player)entity.getBukkitEntity(), f);
        }
        return true;
    }

    public boolean L() {
        return false;
    }

    static {
        NMSEntityUtils.safeRegisterCustomEntity(EntityResourcePoint.class, (String)"RPCrystal");
    }
}

