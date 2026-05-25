package net.xtrafrancyz.VimeNetwork.impl.player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.goals.Goal;
import net.xtrafrancyz.VimeNetwork.api.util.Invs;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class GoalsInventory implements IMenu {
   private static final ItemStack HELP_COMMON;
   private static final ItemStack HELP_OBTAIN;
   private static final ItemStack HELP_CANCEL;
   private static final ItemStack CANCEL_ITEM;
   private final Inventory inv;
   private final NetworkPlayer player;

   public GoalsInventory(NetworkPlayer player) {
      this.player = player;
      int lines = (player.getGoals().getActiveGoals().size() + player.getGoals().getCustomGoals().size() - 1) / 9 + 1;
      this.inv = Bukkit.createInventory(this, 27 * lines, "Задания");
      this.inv.setItem(3, HELP_COMMON.clone());
      this.inv.setItem(4, HELP_OBTAIN.clone());
      this.inv.setItem(5, HELP_CANCEL.clone());
      this.update();
   }

   public void update() {
      long time = System.currentTimeMillis();
      Iterator<Map.Entry<String, Goal>> it = this.player.getGoals().getActiveGoals().entrySet().iterator();

      for(int i = 9; it.hasNext(); ++i) {
         Map.Entry<String, Goal> entry = (Map.Entry)it.next();
         Goal goal = (Goal)entry.getValue();
         List<String> text = goal.getText(true);
         List<String> lore = new ArrayList(text.size() + 4);
         if (text.size() > 1) {
            lore.addAll(text.subList(1, text.size()));
         }

         lore.add("");
         lore.add("&eВыполнено: &f" + goal.getProgress() + "/" + goal.getGoal());
         List<String> rewardText = goal.getRewardText();
         if (rewardText != null) {
            lore.add("&eНаграда:");

            for(String line : rewardText) {
               lore.add("  " + line);
            }
         }

         ParsedTime parsed = new ParsedTime(goal.finishTime * 1000L - time);
         String s = "";
         if (parsed.days > 0) {
            s = parsed.days + " д. ";
         }

         if (parsed.hours > 0) {
            s = s + parsed.hours + " ч.";
         } else {
            s = s + parsed.minutes + " м.";
         }

         lore.add("&eОсталось: &f" + s);
         this.inv.setItem(i, Items.name(goal.getItem(), (String)text.get(0), lore));
         this.inv.setItem(i + 9, Items.nbt(CANCEL_ITEM).setString("goal", (String)entry.getKey()).build());
         if (i % 9 == 8) {
            i += 18;
         }
      }

      it = this.player.getGoals().getCustomGoals().entrySet().iterator();

      for(int i = 1; it.hasNext(); ++i) {
         Map.Entry<String, Goal> entry = (Map.Entry)it.next();
         Goal goal = (Goal)entry.getValue();
         List<String> text = goal.getText(true);
         List<String> lore = new LinkedList();
         if (text.size() > 1) {
            lore.addAll(text.subList(1, text.size()));
         }

         lore.add("");
         lore.add("&eВыполнено: &f" + goal.getProgress() + "/" + goal.getGoal());
         List<String> rewardText = goal.getRewardText();
         if (rewardText != null) {
            lore.add("&eНаграда:");

            for(String line : rewardText) {
               lore.add("  " + line);
            }
         }

         if (goal.finishTime != -1L) {
            ParsedTime parsed = new ParsedTime(goal.finishTime * 1000L - time);
            String s = "";
            if (parsed.days > 0) {
               s = parsed.days + " д. ";
            }

            if (parsed.hours > 0) {
               s = s + parsed.hours + " ч.";
            } else {
               s = s + parsed.minutes + " м.";
            }

            lore.add("&eОсталось: &f" + s);
         }

         this.inv.setItem(this.inv.getSize() - 9 - i, Items.name(goal.getItem(), (String)text.get(0), lore));
      }

   }

   public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
      if (is.getTypeId() == CANCEL_ITEM.getTypeId()) {
         String id = Items.nbt(is).getString("goal");
         if (!id.isEmpty() && this.player.getCoins() >= 500) {
            ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
               this.player.getGoals().remove(id);
               this.player.takeCoins(500);
               this.inv.setItem(slot, (ItemStack)null);
               this.inv.setItem(slot - 9, (ItemStack)null);
            }, "Отмена задания");
            menu.setConfirmText("&cОтменить задание", "&fЦена: &c500 коинов");
            Invs.forceOpen(player, (InventoryHolder)menu);
         }
      }

   }

   public Inventory getInventory() {
      return this.inv;
   }

   static {
      HELP_COMMON = Items.glow(Items.name(Material.PAPER, "&aЧто это?", "&f Здесь показаны активные задания", "&fи степень их выполнения."));
      HELP_OBTAIN = Items.glow(Items.name(Material.PAPER, "&aКак получить задания?", "&f Задания выдаются в лобби,", "&e2 задания&f каждые &e24 часа."));
      HELP_CANCEL = Items.glow(Items.name(Material.PAPER, "&aЗачем их отменять?", "&f Только для того чтобы", "&fне показывалось оповещение", "&fоб активных заданиях.", "", "&f Взять новое, вместо старого,", "&fне получится."));
      CANCEL_ITEM = Items.name(new ItemStack(Material.INK_SACK, 1, (short)1), "&cОтменить задание", "&fЦена: &c500 коинов");
   }
}
