package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet67SessionInfo extends Packet {
   public int userid;
   public String session;
   public String server;

   public Packet67SessionInfo() {
   }

   public Packet67SessionInfo(int userid, String session, String server) {
      this.userid = userid;
      this.session = session;
      this.server = server;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeInt(this.userid);
      buf.writeStringNullable(this.session);
      buf.writeStringNullable(this.server);
   }

   protected void read0(Buf buf) throws Exception {
      this.userid = buf.readInt();
      this.session = buf.readStringNullable();
      this.server = buf.readStringNullable();
   }

   protected void process0(PacketHandler handler) {
      handler.handle67SessionInfo(this);
   }
}
