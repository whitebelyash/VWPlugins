/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.DataWatcher
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.ItemStack
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn
 *  net.minecraft.server.v1_6_R3.Packet23VehicleSpawn
 *  net.minecraft.server.v1_6_R3.Packet29DestroyEntity
 *  net.minecraft.server.v1_6_R3.Packet34EntityTeleport
 *  net.minecraft.server.v1_6_R3.Packet35EntityHeadRotation
 *  net.minecraft.server.v1_6_R3.Packet39AttachEntity
 *  net.minecraft.server.v1_6_R3.Packet40EntityMetadata
 *  net.minecraft.server.v1_6_R3.Packet5EntityEquipment
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 *  org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.util.NumberConversions
 */
package net.xtrafrancyz.VimeNetwork.impl.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.v1_6_R3.DataWatcher;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.minecraft.server.v1_6_R3.Packet;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

public class VNPC
implements NPC {
    private final int entityId;
    private String name;
    World world;
    double x;
    double y;
    double z;
    float yaw;
    float pitch;
    int viewDistance;
    Set<Player> visibleTo;
    private ItemStack[] armor;
    private ItemStack itemInHand;
    private DataWatcher dataWatcher;
    private Map<String, Object> data;
    private boolean nameVisible = true;
    private int itemEntityId = -1;

    VNPC(String name, Location loc) {
        this.name = name;
        this.entityId = VNPC.nextEntityId();
        this.world = loc.getWorld();
        this.visibleTo = new HashSet<Player>();
        this.dataWatcher = new DataWatcher();
        this.armor = new ItemStack[4];
        this.viewDistance = ((CraftWorld)loc.getWorld()).getHandle().spigotConfig.playerTrackingRange;
        this.data = new HashMap<String, Object>();
        this.setLocation(loc);
        this.dataWatcher.a(0, (Object)0);
        this.dataWatcher.a(1, (Object)300);
        this.dataWatcher.a(7, (Object)0);
        this.dataWatcher.a(8, (Object)0);
        this.dataWatcher.a(9, (Object)0);
        this.dataWatcher.a(6, (Object)Float.valueOf(1.0f));
        this.dataWatcher.a(16, (Object)0);
        this.dataWatcher.a(17, (Object)Float.valueOf(0.0f));
        this.dataWatcher.a(18, (Object)0);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getEntityId() {
        return this.entityId;
    }

    @Override
    public void setLocation(Location loc) {
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
        if (!this.visibleTo.isEmpty()) {
            Packet34EntityTeleport packet = new Packet34EntityTeleport();
            packet.a = this.entityId;
            packet.b = MathHelper.floor((double)(this.x * 32.0));
            packet.c = MathHelper.floor((double)(this.y * 32.0));
            packet.d = MathHelper.floor((double)(this.z * 32.0));
            packet.e = (byte)(this.yaw * 256.0f / 360.0f);
            packet.f = (byte)(this.pitch * 256.0f / 360.0f);
            Packet35EntityHeadRotation packet2 = new Packet35EntityHeadRotation(this.entityId, (byte)MathHelper.d((float)(this.yaw * 256.0f / 360.0f)));
            for (Player player : this.visibleTo) {
                U.sendPacket(player, (Packet)packet);
                U.sendPacket(player, (Packet)packet2);
            }
        }
    }

    @Override
    public void setItemInHand(ItemStack itemInHand) {
        this.itemInHand = itemInHand;
        if (!this.visibleTo.isEmpty()) {
            Packet5EntityEquipment packet = new Packet5EntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy((ItemStack)itemInHand));
            for (Player player : this.visibleTo) {
                U.sendPacket(player, (Packet)packet);
            }
        }
    }

    @Override
    public void setArmor(ItemStack[] armor) {
        if (armor.length != 4) {
            return;
        }
        this.armor = armor;
    }

    @Override
    public void setHelmet(ItemStack helmet) {
        this.armor[3] = helmet;
        this.broadcastArmorPacket(3);
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
        this.armor[2] = chestplate;
        this.broadcastArmorPacket(2);
    }

    @Override
    public void setLeggings(ItemStack leggings) {
        this.armor[1] = leggings;
        this.broadcastArmorPacket(1);
    }

    @Override
    public void setBoots(ItemStack boots) {
        this.armor[0] = boots;
        this.broadcastArmorPacket(0);
    }

    @Override
    public void setViewDistance(int blocks) {
        this.viewDistance = blocks;
    }

    @Override
    public void setNameVisible(boolean visible) {
        if (this.nameVisible && !visible) {
            for (Player player : this.visibleTo) {
                this.sendHideNamePacket(player);
            }
        }
        if (!this.nameVisible && visible && !this.visibleTo.isEmpty()) {
            for (Player player : this.visibleTo) {
                U.sendPacket(player, (Packet)new Packet29DestroyEntity(new int[]{this.getItemEntityId()}));
            }
        }
        this.nameVisible = visible;
    }

    @Override
    public void remove() {
        VimeNetwork.npcs().remove(this.entityId);
    }

    @Override
    public Location getLocation() {
        return new Location(this.world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setData(String key, Object value) {
        this.data.put(key, value);
    }

    @Override
    public Object getData(String key) {
        return this.data.get(key);
    }

    double distanceSquared(Location loc) {
        return NumberConversions.square((double)(this.x - loc.getX())) + NumberConversions.square((double)(this.y - loc.getY())) + NumberConversions.square((double)(this.z - loc.getZ()));
    }

    private int getItemEntityId() {
        if (this.itemEntityId == -1) {
            this.itemEntityId = VNPC.nextEntityId();
        }
        return this.itemEntityId;
    }

    private void broadcastArmorPacket(int ... slots) {
        if (!this.visibleTo.isEmpty()) {
            Packet5EntityEquipment[] packets = new Packet5EntityEquipment[slots.length];
            for (int i = 0; i < slots.length; ++i) {
                packets[i] = new Packet5EntityEquipment(this.entityId, slots[i] + 1, CraftItemStack.asNMSCopy((ItemStack)this.armor[slots[i]]));
            }
            for (Player player : this.visibleTo) {
                for (Packet5EntityEquipment packet : packets) {
                    U.sendPacket(player, (Packet)packet);
                }
            }
        }
    }

    private void sendHideNamePacket(Player player) {
        Packet23VehicleSpawn itemSpawn = new Packet23VehicleSpawn();
        itemSpawn.a = this.getItemEntityId();
        itemSpawn.j = 2;
        itemSpawn.k = 1;
        itemSpawn.b = MathHelper.floor((double)(this.x * 32.0));
        itemSpawn.c = MathHelper.floor((double)((this.y + 1.0) * 32.0));
        itemSpawn.d = MathHelper.floor((double)(this.z * 32.0));
        itemSpawn.h = 0;
        itemSpawn.i = 0;
        itemSpawn.e = 0;
        itemSpawn.f = 0;
        itemSpawn.g = 0;
        U.sendPacket(player, (Packet)itemSpawn);
        DataWatcher watcher = new DataWatcher();
        watcher.a(10, (Object)new net.minecraft.server.v1_6_R3.ItemStack(77, 1, 0));
        U.sendPacket(player, (Packet)new Packet40EntityMetadata(this.itemEntityId, watcher, true));
        Packet39AttachEntity attachEntity = new Packet39AttachEntity();
        attachEntity.a = 0;
        attachEntity.b = this.itemEntityId;
        attachEntity.c = this.entityId;
        U.sendPacket(player, (Packet)attachEntity);
    }

    public void sendSpawnPacket(Player player) {
        Packet20NamedEntitySpawn spawn = new Packet20NamedEntitySpawn();
        spawn.a = this.entityId;
        spawn.b = this.name;
        spawn.c = MathHelper.floor((double)(this.x * 32.0));
        spawn.d = MathHelper.floor((double)(this.y * 32.0));
        spawn.e = MathHelper.floor((double)(this.z * 32.0));
        spawn.f = (byte)(this.yaw * 256.0f / 360.0f);
        spawn.g = (byte)(this.pitch * 256.0f / 360.0f);
        spawn.h = this.itemInHand == null ? 0 : this.itemInHand.getTypeId();
        Reflect.set(spawn, "i", (Object)this.dataWatcher);
        U.sendPacket(player, (Packet)spawn);
        if (this.itemInHand != null) {
            U.sendPacket(player, (Packet)new Packet5EntityEquipment(this.entityId, 0, CraftItemStack.asNMSCopy((ItemStack)this.itemInHand)));
        }
        for (int i = 0; i < 4; ++i) {
            if (this.armor[i] == null) continue;
            U.sendPacket(player, (Packet)new Packet5EntityEquipment(this.entityId, i + 1, CraftItemStack.asNMSCopy((ItemStack)this.armor[i])));
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)VNPlugin.instance(), () -> {
            float yaw0 = this.yaw % 360.0f;
            yaw0 = yaw0 >= 180.0f ? (yaw0 -= 45.0f) : (yaw0 += 45.0f);
            Packet34EntityTeleport packet = new Packet34EntityTeleport();
            packet.a = this.entityId;
            packet.b = MathHelper.floor((double)(this.x * 32.0));
            packet.c = MathHelper.floor((double)(this.y * 32.0));
            packet.d = MathHelper.floor((double)(this.z * 32.0));
            packet.e = (byte)(yaw0 * 256.0f / 360.0f);
            packet.f = (byte)(this.pitch * 256.0f / 360.0f);
            U.sendPacket(player, (Packet)packet);
            U.sendPacket(player, (Packet)new Packet35EntityHeadRotation(this.entityId, (byte)MathHelper.d((float)(this.yaw * 256.0f / 360.0f))));
        }, 3L);
        if (!this.nameVisible) {
            this.sendHideNamePacket(player);
        }
    }

    public void sendRemovePacket(Player player) {
        if (this.itemEntityId == -1) {
            U.sendPacket(player, (Packet)new Packet29DestroyEntity(new int[]{this.entityId}));
        } else {
            U.sendPacket(player, (Packet)new Packet29DestroyEntity(new int[]{this.entityId, this.itemEntityId}));
        }
    }

    private static int nextEntityId() {
        int id = (Integer)Reflect.get(Entity.class, "entityCount");
        Reflect.set(Entity.class, "entityCount", (Object)(id + 1));
        return id;
    }
}

