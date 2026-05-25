package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextTimerHologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.TextTimer;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;

public class VTextTimerHologram extends VTextHologram implements TextTimerHologram {
   long duration = -1L;
   long timerDuration = -1L;

   public VTextTimerHologram(int id, Vec3f loc, long duration, String... text) {
      super(id, loc, text);
      this.duration = duration;
   }

   public void setTimerDuration(long millis) {
      this.timerDuration = millis;
      if (!this.hidden) {
         ByteMap data = new ByteMap();
         data.put(".millis", this.timerDuration);
         Texteria3D.editElementInGroup(this.getTexteriaId(), "0", data, Bukkit.getOnlinePlayers());
      }

   }

   WorldGroup getGroup() {
      WorldGroup group = new WorldGroup(this.getTexteriaId());
      group.setLocation(this.loc.x, this.loc.y, this.loc.z);
      group.setAdjustableAngle(true);
      group.setRenderDistance(100);
      group.setScale(2.0F);
      group.setCentered(true);
      group.setDuration(this.duration);
      group.add((new TextTimer("0", this.text)).setTimerDuration(this.timerDuration).setShadow(false).setBackground(1073741824));
      return group;
   }
}
