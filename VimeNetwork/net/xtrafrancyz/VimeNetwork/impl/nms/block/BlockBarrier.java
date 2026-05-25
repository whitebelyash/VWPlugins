package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.Material;

public class BlockBarrier extends VBlock {
   public BlockBarrier(int i) {
      super(i, Material.SHATTERABLE);
      this.r();
      this.setResistance(6000000.0F);
      this.a(k);
      this.C();
   }

   public boolean c() {
      return false;
   }
}
