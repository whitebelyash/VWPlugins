package net.xtrafrancyz.VimeNetwork.impl.nms.block;

import java.util.Random;
import net.minecraft.server.v1_6_R3.BlockStepAbstract;
import net.minecraft.server.v1_6_R3.ItemStack;
import net.minecraft.server.v1_6_R3.Material;

public class BlockRedSandstoneStep extends BlockStepAbstract {
   public BlockRedSandstoneStep(int i, boolean b) {
      super(i, b, Material.STONE);
      this.c(2.0F);
      this.b(10.0F);
      x[i] = true;
   }

   protected ItemStack d_(int i) {
      return new ItemStack(182, 2, 0);
   }

   public int getDropType(int i, Random random, int j) {
      return 182;
   }

   public String c(int i) {
      return "Плита из красного песчаника";
   }
}
