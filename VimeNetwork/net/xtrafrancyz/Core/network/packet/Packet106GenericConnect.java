package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet106GenericConnect extends ResponsePacket {
   public String name;

   private Packet106GenericConnect() {
   }

   public Packet106GenericConnect(String name) {
      this.name = name;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.name);
   }

   protected void read0(Buf buf) throws Exception {
      this.name = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle106GenericConnect(this);
   }
}
