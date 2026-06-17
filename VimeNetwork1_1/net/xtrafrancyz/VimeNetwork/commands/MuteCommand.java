package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MuteCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!VimeNetwork.hasPermission(sender, Permission.MUTE, true)) {
         return true;
      } else {
         if (command.getName().equals("mute")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.RED + "Использование: /mute <ник> [время в минутах] [причина]");
            } else {
               int minutes = 0;
               String reason = "";
               if (args.length > 1) {
                  try {
                     minutes = Integer.parseInt(args[1]);

                     for(int i = 2; i < args.length; ++i) {
                        reason = reason + args[i] + " ";
                     }
                  } catch (NumberFormatException var9) {
                     minutes = 1440;

                     for(int i = 1; i < args.length; ++i) {
                        reason = reason + args[i] + " ";
                     }
                  }
               }

               if (minutes <= 0) {
                  U.msg(sender, "&cОшибочно указано время мута");
                  return true;
               }

               if (reason.isEmpty()) {
                  reason = "Не указана";
               } else {
                  reason = reason.substring(0, reason.length() - 1);
               }

               VimeNetwork.mute(args[0], minutes, reason, sender.getName());
            }
         } else if (command.getName().equals("unmute")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.RED + "Использование: /unmute <ник>");
            } else {
               VimeNetwork.mysql().update("DELETE FROM `mutes` WHERE username = '" + StringEscapeUtils.escapeSql(args[0]) + "'", (updates) -> {
                  if (updates > 0) {
                     VimeNetwork.unmute(args[0], sender.getName());
                     VimeNetwork.logAction(sender.getName(), "mod.unmute", args[0]);
                  } else {
                     U.msg(sender, T.error("VimeWorld", "Игрок &e" + args[0] + "&c не замучен"));
                  }

               });
            }
         }

         return true;
      }
   }
}
