/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityTracker
 *  net.minecraft.server.v1_6_R3.ExceptionWorldConflict
 *  net.minecraft.server.v1_6_R3.IDataManager
 *  net.minecraft.server.v1_6_R3.IProgressUpdate
 *  net.minecraft.server.v1_6_R3.IWorldAccess
 *  net.minecraft.server.v1_6_R3.MinecraftServer
 *  net.minecraft.server.v1_6_R3.WorldManager
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  net.minecraft.server.v1_6_R3.WorldSettings
 *  org.bukkit.Location
 */
package net.xtrafrancyz.VimeNetwork.impl.world.clone;

import net.minecraft.server.v1_6_R3.EntityTracker;
import net.minecraft.server.v1_6_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_6_R3.IDataManager;
import net.minecraft.server.v1_6_R3.IProgressUpdate;
import net.minecraft.server.v1_6_R3.IWorldAccess;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.WorldManager;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.minecraft.server.v1_6_R3.WorldSettings;
import net.xtrafrancyz.VimeNetwork.impl.world.clone.NmsCloneDataManager;
import org.bukkit.Location;

public class NmsWorldClone
extends WorldServer {
    public WorldServer original;

    public NmsWorldClone(WorldServer original, MinecraftServer server, String name, int dimension) {
        super(server, (IDataManager)new NmsCloneDataManager(original, name, dimension), name, dimension, new WorldSettings(original.getWorldData()), server.methodProfiler, server.getLogger(), original.getWorld().getEnvironment(), null);
        this.original = original;
        this.setDayTime(original.getDayTime());
        this.getWorld().setFullTime(original.getWorld().getFullTime());
        this.getWorld().setStorm(original.getWorld().hasStorm());
        this.getWorld().setWeatherDuration(original.getWorld().getWeatherDuration());
        this.getWorld().setThunderDuration(original.getWorld().getThunderDuration());
        this.getWorld().setThundering(original.getWorld().isThundering());
        this.difficulty = original.difficulty;
        Location spawn = original.getWorld().getSpawnLocation();
        this.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        this.tracker = new EntityTracker((WorldServer)this);
        this.addIWorldAccess((IWorldAccess)new WorldManager(server, (WorldServer)this));
    }

    public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
    }

    public void saveLevel() {
    }

    public void flushSave() {
    }
}

