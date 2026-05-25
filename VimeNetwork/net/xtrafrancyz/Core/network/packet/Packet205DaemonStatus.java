package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet205DaemonStatus extends Packet {
   private Packet205DaemonStatus() {
   }

   protected void write0(Buf buf) throws Exception {
   }

   protected void read0(Buf buf) throws Exception {
   }

   protected void process0(PacketHandler handler) {
      handler.handle205DaemonStatus(this);
   }
}
