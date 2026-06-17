/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FindCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.WARDEN, true)) {
            return true;
        }
        if (args.length == 0) {
            U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:", "&e/find &7<\u0438\u0433\u0440\u043e\u043a>&f: \u041d\u0430\u0439\u0442\u0438 \u0441\u0435\u0440\u0432\u0435\u0440 \u0438\u0433\u0440\u043e\u043a\u0430");
            return true;
        }
        VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(args[0], 24), packet0 -> {
            if (packet0.getId() == 1) {
                Packet1PlayerInfo packet = (Packet1PlayerInfo)packet0;
                if (packet.bukkit == null) {
                    U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                } else {
                    sender.sendMessage(U.colored("&e\u0418\u0433\u0440\u043e\u043a &f" + packet.username + "&e \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440\u0435 &f" + packet.bukkit + "&e, \u043f\u0440\u043e\u043a\u0441\u0438 &f" + packet.bungee));
                }
            } else {
                U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            }
        }, 300L, () -> U.msg(sender, "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
        return true;
    }
}

