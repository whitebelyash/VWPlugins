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
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "msg": {
                if (args.length < 2) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u043a\u043e\u043c\u0443> <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
                    return true;
                }
                Player player = Bukkit.getPlayerExact((String)args[0]);
                if (player == null) {
                    U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + args[0] + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d"));
                    break;
                }
                MysqlPlayer senderInfo = (MysqlPlayer)VPlayer.get(sender.getName());
                MysqlPlayer recieverInfo = (MysqlPlayer)VPlayer.get(player);
                String message = args[1];
                for (int i = 2; i < args.length; ++i) {
                    message = message + " " + args[i];
                }
                this.trySendPrivateMessage(senderInfo, recieverInfo, message);
                break;
            }
            case "reply": {
                MysqlPlayer lastWriter;
                if (args.length < 1) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u043e\u0442\u0432\u0435\u0442>");
                    return true;
                }
                MysqlPlayer senderInfo = (MysqlPlayer)VPlayer.get(sender.getName());
                if (senderInfo.lastWriter == null || (lastWriter = (MysqlPlayer)VPlayer.PLAYERS.get(senderInfo.lastWriter)) == null) {
                    U.msg(sender, T.error("VimeWorld", "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043d\u0438\u043a\u043e\u0433\u043e, \u043a\u043e\u043c\u0443 \u0431\u044b \u0432\u044b \u043c\u043e\u0433\u043b\u0438 \u043e\u0442\u0432\u0435\u0442\u0438\u0442\u044c"));
                    return true;
                }
                String message = args[0];
                for (int i = 1; i < args.length; ++i) {
                    message = message + " " + args[i];
                }
                this.trySendPrivateMessage(senderInfo, lastWriter, message);
            }
        }
        return true;
    }

    private void trySendPrivateMessage(MysqlPlayer sender, MysqlPlayer receiver, String message) {
        if (sender.ignoreAll) {
            U.msg((CommandSender)sender.player, T.error("VimeWorld", "\u0412\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b\u0438 \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f. \u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c: &e/unignore @all"));
            return;
        }
        if (!sender.rank.has(Rank.CHIEF) && receiver.ignoreAll) {
            U.msg((CommandSender)sender.player, T.error(receiver.getName(), "\u041e\u0442\u043a\u043b\u044e\u0447\u0438\u043b \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"));
            return;
        }
        if (sender.ignored.contains(receiver.getName())) {
            U.msg((CommandSender)sender.player, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + receiver.getName() + "&c \u0443 \u0432\u0430\u0441 \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435. \u0414\u043b\u044f \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 &e/unignore " + receiver.getName()));
            return;
        }
        if (!sender.rank.has(Rank.CHIEF) && receiver.ignored.contains(sender.getName())) {
            U.msg((CommandSender)sender.player, T.error(receiver.getName(), "\u0412\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435 \u0443 \u044d\u0442\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430"));
            return;
        }
        sender.player.sendMessage(U.colored("&e[&f\u0412\u044b&e -> &f" + receiver.player.getDisplayName() + "&e] ") + message);
        receiver.player.sendMessage(U.colored("&e[&f" + sender.player.getDisplayName() + "&e -> &f\u0412\u044b&e] ") + message);
        receiver.lastWriter = sender.getName();
        sender.lastWriter = sender.getName();
    }
}

