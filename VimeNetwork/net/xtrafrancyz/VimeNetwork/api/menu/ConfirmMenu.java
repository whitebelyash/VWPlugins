package net.xtrafrancyz.VimeNetwork.api.menu;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfirmMenu implements IMenu {
   private static final Set CONFIRM_SLOTS = ImmutableSet.of(10, 11, 12, 19, 20, 21, new Integer[]{28, 29, 30});
   private static final Set CANCEL_SLOTS = ImmutableSet.of(14, 15, 16, 23, 24, 25, new Integer[]{32, 33, 34});
   private Runnable callback;
   private Runnable cancelledCallback;
   private Inventory prev;
   private Inventory inv;
   private boolean confirmInited = false;
   private boolean cancelInited = false;
   private boolean backOnConfirm = true;

   public ConfirmMenu(Inventory prev, Runnable callback, String title) {
      this.callback = callback;
      this.prev = prev;
      this.inv = Bukkit.createInventory(this, 45, title);
   }

   public void setConfirmText(String name, String... lore) {
      this.confirmInited = true;
      ItemStack item = Items.name(Material.EMERALD_BLOCK, name, lore);
      CONFIRM_SLOTS.forEach((slot) -> this.inv.setItem(slot, item));
   }

   public void setCancelText(String name, String... lore) {
      this.cancelInited = true;
      ItemStack item = Items.name(Material.REDSTONE_BLOCK, name, lore);
      CANCEL_SLOTS.forEach((slot) -> this.inv.setItem(slot, item));
   }

   public void setBackOnConfirm(boolean flag) {
      this.backOnConfirm = flag;
   }

   public void setCancelledCallback(Runnable callback) {
      this.cancelledCallback = callback;
   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      if (CONFIRM_SLOTS.contains(slot)) {
         this.callback.run();
         if (this.backOnConfirm) {
            if (this.prev != null) {
               Invs.forceOpen(player, (Inventory)this.prev);
            } else {
               player.closeInventory();
            }
         }
      } else if (CANCEL_SLOTS.contains(slot)) {
         if (this.cancelledCallback != null) {
            this.cancelledCallback.run();
         } else if (this.prev != null) {
            Invs.forceOpen(player, (Inventory)this.prev);
         } else {
            player.closeInventory();
         }
      }

   }

   public Inventory getInventory() {
      if (!this.confirmInited) {
         ItemStack confirm = Items.name(Material.EMERALD_BLOCK, "&aOK");
         CONFIRM_SLOTS.forEach((slot) -> this.inv.setItem(slot, confirm));
      }

      if (!this.cancelInited) {
         ItemStack cancel = Items.name(Material.REDSTONE_BLOCK, "&cОтмена");
         CANCEL_SLOTS.forEach((slot) -> this.inv.setItem(slot, cancel));
      }

      return this.inv;
   }
}
