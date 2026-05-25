package net.xtrafrancyz.VimeNetwork.impl.holo;

import net.xtrafrancyz.VimeNetwork.api.geom.Vec3f;
import net.xtrafrancyz.VimeNetwork.api.holo.TextHologram;
import net.xtrafrancyz.bukkit.texteria.Texteria3D;
import net.xtrafrancyz.bukkit.texteria.elements.Text;
import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;
import net.xtrafrancyz.bukkit.texteria.world.WorldGroup;
import org.bukkit.Bukkit;

public class VTextHologram extends VHologram implements TextHologram {
   public String[] text;

   public VTextHologram(int id, Vec3f loc, String... text) {
      super(id, loc);
      this.text = text;
   }

   public void setText(String... lines) {
      this.text = lines;
      ByteMap data = new ByteMap();
      data.put(".text", this.text);
      Texteria3D.editElementInGroup(this.getTexteriaId(), "0", data, Bukkit.getOnlinePlayers());
   }

   WorldGroup getGroup() {
      WorldGroup group = new WorldGroup(this.getTexteriaId());
      group.setLocation(this.loc.x, this.loc.y, this.loc.z);
      group.setAdjustableAngle(true);
      group.setRenderDistance(100);
      group.setScale(2.0F);
      group.setCentered(true);
      group.add((new Text("0", this.text)).setShadow(false).setBackground(1073741824));
      return group;
   }
}
