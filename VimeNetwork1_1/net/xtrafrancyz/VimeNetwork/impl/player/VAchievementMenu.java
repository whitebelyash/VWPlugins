package net.xtrafrancyz.VimeNetwork.impl.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievements;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.CompletedAchievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.StatAchievement;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.WinAchievement;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VAchievementMenu implements IMenu {
   private static final short BACK_SLOT = 4;
   private static final ItemStack BACK_ITEM;
   private static final SimpleDateFormat DATE_FORMAT;
   private Inventory menu;
   private VPlayer player;

   public VAchievementMenu(VPlayer player) {
      this.player = player;
      this.menu = Bukkit.createInventory(this, 54, "Достижения");

      for(Achievement.Group group : Achievement.Group.values()) {
         if (group.getSlot() != -1) {
            ItemStack is = group.getItemStack().clone();
            List<String> lore = new LinkedList();
            int count = this.getCompletedCount(group);
            int total = group.getAchievements().size();
            int percent = Math.round((float)(100 * count) / (float)total);
            lore.add("&7Прогресс: &f" + count + "&7/&f" + total + "&7 (" + (percent == 100 ? "&a" : "") + percent + "%&7)");
            lore.add("");
            lore.add("&aНажмите для просмотра");
            is = Items.name(is, "&b&l" + group.getName(), lore);
            this.menu.setItem(group.getSlot(), is);
         }
      }

   }

   private int getCompletedCount(Achievement.Group group) {
      int count = 0;
      Achievements a = this.player.getAchievements();

      for(Achievement achievement : group.getAchievements()) {
         if (a.isCompleted(achievement)) {
            ++count;
         }
      }

      return count;
   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      for(Achievement.Group group : Achievement.Group.values()) {
         if (group.getSlot() == slot) {
            (new Category(group)).show(player);
            return;
         }
      }

   }

   public Inventory getInventory() {
      return this.menu;
   }

   static {
      BACK_ITEM = Items.name(Material.BED, "&f← &eНазад");
      DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
   }

   private class Category implements IMenu {
      private Inventory inv;

      public Category(Achievement.Group group) {
         this.inv = Bukkit.createInventory(this, 54, group.getName());
         this.inv.setItem(4, VAchievementMenu.BACK_ITEM.clone());
         int common = 9;
         int win = 38;

         for(Achievement a : group.getAchievements()) {
            if (!a.isHidden() || VAchievementMenu.this.player.getAchievements().isCompleted(a)) {
               if (a instanceof WinAchievement) {
                  this.inv.setItem(win++, this.getItem(a));
               } else {
                  this.inv.setItem(common++, this.getItem(a));
               }
            }
         }

      }

      private ItemStack getItem(Achievement a) {
         boolean complete = false;
         CompletedAchievement ca = VAchievementMenu.this.player.getAchievements().getCompletedAchievement(a);
         String name;
         Material type;
         if (ca != null) {
            name = "&a" + a.getName();
            complete = true;
            type = Material.DIAMOND;
         } else {
            type = Material.COAL;
            if (a.getGroup() == Achievement.Group.SECRET) {
               name = "&7???";
            } else {
               name = "&c" + a.getName();
            }
         }

         List<String> lore = new LinkedList();
         if (!complete && a.getGroup() == Achievement.Group.SECRET) {
            lore.add("&7???");
         } else {
            for(String line : a.getDescription()) {
               lore.add("&f" + line);
            }

            lore.add("");
            lore.add("&7Награда: &e" + U.pluralsCoins(a.getReward()));
            if (a instanceof StatAchievement) {
               StatAchievement sa = (StatAchievement)a;
               int progress = VAchievementMenu.this.player.getStats().get(sa.getStat());
               lore.add("&7Прогресс: &f" + progress + "&7/&f" + sa.getNeeded() + " &7(" + (progress >= sa.getNeeded() ? "&a" : "") + Math.min(100, Math.round(100.0F * (float)progress / (float)sa.getNeeded())) + "%&7)");
            }

            if (complete) {
               lore.add("&7Дата выполнения: &a" + VAchievementMenu.DATE_FORMAT.format(new Date((long)ca.getTimestamp() * 1000L)));
            }
         }

         return Items.name(type, name, lore);
      }

      public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
         if (slot == 4) {
            Invs.forceOpen(player, (Inventory)VAchievementMenu.this.menu);
         }

      }

      public Inventory getInventory() {
         return this.inv;
      }
   }
}
