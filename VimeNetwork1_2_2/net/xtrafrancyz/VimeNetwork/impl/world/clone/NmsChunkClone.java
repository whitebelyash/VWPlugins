/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Chunk
 *  net.minecraft.server.v1_6_R3.ChunkSection
 *  net.minecraft.server.v1_6_R3.Entity
 *  net.minecraft.server.v1_6_R3.EntityTypes
 *  net.minecraft.server.v1_6_R3.NBTTagCompound
 *  net.minecraft.server.v1_6_R3.NextTickListEntry
 *  net.minecraft.server.v1_6_R3.NibbleArray
 *  net.minecraft.server.v1_6_R3.TileEntity
 *  net.minecraft.server.v1_6_R3.World
 */
package net.xtrafrancyz.VimeNetwork.impl.world.clone;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_6_R3.Chunk;
import net.minecraft.server.v1_6_R3.ChunkSection;
import net.minecraft.server.v1_6_R3.Entity;
import net.minecraft.server.v1_6_R3.EntityTypes;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NextTickListEntry;
import net.minecraft.server.v1_6_R3.NibbleArray;
import net.minecraft.server.v1_6_R3.TileEntity;
import net.minecraft.server.v1_6_R3.World;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;

public class NmsChunkClone
extends Chunk {
    public NmsChunkClone(Chunk original, World world, int x, int z) {
        super(world, x, z);
        System.arraycopy(original.heightMap, 0, this.heightMap, 0, 256);
        System.arraycopy(original.b, 0, this.b, 0, 256);
        System.arraycopy(original.c, 0, this.c, 0, 256);
        System.arraycopy(original.m(), 0, this.m(), 0, 256);
        this.done = original.done;
        this.mustSave = false;
        ChunkSection[] originalSections = original.i();
        ChunkSection[] sections = this.i();
        for (int i = 0; i < 16; ++i) {
            ChunkSection o = originalSections[i];
            if (o == null) continue;
            ChunkSection cloned = sections[i] = new ChunkSection(o.getYPosition(), !world.worldProvider.g);
            System.arraycopy(o.getIdArray(), 0, cloned.getIdArray(), 0, 4096);
            NmsChunkClone.copyNibbleArray(o.getDataArray(), cloned.getDataArray());
            NmsChunkClone.copyNibbleArray(o.getEmittedLightArray(), cloned.getEmittedLightArray());
            NmsChunkClone.copyNibbleArray(o.getExtendedIdArray(), cloned.getExtendedIdArray());
            NmsChunkClone.copyNibbleArray(o.getSkyLightArray(), cloned.getSkyLightArray());
            Reflect.set(cloned, "nonEmptyBlockCount", Reflect.get(o, "nonEmptyBlockCount"));
            Reflect.set(cloned, "tickingBlockCount", Reflect.get(o, "tickingBlockCount"));
        }
        for (int i = 0; i < original.entitySlices.length; ++i) {
            Iterator it = original.entitySlices[i].iterator();
            while (it.hasNext()) {
                Entity entity;
                NBTTagCompound tag = new NBTTagCompound();
                if (!((Entity)it.next()).d(tag) || (entity = EntityTypes.a((NBTTagCompound)tag, (World)world)) == null) continue;
                this.a(entity);
                Entity entity1 = entity;
                NBTTagCompound entityTag = tag;
                while (entity1 != null && entityTag.hasKey("Riding")) {
                    Entity entity2 = EntityTypes.a((NBTTagCompound)entityTag.getCompound("Riding"), (World)world);
                    if (entity2 != null) {
                        this.a(entity2);
                        entity1.mount(entity2);
                    }
                    entity1 = entity2;
                    entityTag = entityTag.getCompound("Riding");
                }
            }
        }
        Iterator i = original.tileEntities.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = i.next();
            NBTTagCompound tag = new NBTTagCompound();
            ((TileEntity)entry.getValue()).b(tag);
            TileEntity clone = TileEntity.c((NBTTagCompound)tag);
            if (clone == null) continue;
            this.a(clone);
        }
        List list = original.world.a(original, false);
        if (list != null) {
            long time = world.getTime();
            for (Object o : list) {
                NextTickListEntry entry = (NextTickListEntry)o;
                world.b(entry.a, entry.b, entry.c, entry.d, (int)(entry.e - time), entry.f);
            }
        }
    }

    private static void copyNibbleArray(NibbleArray from, NibbleArray to) {
        if (from == null) {
            return;
        }
        Reflect.set(to, "trivialValue", Reflect.get(from, "trivialValue"));
        Reflect.set(to, "trivialByte", Reflect.get(from, "trivialByte"));
        if (from.isTrivialArray()) {
            Reflect.set(to, "a", null);
        } else {
            byte[] values = from.getValueArray();
            byte[] clone = new byte[values.length];
            System.arraycopy(values, 0, clone, 0, values.length);
            Reflect.set(to, "a", (Object)clone);
        }
    }

    public void initLighting() {
    }

    public boolean a(boolean var1) {
        return false;
    }
}

