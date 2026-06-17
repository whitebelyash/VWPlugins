package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import java.util.function.Consumer;
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

public class LuckyMultishotBow extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(Material.BOW, "&aМультилук", "&7Стреляет сразу несколькими стрелами");
      is = this.lb.controller.setShootBowCallback(this, is);
      drop.add(is);
      drop.add(new ItemStack(Material.ARROW, 10));
   }

   public void onShootBow(EntityShootBowEvent event) {
      EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
      Consumer<Integer> copyArrow = (angle) -> {
         EntityArrow arrow = new EntityArrow(handle.world, handle.locX, handle.locY, handle.locZ);
         arrow.shooter = handle.shooter;
         arrow.fromPlayer = handle.fromPlayer;
         arrow.fireTicks = handle.fireTicks;
         arrow.a(handle.d());
         Reflect.set((Object)arrow, "damage", Reflect.get((Object)handle, "damage"));
         Reflect.set((Object)arrow, "aw", Reflect.get((Object)handle, "aw"));
         float yaw = handle.shooter.yaw + (float)angle;
         arrow.motX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(handle.shooter.pitch / 180.0F * (float)Math.PI));
         arrow.motZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(handle.shooter.pitch / 180.0F * (float)Math.PI));
         arrow.motY = (double)(-MathHelper.sin(handle.shooter.pitch / 180.0F * (float)Math.PI));
         arrow.shoot(arrow.motX, arrow.motY, arrow.motZ, event.getForce() * 1.5F * 2.0F, 1.0F);
         arrow.world.addEntity(arrow);
      };
      copyArrow.accept(10);
      copyArrow.accept(-10);
   }
}
