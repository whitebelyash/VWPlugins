package net.xtrafrancyz.VimeNetwork.listeners;

import net.minecraft.server.v1_6_R3.EntityItem;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R3.CraftServer;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_6_R3.entity.CraftItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CancelDropItemFix implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onInventoryClick(InventoryClickEvent event) {
      if (event.getWhoClicked().getType() == EntityType.PLAYER) {
         if (VimeNetwork.features().CANCEL_DROP_ITEM_FIX.isEnabled() && event.getClick() == ClickType.DROP && !event.isCancelled()) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null) {
               return;
            }

            CraftItem item = new CraftItem((CraftServer)Bukkit.getServer(), new EntityItem(((CraftWorld)event.getWhoClicked().getWorld()).getHandle()));
            item.setItemStack(itemStack);
            WrappedPlayerDropItemEvent evt = new WrappedPlayerDropItemEvent((Player)event.getWhoClicked(), item);
            Bukkit.getPluginManager().callEvent(evt);
            if (evt.isCancelled()) {
               event.setCancelled(true);
            }
         }

      }
   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerDrop(PlayerDropItemEvent event) {
      if (event.isCancelled() && VimeNetwork.features().CANCEL_DROP_ITEM_FIX.isEnabled() && !(event instanceof WrappedPlayerDropItemEvent)) {
         Player p = event.getPlayer();
         ItemStack item = event.getItemDrop().getItemStack().clone();
         item.setAmount(p.getInventory().getItemInHand().getAmount() + 1);
         event.getItemDrop().remove();
         p.getInventory().setItem(p.getInventory().getHeldItemSlot(), item);
         event.setCancelled(false);
      }

   }

   public static class WrappedPlayerDropItemEvent extends PlayerDropItemEvent {
      public WrappedPlayerDropItemEvent(Player player, Item drop) {
         super(player, drop);
      }
   }
}
