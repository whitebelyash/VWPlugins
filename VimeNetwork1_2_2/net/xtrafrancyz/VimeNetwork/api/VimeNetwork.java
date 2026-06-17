/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet250CustomPayload
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.craftbukkit.libs.com.google.gson.Gson
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.api;

import java.time.ZoneId;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet250CustomPayload;
import net.xtrafrancyz.Commons.player.Permission;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet61SendPlayerToServer;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.CoreBukkit;
import net.xtrafrancyz.VimeNetwork.api.Features;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.Metrics;
import net.xtrafrancyz.VimeNetwork.api.VimeTexteria;
import net.xtrafrancyz.VimeNetwork.api.holo.Holograms;
import net.xtrafrancyz.VimeNetwork.api.mysql.MysqlThread;
import net.xtrafrancyz.VimeNetwork.api.npc.NPCs;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.updater.UpdateWatcher;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.VimeNetwork.packet.BungeeBridge;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;

public class VimeNetwork {
    public static final ZoneId TZ_MOSCOW = ZoneId.of("Europe/Moscow");
    public static final Gson gson = new Gson();

    private VimeNetwork() {
    }

    public static boolean isDev() {
        return VNPlugin.instance().config.dev;
    }

    public static boolean isTournament() {
        return VNPlugin.instance().config.tournament;
    }

    public static Lobby lobby() {
        return VNPlugin.instance().lobby;
    }

    public static Features features() {
        return Features.inst;
    }

    public static VimeTexteria texteria() {
        return VimeTexteria.inst;
    }

    public static MysqlThread mysql() {
        return VNPlugin.instance().mysql;
    }

    public static CoreBukkit core() {
        return VNPlugin.instance().core;
    }

    public static UpdateWatcher updateWatcher() {
        return VNPlugin.instance().updateWatcher;
    }

    public static Metrics metrics() {
        return VNPlugin.instance().metrics;
    }

    public static Holograms holograms() {
        return VNPlugin.instance().holograms;
    }

    public static NPCs npcs() {
        return VNPlugin.instance().npcs;
    }

    public static boolean hasRank(CommandSender who, Rank rank, boolean notify) {
        if (who instanceof ConsoleCommandSender) {
            return true;
        }
        if (notify) {
            return VimeNetwork.getPlayer(who.getName()).hasAndNotify(rank);
        }
        return VimeNetwork.getPlayer(who.getName()).has(rank);
    }

    public static boolean hasPermission(CommandSender who, Permission permission, boolean notify) {
        if (who instanceof ConsoleCommandSender) {
            return true;
        }
        if (notify) {
            return VimeNetwork.getPlayer(who.getName()).hasAndNotify(permission);
        }
        return VimeNetwork.getPlayer(who.getName()).has(permission);
    }

    public static NetworkPlayer getPlayer(String player) {
        return VPlayer.get(player);
    }

    public static NetworkPlayer getPlayer(Player player) {
        return VPlayer.get(player);
    }

    public static NetworkPlayer getPlayer(int userid) {
        return VPlayer.IDS.get(userid);
    }

    public static boolean isPlayerOnline(String player) {
        return VPlayer.PLAYERS.containsKey(player);
    }

    public static boolean isPlayerOnline(Player player) {
        return VPlayer.PLAYERS.containsKey(player.getName());
    }

    public static boolean isPlayerOnline(int userid) {
        return VPlayer.IDS.containsKey(userid);
    }

    public static void addCommandHelp(String command, String help) {
        VNPlugin.instance().help.addCommand(command, help);
    }

    public static void addCommandHelp(String command, String help, Rank rank) {
        VNPlugin.instance().help.addCommand(command, help, rank);
    }

    public static void addCommandHelp(String command, String help, Permission permission) {
        VNPlugin.instance().help.addCommand(command, help, permission);
    }

    public static void ban(String player, int minutes, String reason, String moder) {
        VimeNetwork.core().sendPacket(new Packet52CustomMessage("ban", Packet52CustomMessage.Scope.CORE).put("username", player).put("minutes", minutes).put("reason", reason).put("moder", moder));
    }

    public static void unban(String username, String moder) {
        VimeNetwork.core().sendPacket(new Packet52CustomMessage("unban", Packet52CustomMessage.Scope.CORE).put("username", username).put("moder", moder));
    }

    public static void mute(String username, int minutes, String reason, String moder) {
        VimeNetwork.core().sendPacket(new Packet52CustomMessage("mute", Packet52CustomMessage.Scope.CORE).put("username", username).put("minutes", minutes).put("reason", reason).put("moder", moder));
    }

    public static void unmute(String username, String moder) {
        VimeNetwork.core().sendPacket(new Packet52CustomMessage("unmute", Packet52CustomMessage.Scope.CORE).put("username", username).put("moder", moder));
    }

    public static void logAction(String username, String action) {
        VimeNetwork.logAction(username, action, null, null);
    }

    public static void logAction(String username, String action, String target) {
        VimeNetwork.logAction(username, action, target, null);
    }

    public static void logAction(String username, String action, String target, String comment) {
        if (action == null || username == null) {
            return;
        }
        target = target == null ? "NULL" : "'" + StringEscapeUtils.escapeSql((String)target) + "'";
        comment = comment == null ? "NULL" : "'" + StringEscapeUtils.escapeSql((String)comment) + "'";
        VimeNetwork.mysql().query("INSERT INTO `user_log_actions` (`username`, `time`, `action`, `data`, `comment`) VALUES ('" + username + "', " + System.currentTimeMillis() / 1000L + ", '" + action + "', " + target + ", " + comment + ")");
    }

    public static void setWindowTitle(Player player, String title) {
        U.sendPacket(player, (Packet)new Packet250CustomPayload("Vime", ("setTitle:" + title).getBytes()));
    }

    public static void toLobby(Player ... players) {
        for (Player player : players) {
            BungeeBridge.toLobby(player);
        }
    }

    public static void toServer(String server, Player ... players) {
        for (Player player : players) {
            BungeeBridge.toServer(player, server);
        }
    }

    public static void toServer(String server, Player player, CoreByteMap switchData) {
        if (VimeNetwork.core().isConnected() && switchData != null && !switchData.isEmpty()) {
            VimeNetwork.core().sendPacket(new Packet61SendPlayerToServer(player.getName(), server, switchData));
        } else {
            VimeNetwork.toServer(server, player);
        }
    }
}

