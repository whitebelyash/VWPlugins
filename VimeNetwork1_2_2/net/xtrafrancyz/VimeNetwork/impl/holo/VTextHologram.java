/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.elements.Element
 *  net.xtrafrancyz.bukkit.texteria.elements.Text
 *  net.xtrafrancyz.bukkit.texteria.utils.ByteMap
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VHologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.Element;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class VTextHologram
extends VHologram
implements TextHologram {
    public String[] text;

    public VTextHologram(int id, Vec3f loc, String ... text) {
        super(id, loc);
        this.text = text;
    }

    @Override
    public void setText(String ... lines) {
        this.text = lines;
        ByteMap data = new ByteMap();
        data.put((Object)".text", (Object)this.text);
        Texteria3D.editElementInGroup((String)this.getTexteriaId(), (String)"0", (ByteMap)data, (Player[])Bukkit.getOnlinePlayers());
    }

    @Override
    WorldGroup getGroup() {
        WorldGroup group = new WorldGroup(this.getTexteriaId());
        group.setLocation(this.loc.x, this.loc.y, this.loc.z);
        group.setAdjustableAngle(true);
        group.setRenderDistance(100);
        group.setScale(2.0f);
        group.setCentered(true);
        group.add((Element)new Text("0", this.text).setShadow(false).setBackground(0x40000000));
        return group;
    }
}

