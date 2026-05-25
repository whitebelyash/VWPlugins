package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet64SessionCreate extends ResponsePacket {
   public int[] players;

   public Packet64SessionCreate() {
   }

   public Packet64SessionCreate(int[] players) {
      this.players = players;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeVarInt(this.players.length);

      for(int userid : this.players) {
         buf.writeInt(userid);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.players = new int[buf.readVarInt()];

      for(int i = 0; i < this.players.length; ++i) {
         this.players[i] = buf.readInt();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle64SessionCreate(this);
   }
}
