/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Chunk
 *  net.minecraft.server.v1_6_R3.IChunkLoader
 *  net.minecraft.server.v1_6_R3.World
 *  net.minecraft.server.v1_6_R3.WorldServer
 */
package net.xtrafrancyz.VimeNetwork.impl.world.clone;

import net.minecraft.server.v1_6_R3.Chunk;
import net.minecraft.server.v1_6_R3.IChunkLoader;
import net.minecraft.server.v1_6_R3.World;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.impl.world.clone.NmsChunkClone;

public class NmsCloneChunkLoader
implements IChunkLoader {
    private final WorldServer original;

    public NmsCloneChunkLoader(WorldServer original) {
        this.original = original;
    }

    public Chunk a(World world, int x, int z) {
        Chunk chunk = this.original.chunkProviderServer.getChunkAt(x, z);
        return new NmsChunkClone(chunk, world, x, z);
    }

    public void a(World world, Chunk chunk) {
    }

    public void b(World world, Chunk chunk) {
    }

    public void a() {
    }

    public void b() {
    }
}

