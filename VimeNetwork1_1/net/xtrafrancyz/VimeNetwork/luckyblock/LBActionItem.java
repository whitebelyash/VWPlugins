package net.xtrafrancyz.VimeNetwork.luckyblock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public abstract class LBActionItem extends LBAction {
   public void onBreak(Block block, Player player) {
      Location loc = block.getLocation().add((double)0.5F, (double)0.5F, (double)0.5F);
      List<ItemStack> list = new LinkedList();
      this.populateDrop(list, block, player);

      for(ItemStack is : list) {
         this.giveItem(player, loc, is);
      }

   }

   protected void giveItem(Player player, Location loc, ItemStack is) {
      HashMap<Integer, ItemStack> map = player.getInventory().addItem(new ItemStack[]{is});

      for(ItemStack leftover : map.values()) {
         loc.getWorld().dropItemNaturally(loc, leftover);
      }

   }

   protected abstract void populateDrop(List var1, Block var2, Player var3);

   public void onItemInteract(PlayerInteractEvent event) {
   }

   public void onShootBow(EntityShootBowEvent event) {
   }

   public void onItemConsume(PlayerItemConsumeEvent event) {
   }
}
