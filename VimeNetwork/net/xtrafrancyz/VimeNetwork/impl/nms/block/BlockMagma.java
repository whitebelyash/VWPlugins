package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.AxisAlignedBB;
import net.minecraft.server.v1_6_R3.DamageSource;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.Material;
import net.minecraft.server.v1_6_R3.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class BlockMagma extends VBlock {
   public BlockMagma(int i) {
      super(i, Material.STONE);
      this.setLightValue(0.2F);
      this.setHardness(0.5F);
   }

   public AxisAlignedBB b(World world, int x, int y, int z) {
      float f = 0.01F;
      return AxisAlignedBB.a().a((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)((float)(y + 1) - f), (double)((float)(z + 1) - f));
   }

   public void a(World world, int x, int y, int z, Entity entity) {
      if (entity instanceof EntityLiving) {
         CraftEntity damagee = entity.getBukkitEntity();
         if (damagee instanceof Player && ((Player)damagee).isSneaking()) {
            return;
         }

         Block damager = world.getWorld().getBlockAt(x, y, z);
         EntityDamageByBlockEvent event = new EntityDamageByBlockEvent(damager, damagee, DamageCause.CONTACT, (double)1.0F);
         world.getServer().getPluginManager().callEvent(event);
         if (!event.isCancelled()) {
            damagee.setLastDamageCause(event);
            entity.damageEntity(DamageSource.FIRE, (float)event.getDamage());
         }
      } else {
         entity.damageEntity(DamageSource.FIRE, 1.0F);
      }

   }
}
