package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet302Unsubscribe extends Packet {
   public String event;

   private Packet302Unsubscribe() {
   }

   public Packet302Unsubscribe(String event) {
      this.event = event;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.event);
   }

   protected void read0(Buf buf) throws Exception {
      this.event = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle302Unsubscribe(this);
   }

   public String toString() {
      return super.toString() + "{event=" + this.event + "}";
   }
}
