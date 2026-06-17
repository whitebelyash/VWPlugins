/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer(sender.getName());
        if (VimeNetwork.lobby().getServerType() == ServerType.BUILD) {
            if (!networkPlayer.hasAndNotify(Permission.BUILDER)) {
                return true;
            }
        } else {
            if (!networkPlayer.hasAndNotify(Permission.VANISH)) {
                return true;
            }
            if (!networkPlayer.has(Rank.CHIEF) && !Spectators.instance().contains((Player)sender)) {
                U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f (/vanish)"));
                return true;
            }
        }
        if (args.length == 1) {
            if (args[0].contains(",")) {
                this.teleport(sender, args[0].split(","));
            } else {
                Player target = Bukkit.getPlayerExact((String)args[0]);
                if (target != null) {
                    this.teleport(sender, target);
                } else {
                    U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                }
            }
            return true;
        }
        if (args.length == 2 && networkPlayer.getRank().has(Rank.CHIEF)) {
            Player player1 = Bukkit.getPlayerExact((String)args[0]);
            if (player1 == null) {
                U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                return true;
            }
            Player player2 = Bukkit.getPlayerExact((String)args[1]);
            if (player2 == null) {
                U.msg(sender, "&c\u0418\u0433\u0440\u043e\u043a &f" + args[1] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
                return true;
            }
            this.teleport((CommandSender)player1, player2);
            return true;
        }
        if (args.length > 2 && !args[0].contains(",")) {
            this.teleport(sender, args);
            return true;
        }
        this.teleport(sender, Joiner.on((String)"").join((Object[])args).split(","));
        return true;
    }

    private void help(CommandSender sender) {
        ArrayList<String> list = new ArrayList<String>();
        list.add("&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        list.add("&e/tp &7<\u0438\u0433\u0440\u043e\u043a>&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043a \u0438\u0433\u0440\u043e\u043a\u0443");
        if (VimeNetwork.hasRank(sender, Rank.CHIEF, false)) {
            list.add("&e/tp &7<\u0438\u0433\u0440\u043e\u043a 1> <\u0438\u0433\u0440\u043e\u043a 2>&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043f\u0435\u0440\u0432\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430 \u043a\u043e \u0432\u0442\u043e\u0440\u043e\u043c\u0443");
        }
        list.add("&e/tp &7<x> <y> <z> [yaw] [pitch]&f: \u0422\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0430\u0446\u0438\u044f \u043d\u0430 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b");
        U.msg(sender, list);
    }

    private void teleport(CommandSender sender, String[] loc) {
        if (loc.length < 3) {
            this.help(sender);
            return;
        }
        try {
            Location parsed = new Location(((Player)sender).getWorld(), Double.parseDouble(loc[0].trim()), Double.parseDouble(loc[1].trim()), Double.parseDouble(loc[2].trim()));
            if (loc.length > 3) {
                parsed.setYaw(Float.parseFloat(loc[3].trim()) + 1.0E-4f);
            }
            if (loc.length > 4) {
                parsed.setPitch(Float.parseFloat(loc[4].trim()) + 1.0E-4f);
            }
            if (parsed.getX() % 1.0 < 1.0E-4 && parsed.getZ() % 1.0 < 1.0E-4) {
                parsed.setX(parsed.getX() + 0.5);
                parsed.setZ(parsed.getZ() + 0.5);
            }
            this.teleport(sender, parsed);
        }
        catch (Exception ex) {
            this.help(sender);
        }
    }

    private void teleport(CommandSender sender, Player entity) {
        this.teleport(sender, entity.getLocation());
        U.msg(sender, "&a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043a \u0438\u0433\u0440\u043e\u043a\u0443 " + entity.getName());
    }

    private void teleport(CommandSender sender, Location loc) {
        Player player = (Player)sender;
        if (loc.getYaw() == 0.0f) {
            loc.setYaw(player.getLocation().getYaw());
        }
        if (loc.getPitch() == 0.0f) {
            loc.setPitch(player.getLocation().getPitch());
        }
        player.teleport(loc);
    }
}

