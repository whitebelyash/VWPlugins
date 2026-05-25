package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet0KeepAlive extends Packet {
   public void write0(Buf buf) throws Exception {
   }

   public void read0(Buf buf) throws Exception {
   }

   public void process0(PacketHandler handler) {
      handler.handle0KeepAlive(this);
   }
}
