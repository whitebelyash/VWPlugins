/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityArrow
 *  net.minecraft.server.v1_6_R3.EntityFallingBlock
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
import net.minecraft.server.v1_6_R3.EntityFallingBlock;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;

public class LuckySpiderBow
extends LBActionItem {
    @Override
    protected void populateDrop(List<ItemStack> drop, Block block, Player player) {
        ItemStack is = Items.name(Material.BOW, "&a\u041b\u0443\u043a-\u043f\u0430\u0443\u043a", "&7\u0421\u0442\u0440\u0435\u043b\u044f\u0435\u0442 \u043f\u0430\u0443\u0442\u0438\u043d\u043e\u0439");
        is = this.lb.controller.setShootBowCallback(this, is);
        is.setDurability((short)(Material.BOW.getMaxDurability() - 9));
        drop.add(is);
        drop.add(new ItemStack(Material.ARROW, 10));
    }

    @Override
    public void onShootBow(EntityShootBowEvent event) {
        EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
        EntityFallingBlock web = new EntityFallingBlock(handle.world, handle.locX, handle.locY, handle.locZ, Material.WEB.getId());
        web.motX = handle.motX;
        web.motY = handle.motY;
        web.motZ = handle.motZ;
        web.a(false);
        web.dropItem = false;
        web.c = 1;
        web.world.addEntity((Entity)web);
        event.setProjectile((org.bukkit.entity.Entity)web.getBukkitEntity());
    }
}

