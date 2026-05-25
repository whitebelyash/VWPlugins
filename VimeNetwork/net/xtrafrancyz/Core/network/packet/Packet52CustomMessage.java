package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet52CustomMessage extends ResponsePacket {
   public String tag;
   public Scope scope;
   public String receiver;
   public CoreByteMap data;

   private Packet52CustomMessage() {
      this.scope = Packet52CustomMessage.Scope.ALL;
      this.receiver = null;
      this.data = new CoreByteMap();
   }

   public Packet52CustomMessage(String tag) {
      this.scope = Packet52CustomMessage.Scope.ALL;
      this.receiver = null;
      this.data = new CoreByteMap();
      this.tag = tag;
   }

   public Packet52CustomMessage(String tag, Scope scope) {
      this.scope = Packet52CustomMessage.Scope.ALL;
      this.receiver = null;
      this.data = new CoreByteMap();
      this.tag = tag;
      this.scope = scope;
   }

   public Packet52CustomMessage(String tag, Scope scope, String receiver) {
      this.scope = Packet52CustomMessage.Scope.ALL;
      this.receiver = null;
      this.data = new CoreByteMap();
      this.tag = tag;
      this.scope = scope;
      this.receiver = receiver;
   }

   public Packet52CustomMessage put(String key, Object value) {
      this.data.put(key, value);
      return this;
   }

   protected void write0(Buf buf) throws Exception {
      buf.writeString(this.tag);
      buf.write((byte)this.scope.ordinal());
      if (this.scope.hasData) {
         buf.writeString(this.receiver);
      }

      buf.writeByteArray(this.data.toByteArray());
   }

   protected void read0(Buf buf) throws Exception {
      this.tag = buf.readString();
      this.scope = Packet52CustomMessage.Scope.values()[buf.read()];
      if (this.scope.hasData) {
         this.receiver = buf.readString();
      }

      this.data = new CoreByteMap(buf.readByteArray());
   }

   protected void process0(PacketHandler handler) {
      handler.handle52CustomMessage(this);
   }

   public String toString() {
      return super.toString() + "{tag=" + this.tag + ", scope=" + this.scope.name() + ", dataSize=" + this.data.size() + "}";
   }

   public static enum Scope {
      ALL(false),
      ALL_BUNGEE(false),
      ALL_BUKKIT(false),
      SELECTED_BUNGEE(true),
      SELECTED_BUKKIT(true),
      BUNGEE_OF_PLAYER(true),
      BUKKIT_OF_PLAYER(true),
      CORE(false);

      boolean hasData;

      private Scope(boolean hasData) {
         this.hasData = hasData;
      }
   }
}
