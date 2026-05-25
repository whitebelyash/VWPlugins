package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet51ChatMessage extends Packet {
   public List receivers;
   public String message;

   private Packet51ChatMessage() {
   }

   public Packet51ChatMessage(List receivers, String message) {
      this.receivers = receivers;
      this.message = message;
   }

   public Packet51ChatMessage(String receiver, String message) {
      this(Collections.singletonList(receiver), message);
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.message);
      buf.writeVarInt(this.receivers.size());

      for(String username : this.receivers) {
         buf.writeString(username);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.message = buf.readString();
      int size = buf.readVarInt();
      this.receivers = new ArrayList(size);

      for(int i = 0; i < size; ++i) {
         this.receivers.add(buf.readString());
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle51ChatMessage(this);
   }

   public String toString() {
      return super.toString() + "{receivers=" + this.receivers + ", message=" + this.message + "}";
   }
}
