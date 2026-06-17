/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Core.network.packet.Packet62FriendAction;
import net.xtrafrancyz.VimeNetwork.api.CoreBukkit;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendCommand
extends CommandRoot {
    private CoreBukkit core = VimeNetwork.core();

    @Override
    protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
        if (!this.core.isConnected()) {
            U.msg(sender, "&c\u0412 \u0434\u0430\u043d\u043d\u044b\u0439 \u043c\u043e\u043c\u0435\u043d\u0442 \u043a\u043e\u043c\u0430\u043d\u0434\u0430 \u043d\u0435 \u0440\u0430\u0431\u043e\u0442\u0430\u0435\u0442. \u041f\u043e\u043f\u0440\u043e\u0431\u0443\u0439\u0442\u0435 \u043f\u043e\u0437\u0436\u0435");
            return;
        }
        if (VimeNetwork.getPlayer(sender.getName()).getId() == -1) {
            U.msg(sender, "&c\u0414\u0430\u043d\u043d\u044b\u0435 \u0437\u0430\u0433\u0440\u0443\u0436\u0430\u044e\u0442\u0441\u044f, \u043f\u043e\u0434\u043e\u0436\u0434\u0438\u0442\u0435 \u043d\u0435\u043c\u043d\u043e\u0433\u043e...");
            return;
        }
        super.runCommand(action, sender, cmd, label, args);
    }

    @Override
    protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
        this.help(new SubCommandData(sender, label, "help", new String[0]));
        return false;
    }

    @CmdSub(value={"add"}, aliases={"a"})
    protected void add(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " add <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0441\u0435\u0431\u044f \u0432 \u0434\u0440\u0443\u0437\u044c\u044f");
            return;
        }
        this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.ADD, data.getArgs()[0]));
    }

    @CmdSub(value={"accept"}, aliases={"acpt"})
    protected void accept(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " accept <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0440\u0438\u043d\u044f\u0442\u044c \u0437\u0430\u044f\u0432\u043a\u0443 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f \u043e\u0442 \u0441\u0435\u0431\u044f");
            return;
        }
        if (data.getSub().equals("acpt")) {
            Texteria2D.removeGroup((String)"vn.n.", (Player[])new Player[]{data.getPlayer()});
        }
        this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.ACCEPT, data.getArgs()[0]));
    }

    @CmdSub(value={"deny"}, aliases={"d", "dny"})
    protected void deny(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " deny <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u0442\u044c \u0437\u0430\u044f\u0432\u043a\u0443 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f \u043e\u0442 \u0441\u0435\u0431\u044f");
            return;
        }
        if (data.getSub().equals("dny")) {
            Texteria2D.removeGroup((String)"vn.n.", (Player[])new Player[]{data.getPlayer()});
        }
        this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.DENY, data.getArgs()[0]));
    }

    @CmdSub(value={"remove"}, aliases={"r"})
    protected void remove(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " remove <\u043d\u0438\u043a \u0434\u0440\u0443\u0433\u0430>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u0435\u0431\u044f \u0438\u0437 \u0434\u0440\u0443\u0437\u0435\u0439");
            return;
        }
        this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.REMOVE, data.getArgs()[0]));
    }

    @CmdSub(value={"list"}, aliases={"l"})
    protected void list(SubCommandData data) {
        if (data.getArgs().length > 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " list [\u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430]");
            return;
        }
        String page = "1";
        if (data.getArgs().length == 1) {
            page = data.getArgs()[0];
            try {
                Integer.parseInt(page);
            }
            catch (NumberFormatException ex) {
                U.msg(data.getSender(), "&c\u041d\u043e\u043c\u0435\u0440 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u0447\u0438\u0441\u043b\u043e\u043c, \u044d\u0442\u043e \u0436\u0435 \u043b\u043e\u0433\u0438\u0447\u043d\u043e");
                return;
            }
        }
        this.core.sendPacket(new Packet62FriendAction(this.getPlayerId(data), Packet62FriendAction.Action.LIST, page));
    }

    @CmdSub(value={"help"}, hidden=true)
    protected void help(SubCommandData data) {
        U.msg(data.getSender(), "&e---------- &2\u0414\u0440\u0443\u0437\u044c\u044f &f(&e/friend /f&f)&e ---------------", "&e/" + data.getLabel() + "&7 add&8(a)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0432 \u0434\u0440\u0443\u0437\u044c\u044f", "&e/" + data.getLabel() + "&7 accept <\u0438\u0433\u0440\u043e\u043a>&f: \u043f\u0440\u0438\u043d\u044f\u0442\u044c \u0437\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0434\u0440\u0443\u0436\u0431\u0443", "&e/" + data.getLabel() + "&7 deny&8(d)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u043e\u0442\u043a\u043b\u043e\u043d\u0438\u0442\u044c \u0437\u0430\u043f\u0440\u043e\u0441 \u043d\u0430 \u0434\u0440\u0443\u0436\u0431\u0443", "&e/" + data.getLabel() + "&7 remove&8(r)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0434\u0440\u0443\u0433\u0430", "&e/" + data.getLabel() + "&7 list&8(l)&7 [\u0441\u0442\u0440\u0430\u043d\u0438\u0446\u0430]&f: \u0441\u043f\u0438\u0441\u043e\u043a \u0432\u0430\u0448\u0438\u0445 \u0434\u0440\u0443\u0437\u0435\u0439");
    }

    private int getPlayerId(SubCommandData data) {
        return VimeNetwork.getPlayer(data.getSender().getName()).getId();
    }
}

