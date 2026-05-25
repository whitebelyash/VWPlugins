package net.xtrafrancyz.bukkit.texteria.elements;

import net.xtrafrancyz.bukkit.texteria.utils.ByteMap;

public class RadialProgressBar extends Element {
   public int size;
   public float progress;

   public RadialProgressBar(String id, int size, float progress) {
      super(id);
      this.size = size;
      this.progress = progress;
   }

   public void write(ByteMap map) {
      super.write(map);
      map.put("size", this.size);
      if (this.progress != -99.0F) {
         map.put("progress", this.progress);
      }

   }

   protected String getType() {
      return "RadialProgressBar";
   }
}
