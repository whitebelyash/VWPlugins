/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.TextTimer
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextTimerHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VTextHologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VTextTimerHologram
extends VTextHologram
implements TextTimerHologram {
    long duration = -1L;
    long timerDuration = -1L;

    public VTextTimerHologram(int id, Vec3f loc, long duration, String ... text) {
        super(id, loc, text);
        this.duration = duration;
    }

    @Override
    public void setTimerDuration(long millis) {
        this.timerDuration = millis;
        if (!this.hidden) {
            ByteMap data = new ByteMap();
            data.put((Object)".millis", (Object)this.timerDuration);
            Texteria3D.editElementInGroup((String)this.getTexteriaId(), (String)"0", (ByteMap)data, (Player[])Bukkit.getOnlinePlayers());
        }
    }

    @Override
    WorldGroup getGroup() {
        WorldGroup group = new WorldGroup(this.getTexteriaId());
        group.setLocation(this.loc.x, this.loc.y, this.loc.z);
        group.setAdjustableAngle(true);
        group.setRenderDistance(100);
        group.setScale(2.0f);
        group.setCentered(true);
        group.setDuration(this.duration);
        group.add((Element)new TextTimer("0", this.text).setTimerDuration(this.timerDuration).setShadow(false).setBackground(0x40000000));
        return group;
    }
}

