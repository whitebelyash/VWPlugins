/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet56StreamAction;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class StreamCommand
extends CommandRoot {
    @Override
    protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.core().isConnected()) {
            U.msg(sender, "&c\u0412 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0437\u0436\u0435");
            return;
        }
        super.runCommand(action, sender, cmd, label, args);
    }

    @Override
    protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
        this.help(new SubCommandData(sender, label, "help", new String[0]));
        return false;
    }

    @CmdSub(value={"add"}, ranks={Rank.YOUTUBE, Rank.ADMIN}, hidden=true)
    protected void add(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u0441\u0441\u044b\u043b\u043a\u0430 \u043d\u0430 \u0441\u0442\u0440\u0438\u043c>");
        } else {
            VimeNetwork.core().sendPacket(new Packet56StreamAction(data.getSender().getName(), data.getArgs()[0], Packet56StreamAction.Action.ADD));
        }
    }

    @CmdSub(value={"remove"}, ranks={Rank.YOUTUBE, Rank.ADMIN}, hidden=true)
    protected void remove(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u0441\u0441\u044b\u043b\u043a\u0430 \u043d\u0430 \u0441\u0442\u0440\u0438\u043c>");
        } else {
            VimeNetwork.core().sendPacket(new Packet56StreamAction(data.getSender().getName(), data.getArgs()[0], Packet56StreamAction.Action.REMOVE));
        }
    }

    @CmdSub(value={"list"})
    protected void list(SubCommandData data) {
        VNPlugin.instance().streamMenu.show(data.getPlayer());
    }

    @CmdSub(value={"help"})
    protected void help(SubCommandData data) {
        U.msg(data.getSender(), "&e---------- &f\u0421\u0442\u0440\u0438\u043c\u044b&e ---------------");
        if (this.hasPerm(data.getSender())) {
            U.msg(data.getSender(), "\u041f\u043e\u0434\u0434\u0435\u0440\u0436\u0438\u0432\u0430\u044e\u0442\u0441\u044f \u0441\u0435\u0440\u0432\u0438\u0441\u044b: YouTube, Twitch, vk.com, GoodGame.ru");
            U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 add <\u0441\u0441\u044b\u043b\u043a\u0430 \u043d\u0430 \u0441\u0442\u0440\u0438\u043c>&f: \u0414\u043e\u0431\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0440\u0438\u043c\u0430");
            U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 remove <\u0441\u0441\u044b\u043b\u043a\u0430 \u043d\u0430 \u0441\u0442\u0440\u0438\u043c>&f: \u0423\u0434\u0430\u043b\u0435\u043d\u0438\u0435 \u0441\u0442\u0440\u0438\u043c\u0430");
        }
        U.msg(data.getSender(), "&e/" + data.getLabel() + "&7 list&f: \u0421\u043f\u0438\u0441\u043e\u043a \u0441\u0442\u0440\u0438\u043c\u043e\u0432");
    }

    private boolean hasPerm(CommandSender sender) {
        switch (VimeNetwork.getPlayer(sender.getName()).getRank()) {
            case YOUTUBE: 
            case ADMIN: {
                return true;
            }
        }
        return false;
    }
}

