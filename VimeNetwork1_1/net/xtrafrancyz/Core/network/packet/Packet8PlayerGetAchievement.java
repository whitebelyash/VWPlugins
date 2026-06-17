package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet8PlayerGetAchievement extends Packet {
   public int userid;
   public int achievement;

   private Packet8PlayerGetAchievement() {
   }

   public Packet8PlayerGetAchievement(int userid, int achievement) {
      this.userid = userid;
      this.achievement = achievement;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeInt(this.userid);
      buf.writeVarInt(this.achievement);
   }

   protected void read0(Buf buf) throws Exception {
      this.userid = buf.readInt();
      this.achievement = buf.readVarInt();
   }

   protected void process0(PacketHandler handler) {
      handler.handle8PlayerGetAchievement(this);
   }
}
