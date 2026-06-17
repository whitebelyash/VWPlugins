/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.Packet54PrivateMessage;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreMessageCommand
implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "msg": {
                if (args.length < 2) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u043a\u043e\u043c\u0443> <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>");
                    return true;
                }
                if (args[0].equalsIgnoreCase(sender.getName())) {
                    U.msg(sender, "&6\u041e\u0434\u0438\u043d\u043e\u0447\u0435\u0441\u0442\u0432\u043e...");
                    return true;
                }
                String message = args[1];
                for (int i = 2; i < args.length; ++i) {
                    message = message + " " + args[i];
                }
                this.trySendPrivateMessage(sender, args[0], message);
                break;
            }
            case "reply": {
                if (args.length < 1) {
                    U.msg(sender, "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: &f/" + label + " <\u043e\u0442\u0432\u0435\u0442>");
                    return true;
                }
                String message = args[0];
                for (int i = 1; i < args.length; ++i) {
                    message = message + " " + args[i];
                }
                this.trySendPrivateMessage(sender, "@last", message);
            }
        }
        return true;
    }

    private void trySendPrivateMessage(CommandSender sender, String receiver, String message) {
        VimeNetwork.core().sendPacket(new Packet54PrivateMessage(receiver, sender.getName(), message), packet0 -> {
            Packet53Answer packet = (Packet53Answer)packet0;
            switch (packet.status) {
                case "NoLastWriter": {
                    U.msg(sender, T.error("VimeWorld", "\u0423 \u0432\u0430\u0441 \u043d\u0435\u0442 \u043d\u0438\u043a\u043e\u0433\u043e, \u043a\u043e\u043c\u0443 \u0431\u044b \u0432\u044b \u043c\u043e\u0433\u043b\u0438 \u043e\u0442\u0432\u0435\u0442\u0438\u0442\u044c"));
                    break;
                }
                case "YouIgnoreAll": {
                    U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b\u0438 \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f. \u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0445 \u043c\u043e\u0436\u043d\u043e \u0432 \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0430\u0445 &f/me"));
                    break;
                }
                case "YouIgnorePlayer": {
                    U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &f" + receiver + "&c \u0443 \u0432\u0430\u0441 \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435. \u0414\u043b\u044f \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u043a\u0438 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u0443\u0439\u0442\u0435 &f/unignore " + receiver));
                    break;
                }
                case "RecIgnoreAll": {
                    U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a \u043e\u0442\u043a\u043b\u044e\u0447\u0438\u043b \u043f\u0440\u0438\u0432\u0430\u0442\u043d\u044b\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f."));
                    break;
                }
                case "RecIgnoreYou": {
                    U.msg(sender, T.error("VimeWorld", "\u0412\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435 \u0443 \u044d\u0442\u043e\u0433\u043e \u0438\u0433\u0440\u043e\u043a\u0430."));
                    break;
                }
                default: {
                    if (packet.status.startsWith("-")) {
                        U.msg(sender, T.error("VimeWorld", "\u0418\u0433\u0440\u043e\u043a &f" + packet.status.substring(1) + "&c \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d"));
                        break;
                    }
                    if (packet.status.startsWith("+")) {
                        sender.sendMessage(U.colored("&e[&f\u0412\u044b&e -> &f" + packet.status.substring(1) + "&e] ") + message);
                        break;
                    }
                    U.msg(sender, "&c\u041f\u0440\u043e\u0438\u0437\u043e\u0448\u043b\u0430 \u043e\u0448\u0438\u0431\u043a\u0430. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0437\u0436\u0435.");
                }
            }
        }, 200L);
    }
}

