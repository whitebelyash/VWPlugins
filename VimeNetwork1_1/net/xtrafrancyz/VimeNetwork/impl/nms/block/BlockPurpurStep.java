package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.BlockStepAbstract;
import net.minecraft.server.v1_6_R3.ItemStack;
import net.minecraft.server.v1_6_R3.Material;

public class BlockPurpurStep extends BlockStepAbstract {
   public BlockPurpurStep(int i, boolean b) {
      super(i, b, Material.STONE);
      this.c(2.0F);
      this.b(10.0F);
      x[i] = true;
   }

   protected ItemStack d_(int i) {
      return new ItemStack(205, 2, 0);
   }

   public int getDropType(int i, Random random, int j) {
      return 205;
   }

   public String c(int i) {
      return "Пурпуровая плита";
   }
}
