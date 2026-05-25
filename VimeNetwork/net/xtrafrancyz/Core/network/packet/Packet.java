package net.xtrafrancyz.Core.network.packet;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public abstract class Packet {
   public static final AtomicLong COMPRESS_SAVED = new AtomicLong(0L);
   public static final int PROTO_VERSION = 4;
   public static final Map idToPacket = new HashMap();
   public static final Map packetToId = new HashMap();
   private final int id;

   private static void registerPacket(int id, Class clazz) {
      idToPacket.put(id, new PacketData(clazz));
      packetToId.put(clazz, id);
   }

   public Packet() {
      this.id = (Integer)packetToId.get(this.getClass());
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
      registerPacket(0, Packet0KeepAlive.class);
      registerPacket(1, Packet1PlayerInfo.class);
      registerPacket(2, Packet2PlayerConnect.class);
      registerPacket(3, Packet3PlayerDisconnect.class);
      registerPacket(4, Packet4PlayerChangeServer.class);
      registerPacket(5, Packet5PlayerCoinsChange.class);
      registerPacket(6, Packet6PlayerMetaChange.class);
      registerPacket(7, Packet7MiniDot.class);
      registerPacket(8, Packet8PlayerGetAchievement.class);
      registerPacket(9, Packet9PlayerStatChange.class);
      registerPacket(10, Packet10PlayerGiveExp.class);
      registerPacket(50, Packet50TotalOnline.class);
      registerPacket(51, Packet51ChatMessage.class);
      registerPacket(52, Packet52CustomMessage.class);
      registerPacket(53, Packet53Answer.class);
      registerPacket(54, Packet54PrivateMessage.class);
      registerPacket(55, Packet55PrivateIgnore.class);
      registerPacket(56, Packet56StreamAction.class);
      registerPacket(57, Packet57StreamStatus.class);
      registerPacket(58, Packet58PartyAction.class);
      registerPacket(59, Packet59Party.class);
      registerPacket(60, Packet60PartyInvite.class);
      registerPacket(61, Packet61SendPlayerToServer.class);
      registerPacket(62, Packet62FriendAction.class);
      registerPacket(63, Packet63FriendRequest.class);
      registerPacket(64, Packet64SessionCreate.class);
      registerPacket(65, Packet65SessionRemovePlayer.class);
      registerPacket(66, Packet66SessionEnd.class);
      registerPacket(67, Packet67SessionInfo.class);
      registerPacket(68, Packet68Report.class);
      registerPacket(100, Packet100BukkitConnect.class);
      registerPacket(101, Packet101BukkitUpdateInfo.class);
      registerPacket(102, Packet102BungeeConnect.class);
      registerPacket(103, Packet103OnBukkitUpdate.class);
      registerPacket(104, Packet104OnBukkitDisconnect.class);
      registerPacket(105, Packet105DaemonConnect.class);
      registerPacket(106, Packet106GenericConnect.class);
      registerPacket(200, Packet200GetServersInfo.class);
      registerPacket(201, Packet201ServersInfo.class);
      registerPacket(202, Packet202GetPlayerInfo.class);
      registerPacket(203, Packet203GetBungeesInfo.class);
      registerPacket(204, Packet204BungeesInfo.class);
      registerPacket(205, Packet205DaemonStatus.class);
      registerPacket(301, Packet301Subscribe.class);
      registerPacket(302, Packet302Unsubscribe.class);
      registerPacket(303, Packet303ProtocolCheck.class);
      registerPacket(304, Packet304ConsoleLog.class);
      registerPacket(305, Packet305ChatLog.class);
   }

   public static class PacketData {
      private Class clazz;
      private Constructor constructor;

      public PacketData(Class clazz) {
         this.clazz = clazz;
      }

      public Packet create() throws ReflectiveOperationException {
         if (this.constructor == null) {
            Constructor<? extends Packet> temp = this.clazz.getDeclaredConstructor();
            temp.setAccessible(true);
            this.constructor = temp;
         }

         return (Packet)this.constructor.newInstance();
      }
   }
}
