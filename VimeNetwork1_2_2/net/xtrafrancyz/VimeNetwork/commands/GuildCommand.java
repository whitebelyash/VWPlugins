/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  org.bukkit.Bukkit
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.CoreBukkit;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class GuildCommand
extends CommandRoot
implements Listener {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[ \u0406\u0456\u0407\u0457\u0404\u0454\u0401\u0451\u0410-\u042f\u0430-\u044fA-Za-z0-9._-]+$");
    private static final Pattern TAG_PATTERN = Pattern.compile("^[\u0406\u0456\u0407\u0457\u0404\u0454\u0401\u0451\u0410-\u042f\u0430-\u044fA-Za-z0-9._-]+$");
    private static final int CREATION_PRICE = 150000;
    private static final int RENAMING_PRICE = 100000;
    private static final Set<String> TAG_BLACKLIST = new HashSet<String>(Arrays.asList("ebl0", "hyem", "3blo", "p1zd", "ueba", "pizd", "eblo", "ebat", "blya", "pidr", "xuy", "ebal", "her", "h3r", "3bal", "3ba1", "eba1", "p1dr", "xy1", "b1ya", "blea", "61ya", "xyi", "xyev", "hyev", "huya", "huev", "xyem", "xy_i", "hyi_", "_hyi", "h_yi", "xyyi", "xyii", "xu_i", "xuui", "xyll", "mudk", "xxyi", "twar", "dick", "cock", "cunt", "quim", "t8ap", "1bap", "tbap", "uebk", "yoba", "y0ba", "eb0k", "ebu", "uebu", "yeba", "mraz", "hyil", "g0vn", "govn", "lycu", "lucy", "l1cy", "okss", "oksi", "adm", "modr", "oks1", "admi", "oksl", "xelp", "smai", "smak", "lyc1", "aksi", "admn", "aclm", "adm_", "anus", "anal", "a_dm", "pisy", "ad_m", "sosu", "s0su", "sosi", "sisi", "4len", "s1s1", "suka", "suki", "srat", "sratb", "sru", "ass", "qay", "gay", "g4y", "lsd", "lesb", "sh1t", "shit", "cum", "l3sb", "slut", "elda", "eldk", "e1da", "sprm", "dcp", "daun", "porn", "sex", "cekc", "ebis", "urod", "ueby", "yebu", "xu_y", "x_uy", "xu-y", "cyka", "syka", ".loh", ".lox", "lox.", "loh.", "hui.", "xui.", "xuy.", "huy.", ".xui", ".ass", ".huy", ".xuy", ".adm", "adm.", "-adm", "hui", "huy", "hyi", "04ko", "o4ko", "bdsm", "bdcm", "ceo", "cto", "mod", ".mod", "hlpr", "help", "cuka", "own", "hax", "\u0445\u0443\u0439", "\u043f\u0438\u0437\u0434\u0430", "\u043f\u0435\u043d\u0438\u0441", "boobs", "\u0447\u043b\u0435\u043d", "\u0430\u0434\u043c\u0438\u043d", "\u043c\u043e\u0434\u0435\u0440", "\u0430\u0434\u043c", "\u043c\u043e\u0434", "moder", "admin", "\u043c\u0440\u0430\u0437\u044c", "fuck", "cunt", "pizda", "\u043f\u0438\u0434\u043e\u0440", "pidor", "\u0441\u0443\u043a\u0430", "\u0441\u043e\u0441\u0438", "\u0433\u043e\u0432\u043d\u043e", "\u0443\u0435\u0431\u0443", "\u0443\u0451\u0431\u0430", "\u0443\u0451\u0431\u044b", "\u043f\u0438\u0434\u0440", "\u0445\u0443\u0439\u043d\u044f", "\u0445\u0443\u0451\u0432\u043e", "\u0445\u0435\u0440\u043d\u044f", "\u043c\u0440\u0430\u0437\u044c", "\u0435\u0431\u0430\u043b", "\u0435\u0431\u043b\u044f", "\u043c\u0443\u0434\u0430\u043a", "\u0433\u0435\u0439", "\u0433\u0435\u0438", "\u043b\u043e\u0445", "\u043b\u043e\u0445\u0438", "\u0435\u043b\u0434\u0430", "\u0435\u043b\u0434\u0430\u043a", "\u0434\u0430\u0443\u043d", "\u0430\u043d\u0443\u0441", "\u0441\u0435\u043a\u0441", "\u0443\u0440\u043e\u0434"));
    public static final String TITLE = "&2\u0413\u0438\u043b\u044c\u0434\u0438\u044f";
    private CoreBukkit core = VimeNetwork.core();
    private Map<String, Runnable> awaitConfirmation = new HashMap<String, Runnable>();

    public GuildCommand() {
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)VNPlugin.instance());
    }

    @EventHandler
    private void onPlayerLeave(PlayerLeaveEvent event) {
        this.awaitConfirmation.remove(event.getPlayer().getName());
    }

    @Override
    protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
        boolean hasGuild;
        boolean bl = hasGuild = VimeNetwork.getPlayer(sender.getName()).getGuild() != null;
        if (args.length == 0) {
            if (hasGuild) {
                this.menu(new SubCommandData(sender, label, "menu", new String[0]));
            } else {
                this.help(new SubCommandData(sender, label, "help", new String[0]));
            }
        } else {
            if (!hasGuild) {
                U.msg(sender, "&c\u0427\u0442\u043e\u0431\u044b \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u0439\u043d\u044b\u0439 \u0447\u0430\u0442, \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438");
                return false;
            }
            this.core.sendPacket(new Packet69Guild(VimeNetwork.getPlayer(sender.getName()).getId(), Packet69Guild.Action.MESSAGE).put("message", Joiner.on((String)" ").join((Object[])args)));
        }
        return false;
    }

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

    @CmdSub(value={"create"})
    protected void create(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() != null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0412\u044b \u0443\u0436\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " create <\u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438>");
            return;
        }
        if (!player.hasAndNotify(Rank.PREMIUM)) {
            return;
        }
        if (player.getCoins() < 150000) {
            U.msg(data.getSender(), "&c\u0426\u0435\u043d\u0430 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438: &f150000 \u043a\u043e\u0438\u043d\u043e\u0432");
            return;
        }
        String name = Joiner.on((String)" ").join((Object[])data.getArgs());
        if (!this.validateGuildName(data.getSender(), name)) {
            return;
        }
        U.msg(data.getSender(), "\u0426\u0435\u043d\u0430 \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438: &e150000 \u043a\u043e\u0438\u043d\u043e\u0432");
        U.msg(data.getSender(), "\u0414\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f \u0441\u043e\u0437\u0434\u0430\u043d\u0438\u044f \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &a/guild confirm");
        this.awaitConfirmation.put(data.getSender().getName(), () -> this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.CREATE).put("name", name)));
    }

    @CmdSub(value={"rename"})
    protected void rename(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " rename <\u043d\u043e\u0432\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435>");
            return;
        }
        if (player.getCoins() < 100000) {
            U.msg(data.getSender(), "&c\u0426\u0435\u043d\u0430 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438: &f100000 \u043a\u043e\u0438\u043d\u043e\u0432");
            return;
        }
        String name = Joiner.on((String)" ").join((Object[])data.getArgs());
        if (!this.validateGuildName(data.getSender(), name)) {
            return;
        }
        U.msg(data.getSender(), "\u0426\u0435\u043d\u0430 \u0438\u0437\u043c\u0435\u043d\u0435\u043d\u0438\u044f \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438: &e100000 \u043a\u043e\u0438\u043d\u043e\u0432");
        U.msg(data.getSender(), "\u0414\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &a/guild confirm");
        this.awaitConfirmation.put(data.getSender().getName(), () -> this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.RENAME).put("name", name)));
    }

    @CmdSub(value={"disband"})
    protected void disband(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        U.msg(data.getSender(), "\u0414\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f \u0443\u0434\u0430\u043b\u0435\u043d\u0438\u044f \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &6/" + data.getLabel() + " confirm");
        U.msg(data.getSender(), "&c\u042d\u0442\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435 \u043d\u0435\u043b\u044c\u0437\u044f \u0431\u0443\u0434\u0435\u0442 \u043e\u0442\u043c\u0435\u043d\u0438\u0442\u044c! \u0412\u0435\u0441\u044c \u043f\u0440\u043e\u0433\u0440\u0435\u0441\u0441 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u0431\u0443\u0434\u0435\u0442 \u0443\u0434\u0430\u043b\u0435\u043d!");
        this.awaitConfirmation.put(data.getSender().getName(), () -> this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.DISBAND)));
    }

    @CmdSub(value={"transfer"})
    protected void transfer(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " transfer <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        U.msg(data.getSender(), "\u0414\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f \u043f\u0435\u0440\u0435\u0434\u0430\u0447\u0438 \u043f\u0440\u0430\u0432 \u043b\u0438\u0434\u0435\u0440\u0430 \u043d\u0430\u043f\u0438\u0448\u0438\u0442\u0435 &6/" + data.getLabel() + " confirm");
        U.msg(data.getSender(), "&c\u042d\u0442\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0435 \u043d\u0435\u043b\u044c\u0437\u044f \u0431\u0443\u0434\u0435\u0442 \u043e\u0442\u043c\u0435\u043d\u0438\u0442\u044c! \u041f\u043e\u0441\u043b\u0435 \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0438\u044f \u044d\u0442\u043e\u0439 \u043e\u043f\u0435\u0440\u0430\u0446\u0438\u0438 \u0432\u044b \u0441\u0442\u0430\u043d\u0435\u0442\u0435 \u041e\u0444\u0438\u0446\u0435\u0440\u043e\u043c!");
        this.awaitConfirmation.put(data.getSender().getName(), () -> this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.TRANSFER).put("target", data.getArgs()[0])));
    }

    @CmdSub(value={"confirm"})
    protected void confirm(SubCommandData data) {
        Runnable runnable = this.awaitConfirmation.remove(data.getSender().getName());
        if (runnable == null) {
            U.msg(data.getSender(), "&c\u041d\u0435\u0442 \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0434\u043b\u044f \u043f\u043e\u0434\u0442\u0432\u0435\u0440\u0436\u0434\u0435\u043d\u0438\u044f");
            return;
        }
        runnable.run();
    }

    @CmdSub(value={"invite"}, aliases={"i"})
    protected void invite(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " invite <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.INVITE).put("target", data.getArgs()[0]));
    }

    @CmdSub(value={"accept"}, aliases={"acpt"})
    protected void accept(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() != null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0412\u044b \u0443\u0436\u0435 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0435\u0441\u044c \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " accept <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        if (data.getSub().equals("acpt")) {
            Texteria2D.removeGroup((String)"vn.n.", (Player[])new Player[]{data.getPlayer()});
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.ACCEPT).put("inviter", data.getArgs()[0]));
    }

    @CmdSub(value={"kick"})
    protected void kick(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " kick <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.KICK).put("target", data.getArgs()[0]));
    }

    @CmdSub(value={"promote"})
    protected void promote(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " promote <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.PROMOTE).put("target", data.getArgs()[0]));
    }

    @CmdSub(value={"demote"})
    protected void demote(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " demote <\u043d\u0438\u043a \u0438\u0433\u0440\u043e\u043a\u0430>");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.DEMOTE).put("target", data.getArgs()[0]));
    }

    @CmdSub(value={"deposit"}, aliases={"d"})
    protected void deposit(SubCommandData data) {
        int amount;
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " deposit <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043a\u043e\u0438\u043d\u043e\u0432>");
            return;
        }
        try {
            amount = Integer.parseInt(data.getArgs()[0]);
        }
        catch (Exception ex) {
            U.msg(data.getSender(), "&c\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0447\u0438\u0441\u043b\u043e");
            return;
        }
        if (player.getCoins() < amount) {
            U.msg(data.getSender(), "&c\u0423 \u0432\u0430\u0441 \u043d\u0435\u0434\u043e\u0441\u0442\u0430\u0442\u043e\u0447\u043d\u043e \u043a\u043e\u0438\u043d\u043e\u0432");
            return;
        }
        if (amount <= 0) {
            U.msg(data.getSender(), "&c\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0447\u0438\u0441\u043b\u043e \u0431\u043e\u043b\u044c\u0448\u0435 \u043d\u0443\u043b\u044f");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.DEPOSIT).put("amount", amount));
    }

    @CmdSub(value={"leave"})
    protected void leave(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.LEAVE));
    }

    @CmdSub(value={"tag"})
    protected void tag(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " tag <\u0442\u0435\u0433>", "&c\u0417\u0430\u043f\u0440\u0435\u0449\u0435\u043d\u043e \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u0442\u044c \u043e\u0441\u043a\u043e\u0440\u0431\u0438\u0442\u0435\u043b\u044c\u043d\u044b\u0435 \u0438 \u043c\u0430\u0442\u0435\u0440\u043d\u044b\u0435 \u0442\u0435\u0433\u0438 \u0438\u043b\u0438 \u0442\u0435\u0433\u0438, \u043a\u043e\u0442\u043e\u0440\u044b\u0435 \u0431\u0443\u0434\u0443\u0442 \u0432\u044b\u0434\u0430\u0432\u0430\u0442\u044c \u0432\u0430\u0441 \u0437\u0430 \u0430\u0434\u043c\u0438\u043d\u0438\u0441\u0442\u0440\u0430\u0446\u0438\u044e. \u0417\u0430 \u044d\u0442\u043e \u043b\u0438\u0434\u0435\u0440 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043c\u043e\u0436\u0435\u0442 \u043f\u043e\u043b\u0443\u0447\u0438\u0442\u044c \u0431\u0430\u043d.");
            return;
        }
        String tag = data.getArgs()[0];
        if (!(tag.equals("-") || tag.length() >= 2 && tag.length() <= 5)) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u0438\u043d\u0430 \u0442\u0435\u0433\u0430 \u0434\u043e\u043b\u0436\u043d\u0430 \u0431\u044b\u0442\u044c \u043e\u0442 2 \u0434\u043e 5 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432"));
            return;
        }
        if (!TAG_PATTERN.matcher(tag).matches()) {
            U.msg(data.getSender(), "&c\u0422\u0435\u0433 \u043c\u043e\u0436\u0435\u0442 \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0431\u0443\u043a\u0432\u044b \u043a\u0438\u0440\u0438\u043b\u043b\u0438\u0446\u044b, \u043b\u0430\u0442\u0438\u043d\u0438\u0446\u044b, \u0446\u0438\u0444\u0440\u044b \u0438 ._-");
            return;
        }
        if (TAG_BLACKLIST.contains(tag.toLowerCase())) {
            U.msg(data.getSender(), "&c\u042d\u0442\u043e\u0442 \u0442\u0435\u0433 \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u0441\u044f \u0432 \u0447\u0435\u0440\u043d\u043e\u043c \u0441\u043f\u0438\u0441\u043a\u0435");
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.SET_TAG).put("tag", data.getArgs()[0]));
    }

    @CmdSub(value={"party"})
    protected void party(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.PARTY));
    }

    @CmdSub(value={"motd"})
    protected void motd(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&e/" + data.getLabel() + " &7motd add <\u0442\u0435\u043a\u0441\u0442>&f: \u0434\u043e\u0431\u0430\u0432\u0438\u0442\u044c \u043d\u043e\u0432\u0443\u044e \u0441\u0442\u0440\u043e\u043a\u0443 \u0432 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435", "&e/" + data.getLabel() + " &7motd edit <\u043d\u043e\u043c\u0435\u0440 \u0441\u0442\u0440\u043e\u043a\u0438> <\u0442\u0435\u043a\u0441\u0442>&f: \u0437\u0430\u043c\u0435\u043d\u0438\u0442\u044c \u0441\u0442\u0440\u043e\u043a\u0443 \u0432 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435", "&e/" + data.getLabel() + " &7motd clear&f: \u0443\u0434\u0430\u043b\u0438\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435", "&e/" + data.getLabel() + " &7motd preview&f: \u043f\u0440\u043e\u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435");
            return;
        }
        switch (data.getArgs()[0].toLowerCase()) {
            case "add": {
                if (data.getArgs().length == 1) {
                    U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " motd add <\u0442\u0435\u043a\u0441\u0442>");
                    return;
                }
                this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.MOTD).put("action", "addLine").put("text", GuildCommand.join(1, data.getArgs())));
                break;
            }
            case "edit": {
                int line;
                if (data.getArgs().length < 2) {
                    U.msg(data.getSender(), "&c\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: /" + data.getLabel() + " motd edit <\u043d\u043e\u043c\u0435\u0440 \u0441\u0442\u0440\u043e\u043a\u0438> <\u0442\u0435\u043a\u0441\u0442>");
                    return;
                }
                try {
                    line = Integer.parseInt(data.getArgs()[1]);
                }
                catch (Exception ex) {
                    U.msg(data.getSender(), "&c\u041d\u043e\u043c\u0435\u0440 \u0441\u0442\u0440\u043e\u043a\u0438 \u0434\u043e\u043b\u0436\u0435\u043d \u0431\u044b\u0442\u044c \u0447\u0438\u0441\u043b\u043e\u043c");
                    return;
                }
                this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.MOTD).put("action", "editLine").put("line", line).put("text", GuildCommand.join(2, data.getArgs())));
                break;
            }
            case "clear": {
                this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.MOTD).put("action", "clear"));
                break;
            }
            case "preview": {
                this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.MOTD).put("action", "preview"));
            }
        }
    }

    @CmdSub(value={"menu"})
    protected void menu(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.MENU));
    }

    @CmdSub(value={"list"}, aliases={"l"})
    protected void list(SubCommandData data) {
        NetworkPlayer player = VimeNetwork.getPlayer(data.getPlayer());
        if (player.getGuild() == null) {
            U.msg(data.getSender(), T.error(TITLE, "\u0414\u043b\u044f \u044d\u0442\u043e\u0433\u043e \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u044f \u0432\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u043d\u0430\u0445\u043e\u0434\u0438\u0442\u044c\u0441\u044f \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438"));
            return;
        }
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
        this.core.sendPacket(new Packet69Guild(player.getId(), Packet69Guild.Action.LIST).put("page", page));
    }

    @CmdSub(value={"help"}, hidden=true)
    protected void help(SubCommandData data) {
        U.msg(data.getSender(), "&e---------- &2\u0413\u0438\u043b\u044c\u0434\u0438\u0438 &f(&e/guild /g&f)&e ---------------", "&e/" + data.getLabel() + "&7 create <\u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435>&f: \u0441\u043e\u0437\u0434\u0430\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&e/" + data.getLabel() + "&7 invite&8(i)&7 <\u0438\u0433\u0440\u043e\u043a>&f: \u043f\u0440\u0438\u0433\u043b\u0430\u0441\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&e/" + data.getLabel() + "&7 kick <\u0438\u0433\u0440\u043e\u043a>&f: \u0438\u0441\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u0438\u0437 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 promote <\u0438\u0433\u0440\u043e\u043a>&f: \u0441\u0434\u0435\u043b\u0430\u0442\u044c \u0438\u0433\u0440\u043e\u043a\u0430 \u041e\u0444\u0438\u0446\u0435\u0440\u043e\u043c", "&e/" + data.getLabel() + "&7 demote <\u0438\u0433\u0440\u043e\u043a>&f: \u0441\u043d\u044f\u0442\u044c \u0441\u0442\u0430\u0442\u0443\u0441 \u041e\u0444\u0438\u0446\u0435\u0440\u0430", "&e/" + data.getLabel() + "&7 transfer <\u0438\u0433\u0440\u043e\u043a>&f: \u043f\u0435\u0440\u0435\u0434\u0430\u0442\u044c \u043f\u0440\u0430\u0432\u0430 \u043b\u0438\u0434\u0435\u0440\u0430 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 disband&f: \u0440\u0430\u0441\u043f\u0443\u0441\u0442\u0438\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&e/" + data.getLabel() + "&7 deposit&8(d)&7 <\u043a\u043e\u0438\u043d\u044b>&f: \u043f\u043e\u043b\u043e\u0436\u0438\u0442\u044c \u043a\u043e\u0438\u043d\u044b \u043d\u0430 \u0441\u0447\u0435\u0442 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 menu&f: \u043e\u0442\u043a\u0440\u044b\u0442\u044c \u043c\u0435\u043d\u044e \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u0439 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 motd&f: \u0440\u0435\u0434\u0430\u043a\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c \u043f\u0440\u0438\u0432\u0435\u0442\u0441\u0442\u0432\u0435\u043d\u043d\u043e\u0435 \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 tag <\u0442\u0435\u0433>&f: \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0442\u0435\u0433 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 (\u043f\u0440\u0438\u0441\u0442\u0430\u0432\u043a\u0430 \u0432\u043e\u0437\u043b\u0435 \u043d\u0438\u043a\u0430)", "&e/" + data.getLabel() + "&7 rename <\u043d\u043e\u0432\u043e\u0435 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435>&f: \u043f\u0435\u0440\u0435\u0438\u043c\u0435\u043d\u043e\u0432\u0430\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&e/" + data.getLabel() + "&7 party&f: \u0441\u043e\u0431\u0440\u0430\u0442\u044c \u0433\u0440\u0443\u043f\u043f\u0443 \u0438\u0437 \u0447\u043b\u0435\u043d\u043e\u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043e\u043d\u043b\u0430\u0439\u043d", "&e/" + data.getLabel() + "&7 list&8(l)&f: \u0441\u043f\u0438\u0441\u043e\u043a \u0447\u043b\u0435\u043d\u043e\u0432 \u0433\u0438\u043b\u044c\u0434\u0438\u0438", "&e/" + data.getLabel() + "&7 leave&f: \u043f\u043e\u043a\u0438\u043d\u0443\u0442\u044c \u0433\u0438\u043b\u044c\u0434\u0438\u044e", "&e/" + data.getLabel() + "&7 help&f: \u043f\u043e\u043a\u0430\u0437\u0430\u0442\u044c \u0438\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044e \u043e \u043a\u043e\u043c\u0430\u043d\u0434\u0430\u0445");
    }

    private boolean validateGuildName(CommandSender sender, String name) {
        if (!NAME_PATTERN.matcher(name).matches()) {
            U.msg(sender, "&c\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u043c\u043e\u0436\u0435\u0442 \u0441\u043e\u0434\u0435\u0440\u0436\u0430\u0442\u044c \u0442\u043e\u043b\u044c\u043a\u043e \u0431\u0443\u043a\u0432\u044b \u043a\u0438\u0440\u0438\u043b\u043b\u0438\u0446\u044b, \u043b\u0430\u0442\u0438\u043d\u0438\u0446\u044b, \u043f\u0440\u043e\u0431\u0435\u043b, \u0446\u0438\u0444\u0440\u044b \u0438 ._-");
            return false;
        }
        if (name.length() < 2 || name.length() > 20) {
            U.msg(sender, "&c\u041d\u0430\u0437\u0432\u0430\u043d\u0438\u0435 \u0433\u0438\u043b\u044c\u0434\u0438\u0438 \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u043d\u0435 \u043a\u043e\u0440\u043e\u0447\u0435 2-\u0445 \u0441\u0438\u043c\u0432\u043e\u043b\u043e\u0432 \u0438 \u043d\u0435 \u0434\u043b\u0438\u043d\u043d\u0435\u0435 20");
            return false;
        }
        return true;
    }

    public static String join(int start, String[] arr) {
        return Joiner.on((String)" ").join((Object[])Arrays.copyOfRange(arr, start, arr.length));
    }
}

