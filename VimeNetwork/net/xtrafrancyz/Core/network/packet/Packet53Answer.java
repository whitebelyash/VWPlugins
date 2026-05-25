package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet53Answer extends ResponsePacket {
   public String status;

   private Packet53Answer() {
   }

   public Packet53Answer(String status) {
      this.status = status;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.status);
   }

   protected void read0(Buf buf) throws Exception {
      this.status = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle53Answer(this);
   }

   public String toString() {
      return super.toString() + "{status=" + this.status + "}";
   }
}
