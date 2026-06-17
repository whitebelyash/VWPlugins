package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet303ProtocolCheck extends ResponsePacket {
   public int version;

   private Packet303ProtocolCheck() {
   }

   public Packet303ProtocolCheck(int version) {
      this.version = version;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeInt(this.version);
   }

   protected void read0(Buf buf) throws Exception {
      this.version = buf.readInt();
   }

   protected void process0(PacketHandler handler) {
      handler.handle303ProtocolCheck(this);
   }

   public String toString() {
      return super.toString() + "{version=" + this.version + "}";
   }
}
