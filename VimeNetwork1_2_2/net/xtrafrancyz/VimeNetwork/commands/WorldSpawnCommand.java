/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldSpawnCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.hasRank(sender, Rank.ADMIN, true)) {
            return true;
        }
        if (!(sender instanceof Player)) {
            U.msg(sender, "&c\u041a\u043e\u043c\u0430\u043d\u0434\u0430 \u0434\u043e\u0441\u0442\u0443\u043f\u043d\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u0438\u0433\u0440\u043e\u043a\u0430\u043c");
            return true;
        }
        Player player = (Player)sender;
        if (args.length == 0) {
            this.help((CommandSender)player);
        } else if (args[0].equalsIgnoreCase("tp")) {
            player.teleport(player.getWorld().getSpawnLocation());
            U.msg((CommandSender)player, "&a\u0412\u044b \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d\u044b \u043d\u0430 \u0441\u043f\u0430\u0432\u043d");
        } else if (args[0].equalsIgnoreCase("set")) {
            int z;
            int y;
            int x;
            if (args.length == 1) {
                Location loc = player.getLocation();
                x = loc.getBlockX();
                y = loc.getBlockY();
                z = loc.getBlockZ();
            } else if (args.length == 4) {
                try {
                    x = Integer.parseInt(args[1]);
                    y = Integer.parseInt(args[2]);
                    z = Integer.parseInt(args[3]);
                }
                catch (Exception e) {
                    U.msg((CommandSender)player, "&c\u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0431\u044b\u0442\u044c \u0446\u0435\u043b\u044b\u043c\u0438 \u0447\u0438\u0441\u043b\u0430\u043c\u0438");
                    return true;
                }
            } else {
                U.msg((CommandSender)player, "&c/" + label + " set [x y z]");
                return true;
            }
            if (player.getWorld().setSpawnLocation(x, y, z)) {
                U.msg((CommandSender)player, "&a\u0422\u043e\u0447\u043a\u0430 \u0441\u043f\u0430\u0432\u043d\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430: " + x + " " + y + " " + z);
            } else {
                U.msg((CommandSender)player, "&c\u041e\u0448\u0438\u0431\u043a\u0430, \u043c\u0438\u0440 \u043d\u0435 \u0434\u0430\u043b \u043f\u043e\u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0442\u043e\u0447\u043a\u0443 \u0441\u043f\u0430\u0432\u043d\u0430");
            }
        } else if (args[0].equalsIgnoreCase("get")) {
            Location loc = player.getWorld().getSpawnLocation();
            U.msg((CommandSender)player, "&a\u0422\u043e\u0447\u043a\u0430 \u0441\u043f\u0430\u0432\u043d\u0430: " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
        } else {
            this.help((CommandSender)player);
        }
        return true;
    }

    private void help(CommandSender sender) {
        U.msg(sender, "&e/worldspawn tp&f: \u0422\u043f \u043d\u0430 \u0442\u043e\u0447\u043a\u0443 \u0441\u043f\u0430\u0432\u043d\u0430", "&e/worldspawn set [x y z]&f: \u0423\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u0442\u043e\u0447\u043a\u0438 \u0441\u043f\u0430\u0432\u043d\u0430", "&e/worldspawn get&f: \u0422\u0435\u043a\u0443\u0449\u0430\u044f \u0442\u043e\u0447\u043a\u0430 \u0441\u043f\u0430\u0432\u043d\u0430");
    }
}

