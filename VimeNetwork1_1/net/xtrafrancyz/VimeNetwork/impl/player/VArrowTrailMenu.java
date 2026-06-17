package net.xtrafrancyz.VimeNetwork.impl.player;

import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.ArrowTrail;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VArrowTrailMenu implements IMenu {
   private final Inventory inv;
   private final VPlayer player;
   private final Inventory prev;

   public VArrowTrailMenu(Inventory prev, NetworkPlayer nplayer) {
      this.prev = prev;
      this.inv = Bukkit.createInventory(this, 36, "След за стрелой");
      this.player = (VPlayer)nplayer;
      int index = 0;

      for(ArrowTrail trail : ArrowTrail.values()) {
         ItemStack is = trail.getItem();
         String color;
         String lore;
         if (this.player.availableArrowTrails.contains(trail.getId())) {
            if (this.player.getArrowTrail() == trail) {
               color = "&a";
               lore = "&aВыбрано";
            } else {
               color = "&b";
               lore = "&2Нажмите для выбора";
            }
         } else {
            color = "&c";
            lore = "&cМожно найти в сокровищнице";
         }

         Items.name(is, color + trail.getName(), lore);
         this.inv.setItem(this.getSlot(index++), is);
      }

   }

   private int getSlot(int index) {
      return 10 + 9 * (index / 7) + index % 7;
   }

   private int getIndex(int slot) {
      if (slot % 9 != 0 && (slot + 1) % 9 != 0) {
         slot -= 10;
         if (slot < 0) {
            return -1;
         } else {
            int row = slot / 9;
            return row * 7 + (slot - row * 9) % 7;
         }
      } else {
         return -1;
      }
   }

   public void onClick(ItemStack is, Player bukkitPlayer, int slot, ClickType clickType) {
      if (this.prev != null && slot == 4) {
         Invs.forceOpen(bukkitPlayer, (Inventory)this.prev);
      } else {
         int index = this.getIndex(slot);
         if (index >= 0 && index < ArrowTrail.values().length) {
            ArrowTrail selected = ArrowTrail.values()[index];
            if (this.player.availableArrowTrails.contains(selected.getId()) && this.player.getArrowTrail() != selected) {
               this.player.setArrowTrail(selected);
               bukkitPlayer.closeInventory();
            }

         }
      }
   }

   public Inventory getInventory() {
      return this.inv;
   }
}
