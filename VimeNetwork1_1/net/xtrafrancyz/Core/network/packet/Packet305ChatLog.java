package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet305ChatLog extends Packet {
   public String username;
   public String message;

   public Packet305ChatLog() {
   }

   public Packet305ChatLog(String username, String message) {
      this.username = username;
      this.message = message;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.username);
      buf.writeString(this.message);
   }

   protected void read0(Buf buf) throws Exception {
      this.username = buf.readString();
      this.message = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle305ChatLog(this);
   }

   public String toString() {
      return super.toString() + "{username=" + this.username + ", message=" + this.message + "}";
   }
}
