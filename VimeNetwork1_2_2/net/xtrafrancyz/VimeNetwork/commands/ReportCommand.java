/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.util.Arrays;
import net.xtrafrancyz.Core.network.packet.Packet68Report;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ReportCommand
implements CommandExecutor,
Listener {
    public ReportCommand() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)VNPlugin.instance());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.core().isConnected()) {
            U.msg(sender, "&c\u0412 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0437\u0436\u0435");
            return true;
        }
        if (VimeNetwork.getPlayer(sender.getName()).getId() == -1) {
            U.msg(sender, "&c\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u0435\u0449\u0435 \u043d\u0435 \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u043b\u0430\u0441\u044c. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0437\u0436\u0435");
            return true;
        }
        if (args.length == 0) {
            this.printHelp(sender);
            return true;
        }
        if (args.length >= 2 && args[1].equalsIgnoreCase(sender.getName())) {
            U.msg(sender, "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u043e\u0436\u0430\u043b\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043d\u0430 \u0441\u0435\u0431\u044f");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "chat": {
                if (args.length != 2) {
                    U.msg(sender, "&e/report chat&7 <\u043d\u0438\u043a>&f: \u0441\u043e\u043e\u0431\u0449\u0438\u0442\u044c \u043e \u043d\u0430\u0440\u0443\u0448\u0435\u043d\u0438\u0438 \u0432 \u0447\u0430\u0442\u0435");
                    break;
                }
                this.sendReport(sender, args[1], Packet68Report.Type.CHAT, null);
                break;
            }
            case "cheat": {
                if (args.length < 2) {
                    U.msg(sender, "&e/report cheat&7 <\u043d\u0438\u043a> [\u043a\u043e\u043c\u043c\u0435\u043d\u0442\u0430\u0440\u0438\u0439]&f: \u0441\u043e\u043e\u0431\u0449\u0438\u0442\u044c \u043e \u0447\u0438\u0442\u0435\u0440\u0435");
                    break;
                }
                String message = args.length > 2 ? Joiner.on((char)' ').join(Arrays.asList(Arrays.copyOfRange(args, 2, args.length))) : "-";
                this.sendReport(sender, args[1], Packet68Report.Type.CHEAT, message);
                break;
            }
            case "retard": {
                if (args.length < 2) {
                    U.msg(sender, "&e/report retard&7 <\u043d\u0438\u043a> <\u043a\u043e\u043c\u043c\u0435\u043d\u0442\u0430\u0440\u0438\u0439>&f: \u0435\u0441\u043b\u0438 \u0438\u0433\u0440\u043e\u043a \u043d\u0435\u0432\u044b\u043d\u043e\u0441\u0438\u043c (\u043c\u0435\u0448\u0430\u0435\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u0435, \u0430\u0444\u043a, \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435 \u0434\u0435\u043b\u0430\u0435\u0442 \u0438 \u0442.\u0434.)");
                    break;
                }
                String message = Joiner.on((char)' ').join(Arrays.asList(Arrays.copyOfRange(args, 2, args.length)));
                if (message.isEmpty()) {
                    U.msg(sender, "&c\u041d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 \u043a\u043e\u043c\u043c\u0435\u043d\u0442\u0430\u0440\u0438\u0439");
                    break;
                }
                this.sendReport(sender, args[1], Packet68Report.Type.RETARD, message);
                break;
            }
            default: {
                this.printHelp(sender);
            }
        }
        return true;
    }

    private void sendReport(CommandSender sender, String target, Packet68Report.Type type, String message) {
        VimeNetwork.core().sendPacket(new Packet68Report(sender.getName(), target, type, message));
    }

    private void printHelp(CommandSender sender) {
        U.msg(sender, "&e---------- &c\u0421\u043e\u043e\u0431\u0449\u0438\u0442\u044c \u043e \u043d\u0430\u0440\u0443\u0448\u0435\u043d\u0438\u0438 &f(&e/report&f)&e ---------------", "&e/report cheat&7 <\u043d\u0438\u043a> [\u043a\u043e\u043c\u043c\u0435\u043d\u0442\u0430\u0440\u0438\u0439]&f: \u0441\u043e\u043e\u0431\u0449\u0438\u0442\u044c \u043e \u0447\u0438\u0442\u0435\u0440\u0435", "&e/report chat&7 <\u043d\u0438\u043a>&f: \u0441\u043e\u043e\u0431\u0449\u0438\u0442\u044c \u043e \u043d\u0430\u0440\u0443\u0448\u0435\u043d\u0438\u0438 \u0432 \u0447\u0430\u0442\u0435", "&e/report retard&7 <\u043d\u0438\u043a> <\u043a\u043e\u043c\u043c\u0435\u043d\u0442\u0430\u0440\u0438\u0439>&f: \u0435\u0441\u043b\u0438 \u0438\u0433\u0440\u043e\u043a \u043d\u0435\u0432\u044b\u043d\u043e\u0441\u0438\u043c");
    }
}

