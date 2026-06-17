/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityArrow
 *  net.minecraft.server.v1_6_R3.EntityLiving
 *  net.minecraft.server.v1_6_R3.EntitySnowball
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityArrow;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntitySnowball;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class UnluckyTroloBow
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.BOW, "&a\u0422\u0440\u043e\u043b\u043e\u043b\u0443\u043a", "&7\u0414\u0430\u0451\u0442 \u0432\u0440\u0430\u0433\u0443 \u043f\u043e\u043d\u044f\u0442\u044c \u0447\u0442\u043e \u0442\u044b \u043d\u0435 \u0448\u0443\u0442\u0438\u0448\u044c");
        is = this.lb.controller.setShootBowCallback(this, is);
        drop.add(is);
        drop.add(new ItemStack(Material.ARROW, 10));
    }

    @Override
    public void onShootBow(EntityShootBowEvent event) {
        EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
        EntitySnowball snowball = new EntitySnowball(handle.world, handle.locX, handle.locY, handle.locZ);
        snowball.shooter = (EntityLiving)handle.shooter;
        snowball.motX = handle.motX;
        snowball.motY = handle.motY;
        snowball.motZ = handle.motZ;
        float yaw = event.getEntity().getLocation().getYaw();
        snowball.motX = -MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
        snowball.motZ = MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
        snowball.motY = -MathHelper.sin((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
        snowball.shoot(snowball.motX, snowball.motY, snowball.motZ, event.getForce() * 1.5f * 2.0f, 1.0f);
        snowball.world.addEntity((Entity)snowball);
        event.setProjectile((org.bukkit.entity.Entity)snowball.getBukkitEntity());
    }
}

