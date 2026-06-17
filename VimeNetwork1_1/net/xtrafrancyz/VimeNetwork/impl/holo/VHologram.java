package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.Hologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;

public abstract class VHologram implements Hologram {
   public int id;
   public Vec3f loc;
   public boolean hidden = false;

   public VHologram(int id, Vec3f loc) {
      this.id = id;
      this.loc = loc;
   }

   public void move(Vec3f pos) {
      this.loc = pos;
      ByteMap data = new ByteMap();
      ByteMap loc = new ByteMap();
      loc.put("x", pos.x);
      loc.put("y", pos.y);
      loc.put("z", pos.z);
      data.put("loc", loc);
      Texteria3D.editGroup(this.getTexteriaId(), data, Bukkit.getOnlinePlayers());
   }

   public void update() {
      Texteria3D.addGroup(this.getGroup(), Bukkit.getOnlinePlayers());
      this.hidden = false;
   }

   public void hide() {
      this.hidden = true;
      Texteria3D.removeGroup(this.getTexteriaId(), Bukkit.getOnlinePlayers());
   }

   public void show() {
      this.update();
   }

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

   public int getId() {
      return this.id;
   }
}
