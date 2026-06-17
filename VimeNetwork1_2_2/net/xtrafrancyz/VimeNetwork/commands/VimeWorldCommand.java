/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  net.xtrafrancyz.bukkit.texteria.utils.ParsedTime
 *  org.bukkit.Bukkit
 *  org.bukkit.ChatColor
 *  org.bukkit.block.Block
 *  org.bukkit.block.Sign
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 */
package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collections;
import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet61SendPlayerToServer;
import net.xtrafrancyz.VimeNetwork.Debug;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.command.CmdSub;
import net.xtrafrancyz.VimeNetwork.api.command.CommandRoot;
import net.xtrafrancyz.VimeNetwork.api.command.SubCommandData;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.player.treasure.TreasureType;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.util.Items;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.tasks.Restart;
import net.xtrafrancyz.bukkit.texteria.utils.ParsedTime;
import org.apache.mina.core.service.IoServiceStatistics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VimeWorldCommand
extends CommandRoot {
    private final VNPlugin plugin;

    public VimeWorldCommand(VNPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void runCommand(Runnable action, CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            super.runCommand(action, sender, cmd, label, args);
            return;
        }
        NetworkPlayer player = VimeNetwork.getPlayer(sender.getName());
        player.getAchievements().complete(Achievement.SECRET_COOL_HACKER);
        if (player.has(Rank.CHIEF) || VimeNetwork.isTournament() && player.has(Permission.ORGANIZER)) {
            super.runCommand(action, sender, cmd, label, args);
        }
    }

    @Override
    protected boolean main(CommandSender sender, Command cmd, String label, String[] args) {
        this.help(new SubCommandData(sender, label, "help", new String[0]));
        return false;
    }

    @CmdSub(value={"spec"}, rank=Rank.CHIEF)
    private void spectator(SubCommandData data) {
        if (Spectators.instance().contains(data.getPlayer())) {
            U.msg(data.getSender(), "&d\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u043e\u0431\u044b\u0447\u043d\u044b\u043c \u0438\u0433\u0440\u043e\u043a\u043e\u043c");
            Spectators.instance().remove(data.getPlayer());
        } else {
            U.msg(data.getSender(), "&d\u0412\u044b \u0441\u0442\u0430\u043b\u0438 \u0441\u043f\u0435\u043a\u0442\u0440\u043e\u043c");
            Spectators.instance().add(data.getPlayer());
        }
    }

    @CmdSub(value={"tolobby"}, ranks={Rank.CHIEF, Rank.ADMIN, Rank.ORGANIZER})
    private void toLobby(SubCommandData data) {
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
        if (player != null) {
            VimeNetwork.toLobby(player);
        } else {
            U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
        }
    }

    @CmdSub(value={"tolobbyall"}, rank=Rank.CHIEF)
    private void toLobbyAll(SubCommandData data) {
        U.msg(data.getSender(), "&a\u0412\u0441\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u044b \u0432 \u043b\u043e\u0431\u0431\u0438 ");
        VimeNetwork.toLobby(Bukkit.getOnlinePlayers());
    }

    @CmdSub(value={"toserver"}, rank=Rank.CHIEF)
    private void toServer(SubCommandData data) {
        if (data.getArgs().length != 2) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a> <\u0441\u0435\u0440\u0432\u0435\u0440>");
            return;
        }
        Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
        if (player != null) {
            VimeNetwork.toServer(data.getArgs()[1], player);
        } else {
            U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
        }
    }

    @CmdSub(value={"toserverall"}, rank=Rank.CHIEF)
    private void toServerAll(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0441\u0435\u0440\u0432\u0435\u0440>");
            return;
        }
        U.msg(data.getSender(), "&a\u0412\u0441\u0435 \u0438\u0433\u0440\u043e\u043a\u0438 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u044b \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440 " + data.getArgs()[0]);
        VimeNetwork.toServer(data.getArgs()[0], Bukkit.getOnlinePlayers());
    }

    @CmdSub(value={"toserverof"}, rank=Rank.CHIEF)
    private void toServerOf(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(data.getArgs()[0], 8), p -> {
            if (p.getId() == 1) {
                VimeNetwork.toServer(((Packet1PlayerInfo)p).bukkit, data.getPlayer());
            } else {
                U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            }
        }, 300L, () -> U.msg(data.getSender(), "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
    }

    @CmdSub(value={"summon"}, ranks={Rank.CHIEF, Rank.ADMIN, Rank.ORGANIZER})
    private void summon(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a>");
            return;
        }
        VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(data.getArgs()[0], 8), p -> {
            if (p.getId() == 1) {
                U.msg(data.getSender(), "&a\u0418\u0433\u0440\u043e\u043a &f" + ((Packet1PlayerInfo)p).username + "&a \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u043d \u043a \u0432\u0430\u043c \u043d\u0430 \u0441\u0435\u0440\u0432\u0435\u0440");
                VimeNetwork.core().sendPacket(new Packet61SendPlayerToServer(((Packet1PlayerInfo)p).username, VimeNetwork.lobby().getServerId()));
            } else {
                U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            }
        }, 300L, () -> U.msg(data.getSender(), "&c\u041e\u0448\u0438\u0431\u043a\u0430 \u0441\u0432\u044f\u0437\u0438 \u0441 \u0433\u043b\u0430\u0432\u043d\u044b\u043c \u0441\u0435\u0440\u0432\u0435\u0440\u043e\u043c"));
    }

    @CmdSub(value={"item"}, aliases={"i"}, rank=Rank.CHIEF)
    private void item(SubCommandData data) {
        data.getPlayer().getInventory().addItem(new ItemStack[]{Items.parse(Joiner.on((char)' ').join((Object[])data.getArgs()))});
    }

    @CmdSub(value={"stats"}, aliases={"status"}, rank=Rank.CHIEF)
    private void stats(SubCommandData data) {
        Runtime runtime = Runtime.getRuntime();
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("&e------------ &f\u0421\u0442\u0430\u0442\u0438\u0441\u0442\u0438\u043a\u0430 &e------------");
        lines.add("&e\u0412\u0440\u0435\u043c\u044f \u0440\u0430\u0431\u043e\u0442\u044b: &f" + new ParsedTime(System.currentTimeMillis() - ManagementFactory.getRuntimeMXBean().getStartTime()).format());
        lines.add("&e\u041f\u0430\u043c\u044f\u0442\u044c: &f" + (runtime.totalMemory() - runtime.freeMemory()) / 1024L / 1024L + " MB / " + runtime.totalMemory() / 1024L / 1024L + " MB up to " + runtime.maxMemory() / 1024L / 1024L + " MB");
        lines.add("&e\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043a \u0431\u0434: " + (this.plugin.mysql.isConnected() ? "&a\u0430\u043a\u0442\u0438\u0432\u043d\u043e" : "&c\u0440\u0430\u0437\u043e\u0440\u0432\u0430\u043d\u043e"));
        lines.add("&e\u0417\u0430\u043f\u0440\u043e\u0441\u043e\u0432 \u043a \u0431\u0434: &f" + this.plugin.mysql.getExecutedQueries());
        lines.add("&e\u041f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u0438\u0435 \u043a Core: &f" + (this.plugin.core.isConnected() ? "&a\u0430\u043a\u0442\u0438\u0432\u043d\u043e" : "&c\u0440\u0430\u0437\u043e\u0440\u0432\u0430\u043d\u043e"));
        if (this.plugin.core.isConnected()) {
            IoServiceStatistics statistics = this.plugin.core.getConnector().getStatistics();
            lines.add("&e\u041f\u0430\u043a\u0435\u0442\u043e\u0432 \u043a Core: &f" + (statistics.getWrittenMessages() + statistics.getReadMessages()));
            lines.add("&e\u0422\u0440\u0430\u0444\u0438\u043a \u043a Core: &f" + F.formatBytes(statistics.getWrittenBytes() + statistics.getReadBytes()));
        }
        U.msg(data.getSender(), lines);
    }

    @CmdSub(value={"ptime"}, rank=Rank.CHIEF)
    private void ptime(SubCommandData data) {
        if (data.getArgs().length != 1) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0441\u0434\u0432\u0438\u0433 \u0432\u043e \u0432\u0440\u0435\u043c\u0435\u043d\u0438> (0 - \u0441\u0431\u0440\u043e\u0441\u0438\u0442\u044c)");
            return;
        }
        data.getPlayer().setPlayerTime(Long.parseLong(data.getArgs()[0]), true);
    }

    @CmdSub(value={"addcoins"}, rank=Rank.CHIEF)
    private void addCoins(SubCommandData data) {
        if (data.getArgs().length != 2) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a|@all> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        int coins = Integer.parseInt(data.getArgs()[1]);
        if (data.getArgs()[0].equals("@all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                VimeNetwork.getPlayer(player).addCoins(coins);
            }
        } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
                VimeNetwork.getPlayer(player).addCoins(coins);
            }
        }
    }

    @CmdSub(value={"addcoinsexact"}, rank=Rank.CHIEF)
    private void addCoinsExact(SubCommandData data) {
        if (data.getArgs().length != 2) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a|@all> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        int coins = Integer.parseInt(data.getArgs()[1]);
        if (data.getArgs()[0].equals("@all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                VimeNetwork.getPlayer(player).addCoinsExact(coins);
            }
        } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
                VimeNetwork.getPlayer(player).addCoinsExact(coins);
            }
        }
    }

    @CmdSub(value={"giveexp"}, rank=Rank.CHIEF)
    private void giveExp(SubCommandData data) {
        if (data.getArgs().length != 2) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a|@all> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        int exp = Integer.parseInt(data.getArgs()[1]);
        if (data.getArgs()[0].equals("@all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                VimeNetwork.getPlayer(player).giveExp(exp);
            }
        } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
                VimeNetwork.getPlayer(player).giveExp(exp);
            }
        }
    }

    @CmdSub(value={"giveexpexact"}, rank=Rank.CHIEF)
    private void giveExpExact(SubCommandData data) {
        if (data.getArgs().length != 2) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a|@all> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        int exp = Integer.parseInt(data.getArgs()[1]);
        if (data.getArgs()[0].equals("@all")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                VimeNetwork.getPlayer(player).giveExpExact(exp);
            }
        } else {
            Player player = this.plugin.getServer().getPlayerExact(data.getArgs()[0]);
            if (player != null) {
                VimeNetwork.getPlayer(player).giveExpExact(exp);
            }
        }
    }

    @CmdSub(value={"addchest"}, rank=Rank.CHIEF)
    private void addChest(SubCommandData data) {
        int amount;
        TreasureType type;
        if (data.getArgs().length < 3) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a> <basic|ancient|mythical> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        Player player = Bukkit.getPlayerExact((String)data.getArgs()[0]);
        if (player == null) {
            U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            return;
        }
        try {
            type = TreasureType.valueOf(data.getArgs()[1].toUpperCase());
        }
        catch (Exception e) {
            U.msg(data.getSender(), "&c\u0422\u0438\u043f \u0441\u0443\u043d\u0434\u0443\u043a\u0430 " + data.getArgs()[1] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            return;
        }
        try {
            amount = Integer.parseInt(data.getArgs()[2]);
        }
        catch (Exception e) {
            U.msg(data.getSender(), "&c\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0447\u0438\u0441\u043b\u043e\u043c");
            return;
        }
        VimeNetwork.getPlayer(player).getTreasures().add(type, amount);
    }

    @CmdSub(value={"takechest"}, rank=Rank.CHIEF)
    private void takeChest(SubCommandData data) {
        int amount;
        TreasureType type;
        if (data.getArgs().length < 3) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u0438\u0433\u0440\u043e\u043a> <basic|ancient|mythical> <\u043a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e>");
            return;
        }
        Player player = Bukkit.getPlayerExact((String)data.getArgs()[0]);
        if (player == null) {
            U.msg(data.getSender(), "&c\u0418\u0433\u0440\u043e\u043a " + data.getArgs()[0] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            return;
        }
        try {
            type = TreasureType.valueOf(data.getArgs()[1].toUpperCase());
        }
        catch (Exception e) {
            U.msg(data.getSender(), "&c\u0422\u0438\u043f \u0441\u0443\u043d\u0434\u0443\u043a\u0430 " + data.getArgs()[1] + " \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d");
            return;
        }
        try {
            amount = Integer.parseInt(data.getArgs()[2]);
        }
        catch (Exception e) {
            U.msg(data.getSender(), "&c\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0434\u043e\u043b\u0436\u043d\u043e \u0431\u044b\u0442\u044c \u0447\u0438\u0441\u043b\u043e\u043c");
            return;
        }
        VimeNetwork.getPlayer(player).getTreasures().take(type, amount);
    }

    @CmdSub(value={"gc"}, rank=Rank.ADMIN)
    private void gc(SubCommandData data) {
        long start = System.nanoTime();
        System.gc();
        U.msg(data.getSender(), "&a\u0421\u0431\u043e\u0440\u0449\u0438\u043a \u043c\u0443\u0441\u043e\u0440\u0430 \u043e\u0442\u0440\u0430\u0431\u043e\u0442\u0430\u043b: " + F.formatFloat((float)(System.nanoTime() - start) / 1000000.0f, 2) + " \u043c\u0441.");
    }

    @CmdSub(value={"setname"}, rank=Rank.ADMIN)
    private void setName(SubCommandData data) {
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u043d\u043e\u0432\u044b\u0439 \u043d\u0438\u043a>");
            return;
        }
        StringBuilder name = new StringBuilder(data.getArgs()[0]);
        for (int i = 1; i < data.getArgs().length; ++i) {
            name.append(" ").append(data.getArgs()[i]);
        }
        VimeNetwork.getPlayer(data.getPlayer()).getTag().setName(name.toString());
        U.msg(data.getSender(), "&a\u041d\u0430\u0441\u043b\u0430\u0436\u0434\u0430\u0439\u0442\u0435\u0441\u044c \u043d\u043e\u0432\u044b\u043c \u0438\u043c\u0435\u043d\u0435\u043c! - &r" + name);
    }

    @CmdSub(value={"debug"}, rank=Rank.ADMIN)
    private void debug(SubCommandData data) {
        try {
            Debug group = Debug.valueOf(data.getArgs()[0].toUpperCase());
            if (group.isEnabled()) {
                U.msg(data.getSender(), "&e" + group.name() + " \u0434\u0435\u0431\u0430\u0433 &c\u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d.");
                group.setEnabled(false);
            } else {
                U.msg(data.getSender(), "&e" + group.name() + " \u0434\u0435\u0431\u0430\u0433 &a\u0432\u043a\u043b\u044e\u0447\u0435\u043d.");
                group.setEnabled(true);
            }
        }
        catch (Exception e) {
            StringBuilder str = new StringBuilder("<");
            for (Debug group : Debug.values()) {
                if (str.length() != 1) {
                    str.append("&e, ");
                }
                if (group.isEnabled()) {
                    str.append("&a").append(group.name());
                    continue;
                }
                str.append("&c").append(group.name());
            }
            str.append("&e>");
            U.msg(data.getSender(), "&e/" + data.getLabel() + " debug " + str);
        }
    }

    @CmdSub(value={"restart"}, ranks={Rank.ADMIN, Rank.ORGANIZER})
    private void restart(SubCommandData data) {
        if (data.hasArgs()) {
            Restart.restart();
        } else {
            Restart.countdown();
        }
    }

    @CmdSub(value={"lobbysign"}, rank=Rank.ADMIN)
    private void lobbySign(SubCommandData data) {
        Block block = data.getPlayer().getTargetBlock(null, 5);
        if (block == null || block.getState() == null || !(block.getState() instanceof Sign)) {
            U.msg(data.getSender(), "&c\u0412\u044b \u0434\u043e\u043b\u0436\u043d\u044b \u0441\u043c\u043e\u0442\u0440\u0435\u0442\u044c \u043d\u0430 \u0442\u0430\u0431\u043b\u0438\u0447\u043a\u0443");
            return;
        }
        Sign sign = (Sign)block.getState();
        sign.setLine(0, ChatColor.GRAY + "[" + ChatColor.GREEN + "Lobby" + ChatColor.GRAY + "]");
        sign.setLine(1, ChatColor.WHITE + "\u0412\u0435\u0440\u043d\u0443\u0442\u044c\u0441\u044f");
        sign.setLine(2, ChatColor.WHITE + "\u0432 \u043b\u043e\u0431\u0431\u0438");
        sign.setLine(3, null);
        sign.update();
        U.msg(data.getSender(), "&a\u0422\u0430\u0431\u043b\u0438\u0447\u043a\u0430 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d\u0430");
    }

    @CmdSub(value={"head"}, rank=Rank.ADMIN)
    private void head(SubCommandData data) {
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&c/" + data.getLabel() + " " + data.getSub() + " <\u043d\u0438\u043a>");
            return;
        }
        data.getPlayer().getInventory().addItem(new ItemStack[]{Items.head(data.getArgs()[0])});
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @CmdSub(value={"srv-config"}, ranks={Rank.CHIEF, Rank.ADMIN, Rank.ORGANIZER})
    private void srvConfig(SubCommandData data) {
        if (data.getArgs().length == 0) {
            U.msg(data.getSender(), "&e/" + data.getLabel() + " " + data.getSub() + "&7 list [game]&f: \u0441\u043f\u0438\u0441\u043e\u043a \u043a\u043e\u043d\u0444\u0438\u0433\u043e\u0432 \u0434\u043b\u044f \u0438\u0433\u0440\u044b", "&e/" + data.getLabel() + " " + data.getSub() + "&7 set [game] <config-name>&f: \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u0442\u044c \u0437\u0430\u0434\u0430\u043d\u043d\u044b\u0439 \u043a\u043e\u043d\u0444\u0438\u0433", "&e/" + data.getLabel() + " " + data.getSub() + "&7 get&f: \u0443\u0437\u043d\u0430\u0442\u044c \u043a\u0430\u043a\u043e\u0439 \u0441\u0435\u0439\u0447\u0430\u0441 \u043a\u043e\u043d\u0444\u0438\u0433 \u0441\u0442\u043e\u0438\u0442", "&e/" + data.getLabel() + " " + data.getSub() + "&7 reset&f: \u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0435 \u043a\u0430\u043a \u0431\u044b\u043b\u043e");
            return;
        }
        switch (data.getArgs()[0].toLowerCase()) {
            case "list": {
                String game = VimeNetwork.lobby().getServerTypeId();
                if (data.getArgs().length == 2) {
                    game = data.getArgs()[1].toUpperCase();
                }
                if (game.contains("/") || game.contains("\\")) {
                    U.msg(data.getSender(), "&c\u0422\u044b \u0447\u0442\u043e, \u043e\u0445\u0443\u0435\u043b \u0441\u043b\u0435\u0448\u0438 \u043f\u0438\u0445\u0430\u0442\u044c");
                    return;
                }
                File configsDir = new File(UpdateWatcher.UPDATE_DIR, game + "/configs");
                if (!configsDir.exists()) {
                    U.msg(data.getSender(), "&c\u0418\u0433\u0440\u0430 " + game + " \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442");
                    return;
                }
                File[] files = configsDir.listFiles(file -> file.isFile() && file.getName().endsWith(".zip"));
                if (files == null || files.length == 0) {
                    U.msg(data.getSender(), "&a\u0422\u0430\u043c \u043d\u0438\u0447\u0435\u0433\u043e \u043d\u0435\u0442");
                    break;
                }
                ArrayList<String> available = new ArrayList<String>(files.length);
                for (File file2 : files) {
                    String name = file2.getName();
                    available.add(name.substring(0, name.lastIndexOf(46)));
                }
                Collections.sort(available);
                U.msg(data.getSender(), "&a\u0414\u043e\u0441\u0442\u0443\u043f\u043d\u044b\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0438 \u0434\u043b\u044f \u0441\u0435\u0440\u0432\u0435\u0440\u0430 " + game + ":", Joiner.on((String)"&7,&f ").join(available));
                break;
            }
            case "set": {
                String configName;
                String game;
                if (data.getArgs().length == 2) {
                    game = VimeNetwork.lobby().getServerTypeId();
                    configName = data.getArgs()[1];
                } else if (data.getArgs().length == 3) {
                    game = data.getArgs()[1].toUpperCase();
                    configName = data.getArgs()[2];
                } else {
                    U.msg(data.getSender(), "&c\u041d\u0435 \u0442\u0430\u043a \u043d\u0430\u043f\u0438\u0441\u0430\u043b");
                    return;
                }
                if (game.contains("/") || game.contains("\\") || configName.contains("/") || configName.contains("\\")) {
                    U.msg(data.getSender(), "&c\u0422\u044b \u0447\u0442\u043e, \u043e\u0445\u0443\u0435\u043b \u0441\u043b\u0435\u0448\u0438 \u043f\u0438\u0445\u0430\u0442\u044c");
                    return;
                }
                File config = new File(UpdateWatcher.UPDATE_DIR, game + "/configs/" + configName + ".zip");
                if (!config.exists()) {
                    U.msg(data.getSender(), "&c\u0422\u0430\u043a\u043e\u0433\u043e \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043d\u0435 \u0441\u0443\u0449\u0435\u0441\u0442\u0432\u0443\u0435\u0442");
                    return;
                }
                if (!new File("_install.sh.orig").exists()) {
                    new File("_install.sh").renameTo(new File("_install.sh.orig"));
                }
                BufferedWriter writer = null;
                BufferedReader reader = null;
                try {
                    String line;
                    writer = new BufferedWriter(new FileWriter("_install.sh"));
                    reader = new BufferedReader(new FileReader("_install.sh.orig"));
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("CONFIG=")) {
                            line = game.equals(VimeNetwork.lobby().getServerTypeId()) ? "CONFIG=\"" + configName + "\"" : "CONFIG=\"../../" + game + "/configs/" + configName + "\"";
                        }
                        writer.write(line);
                        writer.newLine();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    U.msg(data.getSender(), e.getMessage());
                }
                finally {
                    if (writer != null) {
                        try {
                            writer.close();
                        }
                        catch (IOException iOException) {}
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                U.msg(data.getSender(), "&a\u041a\u043e\u043d\u0444\u0438\u0433 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", "\u0422\u0435\u043f\u0435\u0440\u044c \u0441\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0434\u043e \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c");
                break;
            }
            case "get": {
                String config = null;
                try (BufferedReader reader2 = new BufferedReader(new FileReader("_install.sh"));){
                    String line;
                    while ((line = reader2.readLine()) != null) {
                        if (!line.startsWith("CONFIG=")) continue;
                        config = line.substring(8, line.length() - 1);
                    }
                }
                catch (Exception reader2) {
                    // empty catch block
                }
                if (config != null) {
                    U.msg(data.getSender(), "&a\u0421\u0435\u0439\u0447\u0430\u0441 \u0441\u0442\u043e\u0438\u0442 \u043a\u043e\u043d\u0444\u0438\u0433: &f" + config);
                    break;
                }
                U.msg(data.getSender(), "&c\u0411\u0440\u0435\u0434 \u043a\u0430\u043a\u043e\u0439-\u0442\u043e, \u0441\u0435\u0440\u0432 \u0431\u0438\u0442\u044b\u0439");
                break;
            }
            case "reset": {
                File orig = new File("_install.sh.orig");
                if (!orig.exists()) {
                    U.msg(data.getSender(), "&c\u041d\u0435\u0447\u0435\u0433\u043e \u0440\u0435\u0441\u0435\u0442\u0438\u0442\u044c, \u0432\u0441\u0435 \u0438 \u0442\u0430\u043a \u043d\u043e\u0440\u043c\u0430\u0441");
                    return;
                }
                File current = new File("_install.sh");
                if (current.exists()) {
                    current.delete();
                }
                orig.renameTo(current);
                U.msg(data.getSender(), "&a\u041a\u043e\u043d\u0444\u0438\u0433 \u0443\u0441\u043f\u0435\u0448\u043d\u043e \u0432\u043e\u0441\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d", "\u0422\u0435\u043f\u0435\u0440\u044c \u0441\u0435\u0440\u0432\u0435\u0440 \u043d\u0430\u0434\u043e \u043f\u0435\u0440\u0435\u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c");
                break;
            }
        }
    }

    @CmdSub(value={"help"}, ranks={Rank.CHIEF, Rank.ADMIN, Rank.ORGANIZER}, hidden=true)
    private void help(SubCommandData data) {
        ArrayList<String> cmds = new ArrayList<String>();
        Rank rank = this.getRank(data.getSender());
        for (CommandRoot.PublicSub sub : this.getPublicSubs()) {
            if (!sub.sub.isAvailableFor(rank, null)) continue;
            cmds.add(sub.cmd);
        }
        data.getSender().sendMessage(Joiner.on((String)", ").join(cmds));
    }
}

