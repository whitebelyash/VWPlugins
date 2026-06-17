package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet2PlayerConnect extends Packet {
   public String username;

   private Packet2PlayerConnect() {
   }

   public Packet2PlayerConnect(String username) {
      this.username = username;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeString(this.username);
   }

   public void read0(Buf buf) throws Exception {
      this.username = buf.readString();
   }

   public void process0(PacketHandler handler) {
      handler.handle2PlayerConnect(this);
   }

   public String toString() {
      return super.toString() + "{player=" + this.username + "}";
   }
}
