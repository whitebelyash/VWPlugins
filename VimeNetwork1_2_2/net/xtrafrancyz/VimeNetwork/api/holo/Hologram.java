/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;

public interface Hologram {
    public void move(Vec3f var1);

    public void update();

    public void hide();

    public void show();

    public void remove();

    public int getId();
}

