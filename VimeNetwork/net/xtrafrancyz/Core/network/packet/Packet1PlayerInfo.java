package net.xtrafrancyz.Core.network.packet;

import java.util.HashMap;
import java.util.Map;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.Core.network.Buf;
import net.xtrafrancyz.Core.network.PacketHandler;

public class Packet1PlayerInfo extends ResponsePacket {
   public int id;
   public String username;
   public int coins;
   public int exp;
   public String rank = null;
   public String bukkit = null;
   public String bungee = null;
   public Map meta = null;
   public int[] minidotItems = null;
   public Map minidotDressed = null;
   public int[][] stats;
   public int[][] achievements;
   public int queryFlags = 0;
   public CoreByteMap switchData = null;

   private Packet1PlayerInfo() {
   }

   public Packet1PlayerInfo(int id, String username) {
      this.username = username;
      this.id = id;
   }

   public void write0(Buf buf) throws Exception {
      buf.writeInt(this.id);
      buf.writeString(this.username);
      buf.writeInt(this.coins);
      buf.writeInt(this.exp);
      buf.writeShort((short)this.queryFlags);
      if ((this.queryFlags & 4) == 4) {
         buf.writeString(this.rank);
      }

      if ((this.queryFlags & 8) == 8) {
         buf.writeStringNullable(this.bukkit);
      }

      if ((this.queryFlags & 16) == 16) {
         buf.writeStringNullable(this.bungee);
      }

      if ((this.queryFlags & 1) == 1) {
         buf.writeShort((short)this.meta.size());

         for(Map.Entry entry : this.meta.entrySet()) {
            buf.writeString((String)entry.getKey());
            buf.writeString((String)entry.getValue());
         }
      }

      if ((this.queryFlags & 2) == 2) {
         buf.writeShort((short)this.minidotItems.length);

         for(int id : this.minidotItems) {
            buf.writeInt(id);
         }

         buf.write((byte)this.minidotDressed.size());

         for(Map.Entry entry : this.minidotDressed.entrySet()) {
            buf.writeString((String)entry.getKey());
            buf.writeInt((Integer)entry.getValue());
         }
      }

      if ((this.queryFlags & 32) == 32) {
         buf.writeInt(this.stats.length);

         for(int[] stat : this.stats) {
            buf.writeVarInt(stat[0]);
            buf.writeInt(stat[1]);
         }
      }

      if ((this.queryFlags & 64) == 64) {
         buf.writeInt(this.achievements.length);

         for(int[] a : this.achievements) {
            buf.writeVarInt(a[0]);
            buf.writeInt(a[1]);
         }
      }

      if ((this.queryFlags & 128) == 128) {
         buf.writeByteArray(this.switchData.toByteArray());
      }

   }

   public void read0(Buf buf) throws Exception {
      this.id = buf.readInt();
      this.username = buf.readString();
      this.coins = buf.readInt();
      this.exp = buf.readInt();
      this.queryFlags = buf.readShort();
      if ((this.queryFlags & 4) == 4) {
         this.rank = buf.readString();
      }

      if ((this.queryFlags & 8) == 8) {
         this.bukkit = buf.readStringNullable();
      }

      if ((this.queryFlags & 16) == 16) {
         this.bungee = buf.readStringNullable();
      }

      if ((this.queryFlags & 1) == 1) {
         int size = buf.readShort();
         this.meta = new HashMap(16);

         for(int i = 0; i < size; ++i) {
            this.meta.put(buf.readString(), buf.readString());
         }
      }

      if ((this.queryFlags & 2) == 2) {
         int size = buf.readShort();
         this.minidotItems = new int[size];

         for(int i = 0; i < size; ++i) {
            this.minidotItems[i] = buf.readInt();
         }

         size = buf.read();
         this.minidotDressed = new HashMap(8);

         for(int i = 0; i < size; ++i) {
            this.minidotDressed.put(buf.readString(), buf.readInt());
         }
      }

      if ((this.queryFlags & 32) == 32) {
         this.stats = new int[buf.readInt()][];

         for(int i = 0; i < this.stats.length; ++i) {
            this.stats[i] = new int[]{buf.readVarInt(), buf.readInt()};
         }
      }

      if ((this.queryFlags & 64) == 64) {
         this.achievements = new int[buf.readInt()][];

         for(int i = 0; i < this.achievements.length; ++i) {
            this.achievements[i] = new int[]{buf.readVarInt(), buf.readInt()};
         }
      }

      if ((this.queryFlags & 128) == 128) {
         this.switchData = new CoreByteMap(buf.readByteArray());
      }

   }

   public void process0(PacketHandler handler) {
      handler.handle1PlayerInfo(this);
   }

   public String toString() {
      return super.toString() + "{player=" + this.username + "}";
   }
}
