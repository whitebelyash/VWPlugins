package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet60PartyInvite extends Packet {
   public String username;
   public String inviter;

   private Packet60PartyInvite() {
   }

   public Packet60PartyInvite(String username, String inviter) {
      this.username = username;
      this.inviter = inviter;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.username);
      buf.writeString(this.inviter);
   }

   protected void read0(Buf buf) throws Exception {
      this.username = buf.readString();
      this.inviter = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle60PartyInvite(this);
   }

   public String toString() {
      return super.toString() + "{inviter=" + this.inviter + ", target=" + this.username + "}";
   }
}
