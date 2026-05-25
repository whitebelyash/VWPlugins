package net.xtrafrancyz.VimeNetwork.impl.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_6_R3.DataWatcher;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn;
import net.minecraft.server.v1_6_R3.Packet23VehicleSpawn;
import net.minecraft.server.v1_6_R3.Packet29DestroyEntity;
import net.minecraft.server.v1_6_R3.Packet34EntityTeleport;
import net.minecraft.server.v1_6_R3.Packet35EntityHeadRotation;
import net.minecraft.server.v1_6_R3.Packet39AttachEntity;
import net.minecraft.server.v1_6_R3.Packet40EntityMetadata;
import net.minecraft.server.v1_6_R3.Packet5EntityEquipment;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.npc.NPC;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

public class VNPC implements NPC {
   private final int entityId;
   private String name;
   World world;
   double x;
   double y;
   double z;
   float yaw;
   float pitch;
   int viewDistance;
   Set visibleTo;
   private ItemStack[] armor;
   private ItemStack itemInHand;
   private DataWatcher dataWatcher;
   private Map data;
   private boolean nameVisible = true;
   private int itemEntityId = -1;

   VNPC(String name, Location loc) {
      this.name = name;
      this.entityId = nextEntityId();
      this.world = loc.getWorld();
      this.visibleTo = new HashSet();
      this.dataWatcher = new DataWatcher();
      this.armor = new ItemStack[4];
      this.viewDistance = ((CraftWorld)loc.getWorld()).getHandle().spigotConfig.playerTrackingRange;
      this.data = new HashMap();
      this.setLocation(loc);
      this.dataWatcher.a(0, (byte)0);
      this.dataWatcher.a(1, (short)300);
      this.dataWatcher.a(7, 0);
      this.dataWatcher.a(8, (byte)0);
      this.dataWatcher.a(9, (byte)0);
      this.dataWatcher.a(6, 1.0F);
      this.dataWatcher.a(16, (byte)0);
      this.dataWatcher.a(17, 0.0F);
      this.dataWatcher.a(18, 0);
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void setLocation(Location loc) {
      this.x = loc.getX();
      this.y = loc.getY();
      this.z = loc.getZ();
      this.yaw = loc.getYaw();
      this.pitch = loc.getPitch();
      if (!this.visibleTo.isEmpty()) {
         Packet34EntityTeleport packet = new Packet34EntityTeleport();
         packet.a = this.entityId;
         packet.b = MathHelper.floor(this.x * (double)32.0F);
         packet.c = MathHelper.floor(this.y * (double)32.0F);
         packet.d = MathHelper.floor(this.z * (double)32.0F);
         packet.e = (byte)((int)(this.yaw * 256.0F / 360.0F));
         packet.f = (byte)((int)(this.pitch * 256.0F / 360.0F));
         Packet35EntityHeadRotation packet2 = new Packet35EntityHeadRotation(this.entityId, (byte)MathHelper.d(this.yaw * 256.0F / 360.0F));

         for(Player player : this.visibleTo) {
            U.sendPacket(player, packet);
            U.sendPacket(player, packet2);
         }
      }

   }

   public void setItemInHand(ItemStack itemInHand) {
      this.itemInHand = itemInHand;
      if (!this.visibleTo.isEmpty()) {
         Packet5EntityEquipment packet = new Packet5EntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy(itemInHand));

         for(Player player : this.visibleTo) {
            U.sendPacket(player, packet);
         }
      }

   }

   public void setArmor(ItemStack[] armor) {
      if (armor.length == 4) {
         this.armor = armor;
      }
   }

   public void setHelmet(ItemStack helmet) {
      this.armor[3] = helmet;
      this.broadcastArmorPacket(3);
   }

   public void setChestplate(ItemStack chestplate) {
      this.armor[2] = chestplate;
      this.broadcastArmorPacket(2);
   }

   public void setLeggings(ItemStack leggings) {
      this.armor[1] = leggings;
      this.broadcastArmorPacket(1);
   }

   public void setBoots(ItemStack boots) {
      this.armor[0] = boots;
      this.broadcastArmorPacket(0);
   }

   public void setViewDistance(int blocks) {
      this.viewDistance = blocks;
   }

   public void setNameVisible(boolean visible) {
      if (this.nameVisible && !visible) {
         for(Player player : this.visibleTo) {
            this.sendHideNamePacket(player);
         }
      }

      if (!this.nameVisible && visible && !this.visibleTo.isEmpty()) {
         for(Player player : this.visibleTo) {
            U.sendPacket(player, new Packet29DestroyEntity(new int[]{this.getItemEntityId()}));
         }
      }

      this.nameVisible = visible;
   }

   public void remove() {
      VimeNetwork.npcs().remove(this.entityId);
   }

