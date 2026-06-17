package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet57StreamStatus extends Packet {
   public String url;
   public String owner;
   public String title;
   public String platform;
   public Status status;
   public int viewers = 0;
   public int duration = -1;

   private Packet57StreamStatus() {
   }

   public Packet57StreamStatus(String url, String owner, Status status, int viewers) {
      this.url = url;
      this.owner = owner;
      this.status = status;
      this.viewers = viewers;
   }

   protected void write0(Buf buf) throws Exception {
      buf.write((byte)this.status.ordinal());
      buf.writeVarInt(this.viewers);
      buf.writeInt(this.duration);
      buf.writeString(this.owner);
      buf.writeString(this.url);
      buf.writeStringNullable(this.title);
      buf.writeString(this.platform);
   }

   protected void read0(Buf buf) throws Exception {
      this.status = Packet57StreamStatus.Status.values()[buf.read()];
      this.viewers = buf.readVarInt();
      this.duration = buf.readInt();
      this.owner = buf.readString();
      this.url = buf.readString();
      this.title = buf.readStringNullable();
      this.platform = buf.readString();
   }

   protected void process0(PacketHandler handler) {
      handler.handle57StreamStatus(this);
   }

   public String toString() {
      return super.toString() + "{owner=" + this.owner + ", url=" + this.url + ", viewers=" + this.viewers + "}";
   }

   public static enum Status {
      ONLINE,
      OFFLINE;
   }
}
