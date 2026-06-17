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
 *  org.bukkit.World$Environment
 */
package net.xtrafrancyz.VimeNetwork.impl.world.proxy;

import net.minecraft.server.v1_6_R3.EntityTracker;
import net.minecraft.server.v1_6_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_6_R3.IDataManager;
import net.minecraft.server.v1_6_R3.IProgressUpdate;
import net.minecraft.server.v1_6_R3.IWorldAccess;
import net.minecraft.server.v1_6_R3.MinecraftServer;
import net.minecraft.server.v1_6_R3.WorldManager;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.minecraft.server.v1_6_R3.WorldSettings;
import net.xtrafrancyz.VimeNetwork.impl.world.proxy.NmsProxyDataManager;
import org.bukkit.Location;
import org.bukkit.World;

public class NmsWorldProxy
extends WorldServer {
    public WorldServer proxied;

    public NmsWorldProxy(WorldServer proxied, MinecraftServer server, String name, int dimension) {
        super(server, (IDataManager)new NmsProxyDataManager(proxied, name, dimension), name, dimension, new WorldSettings(proxied.getWorldData()), server.methodProfiler, server.getLogger(), World.Environment.NORMAL, null);
        this.proxied = proxied;
        this.setDayTime(proxied.getDayTime());
        this.getWorld().setFullTime(proxied.getWorld().getFullTime());
        this.getWorld().setStorm(proxied.getWorld().hasStorm());
        this.getWorld().setWeatherDuration(proxied.getWorld().getWeatherDuration());
        this.getWorld().setThunderDuration(proxied.getWorld().getThunderDuration());
        this.getWorld().setThundering(proxied.getWorld().isThundering());
        this.difficulty = proxied.difficulty;
        Location spawn = proxied.getWorld().getSpawnLocation();
        this.getWorld().setSpawnLocation(spawn.getBlockX(), spawn.getBlockY(), spawn.getBlockZ());
        this.tracker = new EntityTracker((WorldServer)this);
        this.addIWorldAccess((IWorldAccess)new WorldManager(server, (WorldServer)this));
        this.worldProvider.e = proxied.worldProvider.e;
    }

    public boolean isChunkLoaded(int chunkX, int chunkZ) {
        return super.isChunkLoaded(chunkX, chunkZ);
    }

    public void save(boolean flag, IProgressUpdate iprogressupdate) throws ExceptionWorldConflict {
    }

    public void saveLevel() {
    }

    public void flushSave() {
    }
}

