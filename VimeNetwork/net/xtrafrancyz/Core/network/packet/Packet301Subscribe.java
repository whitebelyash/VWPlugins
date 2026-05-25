package net.xtrafrancyz.Core.network.packet;

import java.util.Arrays;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet301Subscribe extends Packet {
   public String[] events;

   private Packet301Subscribe() {
   }

   public Packet301Subscribe(String... events) {
      this.events = events;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeVarInt(this.events.length);

      for(String event : this.events) {
         buf.writeString(event);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.events = new String[buf.readVarInt()];

      for(int i = 0; i < this.events.length; ++i) {
         this.events[i] = buf.readString();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle301Subscribe(this);
   }

   public String toString() {
      return super.toString() + "{events=" + Arrays.toString(this.events) + "}";
   }
}
