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
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class VHolograms implements Holograms, Listener {
   private int idCounter = 0;
   private TIntObjectMap holograms = new TIntObjectHashMap();

   public ImageHologram createImage(Vec3f loc, int size, String image) {
      return this.createImage(loc, size, size, image);
   }

   public ImageHologram createImage(Vec3f loc, int width, int height, String image) {
      VImageHologram holo = new VImageHologram(this.idCounter++, loc, width, height, image);
      this.holograms.put(holo.getId(), holo);
      holo.update();
      return holo;
   }

   public TextHologram createText(Vec3f loc, String... lines) {
      VTextHologram holo = new VTextHologram(this.idCounter++, loc, lines);
      this.holograms.put(holo.getId(), holo);
      holo.update();
      return holo;
   }

   public TextTimerHologram createTextTimer(Vec3f loc, long duration, String... lines) {
      VTextTimerHologram holo = new VTextTimerHologram(this.idCounter++, loc, duration, lines);
      this.holograms.put(holo.getId(), holo);
      holo.update();
      return holo;
   }

   public Hologram get(int id) {
      return (Hologram)this.holograms.get(id);
   }

   public void remove(int id) {
      VHologram holo = (VHologram)this.holograms.remove(id);
      if (holo != null) {
         holo.dispose();
      }

   }

   public void remove(Hologram holo) {
      this.holograms.remove(holo.getId());
      ((VHologram)holo).dispose();
   }

   public void reset() {
      for(VHologram holo : this.holograms.valueCollection()) {
         holo.hide();
      }

      this.holograms.clear();
   }

   public List getAll() {
      return new ArrayList(this.holograms.valueCollection());
   }

   @EventHandler
   private void onPlayerJoin(PlayerJoinEvent event) {
      for(VHologram holo : this.holograms.valueCollection()) {
         if (!holo.hidden) {
            Texteria3D.addGroup(holo.getGroup(), new Player[]{event.getPlayer()});
         }
      }

   }
}
