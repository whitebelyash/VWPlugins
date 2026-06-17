/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player;
        int speed;
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer(sender.getName());
        if (VimeNetwork.lobby().getServerType() == ServerType.BUILD) {
            if (!networkPlayer.hasAndNotify(Permission.BUILDER)) {
                return true;
            }
        } else {
            if (!networkPlayer.hasAndNotify(Permission.VANISH)) {
                return true;
            }
            if (!networkPlayer.getRank().has(Rank.CHIEF) && !Spectators.instance().contains((Player)sender)) {
                U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u043c\u043e\u0436\u0435\u0442\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u0432 \u0440\u0435\u0436\u0438\u043c\u0435 \u043d\u0430\u0431\u043b\u044e\u0434\u0430\u0442\u0435\u043b\u044f (/vanish)"));
                return true;
            }
        }
        if (args.length == 0) {
            this.help(sender);
            return true;
        }
        try {
            speed = Integer.parseInt(args[0]);
        }
        catch (Exception ex) {
            this.help(sender);
            return true;
        }
        if (speed < 1) {
            speed = 1;
        }
        if (speed > 10) {
            speed = 10;
        }
        if ((player = networkPlayer.getBukkitPlayer()).isFlying()) {
            player.setFlySpeed(0.1f + 0.05f * (float)(speed - 1));
            U.msg(sender, T.system("VimeWorld", "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c &a\u043f\u043e\u043b\u0451\u0442\u0430&f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u043d\u0430 &a" + speed));
        } else {
            player.setWalkSpeed(0.2f + 0.08f * (float)(speed - 1));
            U.msg(sender, T.system("VimeWorld", "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c &a\u0445\u043e\u0434\u044c\u0431\u044b&f \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430 \u043d\u0430 &a" + speed));
        }
        return true;
    }

    private void help(CommandSender sender) {
        U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /speed <\u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0445\u043e\u0434\u044c\u0431\u044b/\u043f\u043e\u043b\u0451\u0442\u0430>");
    }
}

