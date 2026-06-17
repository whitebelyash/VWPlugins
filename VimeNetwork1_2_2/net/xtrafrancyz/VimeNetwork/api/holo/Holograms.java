/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.VimeNetwork.api.holo;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.VimeNetwork.api.holo.ImageHologram;
import net.xtrafrancyz.VimeNetwork.api.holo.TextHologram;
import net.xtrafrancyz.VimeNetwork.api.holo.TextTimerHologram;

public interface Holograms {
    public ImageHologram createImage(Vec3f var1, int var2, String var3);

    public ImageHologram createImage(Vec3f var1, int var2, int var3, String var4);

    public TextHologram createText(Vec3f var1, String ... var2);

    public TextTimerHologram createTextTimer(Vec3f var1, long var2, String ... var4);

    public Hologram get(int var1);

    public void remove(int var1);

    public void remove(Hologram var1);

    public void reset();

    public List<Hologram> getAll();
}

