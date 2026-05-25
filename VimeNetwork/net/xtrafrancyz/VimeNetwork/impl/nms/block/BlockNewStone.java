package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.Material;

public class BlockNewStone extends VBlock {
   public BlockNewStone(int id) {
      super(id, Material.STONE);
      this.c(1.5F);
      this.b(10.0F);
      this.a(k);
      this.c("stone");
      this.d("stone");
      this.dropExactMeta();
   }

   public int getDropType(int meta, Random random, int j) {
      return meta == 0 ? COBBLESTONE.id : this.id;
   }
}
