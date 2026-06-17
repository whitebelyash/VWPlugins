/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.NBTTagCompound
 *  org.bukkit.World
 */
package net.xtrafrancyz.VimeNetwork.api.world;

import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3i;
import net.xtrafrancyz.VimeNetwork.api.world.Schematic;
import org.bukkit.World;

public class WESchemetic
extends Schematic {
    private Vec3i offset;
    private Vec3i origin;

    WESchemetic(NBTTagCompound tag) {
        super(tag);
        this.offset = new Vec3i(tag.getInt("WEOffsetX"), tag.getInt("WEOffsetY"), tag.getInt("WEOffsetZ"));
        this.origin = new Vec3i(tag.getInt("WEOriginX"), tag.getInt("WEOriginY"), tag.getInt("WEOriginZ"));
    }

    public Vec3i getOffset() {
        return this.offset;
    }

    public Vec3i getOrigin() {
        return this.origin;
    }

    public void pasteWithOffset(World world, Vec3i base) {
        this.paste(world, base.add(this.offset));
    }

    public void pasteWithOffset(World world, Vec3i base, boolean fast) {
        this.paste(world, base.add(this.offset), fast);
    }

    public void pasteWithOffset(World world, Vec3i base, boolean fast, boolean pasteTiles) {
        this.paste(world, base.add(this.offset), fast, pasteTiles);
    }
}

