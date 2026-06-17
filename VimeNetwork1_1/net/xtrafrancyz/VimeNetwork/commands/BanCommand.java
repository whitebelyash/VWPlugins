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

public class BanCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (!VimeNetwork.hasPermission(sender, Permission.BAN, true)) {
         return true;
      } else if (!command.getName().equals("ban")) {
         if (command.getName().equals("unban")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.RED + "Использование: /unban <ник>");
            } else {
               VimeNetwork.mysql().update("UPDATE bans SET status = 0 WHERE username = '" + StringEscapeUtils.escapeSql(args[0]) + "' AND status = 1", (amount) -> {
                  if (amount > 0) {
                     U.bcast(T.success(sender.getName(), "Игрок &e" + args[0] + "&a был разбанен"));
                     VimeNetwork.logAction(sender.getName(), "mod.unban", args[0]);
                  } else {
                     U.msg(sender, "&cИгрок " + args[0] + " не забанен");
                  }

               });
            }
         }

         return true;
      } else {
         if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Использование: /ban <ник> [время в минутах] [причина]");
         } else {
            int minutes = 0;
            String reason = "";
            if (args[0].length() > 20) {
               sender.sendMessage(ChatColor.RED + "Ник игрока не может быть больше 20-ти символов");
               return true;
            }

            if (args.length > 1) {
               try {
                  minutes = Integer.parseInt(args[1]);

                  for(int i = 2; i < args.length; ++i) {
                     reason = reason + args[i] + " ";
                  }
               } catch (NumberFormatException var9) {
                  minutes = 0;

                  for(int i = 1; i < args.length; ++i) {
                     reason = reason + args[i] + " ";
                  }
               }
            }

            if (minutes < 0) {
               minutes = 0;
            }

            if (reason.isEmpty()) {
               reason = "Не указана";
            } else {
               reason = reason.substring(0, reason.length() - 1);
            }

            VimeNetwork.ban(args[0], minutes, reason, sender.getName());
         }

         return true;
      }
   }
}
