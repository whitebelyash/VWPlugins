package net.xtrafrancyz.BedWars.game.entity;

import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityCreature;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntitySheep;
import net.minecraft.server.v1_6_R3.GenericAttributes;
import net.minecraft.server.v1_6_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.BedWars.BWTeam;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_6_R3.event.CraftEventFactory;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

public class TNTSheepEntity extends EntitySheep {
   public TNTSheepEntity(World world, Player source, Player target) {
      super(world);
      BWTeam sourceTeam = PlayerInfo.get(source).team;
      this.setCustomName(ChatColor.translateAlternateColorCodes('&', sourceTeam.chatColor + "&lБешенная овца!"));
      this.setCustomNameVisible(true);
      NMSEntityUtils.clearPathfinding(this);
      this.getAttributeInstance(GenericAttributes.b).setValue((double)128.0F);
      this.getAttributeInstance(GenericAttributes.d).setValue((double)0.5F);
      this.setColor(sourceTeam.wool);
      this.setGoalTarget(((CraftPlayer)target).getHandle());
      ((Sheep)this.getBukkitEntity()).setTarget(target);
      this.goalSelector.a(0, new PathfinderAttackTarget(this, EntityHuman.class, (double)1.0F, false));
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

   static {
      NMSEntityUtils.safeRegisterCustomEntity(TNTSheepEntity.class, "TNTSheep");
   }

   private class PathfinderAttackTarget extends PathfinderGoalMeleeAttack {
      public PathfinderAttackTarget(EntityCreature entity, Class clazz, double d0, boolean flag) {
         super(entity, clazz, d0, flag);
      }

      public boolean a() {
         TNTSheepEntity.this.getNavigation().a(TNTSheepEntity.this.getGoalTarget());
         return true;
      }
   }
}
