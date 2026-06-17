/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import net.xtrafrancyz.Core.network.packet.Packet58PartyAction;
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

public class PartyCommand
extends CommandRoot {
    private CoreBukkit core = VimeNetwork.core();

    @Override
    protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
        if (!VimeNetwork.core().isConnected()) {
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
        if (args.length == 0) {
            this.help(new SubCommandData(sender, label, "help", new String[0]));
        } else {
            this.core.sendPacket(new Packet58PartyAction(VimeNetwork.getPlayer(sender.getName()).getId(), Packet58PartyAction.Action.MESSAGE, Joiner.on((String)" ").join((Object[])args)));
        }
        return false;
    }

    @CmdSub(value={"invite"}, aliases={"i"})
    protected void invite(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0440\u0438\u0433\u043b\u0430\u0441\u0438\u0442\u044c \u0441\u0435\u0431\u044f");
            return;
        }
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.INVITE, data.getArgs()[0]));
    }

    @CmdSub(value={"join"}, aliases={"j"})
    protected void join(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u043d\u0438\u043a \u043b\u0438\u0434\u0435\u0440\u0430>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u043f\u0440\u0438\u0441\u043e\u0435\u0434\u0438\u043d\u0438\u0442\u044c\u0441\u044f \u043a \u0441\u0432\u043e\u0435\u0439 \u0436\u0435 \u0433\u0440\u0443\u043f\u043f\u0435");
            return;
        }
        if (data.getSub().equals("j")) {
            Texteria2D.removeGroup((String)"vn.n.", (Player[])new Player[]{data.getPlayer()});
        }
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.JOIN, data.getArgs()[0]));
    }

    @CmdSub(value={"kick"}, aliases={"k"})
    protected void kick(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0438\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0441\u0435\u0431\u044f \u0438\u0437 \u0433\u0440\u0443\u043f\u043f\u044b");
            return;
        }
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.KICK, data.getArgs()[0]));
    }

    @CmdSub(value={"leave"})
    protected void leave(SubCommandData data) {
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.LEAVE));
    }

    @CmdSub(value={"disband"}, aliases={"d"})
    protected void disband(SubCommandData data) {
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.DISBAND));
    }

    @CmdSub(value={"warp"}, aliases={"w"})
    protected void warp(SubCommandData data) {
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.WARP));
    }

    @CmdSub(value={"list"}, aliases={"l"})
    protected void list(SubCommandData data) {
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.LIST));
    }

    @CmdSub(value={"promote"}, aliases={"p"})
    protected void promote(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        if (data.getArgs()[0].equalsIgnoreCase(data.getSender().getName())) {
            U.msg(data.getSender(), "&c\u0412\u044b \u043d\u0435 \u043c\u043e\u0436\u0435\u0442\u0435 \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0441\u0435\u0431\u044f \u043b\u0438\u0434\u0435\u0440\u043e\u043c");
            return;
        }
        this.core.sendPacket(new Packet58PartyAction(this.getPlayerId(data), Packet58PartyAction.Action.PROMOTE, data.getArgs()[0]));
    }

    @CmdSub(value={"help"})
    protected void help(SubCommandData data) {
        U.msg(data.getSender(), "&e---------- &f\u0413\u0440\u0443\u043f\u043f\u044b (/party /p)&e ---------------", "&e/" + data.getLabel() + "&7 <\u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435>&f: \u043e\u0442\u043f\u0440\u0430\u0432\u0438\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0432\u0441\u0435\u0439 \u0433\u0440\u0443\u043f\u043f\u0435", "&e/" + data.getLabel() + "&7 invite&8(i)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u043f\u0440\u0438\u0433\u043b\u0430\u0441\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0432 \u0433\u0440\u0443\u043f\u043f\u0443", "&e/" + data.getLabel() + "&7 kick&8(k)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u0438\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0438\u0437 \u0433\u0440\u0443\u043f\u043f\u044b", "&e/" + data.getLabel() + "&7 join&8(j)&7 <\u043d\u0438\u043a \u043b\u0438\u0434\u0435\u0440\u0430>&f: \u0432\u0441\u0442\u0443\u043f\u0438\u0442\u044c \u0432 \u0433\u0440\u0443\u043f\u043f\u0443 \u0438\u0433\u0440\u043e\u043a\u0430", "&e/" + data.getLabel() + "&7 promote&8(p)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u043d\u043e\u0432\u044b\u043c \u043b\u0438\u0434\u0435\u0440\u043e\u043c \u0433\u0440\u0443\u043f\u043f\u044b", "&e/" + data.getLabel() + "&7 list&8(l)&f: \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432 \u0433\u0440\u0443\u043f\u043f\u0435", "&e/" + data.getLabel() + "&7 warp&8(w)&f: \u043f\u0435\u0440\u0435\u043d\u043e\u0441\u0438\u0442 \u0432\u0441\u0435\u0445 \u0447\u043b\u0435\u043d\u043e\u0432 \u0433\u0440\u0443\u043f\u043f\u044b \u043d\u0430 \u0432\u0430\u0448 \u0441\u0435\u0440\u0432\u0435\u0440", "&e/" + data.getLabel() + "&7 leave&f: \u0432\u044b\u0439\u0442\u0438 \u0438\u0437 \u0442\u0435\u043a\u0443\u0449\u0435\u0439 \u0433\u0440\u0443\u043f\u043f\u044b", "&e/" + data.getLabel() + "&7 disband&8(d)&f: \u0440\u0430\u0441\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u0432\u0430\u0448\u0443 \u0433\u0440\u0443\u043f\u043f\u0443");
    }

    private int getPlayerId(SubCommandData data) {
        return VimeNetwork.getPlayer(data.getSender().getName()).getId();
    }
}

