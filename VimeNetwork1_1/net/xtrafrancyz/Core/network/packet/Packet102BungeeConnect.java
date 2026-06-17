package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet102BungeeConnect extends ResponsePacket {
   public String id;
   public String host;
   public int port;
   public int max;

   private Packet102BungeeConnect() {
   }

   public Packet102BungeeConnect(String id, String host, int port, int max) {
      this.id = id;
      this.host = host;
      this.port = port;
      this.max = max;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeString(this.id);
      buf.writeString(this.host);
      buf.writeInt(this.port);
      buf.writeInt(this.max);
   }

   public void read0(Buf buf) throws Exception {
      this.id = buf.readString();
      this.host = buf.readString();
      this.port = buf.readInt();
      this.max = buf.readInt();
   }

   public void process0(PacketHandler handler) {
      handler.handle102BungeeConnect(this);
   }

   public String toString() {
      return super.toString() + "{id=" + this.id + "}";
   }
}
