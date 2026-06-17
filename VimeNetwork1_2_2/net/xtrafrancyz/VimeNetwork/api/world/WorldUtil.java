/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.MinecraftServer
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  org.bukkit.Bukkit
 *  org.bukkit.Chunk
 *  org.bukkit.World
 *  org.bukkit.craftbukkit.v1_6_R3.CraftChunk
 *  org.bukkit.entity.Entity
 *  org.bukkit.event.Event
 *  org.bukkit.event.world.WorldInitEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 */
package net.xtrafrancyz.VimeNetwork.api.world;

import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.impl.world.clone.NmsWorldClone;
import net.xtrafrancyz.VimeNetwork.impl.world.proxy.NmsWorldProxy;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftChunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldUtil {
    private static int dimensionCounter = 100;

    public static World createBlockProxy(World original, String name) {
        return WorldUtil.tryCreateWorld(name, () -> new NmsWorldProxy(NMSEntityUtils.getNMSWorld(original), MinecraftServer.getServer(), name, dimensionCounter++));
    }

    public static World createMemoryClone(World original, String name) {
        return WorldUtil.tryCreateWorld(name, () -> new NmsWorldClone(NMSEntityUtils.getNMSWorld(original), MinecraftServer.getServer(), name, dimensionCounter++));
    }

    public static boolean unloadMemoryWorld(World world) {
        if (world == null) {
            return true;
        }
        if (!NMSEntityUtils.getNMSWorld((World)world).players.isEmpty()) {
            return false;
        }
        WorldUnloadEvent e = new WorldUnloadEvent(world);
        Bukkit.getPluginManager().callEvent((Event)e);
        if (e.isCancelled()) {
            System.out.println("Cannot unload world " + world.getName() + " (players)");
            return false;
        }
        Map worlds = (Map)Reflect.get(Bukkit.getServer(), "worlds");
        worlds.remove(world.getName().toLowerCase());
        MinecraftServer.getServer().worlds.remove(NMSEntityUtils.getNMSWorld(world));
        return true;
    }

    public static boolean isMemoryWorld(World world) {
        WorldServer nms = NMSEntityUtils.getNMSWorld(world);
        return nms instanceof NmsWorldClone || nms instanceof NmsWorldProxy;
    }

    public static void clearWorld(World world) {
        WorldServer nmsWorld = NMSEntityUtils.getNMSWorld(world);
        world.getEntities().forEach(Entity::remove);
        boolean oldKeepSpawnInMemory = world.getKeepSpawnInMemory();
        nmsWorld.keepSpawnInMemory = false;
        world.setKeepSpawnInMemory(false);
        for (Chunk chunk : world.getLoadedChunks()) {
            ((CraftChunk)chunk).getHandle().mustSave = false;
            chunk.unload(false, false);
        }
        nmsWorld.keepSpawnInMemory = oldKeepSpawnInMemory;
        Reflect.set(nmsWorld, "lastChunkAccessed", null);
        Reflect.invoke(Reflect.get(nmsWorld, "chunkTickList"), "clear", new Object[0]);
        Reflect.invoke(Reflect.get(nmsWorld, "tickEntriesByChunk"), "clear", new Object[0]);
        Reflect.invoke(Reflect.get(Reflect.get(world, "blockMetadata"), "metadataMap"), "clear", new Object[0]);
    }

    private static World tryCreateWorld(String name, Supplier<WorldServer> creator) {
        World existing = Bukkit.getWorld((String)name);
        if (existing != null) {
            return existing;
        }
        WorldServer newWorld = creator.get();
        MinecraftServer.getServer().worlds.add(newWorld);
        Bukkit.getPluginManager().callEvent((Event)new WorldInitEvent((World)newWorld.getWorld()));
        Bukkit.getPluginManager().callEvent((Event)new WorldLoadEvent((World)newWorld.getWorld()));
        return newWorld.getWorld();
    }
}

