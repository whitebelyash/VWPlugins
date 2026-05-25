package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet105DaemonConnect extends ResponsePacket {
   public String host;
   public int capacity;

   private Packet105DaemonConnect() {
   }

   public Packet105DaemonConnect(String host, int capacity) {
      this.host = host;
      this.capacity = capacity;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.host);
      buf.writeInt(this.capacity);
   }

   protected void read0(Buf buf) throws Exception {
      this.host = buf.readString();
      this.capacity = buf.readInt();
   }

   protected void process0(PacketHandler handler) {
      handler.handle105DaemonConnect(this);
   }
}
