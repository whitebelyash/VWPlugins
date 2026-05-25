package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;

public abstract class ResponsePacket extends Packet {
   public int pResponseId = -1;

   public void write(Buf buf) throws Exception {
      super.write(buf);
      buf.writeInt(this.pResponseId);
   }

   public void read(Buf buf) throws Exception {
      super.read(buf);
      this.pResponseId = buf.readInt();
   }
}
