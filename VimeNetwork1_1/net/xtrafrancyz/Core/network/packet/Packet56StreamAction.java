package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet56StreamAction extends Packet {
   public String url;
   public String sender;
   public Action action;

   private Packet56StreamAction() {
   }

   public Packet56StreamAction(String sender, String url, Action action) {
      this.sender = sender;
      this.url = url;
      this.action = action;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.sender);
      buf.writeString(this.url);
      buf.write((byte)this.action.ordinal());
   }

   protected void read0(Buf buf) throws Exception {
      this.sender = buf.readString();
      this.url = buf.readString();
      this.action = Packet56StreamAction.Action.values()[buf.read()];
   }

   protected void process0(PacketHandler handler) {
      handler.handle56StreamAction(this);
   }

   public String toString() {
      return super.toString() + "{action=" + this.action + ", url=" + this.url + "}";
   }

   public static enum Action {
      ADD,
      REMOVE;
   }
}
