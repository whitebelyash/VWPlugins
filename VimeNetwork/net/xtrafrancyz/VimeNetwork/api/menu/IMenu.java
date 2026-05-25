package net.xtrafrancyz.VimeNetwork.api.menu;

import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface IMenu extends InventoryHolder {
   void onClick(ItemStack var1, Player var2, int var3, ClickType var4);

   default void show(Player player) {
      Invs.forceOpen(player, (Inventory)this.getInventory());
   }
}
