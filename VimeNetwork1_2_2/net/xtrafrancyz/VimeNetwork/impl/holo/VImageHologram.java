/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Image
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 */
package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.ImageHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VHologram;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Image;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;

public class VImageHologram
extends VHologram
implements ImageHologram {
    private String image;
    private int width;
    private int height;

    public VImageHologram(int id, Vec3f loc, int width, int height, String image) {
        super(id, loc);
        this.image = image;
        this.width = width;
        this.height = height;
    }

    @Override
    WorldGroup getGroup() {
        WorldGroup group = new WorldGroup(this.getTexteriaId());
        group.setLocation(this.loc.x, this.loc.y, this.loc.z);
        group.setAdjustableAngle(true);
        group.setRenderDistance(100);
        group.setCentered(true);
        group.add((Element)new Image("0", this.width, this.height, this.image));
        return group;
    }
}

