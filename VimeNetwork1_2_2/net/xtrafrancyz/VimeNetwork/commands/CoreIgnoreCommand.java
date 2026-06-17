/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import java.util.regex.Pattern;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet55PrivateIgnore;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreIgnoreCommand
implements CommandExecutor {
    private static final Pattern NICK_PATTERN = Pattern.compile("^[a-z0-9_]{3,16}$", 2);

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "ignore": {
                if (args.length != 1) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u0438\u0433\u0440\u043e\u043a>");
                    break;
                }
                if (args[0].equals("@all")) {
                    U.msg(sender, T.system("VimeWorld", "\u0412\u044b &c\u0432\u044b\u043a\u043b\u044e\u0447\u0438\u043b\u0438 &f\u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"));
                    VPlayer.get((String)sender.getName()).settings.set(1, false);
                    break;
                }
                if (!NICK_PATTERN.matcher(args[0]).find()) {
                    U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u0432\u0432\u0435\u043b\u0438 \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u043d\u0438\u043a"));
                    break;
                }
                if (args[0].equalsIgnoreCase(sender.getName())) {
                    U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0438\u0433\u043d\u043e\u0440\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u0441\u0435\u0431\u044f"));
                    break;
                }
                U.msg(sender, T.success("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + args[0] + " &a\u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d \u0432 \u0447\u0435\u0440\u043d\u044b\u0439 \u0441\u043f\u0438\u0441\u043e\u043a"));
                Packet55PrivateIgnore packet = new Packet55PrivateIgnore(sender.getName(), null);
                packet.action = Packet55PrivateIgnore.Action.IGNORE;
                packet.target = args[0];
                VimeNetwork.core().sendPacket(packet);
                break;
            }
            case "unignore": {
                if (args.length != 1) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u0438\u0433\u0440\u043e\u043a>");
                    return true;
                }
                if (args[0].equals("@all")) {
                    U.msg(sender, T.system("VimeWorld", "\u0412\u044b &a\u0432\u043a\u043b\u044e\u0447\u0438\u043b\u0438 &f\u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f"));
                    VPlayer.get((String)sender.getName()).settings.set(1, true);
                    break;
                }
                if (!NICK_PATTERN.matcher(args[0]).find()) {
                    U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u0432\u0432\u0435\u043b\u0438 \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u043d\u0438\u043a"));
                    break;
                }
                U.msg(sender, T.success("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &e" + args[0] + " &a\u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0447\u0435\u0440\u043d\u043e\u0433\u043e \u0441\u043f\u0438\u0441\u043a\u0430"));
                Packet55PrivateIgnore packet = new Packet55PrivateIgnore(sender.getName(), null);
                packet.action = Packet55PrivateIgnore.Action.UNIGNORE;
                packet.target = args[0];
                VimeNetwork.core().sendPacket(packet);
            }
        }
        return true;
    }
}

