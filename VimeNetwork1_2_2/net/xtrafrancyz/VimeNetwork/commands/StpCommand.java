/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.collect.Sets;
import java.util.Set;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet200GetServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet201ServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StpCommand
implements CommandExecutor {
    static final Set<String> ALLOWED_SERVER_TYPES = Sets.newHashSet((Object[])new String[]{"SW", "BW", "MW", "GG", "ANN", "BWH", "SWT", "BWQ", "HG", "HGL", "KPVP", "BB", "LOBBY", "DR", "CP", "BP", "DUELS"});
    static final Set<String> ALLOWED_ORGANIZER = Sets.union(ALLOWED_SERVER_TYPES, (Set)Sets.newHashSet((Object[])new String[]{"BWH-T"}));

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasPermission(sender, Permission.ORGANIZER, false) && !VimeNetwork.hasRank(sender, Rank.WARDEN, true)) {
            return true;
        }
        if (args.length == 0) {
            U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:", "&e/stp &7<\u0438\u0433\u0440\u043e\u043a>&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 \u0438\u0433\u0440\u043e\u043a\u0430", "&e/stp &7@<\u0441\u0435\u0440\u0432\u0435\u0440>&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u043d\u044b\u0439 \u0441\u0435\u0440\u0432\u0435\u0440");
            return true;
        }
        String target = args[0];
        if (target.charAt(0) == '@') {
            StpCommand.tpToServer(sender, target.substring(1).toUpperCase());
            return true;
        }
        VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(target, 8), packet0 -> {
            if (packet0.getId() == 1) {
                Packet1PlayerInfo packet = (Packet1PlayerInfo)packet0;
                if (packet.bukkit != null) {
                    StpCommand.tpToServer(sender, packet.bukkit);
                } else {
                    U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + target + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                }
            } else {
                U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + target + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            }
        }, 300L, () -> U.msg(sender, "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
        return true;
    }

    private static void tpToServer(CommandSender sender, String server) {
        Set<String> allowed;
        if (server.equals(VimeNetwork.lobby().getServerId())) {
            U.msg(sender, "&a\u0412\u044b \u0443\u0436\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u043d\u0430 \u043d\u0443\u0436\u043d\u043e\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u0435");
            return;
        }
        Set<String> set = allowed = VimeNetwork.getPlayer(sender.getName()).has(Permission.ORGANIZER) ? ALLOWED_ORGANIZER : ALLOWED_SERVER_TYPES;
        if (!allowed.contains(server.split("_")[0])) {
            U.msg(sender, "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c. \u0412 \u0434\u043e\u0441\u0442\u0443\u043f\u0435 \u043e\u0442\u043a\u0430\u0437\u0430\u043d\u043e");
            return;
        }
        VimeNetwork.core().sendPacket(new Packet200GetServersInfo(2, server), packet0 -> {
            Packet201ServersInfo packet = (Packet201ServersInfo)packet0;
            if (packet.servers.isEmpty()) {
                U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            } else {
                switch (Lobby.State.byId(packet.servers.get((int)0).state)) {
                    case DENY_ALL: 
                    case OFFLINE: {
                        U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 &f" + server + "&c \u0432 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u0437\u0430\u043a\u0440\u044b\u0442 \u0434\u043b\u044f \u0432\u0445\u043e\u0434\u0430");
                        return;
                    }
                }
                U.msg(sender, "&a\u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 &f" + server);
                VimeNetwork.toServer(server, (Player)sender);
            }
        }, 300L, () -> U.msg(sender, "&c\u0421\u0435\u0440\u0432\u0435\u0440 \u043e\u0442\u0432\u0435\u0447\u0430\u0435\u0442 \u0441\u043b\u0438\u0448\u043a\u043e\u043c \u0434\u043e\u043b\u0433\u043e. \u041a\u0430\u043a\u0438\u0435-\u0442\u043e \u043f\u0440\u043e\u0431\u043b\u0435\u043c\u044b..."));
    }
}

