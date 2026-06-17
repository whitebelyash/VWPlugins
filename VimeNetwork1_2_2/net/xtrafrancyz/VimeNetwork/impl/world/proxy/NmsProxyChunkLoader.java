/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Chunk
 *  net.minecraft.server.v1_6_R3.IChunkLoader
 *  net.minecraft.server.v1_6_R3.World
 *  net.minecraft.server.v1_6_R3.WorldServer
 */
package net.xtrafrancyz.VimeNetwork.impl.world.proxy;

import net.minecraft.server.v1_6_R3.Chunk;
import net.minecraft.server.v1_6_R3.IChunkLoader;
import net.minecraft.server.v1_6_R3.World;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.impl.world.proxy.NmsChunkProxy;

public class NmsProxyChunkLoader
implements IChunkLoader {
    private final WorldServer proxiedWorld;

    public NmsProxyChunkLoader(WorldServer proxiedWorld) {
        this.proxiedWorld = proxiedWorld;
    }

    public Chunk a(World world, int x, int z) {
        Chunk chunk = this.proxiedWorld.chunkProviderServer.getChunkAt(x, z);
        NmsChunkProxy proxy = new NmsChunkProxy(chunk, world, x, z);
        return proxy;
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

