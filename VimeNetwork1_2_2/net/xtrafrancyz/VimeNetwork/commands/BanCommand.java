/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BanCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!VimeNetwork.hasPermission(sender, Permission.BAN, true)) {
            return true;
        }
        if (command.getName().equals("ban")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /ban <\u043d\u0438\u043a> [\u0432\u0440\u0435\u043c\u044f \u0432 \u043c\u0438\u043d\u0443\u0442\u0430\u0445] [\u043f\u0440\u0438\u0447\u0438\u043d\u0430]");
            } else {
                int minutes = 0;
                String reason = "";
                if (args[0].length() > 20) {
                    sender.sendMessage(ChatColor.RED + "\u041d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430 \u043d\u0435 \u043c\u043e\u0436\u0435\u0442 \u0431\u044b\u0442\u044c \u0431\u043e\u043b\u044c\u0448\u0435 20-\u0442\u0438 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432");
                    return true;
                }
                if (args.length > 1) {
                    try {
                        minutes = Integer.parseInt(args[1]);
                        for (int i = 2; i < args.length; ++i) {
                            reason = reason + args[i] + " ";
                        }
                    }
                    catch (NumberFormatException e) {
                        minutes = 0;
                        for (int i = 1; i < args.length; ++i) {
                            reason = reason + args[i] + " ";
                        }
                    }
                }
                if (minutes < 0) {
                    minutes = 0;
                }
                reason = reason.isEmpty() ? "\u041d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u0430" : reason.substring(0, reason.length() - 1);
                VimeNetwork.ban(args[0], minutes, reason, sender.getName());
            }
            return true;
        }
        if (command.getName().equals("unban")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /unban <\u043d\u0438\u043a>");
            } else {
                VimeNetwork.unban(args[0], sender.getName());
            }
        }
        return true;
    }
}

