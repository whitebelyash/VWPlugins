package net.xtrafrancyz.BedWars.menu;

import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectatorSettingsMenu implements IMenu {
   public static final ItemStack MENU_ITEM;
   private static final Material[] BOOTS;
   private final Inventory inv;

   public SpectatorSettingsMenu(Player player) {
      int level = getSpeedLevel(player);
      this.inv = Bukkit.createInventory(this, 27);

      for(int i = 1; i <= 5; ++i) {
         ItemStack is;
         if (level == i) {
            is = Items.glow(Items.name(BOOTS[i - 1], "&a&lСкорость полета " + i, new String[]{"&7Выбрано"}));
         } else {
            is = Items.name(BOOTS[i - 1], "&b&lСкорость полета " + i, new String[]{"Нажмите для выбора"});
         }

         this.inv.setItem(10 + i, is);
      }

   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      slot -= 10;
      if (slot >= 1 && slot <= 5) {
         U.msg(player, new String[]{"Вам установлена скорость &a" + slot});
         player.setFlySpeed(0.1F + 0.05F * (float)(slot - 1));
         player.closeInventory();
      }

   }

   private static int getSpeedLevel(Player player) {
      float speed = player.getFlySpeed();

      int i;
      for(i = 0; !(speed <= 0.101F + 0.05F * (float)i); ++i) {
      }

      return i + 1;
   }

   public Inventory getInventory() {
      return this.inv;
   }

   static {
      MENU_ITEM = Items.menuTitle(new ItemStack(Material.REDSTONE_COMPARATOR), "Настройки режима наблюдателя", new String[0]);
      BOOTS = new Material[]{Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS, Material.IRON_BOOTS, Material.GOLD_BOOTS, Material.DIAMOND_BOOTS};
   }
}
