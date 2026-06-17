/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.Arrays;
import java.util.stream.Collectors;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ListCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(VimeNetwork.isTournament() && VimeNetwork.hasPermission(sender, Permission.ORGANIZER, false) || VimeNetwork.hasRank(sender, Rank.CHIEF, true))) {
            return true;
        }
        String list = Arrays.stream(Bukkit.getOnlinePlayers()).map(VimeNetwork::getPlayer).map(NetworkPlayer::getColoredName).collect(Collectors.joining(", "));
        U.msg(sender, "\u041d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 " + F.plurals(Bukkit.getOnlinePlayers().length, "\u0438\u0433\u0440\u043e\u043a", "\u0438\u0433\u0440\u043e\u043a\u0430", "\u0438\u0433\u0440\u043e\u043a\u043e\u0432") + " \u043e\u043d\u043b\u0430\u0439\u043d:", " " + list);
        return true;
    }
}

