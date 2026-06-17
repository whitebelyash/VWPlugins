/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.MODER, true)) {
            return true;
        }
        if (cmd.getName().equals("kick")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /kick <\u043d\u0438\u043a> [\u043f\u0440\u0438\u0447\u0438\u043d\u0430]");
            } else {
                String kickmessage;
                if (args.length > 1) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; ++i) {
                        sb.append(args[i]).append(' ');
                    }
                    kickmessage = sb.substring(0, sb.length() - 1);
                } else {
                    kickmessage = "\u041d\u0435 \u0443\u043a\u0430\u0437\u0430\u043d\u0430";
                }
                String formattedKickMessage = T.kickMessage(args[0], kickmessage, sender.getName());
                Player player = Bukkit.getPlayerExact((String)args[0]);
                if (player != null) {
                    player.kickPlayer(U.colored(formattedKickMessage));
                    sender.sendMessage(ChatColor.GREEN + "\u0418\u0433\u0440\u043e\u043a " + player.getName() + " \u0431\u044b\u043b \u043a\u0438\u043a\u043d\u0443\u0442");
                    VimeNetwork.logAction(sender.getName(), "mod.kick", args[0], kickmessage);
                } else if (VimeNetwork.core().isEnabled()) {
                    VimeNetwork.core().sendPacket(new Packet52CustomMessage("kick", Packet52CustomMessage.Scope.BUNGEE_OF_PLAYER, args[0]).put("username", args[0]).put("reason", formattedKickMessage), answer -> {
                        if (answer instanceof Packet53Answer && ((Packet53Answer)answer).status.equals("notfound")) {
                            sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a " + args[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                        }
                    }, 500L, () -> {
                        sender.sendMessage(ChatColor.GREEN + "\u0418\u0433\u0440\u043e\u043a " + args[0] + " \u0431\u044b\u043b \u043a\u0438\u043a\u043d\u0443\u0442");
                        VimeNetwork.logAction(sender.getName(), "mod.kick", args[0], kickmessage);
                    });
                } else {
                    sender.sendMessage(ChatColor.RED + "\u0418\u0433\u0440\u043e\u043a " + args[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                }
            }
        }
        return true;
    }
}

