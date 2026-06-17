package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
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

public class UnluckyTroloBow extends LBActionItem {
   protected void populateDrop(List drop, Block block, Player player) {
      ItemStack is = Items.name(Material.BOW, "&aТрололук", "&7Даёт врагу понять что ты не шутишь");
      is = this.lb.controller.setShootBowCallback(this, is);
      drop.add(is);
      drop.add(new ItemStack(Material.ARROW, 10));
   }

   public void onShootBow(EntityShootBowEvent event) {
      EntityArrow handle = ((CraftArrow)event.getProjectile()).getHandle();
      EntitySnowball snowball = new EntitySnowball(handle.world, handle.locX, handle.locY, handle.locZ);
      snowball.shooter = (EntityLiving)handle.shooter;
      snowball.motX = handle.motX;
      snowball.motY = handle.motY;
      snowball.motZ = handle.motZ;
      float yaw = event.getEntity().getLocation().getYaw();
      snowball.motX = (double)(-MathHelper.sin(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(handle.shooter.pitch / 180.0F * (float)Math.PI));
      snowball.motZ = (double)(MathHelper.cos(yaw / 180.0F * (float)Math.PI) * MathHelper.cos(handle.shooter.pitch / 180.0F * (float)Math.PI));
      snowball.motY = (double)(-MathHelper.sin(handle.shooter.pitch / 180.0F * (float)Math.PI));
      snowball.shoot(snowball.motX, snowball.motY, snowball.motZ, event.getForce() * 1.5F * 2.0F, 1.0F);
      snowball.world.addEntity(snowball);
      event.setProjectile(snowball.getBukkitEntity());
   }
}
