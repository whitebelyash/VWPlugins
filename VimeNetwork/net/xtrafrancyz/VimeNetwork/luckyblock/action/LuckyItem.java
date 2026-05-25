package net.xtrafrancyz.VimeNetwork.luckyblock.action;

import java.util.List;
import java.util.function.Supplier;
import net.xtrafrancyz.VimeNetwork.luckyblock.LBActionItem;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckyItem extends LBActionItem {
   private ItemStack[] is = null;
   private Supplier[] supplier = null;
   private Supplier supplier2 = null;

   public LuckyItem(ItemStack... is) {
      this.is = is;
   }

   @SafeVarargs
   public LuckyItem(Supplier... supplier) {
      this.supplier = supplier;
   }

   public LuckyItem(Supplier supplier) {
      this.supplier2 = supplier;
   }

   protected void populateDrop(List drop, Block block, Player player) {
      if (this.is != null) {
         for(ItemStack is0 : this.is) {
            drop.add(is0.clone());
         }
      } else if (this.supplier != null) {
         for(Supplier supplier0 : this.supplier) {
            drop.add(supplier0.get());
         }
      } else {
         for(ItemStack is : (List)this.supplier2.get()) {
            drop.add(is);
         }
      }

   }
}
