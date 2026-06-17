package net.xtrafrancyz.VimeNetwork.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.ConfirmMenu;
import net.xtrafrancyz.VimeNetwork.api.menu.IMenu;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ReportsCommand implements CommandExecutor {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private ReportsInventory menu = new ReportsInventory();

   public ReportsCommand() {
      Bukkit.getScheduler().scheduleSyncRepeatingTask(VNPlugin.instance(), () -> {
         if (this.menu.lastUsed != 0L && System.currentTimeMillis() - this.menu.lastUsed > 1200000L) {
            this.menu.cleanup();
         }

      }, 12000L, 12000L);
      VimeNetwork.core().addHandler(Packet52CustomMessage.class, this::onCustomMessage);
   }

   public void onCustomMessage(Packet52CustomMessage packet) {
      if (packet.tag.equals("reports") && packet.data.getString("action", "").equals("list")) {
         ArrayList<Violator> violators = new ArrayList();

         for(CoreByteMap candidate : packet.data.getMapArray("candidates")) {
            Violator violator = new Violator(candidate.getString("username"), Rank.getRank(candidate.getString("rank")));

            for(CoreByteMap report : candidate.getMapArray("reports")) {
               violator.reports.add(new Report(report.getString("reporter"), report.getString("details"), report.getInt("time")));
            }

            violators.add(violator);
         }

         Bukkit.getScheduler().scheduleSyncDelayedTask(VNPlugin.instance(), () -> this.menu.load(violators), 2L);
      }

   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.MODER, true)) {
         return true;
      } else {
         this.menu.show((Player)sender);
         return true;
      }
   }

   private static class ReportsInventory implements IMenu {
      private long lastUsed;
      private Inventory inv;
      private boolean loading;
      private List violators;

      private ReportsInventory() {
         this.lastUsed = 0L;
         this.violators = new ArrayList();
      }

      public void cleanup() {
         this.inv = null;
         this.violators.clear();
         this.lastUsed = 0L;
      }

      private void use() {
         this.lastUsed = System.currentTimeMillis();
         if (this.inv == null) {
            this.inv = Bukkit.createInventory(this, 54, "Меню жалоб");
            this.inv.setItem(4, Items.name(Material.MAGMA_CREAM, "&aОбновить"));
         }

      }

      public void load(ArrayList violators) {
         violators.sort((v1, v2) -> {
            int res = Integer.compare(v2.reports.size(), v1.reports.size());
            return res != 0 ? res : v1.username.compareTo(v2.username);
         });
         this.violators = violators;

         for(int i = 0; i < violators.size(); ++i) {
            Violator violator = (Violator)violators.get(i);
            List<String> lore = new ArrayList();
            lore.add("&aКоличество жалоб: &f" + violator.reports.size());

            for(Report report : violator.reports) {
               lore.add(" &e" + report.reporter + "&f: &7" + report.details);
            }

            lore.add("");
            lore.add("&aНажмите для действий");
            String prefix = "&f";
            if (violator.rank != Rank.PLAYER) {
               prefix = violator.rank.getColor() + "[" + violator.rank.getPrefix() + "] ";
            }

            this.inv.setItem(this.getSlot(i), Items.name(Items.head(violator.username), prefix + violator.username, lore));
         }

         this.loading = false;
      }

      private int getSlot(int index) {
         return 9 + index;
      }

      private int getIndex(int slot) {
         return slot - 9;
      }

      private void requestViolators() {
         this.loading = true;
         this.use();
         if (this.violators != null) {
            for(int i = 0; i < this.violators.size(); ++i) {
               this.inv.clear(this.getSlot(i));
            }
         }

         VimeNetwork.core().sendPacket((new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE)).put("action", "list"));
      }

      public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
         if (!this.loading) {
            if (slot == 4) {
               this.requestViolators();
            } else {
               int index = this.getIndex(slot);
               if (index >= 0 && index < this.violators.size()) {
                  (new ViolatorMenu(this, (Violator)this.violators.get(index))).show(player);
               }

            }
         }
      }

      public Inventory getInventory() {
         return this.inv;
      }

      public void show(Player player) {
         this.requestViolators();
         this.use();
         IMenu.super.show(player);
      }
   }

   private static class ViolatorMenu implements IMenu {
      private ReportsInventory parent;
      private Inventory inv;
      private Violator violator;

      public ViolatorMenu(ReportsInventory parent, Violator violator) {
         this.parent = parent;
         this.violator = violator;
         this.inv = Bukkit.createInventory(this, 54, "Нарушитель " + violator.username);
         this.inv.setItem(4, Items.name(Material.BED, "&f← &eНазад"));
         this.inv.setItem(20, Items.name(new ItemStack(Material.WOOL, 1, (short)5), "&aИгрок хороший", "&7Если у игрока все впорядке", "&7и жалоба на него ошибочна"));
         this.inv.setItem(22, Items.name(Material.WOOL, "&aТелепортироваться", "&7Телепортирует вас к нарушителю"));
         this.inv.setItem(24, Items.name(new ItemStack(Material.WOOL, 1, (short)14), "&aЗабанить", "&7Открывает меню с выбором", "&7причины и времени бана"));
         int index = 0;

         for(Report report : violator.reports) {
            this.inv.setItem(37 + index, Items.name(Material.PAPER, "&fРепорт #" + (index + 1), "&eВремя: &f" + ReportsCommand.DATE_FORMAT.format(new Date((long)(report.time * 1000))), "&eОтправитель: &f" + report.reporter, "&eПричина: &f" + report.details));
            ++index;
         }

      }

      public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
         switch (slot) {
            case 4:
               this.parent.show(player);
               break;
            case 20:
               ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                  VimeNetwork.core().sendPacket((new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE)).put("action", "reject").put("violator", this.violator.username).put("moder", player.getName()));
                  this.parent.requestViolators();
                  this.parent.show(player);
               }, "Отклонить жалобу");
               menu.setConfirmText("&aС игроком все впорядке");
               menu.setBackOnConfirm(false);
               menu.show(player);
               break;
            case 22:
               VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(this.violator.username, 8), (packet0) -> {
                  if (packet0.getId() == 1) {
                     Packet1PlayerInfo response = (Packet1PlayerInfo)packet0;
                     if (response.bukkit != null) {
                        EtpCommand.tpToServerNPlayer(player, response.bukkit, this.violator.username);
                        return;
                     }
                  }

                  U.msg(player, (String[])("&cИгрок &f" + this.violator.username + "&c оффлайн"));
               }, 400L, () -> U.msg(player, (String[])("&cОшибка связи с главным сервером")));
               break;
            case 24:
               (new BanMenu(this.violator, this)).show(player);
         }

      }

      public Inventory getInventory() {
         return this.inv;
      }
   }

   private static class BanMenu implements IMenu {
      private static List BANS = Arrays.asList(Pair.of(0, "Использование читов"), Pair.of(10080, "Использование багов"), Pair.of(2880, "Некорректная постройка"));
      private Violator violator;
      private ViolatorMenu parent;
      private Inventory inv;

      public BanMenu(Violator violator, ViolatorMenu parent) {
         this.violator = violator;
         this.parent = parent;
         this.inv = Bukkit.createInventory(this, 27, "Бан для " + violator.username);
         this.inv.setItem(4, Items.name(Material.BED, "&f← &eНазад"));
         int index = 0;

         for(Pair ban : BANS) {
            String time = "&fнавсегда";
            if ((Integer)ban.getLeft() != 0) {
               time = "на &f" + ban.getLeft() + "&a мин.";
            }

            this.inv.setItem(this.getSlot(index++), Items.name(new ItemStack(Material.WOOL, 1, (short)14), "&aБан " + time, "&f" + (String)ban.getRight()));
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

      public void onClick(ItemStack is, Player player, int slot, ClickType clickType) {
         if (slot == 4) {
            this.parent.show(player);
         } else {
            int index = this.getIndex(slot);
            if (index >= 0 && index < BANS.size()) {
               Pair<Integer, String> ban = (Pair)BANS.get(index);
               ConfirmMenu menu = new ConfirmMenu(this.inv, () -> {
                  VimeNetwork.core().sendPacket((new Packet52CustomMessage("reports", Packet52CustomMessage.Scope.CORE)).put("action", "ban").put("violator", this.violator.username).put("moder", player.getName()).put("duration", ban.getLeft()).put("reason", ban.getRight()));
                  this.parent.parent.requestViolators();
                  this.parent.parent.show(player);
               }, "Подтверждение бана");
               String time = "навсегда&a";
               if ((Integer)ban.getLeft() != 0) {
                  time = "на &f" + ban.getLeft() + "&a мин.";
               }

               menu.setConfirmText("&aЗабанить " + this.violator.username, "&f" + time + " с причной", "&f" + (String)ban.getRight());
               menu.setBackOnConfirm(false);
               menu.show(player);
            }

         }
      }

      public Inventory getInventory() {
         return this.inv;
      }
   }

   private static class Violator {
      public final String username;
      public final Rank rank;
      public List reports;

      public Violator(String username, Rank rank) {
         this.username = username;
         this.rank = rank;
         this.reports = new LinkedList();
      }
   }

   private static class Report {
      public String reporter;
      public String details;
      public int time;

      public Report(String reporter, String details, int time) {
         this.reporter = reporter;
         this.details = details;
         this.time = time;
      }
   }
}
