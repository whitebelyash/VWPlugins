package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet65SessionRemovePlayer extends Packet {
   public String session;
   public int userid;

   public Packet65SessionRemovePlayer() {
   }

   public Packet65SessionRemovePlayer(String session, int userid) {
      this.session = session;
      this.userid = userid;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.session);
      buf.writeInt(this.userid);
   }

   protected void read0(Buf buf) throws Exception {
      this.session = buf.readString();
      this.userid = buf.readInt();
   }

   protected void process0(PacketHandler handler) {
      handler.handle65SessionRemovePlayer(this);
   }
}
