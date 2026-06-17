/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Entity
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet200GetServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet201ServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.commands.StpCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EtpCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.WARDEN, true)) {
            return true;
        }
        if (args.length == 0) {
            U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:", "&e/" + label + " &7<\u0438\u0433\u0440\u043e\u043a>&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0443 \u043d\u0430 \u043b\u044e\u0431\u043e\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0435");
            return true;
        }
        String target = args[0];
        Player targetPlayer = Bukkit.getPlayerExact((String)args[0]);
        if (targetPlayer == null) {
            VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(target, 8), packet0 -> {
                if (packet0.getId() == 1) {
                    Packet1PlayerInfo packet = (Packet1PlayerInfo)packet0;
                    if (packet.bukkit != null) {
                        EtpCommand.tpToServerNPlayer(sender, packet.bukkit, target);
                        return;
                    }
                }
                U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + target + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            }, 400L, () -> U.msg(sender, "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
        } else {
            U.msg(sender, "&a\u0418\u0433\u0440\u043e\u043a \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u043d\u0430 \u0432\u0430\u0448\u0435\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0435");
            Player player = (Player)sender;
            VNPlugin.instance().vanishCommand.enableVanish(player);
            player.teleport((Entity)targetPlayer);
        }
        return false;
    }

    public static void tpToServerNPlayer(CommandSender sender, String server, String player) {
        if (server.equals(VimeNetwork.lobby().getServerId())) {
            ((Player)sender).teleport((Entity)Bukkit.getPlayerExact((String)player));
            return;
        }
        if (!StpCommand.ALLOWED_SERVER_TYPES.contains(server.split("_")[0])) {
            U.msg(sender, "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c. \u0412 \u0434\u043e\u0441\u0442\u0443\u043f\u0435 \u043e\u0442\u043a\u0430\u0437\u0430\u043d\u043e");
            return;
        }
        VimeNetwork.core().sendPacket(new Packet200GetServersInfo(1, server), packet0 -> {
            Packet201ServersInfo packet = (Packet201ServersInfo)packet0;
            if (packet.servers.isEmpty()) {
                U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            } else {
                Packet201ServersInfo.Data data = packet.servers.get(0);
                switch (Lobby.State.byId(data.state)) {
                    case DENY_ALL: 
                    case OFFLINE: {
                        U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u0437\u0430\u043a\u0440\u044b\u0442 \u0434\u043b\u044f \u0432\u0445\u043e\u0434\u0430");
                        return;
                    }
                }
                for (String line : data.menuText) {
                    if (!line.contains("\u041d\u0430\u0431\u043e\u0440 \u0438\u0433\u0440\u043e\u043a\u043e\u0432")) continue;
                    U.msg(sender, "&c\u041d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c \u0441\u0435\u0439\u0447\u0430\u0441 \u0438\u0434\u0435\u0442 \u043d\u0430\u0431\u043e\u0440 \u0438\u0433\u0440\u043e\u043a\u043e\u0432. \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043e\u0442\u043c\u0435\u043d\u0435\u043d\u0430");
                    return;
                }
                U.msg(sender, "&a\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 &f" + server);
                CoreByteMap switchData = new CoreByteMap();
                switchData.put("teleportToPlayer", player);
                VimeNetwork.toServer(server, (Player)sender, switchData);
            }
        }, 400L, () -> U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 \u043e\u0442\u0432\u0435\u0447\u0430\u0435\u0442 \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u0434\u043e\u043b\u0433\u043e. \u041a\u0430\u043a\u0438\u0435-\u0442\u043e \u043f\u0440\u043e\u0431\u043b\u0435\u043c\u044b..."));
    }
}

