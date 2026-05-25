package net.xtrafrancyz.BedWars.menu;

import java.util.List;
import java.util.stream.Collectors;
import net.xtrafrancyz.BedWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SpectatorMenu implements IMenu {
   public static final ItemStack MENU_ITEM;
   private Inventory inv = Bukkit.createInventory(this, 27, "Выбор игрока");

   public void update() {
      this.inv.clear();
      int i = 0;

      for(PlayerInfo player : (List)PlayerInfo.PLAYERS.values().stream().filter((p) -> p.team != null).sorted((p1, p2) -> {
         int diff = p1.team.chatColor.getChar() - p2.team.chatColor.getChar();
         return diff != 0 ? diff : p1.username.compareToIgnoreCase(p2.username);
      }).collect(Collectors.toList())) {
         this.inv.setItem(i++, Items.name(Items.head(player.username), player.team.chatColor + player.username, new String[]{"Нажмите для телепортации"}));
      }

   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      SkullMeta meta = (SkullMeta)is.getItemMeta();
      if (meta != null) {
         Player target = Bukkit.getPlayerExact(meta.getOwner());
         if (target != null) {
            player.closeInventory();
            player.teleport(target);
         }

      }
   }

   public Inventory getInventory() {
      return this.inv;
   }

   static {
      MENU_ITEM = Items.menuTitle(new ItemStack(Material.SKULL_ITEM, 1, (short)3), "Телепортация к игрокам", new String[0]);
   }
}
