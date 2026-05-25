package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet203GetBungeesInfo extends ResponsePacket {
   protected void write0(Buf buf) throws Exception {
   }

   protected void read0(Buf buf) throws Exception {
   }

   protected void process0(PacketHandler handler) {
      handler.handle203GetBungeesInfo(this);
   }
}
