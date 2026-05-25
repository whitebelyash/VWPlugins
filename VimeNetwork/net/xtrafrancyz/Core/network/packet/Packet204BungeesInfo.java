package net.xtrafrancyz.Core.network.packet;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet204BungeesInfo extends ResponsePacket {
   public List servers;

   private Packet204BungeesInfo() {
   }

   public Packet204BungeesInfo(List servers) {
      this.servers = servers;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeVarInt((short)this.servers.size());

      for(Data data : this.servers) {
         buf.writeString(data.id);
         buf.writeVarInt(data.online);
      }

   }

   protected void read0(Buf buf) throws Exception {
      int size = buf.readVarInt();
      this.servers = new ArrayList(size);

      for(int i = 0; i < size; ++i) {
         this.servers.add(new Data(buf.readString(), buf.readVarInt()));
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle204BungeesInfo(this);
   }

   public static class Data {
      String id;
      int online;

      public Data(String id, int online) {
         this.id = id;
         this.online = online;
      }
   }
}
