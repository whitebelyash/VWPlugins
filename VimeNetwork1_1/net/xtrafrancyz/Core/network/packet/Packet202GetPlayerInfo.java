package net.xtrafrancyz.Core.network.packet;

import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet202GetPlayerInfo extends ResponsePacket {
   public static final int FLAG_META = 1;
   public static final int FLAG_MINIDOT = 2;
   public static final int FLAG_RANK = 4;
   public static final int FLAG_LOC_BUKKIT = 8;
   public static final int FLAG_LOC_BUNGEE = 16;
   public static final int FLAG_STATS = 32;
   public static final int FLAG_ACHIEVEMENTS = 64;
   public static final int FLAG_SWITCH_DATA = 128;
   public static final int FLAG_ALL = 127;
   public String username;
   public int queryFlags = 0;

   private Packet202GetPlayerInfo() {
   }

   public Packet202GetPlayerInfo(String username, int flags) {
      this.username = username;
      this.queryFlags = flags;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeString(this.username);
      buf.writeShort((short)this.queryFlags);
   }

   public void read0(Buf buf) throws Exception {
      this.username = buf.readString();
      this.queryFlags = buf.readShort();
   }

   public void process0(PacketHandler handler) {
      handler.handle202GetPlayerInfo(this);
   }

   public String toString() {
      return super.toString() + "{player=" + this.username + ", flags=" + Integer.toBinaryString(this.queryFlags) + "}";
   }
}