   public Location getLocation() {
      return new Location(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
   }

   public String getName() {
      return this.name;
   }

   public void setData(String key, Object value) {
      this.data.put(key, value);
   }

   public Object getData(String key) {
      return this.data.get(key);
   }

   double distanceSquared(Location loc) {
      return NumberConversions.square(this.x - loc.getX()) + NumberConversions.square(this.y - loc.getY()) + NumberConversions.square(this.z - loc.getZ());
   }

   private int getItemEntityId() {
      if (this.itemEntityId == -1) {
         this.itemEntityId = nextEntityId();
      }

      return this.itemEntityId;
   }

   private void broadcastArmorPacket(int... slots) {
      if (!this.visibleTo.isEmpty()) {
         Packet5EntityEquipment[] packets = new Packet5EntityEquipment[slots.length];

         for(int i = 0; i < slots.length; ++i) {
            packets[i] = new Packet5EntityEquipment(this.entityId, slots[i] + 1, CraftItemStack.asNMSCopy(this.armor[slots[i]]));
         }

         for(Player player : this.visibleTo) {
            for(Packet5EntityEquipment packet : packets) {
               U.sendPacket(player, packet);
            }
         }
      }

   }

   private void sendHideNamePacket(Player player) {
      Packet23VehicleSpawn itemSpawn = new Packet23VehicleSpawn();
      itemSpawn.a = this.getItemEntityId();
      itemSpawn.j = 2;
      itemSpawn.k = 1;
      itemSpawn.b = MathHelper.floor(this.x * (double)32.0F);
      itemSpawn.c = MathHelper.floor((this.y + (double)1.0F) * (double)32.0F);
      itemSpawn.d = MathHelper.floor(this.z * (double)32.0F);
      itemSpawn.h = 0;
      itemSpawn.i = 0;
      itemSpawn.e = 0;
      itemSpawn.f = 0;
      itemSpawn.g = 0;
      U.sendPacket(player, itemSpawn);
      DataWatcher watcher = new DataWatcher();
      watcher.a(10, new net.minecraft.server.v1_6_R3.ItemStack(77, 1, 0));
      U.sendPacket(player, new Packet40EntityMetadata(this.itemEntityId, watcher, true));
      Packet39AttachEntity attachEntity = new Packet39AttachEntity();
      attachEntity.a = 0;
      attachEntity.b = this.itemEntityId;
      attachEntity.c = this.entityId;
      U.sendPacket(player, attachEntity);
   }

   public void sendSpawnPacket(Player player) {
      Packet20NamedEntitySpawn spawn = new Packet20NamedEntitySpawn();
      spawn.a = this.entityId;
      spawn.b = this.name;
      spawn.c = MathHelper.floor(this.x * (double)32.0F);
      spawn.d = MathHelper.floor(this.y * (double)32.0F);
      spawn.e = MathHelper.floor(this.z * (double)32.0F);
      spawn.f = (byte)((int)(this.yaw * 256.0F / 360.0F));
      spawn.g = (byte)((int)(this.pitch * 256.0F / 360.0F));
      spawn.h = this.itemInHand == null ? 0 : this.itemInHand.getTypeId();
      Reflect.set((Object)spawn, "i", this.dataWatcher);
      U.sendPacket(player, spawn);
      if (this.itemInHand != null) {
         U.sendPacket(player, new Packet5EntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy(this.itemInHand)));
      }

      for(int i = 0; i < 4; ++i) {
         if (this.armor[i] != null) {
            U.sendPacket(player, new Packet5EntityEquipment(this.entityId, i + 1, CraftItemStack.asNMSCopy(this.armor[i])));
         }
      }

      Bukkit.getScheduler().scheduleSyncDelayedTask(VNPlugin.instance(), () -> {
         float yaw0 = this.yaw % 360.0F;
         if (yaw0 >= 180.0F) {
            yaw0 -= 45.0F;
         } else {
            yaw0 += 45.0F;
         }

         Packet34EntityTeleport packet = new Packet34EntityTeleport();
         packet.a = this.entityId;
         packet.b = MathHelper.floor(this.x * (double)32.0F);
         packet.c = MathHelper.floor(this.y * (double)32.0F);
         packet.d = MathHelper.floor(this.z * (double)32.0F);
         packet.e = (byte)((int)(yaw0 * 256.0F / 360.0F));
         packet.f = (byte)((int)(this.pitch * 256.0F / 360.0F));
         U.sendPacket(player, packet);
         U.sendPacket(player, new Packet35EntityHeadRotation(this.entityId, (byte)MathHelper.d(this.yaw * 256.0F / 360.0F)));
      }, 3L);
      if (!this.nameVisible) {
         this.sendHideNamePacket(player);
      }

   }

   public void sendRemovePacket(Player player) {
      if (this.itemEntityId == -1) {
         U.sendPacket(player, new Packet29DestroyEntity(new int[]{this.entityId}));
      } else {
         U.sendPacket(player, new Packet29DestroyEntity(new int[]{this.entityId, this.itemEntityId}));
      }

   }

   private static int nextEntityId() {
      int id = (Integer)Reflect.get(Entity.class, "entityCount");
      Reflect.set((Class)Entity.class, "entityCount", id + 1);
      return id;
   }
}
