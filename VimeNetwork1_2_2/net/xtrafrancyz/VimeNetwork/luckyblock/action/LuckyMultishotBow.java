/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityArrow
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  org.bukkit.Material
 *  org.bukkit.block.Block
 *  org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow
 *  org.bukkit.entity.Player
 *  org.bukkit.event.entity.EntityShootBowEvent
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityArrow;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class LuckyMultishotBow
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.BOW, "&a\u041c\u0443\u043b\u044c\u0442\u0438\u043b\u0443\u043a", "&7\u0421\u0442\u0440\u0435\u043b\u044f\u0435\u0442 \u0441\u0440\u0430\u0437\u0443 \u043d\u0435\u0441\u043a\u043e\u043b\u044c\u043a\u0438\u043c\u0438 \u0441\u0442\u0440\u0435\u043b\u0430\u043c\u0438");
        is = this.lb.controller.setShootBowCallback(this, is);
        drop.add(is);
        drop.add(new ItemStack(Material.ARROW, 10));
    }

    @Override
    public void onShootBow(EntityShootBowEvent event) {
        EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
        Consumer<Integer> copyArrow = angle -> {
            EntityArrow arrow = new EntityArrow(handle.world, handle.locX, handle.locY, handle.locZ);
            arrow.shooter = handle.shooter;
            arrow.fromPlayer = handle.fromPlayer;
            arrow.fireTicks = handle.fireTicks;
            arrow.a(handle.d());
            Reflect.set(arrow, "damage", Reflect.get(handle, "damage"));
            Reflect.set(arrow, "aw", Reflect.get(handle, "aw"));
            float yaw = handle.shooter.yaw + (float)angle.intValue();
            arrow.motX = -MathHelper.sin((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
            arrow.motZ = MathHelper.cos((float)(yaw / 180.0f * (float)Math.PI)) * MathHelper.cos((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
            arrow.motY = -MathHelper.sin((float)(handle.shooter.pitch / 180.0f * (float)Math.PI));
            arrow.shoot(arrow.motX, arrow.motY, arrow.motZ, event.getForce() * 1.5f * 2.0f, 1.0f);
            arrow.world.addEntity((Entity)arrow);
        };
        copyArrow.accept(10);
        copyArrow.accept(-10);
    }
}

