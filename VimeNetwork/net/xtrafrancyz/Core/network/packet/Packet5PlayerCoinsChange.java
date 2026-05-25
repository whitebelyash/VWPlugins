package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet5PlayerCoinsChange extends Packet {
   public int[] users;
   public int change;

   private Packet5PlayerCoinsChange() {
   }

   public Packet5PlayerCoinsChange(int userid, int change) {
      this(new int[]{userid}, change);
   }

   public Packet5PlayerCoinsChange(int[] users, int change) {
      this.users = users;
      this.change = change;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeSignedVarInt(this.change);
      buf.writeVarInt(this.users.length);

      for(int id : this.users) {
         buf.writeInt(id);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.change = buf.readSignedVarInt();
      this.users = new int[buf.readVarInt()];

      for(int i = 0; i < this.users.length; ++i) {
         this.users[i] = buf.readInt();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle5PlayerCoinsChange(this);
   }

   public String toString() {
      return super.toString() + "{users=" + Arrays.toString(this.users) + ", coins=" + this.change + "}";
   }
}
