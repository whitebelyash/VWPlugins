/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class VHologram
implements Hologram {
    public int id;
    public Vec3f loc;
    public boolean hidden = false;

    public VHologram(int id, Vec3f loc) {
        this.id = id;
        this.loc = loc;
    }

    @Override
    public void move(Vec3f pos) {
        this.loc = pos;
        ByteMap data = new ByteMap();
        ByteMap loc = new ByteMap();
        loc.put((Object)"x", (Object)Float.valueOf(pos.x));
        loc.put((Object)"y", (Object)Float.valueOf(pos.y));
        loc.put((Object)"z", (Object)Float.valueOf(pos.z));
        data.put((Object)"loc", (Object)loc);
        Texteria3D.editGroup((String)this.getTexteriaId(), (ByteMap)data, (Player[])Bukkit.getOnlinePlayers());
    }

    @Override
    public void update() {
        Texteria3D.addGroup((WorldGroup)this.getGroup(), (Player[])Bukkit.getOnlinePlayers());
        this.hidden = false;
    }

    @Override
    public void hide() {
        this.hidden = true;
        Texteria3D.removeGroup((String)this.getTexteriaId(), (Player[])Bukkit.getOnlinePlayers());
    }

    @Override
    public void show() {
        this.update();
    }

    @Override
    public void remove() {
        VimeNetwork.holograms().remove(this);
    }

    void dispose() {
        this.hide();
    }

    abstract WorldGroup getGroup();

    protected String getTexteriaId() {
        return "hl." + this.id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}

