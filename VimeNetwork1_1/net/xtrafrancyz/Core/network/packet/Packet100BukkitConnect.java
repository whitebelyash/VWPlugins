package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet100BukkitConnect extends ResponsePacket {
   public String id;
   public String host;
   public int port;
   public int maxPlayers;

   private Packet100BukkitConnect() {
   }

   public Packet100BukkitConnect(String id, String host, int port, int maxPlayers) {
      this.id = id;
      this.host = host;
      this.port = port;
      this.maxPlayers = maxPlayers;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeString(this.id);
      buf.writeString(this.host);
      buf.writeInt(this.port);
      buf.writeVarInt(this.maxPlayers);
   }

   public void read0(Buf buf) throws Exception {
      this.id = buf.readString();
      this.host = buf.readString();
      this.port = buf.readInt();
      this.maxPlayers = buf.readVarInt();
   }

   public void process0(PacketHandler handler) {
      handler.handle100BukkitConnect(this);
   }

   public String toString() {
      return super.toString() + "{id=" + this.id + ", host=" + this.host + ":" + this.port + "}";
   }
}
