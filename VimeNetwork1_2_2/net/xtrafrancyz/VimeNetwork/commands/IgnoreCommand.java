/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player;
        MysqlPlayer vplayer;
        if (command.getName().equals("ignore")) {
            if (args.length != 1) {
                return false;
            }
            vplayer = (MysqlPlayer)VPlayer.get(sender.getName());
            if (args[0].equals("@all")) {
                U.msg(sender, T.success("VimeWorld", "\u0412\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b\u0438 \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"));
                vplayer.ignoreAll = true;
                return true;
            }
            player = Bukkit.getPlayerExact((String)args[0]);
            if (player == null) {
                U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d"));
            } else if (vplayer != null) {
                if (vplayer.ignored.contains(player.getName())) {
                    U.msg(sender, T.warning("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + player.getName() + " &6\u0443\u0436\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435"));
                } else {
                    U.msg(sender, T.success("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + player.getName() + " &a\u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0447\u0435\u0440\u043d\u044b\u0439 \u0441\u043f\u0438\u0441\u043e\u043a"));
                    vplayer.ignored.add(player.getName());
                }
            }
        }
        if (command.getName().equals("unignore")) {
            if (args.length != 1) {
                return false;
            }
            vplayer = (MysqlPlayer)VPlayer.get(sender.getName());
            if (args[0].equals("@all")) {
                U.msg(sender, T.success("VimeWorld", "\u0412\u044b \u0441\u043d\u043e\u0432\u0430 \u0432\u043a\u043b\u044e\u0447\u0438\u043b\u0438 \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"));
                vplayer.ignoreAll = false;
                return true;
            }
            player = Bukkit.getPlayerExact((String)args[0]);
            if (player == null) {
                U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d"));
            } else if (vplayer != null) {
                if (!vplayer.ignored.contains(player.getName())) {
                    U.msg(sender, T.warning("VimeWorld", "\u0418\u0433\u0440\u043e\u043a\u0430 &e" + player.getName() + " &6\u043d\u0435\u0442 \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435"));
                } else {
                    U.msg(sender, T.success("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + player.getName() + " &a\u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0447\u0435\u0440\u043d\u043e\u0433\u043e \u0441\u043f\u0438\u0441\u043a\u0430"));
                    vplayer.ignored.remove(player.getName());
                }
            }
        }
        return true;
    }
}

