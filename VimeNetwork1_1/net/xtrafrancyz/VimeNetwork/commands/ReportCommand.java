package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.Packet68Report;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.player.DailyMetaValue;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ReportCommand implements CommandExecutor, Listener {
   private static final int DAILY_REPORTS = 3;
   private static final Map CHEAT_TYPES = (Map)Stream.of("Fly", "Speed", "KillAura", "Knockback", "Other").collect(Collectors.toMap(String::toLowerCase, (v) -> v));
   private Map pending = new HashMap();

   public ReportCommand() {
      Bukkit.getPluginManager().registerEvents(this, VNPlugin.instance());
   }

   @EventHandler
   private void onLeave(PlayerLeaveEvent event) {
      this.pending.remove(event.getPlayer().getName());
   }

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.core().isConnected()) {
         U.msg(sender, "&cВ данный момент команда не работает. Попробуйте позже");
         return true;
      } else if (VimeNetwork.getPlayer(sender.getName()).getId() == -1) {
         U.msg(sender, "&cИнформация еще не загрузилась. Попробуйте позже");
         return true;
      } else if (args.length == 0) {
         this.printHelp(sender);
         return true;
      } else if (args.length >= 2 && args[1].equalsIgnoreCase(sender.getName())) {
         U.msg(sender, "&cВы не можете пожаловаться на себя");
         return true;
      } else {
         switch (args[0].toLowerCase()) {
            case "chat":
               if (args.length != 2) {
                  U.msg(sender, "&e/report chat&7 <ник>");
               } else {
                  this.tryReport(sender, args[1], () -> {
                     this.pending.put(sender.getName(), new PendingReport(args[1], Packet68Report.Type.CHAT, (String)null));
                     U.msg(sender, "&aДля подтверждения жалобы напишите команду &f/report confirm", "&aПоследние 20 сообщений этого игрока будут отправлены модераторам на проверку");
                  });
               }
               break;
            case "cheat":
               if (args.length != 3) {
                  U.msg(sender, "&e/report cheat&7 <ник> <тип читов>&f: сообщить о читере");
                  this.printCheatTypes(sender);
               } else {
                  String username = args[1];
                  String cheat = (String)CHEAT_TYPES.get(args[2].toLowerCase());
                  if (cheat == null) {
                     U.msg(sender, "&cУказанный тип чита не найден");
                     this.printCheatTypes(sender);
                  } else {
                     this.tryReport(sender, username, () -> {
                        this.pending.put(sender.getName(), new PendingReport(username, Packet68Report.Type.CHEAT, cheat));
                        U.msg(sender, "&aДля подтверждения жалобы напишите команду &f/report confirm", "&aВаше сообщение будет автоматически обработано системой");
                     });
                  }
               }
               break;
            case "retard":
               if (args.length < 2) {
                  U.msg(sender, "&e/report retard&7 <ник> <комментарий>&f: если игрок невыносим (мешает команде, афк, ничего не делает и т.д.)");
               } else {
                  String message = Joiner.on(' ').join(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));
                  if (message.isEmpty()) {
                     U.msg(sender, "&cНапишите комментарий");
                  } else {
                     this.tryReport(sender, args[1], () -> {
                        this.pending.put(sender.getName(), new PendingReport(args[1], Packet68Report.Type.RETARD, message));
                        U.msg(sender, "&aДля подтверждения жалобы напишите команду &f/report confirm", "&aВаше сообщение будет автоматически обработано системой");
                     });
                  }
               }
               break;
            case "confirm":
               PendingReport report = (PendingReport)this.pending.remove(sender.getName());
               if (report == null) {
                  this.printHelp(sender);
               } else {
                  VimeNetwork.metrics().add("report." + report.type.name().toLowerCase());
                  VimeNetwork.core().sendPacket(new Packet68Report(sender.getName(), report.violator, report.type, report.details));
                  DailyMetaValue dailyReports = new DailyMetaValue(VimeNetwork.getPlayer(sender.getName()), "report");
                  String value = dailyReports.getValue();
                  int remaining = (value == null ? 3 : Integer.parseInt(value)) - 1;
                  dailyReports.setValue(String.valueOf(remaining));
                  U.msg(sender, "Ваше сообщение успешно отправлено. Спасибо что помогаете делать сервер лучше!");
                  if (remaining != 0) {
                     U.msg(sender, "У вас " + U.plurals(remaining, "осталась", "осталось", "осталось") + " еще &e" + remaining + "&f " + U.plurals(remaining, "жалоба", "жалобы", "жалоб") + " на сегодня");
                  }
               }
               break;
            default:
               this.printHelp(sender);
         }

         return true;
      }
   }

   private void tryReport(CommandSender sender, String target, Runnable callback) {
      DailyMetaValue dailyReports = new DailyMetaValue(VimeNetwork.getPlayer(sender.getName()), "report");
      String value = dailyReports.getValue();
      if (value != null && Integer.parseInt(value) <= 0) {
         U.msg(sender, "&cУ вас закончились жалобы на сегодня.");
      } else {
         VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(target, 8), (packet0) -> {
            if (packet0.getId() == 53) {
               if (((Packet53Answer)packet0).status.equals("notfound")) {
                  U.msg(sender, "&cИгрок &f" + target + "&c не найден");
               }
            } else {
               callback.run();
            }

         }, 400L, () -> U.msg(sender, "&cОшибка связи с главным сервером"));
      }
   }

   private void printHelp(CommandSender sender) {
      U.msg(sender, "&e---------- &cСообщить о нарушении &f(&e/report&f)&e ---------------", "&e/report chat&7 <ник>&f: сообщить о нарушении в чате", "&e/report retard&7 <ник> <комментарий>&f: если игрок невыносим", "&e/report cheat&7 <ник> <тип читов>&f: сообщить о читере");
      this.printCheatTypes(sender);
   }

   private void printCheatTypes(CommandSender sender) {
      U.msg(sender, "&aВозможные типы читов:", "&aFly &f- игрок летает", "&aSpeed &f- игрок слишком быстро бегает", "&aKillAura &f- игрок убивает даже у себя за спиной", "&aKnockback &f- игрок почти не отталкивается при ударах", "&aOther &f- любой другой вариант читов");
   }

   private static class PendingReport {
      public String violator;
      public Packet68Report.Type type;
      public String details;

      public PendingReport(String violator, Packet68Report.Type type, String details) {
         this.violator = violator;
         this.type = type;
         this.details = details;
      }
   }
}
