/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.AxisAlignedBB
 *  net.minecraft.server.v1_6_R3.BiomeBase
 *  net.minecraft.server.v1_6_R3.Chunk
 *  net.minecraft.server.v1_6_R3.ChunkCoordIntPair
 *  net.minecraft.server.v1_6_R3.ChunkSection
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EnumSkyBlock
 *  net.minecraft.server.v1_6_R3.IChunkProvider
 *  net.minecraft.server.v1_6_R3.IEntitySelector
 *  net.minecraft.server.v1_6_R3.TileEntity
 *  net.minecraft.server.v1_6_R3.World
 *  net.minecraft.server.v1_6_R3.WorldChunkManager
 */
package net.xtrafrancyz.VimeNetwork.impl.world.proxy;

import java.util.List;
import java.util.Random;
import net.minecraft.server.v1_6_R3.AxisAlignedBB;
import net.minecraft.server.v1_6_R3.BiomeBase;
import net.minecraft.server.v1_6_R3.Chunk;
import net.minecraft.server.v1_6_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_6_R3.ChunkSection;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EnumSkyBlock;
import net.minecraft.server.v1_6_R3.IChunkProvider;
import net.minecraft.server.v1_6_R3.IEntitySelector;
import net.minecraft.server.v1_6_R3.TileEntity;
import net.minecraft.server.v1_6_R3.World;
import net.minecraft.server.v1_6_R3.WorldChunkManager;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;

public class NmsChunkProxy
extends Chunk {
    private Chunk proxied;

    public NmsChunkProxy(Chunk proxied, World world, int i, int i1) {
        super(world, i, i1);
        this.replaceProxiedChunk(proxied);
    }

    public void replaceProxiedChunk(Chunk proxied) {
        this.proxied = proxied;
        this.heightMap = proxied.heightMap;
        this.b = proxied.b;
        this.c = proxied.c;
        this.tileEntities = proxied.tileEntities;
        Reflect.set((Object)this, "sections", Reflect.get(proxied, "sections"));
        Reflect.set((Object)this, "s", Reflect.get(proxied, "s"));
    }

    public int b(int var1, int var2) {
        return this.proxied.b(var1, var2);
    }

    public void initLighting() {
    }

    public int getTypeId(int x, int y, int z) {
        return this.proxied.getTypeId(x, y, z);
    }

    public int b(int x, int y, int z) {
        return this.proxied.b(x, y, z);
    }

    public boolean a(int var1, int var2, int var3, int var4, int var5) {
        return false;
    }

    public int getData(int x, int y, int z) {
        return this.proxied.getData(x, y, z);
    }

    public boolean b(int x, int y, int z, int cyka) {
        return this.proxied.b(x, y, z, cyka);
    }

    public int getBrightness(EnumSkyBlock sky, int x, int y, int z) {
        return this.proxied.getBrightness(sky, x, y, z);
    }

    public void a(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
    }

    public int c(int var1, int var2, int var3, int var4) {
        return this.proxied.c(var1, var2, var3, var4);
    }

    public void a(Entity entity) {
        super.a(entity);
    }

    public void b(Entity entity) {
        super.b(entity);
    }

    public void a(Entity entity, int var2) {
        super.a(entity, var2);
    }

    public boolean d(int x, int y, int z) {
        return this.proxied.d(x, y, z);
    }

    public TileEntity e(int x, int y, int z) {
        return this.proxied.e(x, y, z);
    }

    public void a(TileEntity tile) {
    }

    public void a(int var1, int var2, int var3, TileEntity tile) {
    }

    public void f(int var1, int var2, int var3) {
    }

    public void addEntities() {
        super.addEntities();
    }

    public void removeEntities() {
        super.removeEntities();
    }

    public void e() {
    }

    public void a(Entity entity, AxisAlignedBB bb, List list, IEntitySelector selector) {
        super.a(entity, bb, list, selector);
    }

    public void a(Class clazz, AxisAlignedBB bb, List list, IEntitySelector selector) {
        super.a(clazz, bb, list, selector);
    }

    public boolean a(boolean var1) {
        return false;
    }

    public Random a(long var1) {
        return this.proxied.a(var1);
    }

    public boolean isEmpty() {
        return this.proxied.isEmpty();
    }

    public boolean c(int x, int z) {
        return this.proxied.c(x, z);
    }

    public void a(IChunkProvider ichunkprovider, IChunkProvider ichunkprovider1, int i, int j) {
    }

    public void a(ChunkSection[] achunksection) {
    }

    public void a(byte[] abyte) {
    }

    public BiomeBase a(int x, int y, WorldChunkManager worldchunkmanager) {
        return this.proxied.a(x, y, worldchunkmanager);
    }

    public int d(int x, int y) {
        return this.proxied.d(x, y);
    }

    public ChunkSection[] i() {
        return this.proxied.i();
    }

    public ChunkCoordIntPair l() {
        return super.l();
    }

    public void n() {
    }
}

