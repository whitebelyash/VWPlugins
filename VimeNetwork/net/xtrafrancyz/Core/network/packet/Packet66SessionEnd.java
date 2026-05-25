package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet66SessionEnd extends Packet {
   public String session;

   public Packet66SessionEnd() {
   }

   public Packet66SessionEnd(String session) {
      this.session = session;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.session);
   }

   protected void read0(Buf buf) throws Exception {
      this.session = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle66SessionEnd(this);
   }
}
