/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AlertCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.ADMIN, true)) {
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: " + ChatColor.WHITE + " /alert <message>");
            return true;
        }
        VimeNetwork.core().sendPacket(new Packet52CustomMessage("bcast", Packet52CustomMessage.Scope.ALL_BUNGEE).put("message", Joiner.on((String)" ").join((Object[])args)));
        return true;
    }
}

