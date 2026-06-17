package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet68Report extends ResponsePacket {
   public String reporter;
   public String violator;
   public Type type;
   public String details;

   public Packet68Report() {
   }

   public Packet68Report(String reporter, String violator, Type type) {
      if (type.needDetails) {
         throw new IllegalArgumentException("This report type need details");
      } else {
         this.reporter = reporter;
         this.violator = violator;
         this.type = type;
      }
   }

   public Packet68Report(String reporter, String violator, Type type, String details) {
      this.reporter = reporter;
      this.violator = violator;
      this.type = type;
      this.details = details;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.reporter);
      buf.writeString(this.violator);
      buf.write((byte)this.type.ordinal());
      if (this.type.needDetails) {
         buf.writeString(this.details);
      }

   }

   protected void read0(Buf buf) throws Exception {
      this.reporter = buf.readString();
      this.violator = buf.readString();
      this.type = Packet68Report.Type.values()[buf.read()];
      if (this.type.needDetails) {
         this.details = buf.readString();
      }

   }

   protected void process0(PacketHandler handler) {
      handler.handle68Report(this);
   }

   public String toString() {
      return super.toString() + "{reporter=" + this.reporter + ", violator=" + this.violator + ", type=" + this.type + ", details=" + this.details + "}";
   }

   public static enum Type {
      CHAT(false),
      RETARD(true),
      CHEAT(true);

      boolean needDetails;

      private Type(boolean needDetails) {
         this.needDetails = needDetails;
      }
   }
}
