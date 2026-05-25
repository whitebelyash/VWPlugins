package net.xtrafrancyz.VimeNetwork.listeners;

import java.util.logging.Level;
import net.xtrafrancyz.VimeNetwork.api.menu.IExtendedMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenuClosable;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked().getType() == EntityType.PLAYER) {
         InventoryHolder topHolder = event.getView().getTopInventory().getHolder();
         if (topHolder instanceof IMenu) {
            if (event.getCurrentItem() != null && event.getClickedInventory().getHolder() instanceof IMenu) {
               try {
                  ((IMenu)topHolder).onClick(event.getCurrentItem(), (Player)event.getWhoClicked(), event.getSlot(), event.getClick());
               } catch (Exception e) {
                  Bukkit.getLogger().log(Level.WARNING, topHolder.getClass().getName(), e);
               }
            }

            event.setCancelled(true);
         } else if (topHolder instanceof IExtendedMenu && event.getClickedInventory().getHolder() instanceof IExtendedMenu) {
            event.setCancelled(true);

            try {
               ((IExtendedMenu)topHolder).onClick((Player)event.getWhoClicked(), event);
            } catch (Exception e) {
               Bukkit.getLogger().log(Level.WARNING, topHolder.getClass().getName(), e);
            }
         }

      }
   }

   @EventHandler
   public void onInventoryClose(InventoryCloseEvent event) {
      if (event.getPlayer().getType() == EntityType.PLAYER && event.getInventory().getHolder() instanceof IMenuClosable) {
         ((IMenuClosable)event.getInventory().getHolder()).onClose();
      }

   }
}
