package net.xtrafrancyz.BedWars.game.entity;

import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityAgeable;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityInsentient;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntityVillager;
import net.minecraft.server.v1_6_R3.GroupDataEntity;
import net.minecraft.server.v1_6_R3.MobEffectList;
import net.minecraft.server.v1_6_R3.PathfinderGoalInteract;
import net.minecraft.server.v1_6_R3.PathfinderGoalLookAtTradingPlayer;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;

public class TraderEntity extends EntityVillager {
   public TraderEntity(World world) {
      super(world);
      this.setCustomName(ChatColor.translateAlternateColorCodes('&', "&e&lЖлобский торговец"));
      this.setCustomNameVisible(true);
      NMSEntityUtils.clearPathfinding(this);
      this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
      this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this));
   }

   public boolean M() {
      return false;
   }

   protected void bj() {
   }

   protected void bk() {
   }

   public boolean a(EntityHuman entityHuman) {
      return true;
   }

   protected void n(Entity entity) {
   }

   protected void bw() {
   }

   protected void dropEquipment(boolean flag, int i) {
   }

   protected void dropDeathLoot(boolean flag, int i) {
      CraftEventFactory.callEntityDeathEvent(this);
   }

   public EntityAgeable createChild(EntityAgeable entityAgeable) {
      EntityVillager var2 = new EntityVillager(this.world);
      var2.a((GroupDataEntity)null);
      return var2;
   }

   static {
      NMSEntityUtils.safeRegisterCustomEntity(TraderEntity.class, "BWTrader");
   }

   private static class PathfinderGoalLookAtPlayer extends PathfinderGoalInteract {
      public PathfinderGoalLookAtPlayer(EntityInsentient entity) {
         super(entity, EntityHuman.class, 3.0F, 1.0F);
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
