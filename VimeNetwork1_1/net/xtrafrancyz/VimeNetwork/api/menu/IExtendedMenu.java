package net.xtrafrancyz.VimeNetwork.api.menu;

import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface IExtendedMenu extends InventoryHolder {
   default void show(Player player) {
      Invs.forceOpen(player, (Inventory)this.getInventory());
   }

   void onClick(Player var1, InventoryClickEvent var2);
}
