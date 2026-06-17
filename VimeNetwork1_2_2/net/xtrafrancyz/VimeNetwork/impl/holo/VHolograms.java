/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  net.xtrafrancyz.bukkit.texteria.Texteria3D
 *  net.xtrafrancyz.bukkit.texteria.world.WorldGroup
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerJoinEvent
 */
package net.xtrafrancyz.VimeNetwork.impl.holo;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.VimeNetwork.api.holo.Holograms;
import net.xtrafrancyz.VimeNetwork.api.holo.ImageHologram;
import net.xtrafrancyz.VimeNetwork.api.holo.TextHologram;
import net.xtrafrancyz.VimeNetwork.api.holo.TextTimerHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VImageHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VTextHologram;
import net.xtrafrancyz.VimeNetwork.impl.holo.VTextTimerHologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VHolograms
implements Holograms,
Listener {
    private int idCounter = 0;
    private TIntObjectMap<VHologram> holograms = new TIntObjectHashMap();

    @Override
    public ImageHologram createImage(Vec3f loc, int size, String image) {
        return this.createImage(loc, size, size, image);
    }

    @Override
    public ImageHologram createImage(Vec3f loc, int width, int height, String image) {
        VImageHologram holo = new VImageHologram(this.idCounter++, loc, width, height, image);
        this.holograms.put(holo.getId(), (Object)holo);
        holo.update();
        return holo;
    }

    @Override
    public TextHologram createText(Vec3f loc, String ... lines) {
        VTextHologram holo = new VTextHologram(this.idCounter++, loc, lines);
        this.holograms.put(holo.getId(), (Object)holo);
        holo.update();
        return holo;
    }

    @Override
    public TextTimerHologram createTextTimer(Vec3f loc, long duration, String ... lines) {
        VTextTimerHologram holo = new VTextTimerHologram(this.idCounter++, loc, duration, lines);
        this.holograms.put(holo.getId(), (Object)holo);
        holo.update();
        return holo;
    }

    @Override
    public Hologram get(int id) {
        return (Hologram)this.holograms.get(id);
    }

    @Override
    public void remove(int id) {
        VHologram holo = (VHologram)this.holograms.remove(id);
        if (holo != null) {
            holo.dispose();
        }
    }

    @Override
    public void remove(Hologram holo) {
        this.holograms.remove(holo.getId());
        ((VHologram)holo).dispose();
    }

    @Override
    public void reset() {
        for (VHologram holo : this.holograms.valueCollection()) {
            holo.hide();
        }
        this.holograms.clear();
    }

    @Override
    public List<Hologram> getAll() {
        return new ArrayList<Hologram>(this.holograms.valueCollection());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        for (VHologram holo : this.holograms.valueCollection()) {
            if (holo.hidden) continue;
            Texteria3D.addGroup((WorldGroup)holo.getGroup(), (Player[])new Player[]{event.getPlayer()});
        }
    }
}

