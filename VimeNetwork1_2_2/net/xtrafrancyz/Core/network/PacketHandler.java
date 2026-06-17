/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network;

import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet0KeepAlive;
import net.xtrafrancyz.Core.network.packet.Packet100BukkitConnect;
import net.xtrafrancyz.Core.network.packet.Packet101BukkitUpdateInfo;
import net.xtrafrancyz.Core.network.packet.Packet102BungeeConnect;
import net.xtrafrancyz.Core.network.packet.Packet103OnBukkitUpdate;
import net.xtrafrancyz.Core.network.packet.Packet104OnBukkitDisconnect;
import net.xtrafrancyz.Core.network.packet.Packet105DaemonConnect;
import net.xtrafrancyz.Core.network.packet.Packet106GenericConnect;
import net.xtrafrancyz.Core.network.packet.Packet10PlayerGiveExp;
import net.xtrafrancyz.Core.network.packet.Packet11PlayerGiveExpSimple;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet200GetServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet201ServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet203GetBungeesInfo;
import net.xtrafrancyz.Core.network.packet.Packet204BungeesInfo;
import net.xtrafrancyz.Core.network.packet.Packet205DaemonStatus;
import net.xtrafrancyz.Core.network.packet.Packet2PlayerConnect;
import net.xtrafrancyz.Core.network.packet.Packet301Subscribe;
import net.xtrafrancyz.Core.network.packet.Packet302Unsubscribe;
import net.xtrafrancyz.Core.network.packet.Packet303ProtocolCheck;
import net.xtrafrancyz.Core.network.packet.Packet304ConsoleLog;
import net.xtrafrancyz.Core.network.packet.Packet305ChatLog;
import net.xtrafrancyz.Core.network.packet.Packet3PlayerDisconnect;
import net.xtrafrancyz.Core.network.packet.Packet4PlayerChangeServer;
import net.xtrafrancyz.Core.network.packet.Packet50TotalOnline;
import net.xtrafrancyz.Core.network.packet.Packet51ChatMessage;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.Packet54PrivateMessage;
import net.xtrafrancyz.Core.network.packet.Packet55PrivateIgnore;
import net.xtrafrancyz.Core.network.packet.Packet56StreamAction;
import net.xtrafrancyz.Core.network.packet.Packet57StreamStatus;
import net.xtrafrancyz.Core.network.packet.Packet58PartyAction;
import net.xtrafrancyz.Core.network.packet.Packet59Party;
import net.xtrafrancyz.Core.network.packet.Packet5PlayerCoinsChange;
import net.xtrafrancyz.Core.network.packet.Packet60PartyInvite;
import net.xtrafrancyz.Core.network.packet.Packet61SendPlayerToServer;
import net.xtrafrancyz.Core.network.packet.Packet62FriendAction;
import net.xtrafrancyz.Core.network.packet.Packet63FriendRequest;
import net.xtrafrancyz.Core.network.packet.Packet64SessionCreate;
import net.xtrafrancyz.Core.network.packet.Packet65SessionRemovePlayer;
import net.xtrafrancyz.Core.network.packet.Packet66SessionEnd;
import net.xtrafrancyz.Core.network.packet.Packet67SessionInfo;
import net.xtrafrancyz.Core.network.packet.Packet68Report;
import net.xtrafrancyz.Core.network.packet.Packet69Guild;
import net.xtrafrancyz.Core.network.packet.Packet6PlayerMetaChange;
import net.xtrafrancyz.Core.network.packet.Packet70QueueRegisterGame;
import net.xtrafrancyz.Core.network.packet.Packet71QueueRegisterPlayer;
import net.xtrafrancyz.Core.network.packet.Packet72QueuedGameStart;
import net.xtrafrancyz.Core.network.packet.Packet73QueueUnregisterPlayer;
import net.xtrafrancyz.Core.network.packet.Packet74QueueInfo;
import net.xtrafrancyz.Core.network.packet.Packet75MysqlCacheRequest;
import net.xtrafrancyz.Core.network.packet.Packet76MysqlCacheResponse;
import net.xtrafrancyz.Core.network.packet.Packet77MysqlCacheUpdate;
import net.xtrafrancyz.Core.network.packet.Packet7MiniDot;
import net.xtrafrancyz.Core.network.packet.Packet8PlayerGetAchievement;
import net.xtrafrancyz.Core.network.packet.Packet9PlayerStatChange;

public class PacketHandler {
    public void handle(Packet packet) {
    }

    public void handle0KeepAlive(Packet0KeepAlive packet) {
    }

    public void handle1PlayerInfo(Packet1PlayerInfo packet) {
    }

    public void handle2PlayerConnect(Packet2PlayerConnect packet) {
    }

