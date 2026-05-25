package net.xtrafrancyz.Core.network.packet;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet7MiniDot extends Packet {
   public int userid;
   public Map items;
   public int item;
   public Action action;

   private Packet7MiniDot() {
   }

   public Packet7MiniDot(int userid, int item, Action action) {
      if (action != Packet7MiniDot.Action.UNLOCK) {
         throw new IllegalArgumentException();
      } else {
         this.userid = userid;
         this.item = item;
         this.action = action;
      }
   }

   public Packet7MiniDot(int userid, Map items, Action action) {
      if (action != Packet7MiniDot.Action.DRESS) {
         throw new IllegalArgumentException();
      } else {
         this.userid = userid;
         this.items = items;
         this.action = action;
      }
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeInt(this.userid);
      buf.write((byte)this.action.ordinal());
      if (this.action == Packet7MiniDot.Action.DRESS) {
         buf.writeInt(this.items.size());

         for(Map.Entry item : this.items.entrySet()) {
            buf.writeString((String)item.getKey());
            buf.writeInt((Integer)item.getValue());
         }
      } else {
         buf.writeInt(this.item);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.userid = buf.readInt();
      this.action = Packet7MiniDot.Action.values()[buf.read()];
      if (this.action == Packet7MiniDot.Action.DRESS) {
         this.items = new HashMap();
         int size = buf.readInt();

         for(int i = 0; i < size; ++i) {
            this.items.put(buf.readString(), buf.readInt());
         }
      } else {
         this.item = buf.readInt();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle7MiniDot(this);
   }

   public String toString() {
      return super.toString() + "{userid=" + this.userid + ", action=" + this.action + "}";
   }

   public static enum Action {
      DRESS,
      UNLOCK;
   }
}
