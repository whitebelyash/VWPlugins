package net.xtrafrancyz.VimeNetwork.api.holo;

import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;

public interface Holograms {
   ImageHologram createImage(Vec3f var1, int var2, String var3);

   ImageHologram createImage(Vec3f var1, int var2, int var3, String var4);

   TextHologram createText(Vec3f var1, String... var2);

   TextTimerHologram createTextTimer(Vec3f var1, long var2, String... var4);

   Hologram get(int var1);

   void remove(int var1);

   void remove(Hologram var1);

   void reset();

   List getAll();
}
