/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.EntityHuman
 *  net.minecraft.server.v1_6_R3.ExceptionWorldConflict
 *  net.minecraft.server.v1_6_R3.IChunkLoader
 *  net.minecraft.server.v1_6_R3.IDataManager
 *  net.minecraft.server.v1_6_R3.IPlayerFileData
 *  net.minecraft.server.v1_6_R3.NBTTagCompound
 *  net.minecraft.server.v1_6_R3.WorldData
 *  net.minecraft.server.v1_6_R3.WorldProvider
 *  net.minecraft.server.v1_6_R3.WorldServer
 */
package net.xtrafrancyz.VimeNetwork.impl.world.clone;

import java.io.File;
import java.util.UUID;
import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_6_R3.IChunkLoader;
import net.minecraft.server.v1_6_R3.IDataManager;
import net.minecraft.server.v1_6_R3.IPlayerFileData;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.WorldData;
import net.minecraft.server.v1_6_R3.WorldProvider;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.impl.world.clone.NmsCloneChunkLoader;

public class NmsCloneDataManager
implements IDataManager,
IPlayerFileData {
    private final WorldServer original;
    private final String name;
    private final int dimension;
    private final UUID uuid;

    public NmsCloneDataManager(WorldServer original, String name, int dimension) {
        this.original = original;
        this.name = name;
        this.dimension = dimension;
        this.uuid = UUID.randomUUID();
    }

    public WorldData getWorldData() {
        WorldData data = new WorldData(this.original.getWorldData());
        data.setName(this.name);
        Reflect.set(data, "dimension", (Object)this.dimension);
        Reflect.set(data, "playerData", null);
        return data;
    }

    public void checkSession() throws ExceptionWorldConflict {
    }

    public IChunkLoader createChunkLoader(WorldProvider provider) {
        return new NmsCloneChunkLoader(this.original);
    }

    public void saveWorldData(WorldData data, NBTTagCompound compound) {
    }

    public void saveWorldData(WorldData data) {
    }

    public IPlayerFileData getPlayerFileData() {
        return this;
    }

    public void a() {
    }

    public File getDataFile(String s) {
        return null;
    }

    public String g() {
        return this.name;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void save(EntityHuman human) {
    }

    public NBTTagCompound load(EntityHuman human) {
        return null;
    }

    public String[] getSeenPlayers() {
        return new String[0];
    }
}

