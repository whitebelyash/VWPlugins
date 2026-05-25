package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet62FriendAction extends Packet {
   public int userid;
   public Action action;
   public String target;

   private Packet62FriendAction() {
   }

   public Packet62FriendAction(int userid, Action action) {
      this(userid, action, (String)null);
   }

   public Packet62FriendAction(int userid, Action action, String target) {
      this.userid = userid;
      this.action = action;
      this.target = target;
      if (action.hasTarget && target == null) {
         throw new IllegalArgumentException("Action " + action + " require target");
      }
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeInt(this.userid);
      buf.write((byte)this.action.ordinal());
      if (this.action.hasTarget) {
         buf.writeString(this.target);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.userid = buf.readInt();
      this.action = Packet62FriendAction.Action.values()[buf.read()];
      if (this.action.hasTarget) {
         this.target = buf.readString();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle62FriendAction(this);
   }

   public String toString() {
      return super.toString() + "{userid=" + this.userid + ", action=" + this.action + "}";
   }

   public static enum Action {
      ADD(true),
      ACCEPT(true),
      LIST(true),
      DENY(true),
      REMOVE(true);

      boolean hasTarget;

      private Action(boolean hasTarget) {
         this.hasTarget = hasTarget;
      }
   }
}
