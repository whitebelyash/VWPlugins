package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet304ConsoleLog extends Packet {
   public String message;

   private Packet304ConsoleLog() {
   }

   public Packet304ConsoleLog(String message) {
      this.message = message;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.message);
   }

   protected void read0(Buf buf) throws Exception {
      this.message = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle304ConsoleLog(this);
   }
}
