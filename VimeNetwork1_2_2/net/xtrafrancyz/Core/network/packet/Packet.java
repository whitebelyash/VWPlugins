/*
 * Decompiled with CFR 0.152.
 */
package net.xtrafrancyz.Core.network.packet;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;
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

public abstract class Packet {
    public static final AtomicLong COMPRESS_SAVED = new AtomicLong(0L);
    public static final int PROTO_VERSION = 4;
    public static final Map<Integer, PacketData> idToPacket = new HashMap<Integer, PacketData>();
    public static final Map<Class, Integer> packetToId = new HashMap<Class, Integer>();
    private final int id = packetToId.get(this.getClass());

    private static void registerPacket(int id, Class<? extends Packet> clazz) {
        idToPacket.put(id, new PacketData(clazz));
        packetToId.put(clazz, id);
    }

    public void write(Buf buf) throws Exception {
        this.write0(buf);
    }

    protected abstract void write0(Buf var1) throws Exception;

    public void read(Buf buf) throws Exception {
        this.read0(buf);
    }

    protected abstract void read0(Buf var1) throws Exception;

    public void process(PacketHandler handler) throws Exception {
        handler.handle(this);
        this.process0(handler);
    }

    protected abstract void process0(PacketHandler var1);

    public int getId() {
        return this.id;
    }

    public String toString() {
        return this.getClass().getSimpleName();
    }

    static {
        Packet.registerPacket(0, Packet0KeepAlive.class);
        Packet.registerPacket(1, Packet1PlayerInfo.class);
        Packet.registerPacket(2, Packet2PlayerConnect.class);
        Packet.registerPacket(3, Packet3PlayerDisconnect.class);
        Packet.registerPacket(4, Packet4PlayerChangeServer.class);
        Packet.registerPacket(5, Packet5PlayerCoinsChange.class);
        Packet.registerPacket(6, Packet6PlayerMetaChange.class);
        Packet.registerPacket(7, Packet7MiniDot.class);
        Packet.registerPacket(8, Packet8PlayerGetAchievement.class);
        Packet.registerPacket(9, Packet9PlayerStatChange.class);
        Packet.registerPacket(10, Packet10PlayerGiveExp.class);
        Packet.registerPacket(11, Packet11PlayerGiveExpSimple.class);
        Packet.registerPacket(50, Packet50TotalOnline.class);
        Packet.registerPacket(51, Packet51ChatMessage.class);
        Packet.registerPacket(52, Packet52CustomMessage.class);
        Packet.registerPacket(53, Packet53Answer.class);
        Packet.registerPacket(54, Packet54PrivateMessage.class);
        Packet.registerPacket(55, Packet55PrivateIgnore.class);
        Packet.registerPacket(56, Packet56StreamAction.class);
        Packet.registerPacket(57, Packet57StreamStatus.class);
        Packet.registerPacket(58, Packet58PartyAction.class);
        Packet.registerPacket(59, Packet59Party.class);
        Packet.registerPacket(60, Packet60PartyInvite.class);
        Packet.registerPacket(61, Packet61SendPlayerToServer.class);
        Packet.registerPacket(62, Packet62FriendAction.class);
        Packet.registerPacket(63, Packet63FriendRequest.class);
        Packet.registerPacket(64, Packet64SessionCreate.class);
        Packet.registerPacket(65, Packet65SessionRemovePlayer.class);
        Packet.registerPacket(66, Packet66SessionEnd.class);
        Packet.registerPacket(67, Packet67SessionInfo.class);
        Packet.registerPacket(68, Packet68Report.class);
        Packet.registerPacket(69, Packet69Guild.class);
        Packet.registerPacket(70, Packet70QueueRegisterGame.class);
        Packet.registerPacket(71, Packet71QueueRegisterPlayer.class);
        Packet.registerPacket(72, Packet72QueuedGameStart.class);
        Packet.registerPacket(73, Packet73QueueUnregisterPlayer.class);
        Packet.registerPacket(74, Packet74QueueInfo.class);
        Packet.registerPacket(75, Packet75MysqlCacheRequest.class);
        Packet.registerPacket(76, Packet76MysqlCacheResponse.class);
        Packet.registerPacket(77, Packet77MysqlCacheUpdate.class);
        Packet.registerPacket(100, Packet100BukkitConnect.class);
        Packet.registerPacket(101, Packet101BukkitUpdateInfo.class);
        Packet.registerPacket(102, Packet102BungeeConnect.class);
        Packet.registerPacket(103, Packet103OnBukkitUpdate.class);
        Packet.registerPacket(104, Packet104OnBukkitDisconnect.class);
        Packet.registerPacket(105, Packet105DaemonConnect.class);
        Packet.registerPacket(106, Packet106GenericConnect.class);
        Packet.registerPacket(200, Packet200GetServersInfo.class);
        Packet.registerPacket(201, Packet201ServersInfo.class);
        Packet.registerPacket(202, Packet202GetPlayerInfo.class);
        Packet.registerPacket(203, Packet203GetBungeesInfo.class);
        Packet.registerPacket(204, Packet204BungeesInfo.class);
        Packet.registerPacket(205, Packet205DaemonStatus.class);
        Packet.registerPacket(301, Packet301Subscribe.class);
        Packet.registerPacket(302, Packet302Unsubscribe.class);
        Packet.registerPacket(303, Packet303ProtocolCheck.class);
        Packet.registerPacket(304, Packet304ConsoleLog.class);
        Packet.registerPacket(305, Packet305ChatLog.class);
    }

    public static class PacketData {
        private Class<? extends Packet> clazz;
        private Constructor<? extends Packet> constructor;

        public PacketData(Class<? extends Packet> clazz) {
            this.clazz = clazz;
        }

        public Packet create() throws ReflectiveOperationException {
            if (this.constructor == null) {
                Constructor<? extends Packet> temp = this.clazz.getDeclaredConstructor(new Class[0]);
                temp.setAccessible(true);
                this.constructor = temp;
            }
            return this.constructor.newInstance(new Object[0]);
        }
    }
}

