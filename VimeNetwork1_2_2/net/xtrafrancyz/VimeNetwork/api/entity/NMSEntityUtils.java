/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityCreature
 *  net.minecraft.server.v1_6_R3.EntityInsentient
 *  net.minecraft.server.v1_6_R3.EntityLiving
 *  net.minecraft.server.v1_6_R3.EntityTypes
 *  net.minecraft.server.v1_6_R3.GenericAttributes
 *  net.minecraft.server.v1_6_R3.MathHelper
 *  net.minecraft.server.v1_6_R3.Navigation
 *  net.minecraft.server.v1_6_R3.PathEntity
 *  net.minecraft.server.v1_6_R3.PathfinderGoalSelector
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  org.bukkit.Chunk
 *  org.bukkit.Location
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_6_R3.CraftWorld
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.entity.CreatureSpawnEvent$SpawnReason
 */
package net.xtrafrancyz.VimeNetwork.api.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_6_R3.EntityCreature;
import net.minecraft.server.v1_6_R3.EntityInsentient;
import net.minecraft.server.v1_6_R3.EntityLiving;
import net.minecraft.server.v1_6_R3.EntityTypes;
import net.minecraft.server.v1_6_R3.GenericAttributes;
import net.minecraft.server.v1_6_R3.MathHelper;
import net.minecraft.server.v1_6_R3.Navigation;
import net.minecraft.server.v1_6_R3.PathEntity;
import net.minecraft.server.v1_6_R3.PathfinderGoalSelector;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class NMSEntityUtils {
    private static Field PathfinderGoalSelector_list1;
    private static Field PathfinderGoalSelector_list2;
    private static Field EntityInsentient_goalSelector;
    private static Field EntityInsentient_targetSelector;
    private static Field EntityTypes_d;
    private static Field EntityTypes_e;
    private static Method EntityTypes_registerEntity;

    public static void safeRegisterCustomEntity(Class<? extends net.minecraft.server.v1_6_R3.Entity> clazz, String name) {
        NMSEntityUtils.safeRegisterCustomEntity(clazz, clazz.getSuperclass(), name);
    }

    public static void safeRegisterCustomEntity(Class<? extends net.minecraft.server.v1_6_R3.Entity> clazz, Class nmsClazz, String name) {
        try {
            int id = (Integer)((Map)EntityTypes_e.get(null)).get(nmsClazz);
            Map d = (Map)EntityTypes_d.get(null);
            EntityTypes_registerEntity.invoke(null, clazz, name, id);
            d.put(id, nmsClazz);
        }
        catch (Exception e) {
            VNPlugin.instance().getLogger().log(Level.SEVERE, "NMSEntityUtils failed to register custom entity", e);
        }
    }

    public static void clearPathfinding(EntityInsentient entity) {
        try {
            Object goalSelector = EntityInsentient_goalSelector.get(entity);
            ((List)PathfinderGoalSelector_list1.get(goalSelector)).clear();
            ((List)PathfinderGoalSelector_list2.get(goalSelector)).clear();
            Object targetSelector = EntityInsentient_targetSelector.get(entity);
            ((List)PathfinderGoalSelector_list1.get(targetSelector)).clear();
            ((List)PathfinderGoalSelector_list2.get(targetSelector)).clear();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Entity spawn(net.minecraft.server.v1_6_R3.Entity entity, Location loc) {
        entity.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        NMSEntityUtils.getNMSWorld(loc.getWorld()).addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Chunk chunk = loc.getBlock().getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        return entity.getBukkitEntity();
    }

    public static void walkToLocation(EntityCreature entity, Location loc) {
        Navigation navigation = entity.getNavigation();
        navigation.a(loc.getX(), loc.getY(), loc.getZ());
        PathEntity pathEntity = entity.world.a((net.minecraft.server.v1_6_R3.Entity)entity, MathHelper.floor((double)loc.getX()), (int)loc.getY(), MathHelper.floor((double)loc.getZ()), (float)NMSEntityUtils.getPathfindingRange((EntityLiving)entity), true, false, false, true);
        entity.setPathEntity(pathEntity);
        navigation.a(pathEntity, 1.0);
    }

    private static double getPathfindingRange(EntityLiving entity) {
        return entity.getAttributeInstance(GenericAttributes.b).getValue();
    }

    public static void setMovementSpeed(EntityLiving entity, double speed) {
        entity.getAttributeInstance(GenericAttributes.d).setValue(speed);
    }

    public static double getMovementSpeed(EntityLiving entity) {
        return entity.getAttributeInstance(GenericAttributes.d).getValue();
    }

    public static WorldServer getNMSWorld(World bukkitWorld) {
        return ((CraftWorld)bukkitWorld).getHandle();
    }

    static {
        try {
            PathfinderGoalSelector_list1 = PathfinderGoalSelector.class.getDeclaredField("a");
            PathfinderGoalSelector_list1.setAccessible(true);
            PathfinderGoalSelector_list2 = PathfinderGoalSelector.class.getDeclaredField("b");
            PathfinderGoalSelector_list2.setAccessible(true);
            EntityInsentient_goalSelector = EntityInsentient.class.getDeclaredField("goalSelector");
            EntityInsentient_goalSelector.setAccessible(true);
            EntityInsentient_targetSelector = EntityInsentient.class.getDeclaredField("targetSelector");
            EntityInsentient_targetSelector.setAccessible(true);
            EntityTypes_registerEntity = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, Integer.TYPE);
            EntityTypes_registerEntity.setAccessible(true);
            EntityTypes_d = EntityTypes.class.getDeclaredField("d");
            EntityTypes_d.setAccessible(true);
            EntityTypes_e = EntityTypes.class.getDeclaredField("e");
            EntityTypes_e.setAccessible(true);
        }
        catch (Exception e) {
            VNPlugin.instance().getLogger().log(Level.SEVERE, "NMSEntityUtils initialization failed", e);
        }
    }
}