    public void handle3PlayerDisconnect(Packet3PlayerDisconnect packet) {
    }

    public void handle4PlayerChangeServer(Packet4PlayerChangeServer packet) {
    }

    public void handle5PlayerCoinsChange(Packet5PlayerCoinsChange packet) {
    }

    public void handle6PlayerMetaChange(Packet6PlayerMetaChange packet) {
    }

    public void handle7MiniDot(Packet7MiniDot packet) {
    }

    public void handle8PlayerGetAchievement(Packet8PlayerGetAchievement packet) {
    }

    public void handle9PlayerStatChange(Packet9PlayerStatChange packet) {
    }

    public void handle10PlayerGiveExp(Packet10PlayerGiveExp packet) {
    }

    public void handle11PlayerGiveExpSimple(Packet11PlayerGiveExpSimple packet) {
    }

    public void handle50TotalOnline(Packet50TotalOnline packet) {
    }

    public void handle51ChatMessage(Packet51ChatMessage packet) {
    }

    public void handle52CustomMessage(Packet52CustomMessage packet) {
    }

    public void handle53Answer(Packet53Answer packet) {
    }

    public void handle54PrivateMessage(Packet54PrivateMessage packet) {
    }

    public void handle55PrivateIgnore(Packet55PrivateIgnore packet) {
    }

    public void handle56StreamAction(Packet56StreamAction packet) {
    }

    public void handle57StreamStatus(Packet57StreamStatus packet) {
    }

    public void handle58PartyAction(Packet58PartyAction packet) {
    }

    public void handle59Party(Packet59Party packet) {
    }

    public void handle60PartyInvite(Packet60PartyInvite packet) {
    }

    public void handle61SendPlayerToServer(Packet61SendPlayerToServer packet) {
    }

    public void handle62FriendAction(Packet62FriendAction packet) {
    }

    public void handle63FriendRequest(Packet63FriendRequest packet) {
    }

    public void handle64SessionCreate(Packet64SessionCreate packet) {
    }

    public void handle65SessionRemovePlayer(Packet65SessionRemovePlayer packet) {
    }

    public void handle66SessionEnd(Packet66SessionEnd packet) {
    }

    public void handle67SessionInfo(Packet67SessionInfo packet) {
    }

    public void handle68Report(Packet68Report packet) {
    }

    public void handle69Guild(Packet69Guild packet) {
    }

    public void handle70QueueRegisterGame(Packet70QueueRegisterGame packet) {
    }

    public void handle71QueueRegisterPlayer(Packet71QueueRegisterPlayer packet) {
    }

    public void handle72QueuedGameStart(Packet72QueuedGameStart packet) {
    }

    public void handle73QueueUnregisterPlayer(Packet73QueueUnregisterPlayer packet) {
    }

    public void handle74QueueInfo(Packet74QueueInfo packet) {
    }

    public void handle75MysqlCacheRequest(Packet75MysqlCacheRequest packet) {
    }

    public void handle76MysqlCacheResponse(Packet76MysqlCacheResponse packet) {
    }

    public void handle77MysqlCacheUpdate(Packet77MysqlCacheUpdate packet) {
    }

    public void handle100BukkitConnect(Packet100BukkitConnect packet) {
    }

    public void handle101BukkitUpdateInfo(Packet101BukkitUpdateInfo packet) {
    }

    public void handle102BungeeConnect(Packet102BungeeConnect packet) {
    }

    public void handle103OnBukkitUpdate(Packet103OnBukkitUpdate packet) {
    }

    public void handle104OnBukkitDisconnect(Packet104OnBukkitDisconnect packet) {
    }

    public void handle105DaemonConnect(Packet105DaemonConnect packet) {
    }

    public void handle106GenericConnect(Packet106GenericConnect packet) {
    }

    public void handle200GetServersInfo(Packet200GetServersInfo packet) {
    }

    public void handle201ServersInfo(Packet201ServersInfo packet) {
    }

    public void handle202GetPlayerInfo(Packet202GetPlayerInfo packet) {
    }

    public void handle203GetBungeesInfo(Packet203GetBungeesInfo packet) {
    }

    public void handle204BungeesInfo(Packet204BungeesInfo packet) {
    }

    public void handle205DaemonStatus(Packet205DaemonStatus packet) {
    }

    public void handle301Subscribe(Packet301Subscribe packet) {
    }

    public void handle302Unsubscribe(Packet302Unsubscribe packet) {
    }

    public void handle303ProtocolCheck(Packet303ProtocolCheck packet) {
    }

    public void handle304ConsoleLog(Packet304ConsoleLog packet) {
    }

    public void handle305ChatLog(Packet305ChatLog packet) {
    }
}

