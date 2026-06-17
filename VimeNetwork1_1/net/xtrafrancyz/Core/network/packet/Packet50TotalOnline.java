package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet50TotalOnline extends Packet {
   public int online;

   private Packet50TotalOnline() {
   }

   public Packet50TotalOnline(int online) {
      this.online = online;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeInt(this.online);
   }

   public void read0(Buf buf) throws Exception {
      this.online = buf.readInt();
   }

   public void process0(PacketHandler handler) {
      handler.handle50TotalOnline(this);
   }

   public String toString() {
      return super.toString() + "{online=" + this.online + "}";
   }
}
