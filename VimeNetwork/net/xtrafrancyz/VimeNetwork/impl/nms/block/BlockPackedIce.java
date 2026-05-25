package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.Material;

public class BlockPackedIce extends VBlock {
   public BlockPackedIce(int i) {
      super(i, Material.ICE);
      this.frictionFactor = 0.98F;
      this.setHardness(0.5F);
      this.a(m);
   }

   public int getDropCount(int i, Random random) {
      return 0;
   }
}
