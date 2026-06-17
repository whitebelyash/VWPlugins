/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityAgeable
 *  net.minecraft.server.v1_6_R3.EntityHuman
 *  net.minecraft.server.v1_6_R3.EntityInsentient
 *  net.minecraft.server.v1_6_R3.EntityLiving
 *  net.minecraft.server.v1_6_R3.EntityVillager
 *  net.minecraft.server.v1_6_R3.GroupDataEntity
 *  net.minecraft.server.v1_6_R3.MobEffectList
 *  net.minecraft.server.v1_6_R3.PathfinderGoal
 *  net.minecraft.server.v1_6_R3.PathfinderGoalInteract
 *  net.minecraft.server.v1_6_R3.PathfinderGoalLookAtTradingPlayer
 *  net.minecraft.server.v1_6_R3.World
 *  net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils
 *  org.bukkit.ChatColor
 *  org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.ClashPoint.game.entity;

import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityAgeable;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityInsentient;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntityVillager;
import net.minecraft.server.v1_6_R3.GroupDataEntity;
import net.minecraft.server.v1_6_R3.MobEffectList;
import net.minecraft.server.v1_6_R3.PathfinderGoal;
import net.minecraft.server.v1_6_R3.PathfinderGoalInteract;
import net.minecraft.server.v1_6_R3.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;

public class TraderEntity
extends EntityVillager {
    private final Consumer<Player> action;

    public TraderEntity(World world) {
        super(world);
        this.die();
        this.action = null;
    }

    public TraderEntity(World world, String name, Consumer<Player> action) {
        super(world);
        this.action = action;
        this.setCustomName(ChatColor.translateAlternateColorCodes((char)'&', (String)name));
        this.setCustomNameVisible(true);
        NMSEntityUtils.clearPathfinding((EntityInsentient)this);
        this.goalSelector.a(1, (PathfinderGoal)new PathfinderGoalLookAtTradingPlayer((EntityVillager)this));
        this.goalSelector.a(9, (PathfinderGoal)new PathfinderGoalLookAtPlayer((EntityInsentient)this));
    }

    public boolean M() {
        return false;
    }

    protected void bj() {
    }

    protected void bk() {
    }

    public boolean a(EntityHuman entityHuman) {
        this.action.accept((Player)entityHuman.getBukkitEntity());
        return true;
    }

    protected void n(Entity entity) {
    }

    protected void bw() {
    }

    protected void dropEquipment(boolean flag, int i) {
    }

    protected void dropDeathLoot(boolean flag, int i) {
        CraftEventFactory.callEntityDeathEvent((EntityLiving)this);
    }

    public EntityAgeable createChild(EntityAgeable entityAgeable) {
        EntityVillager var2 = new EntityVillager(this.world);
        var2.a((GroupDataEntity)null);
        return var2;
    }

    static {
        NMSEntityUtils.safeRegisterCustomEntity(TraderEntity.class, (String)"CPTrader");
    }

    private static class PathfinderGoalLookAtPlayer
    extends PathfinderGoalInteract {
        public PathfinderGoalLookAtPlayer(EntityInsentient entity) {
            super(entity, EntityHuman.class, 3.0f, 1.0f);
        }

        public boolean a() {
            boolean flag = super.a();
            if (flag && ((EntityLiving)this.a).hasEffect(MobEffectList.INVISIBILITY)) {
                this.a = null;
                flag = false;
            }
            return flag;
        }
    }
}

