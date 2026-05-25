package net.xtrafrancyz.VimeNetwork.api.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;

public interface Hologram {
   void move(Vec3f var1);

   void update();

   void hide();

   void show();

   void remove();

   int getId();
}
