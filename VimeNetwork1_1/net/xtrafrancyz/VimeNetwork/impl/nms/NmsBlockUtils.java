package net.xtrafrancyz.VimeNetwork.impl.nms;

import java.util.Arrays;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.ItemBlock;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import net.xtrafrancyz.VimeNetwork.impl.nms.block.VBlock;
import net.xtrafrancyz.VimeNetwork.impl.nms.item.ItemBlockWithData;
import org.bukkit.Material;

public class NmsBlockUtils {
   public static Material addBlock(int id, String name, net.minecraft.server.v1_6_R3.Material material, boolean hasMeta) {
      return addBlock(id, name, (Block)(new VBlock(id, material)), hasMeta);
   }

   public static Material addBlock(int id, String name, Block block, boolean hasMeta) {
      if (hasMeta) {
         new ItemBlockWithData(id - 256);
      } else {
         new ItemBlock(id - 256);
      }

      return addIntoMaterials(id, name);
   }

   public static void addItem(int id, boolean hasMeta) {
   }

   private static Material addIntoMaterials(int id, String name) {
      Material[] byId = (Material[])Reflect.get(Material.class, "byId");
      Material newMaterial = (Material)Reflect.addEnum(Material.class, name, id);
      if (byId.length <= id) {
         byId = (Material[])Arrays.copyOf(byId, id + 2);
         Reflect.setFinal((Class)Material.class, "byId", byId);
      }

      byId[id] = newMaterial;
      return newMaterial;
   }
}
