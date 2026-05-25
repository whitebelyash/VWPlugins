package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet55PrivateIgnore extends ResponsePacket {
   public Action action;
   public String username;
   public String target;

   private Packet55PrivateIgnore() {
   }

   public Packet55PrivateIgnore(String username, Action action) {
      this.username = username;
      this.action = action;
   }

   public Packet55PrivateIgnore(String username, Action action, String target) {
      this.username = username;
      this.action = action;
      this.target = target;
   }

   protected void write0(Buf buf) throws Exception {
      buf.write((byte)this.action.ordinal());
      buf.writeString(this.username);
      if (this.action.hasData) {
         buf.writeString(this.target);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.action = Packet55PrivateIgnore.Action.values()[buf.read()];
      this.username = buf.readString();
      if (this.action.hasData) {
         this.target = buf.readString();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle55PrivateIgnore(this);
   }

   public String toString() {
      return super.toString() + "{player=" + this.username + ", action=" + this.action.name() + ", target=" + this.target + "}";
   }

   public static enum Action {
      IGNORE(true),
      UNIGNORE(true);

      boolean hasData = false;

      private Action(boolean hasData) {
         this.hasData = hasData;
      }
   }
}
