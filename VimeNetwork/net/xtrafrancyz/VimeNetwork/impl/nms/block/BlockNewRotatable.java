package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import net.minecraft.server.v1_6_R3.BlockRotatable;
import net.minecraft.server.v1_6_R3.Material;

public class BlockNewRotatable extends BlockRotatable {
   public BlockNewRotatable(int i, Material material) {
      super(i, material);
   }

   public BlockNewRotatable setHardness(float val) {
      this.c(val);
      return this;
   }

   public BlockNewRotatable setResistance(float val) {
      this.b(val);
      return this;
   }

   public BlockNewRotatable setLightValue(float val) {
      this.a(val);
      return this;
   }
}
