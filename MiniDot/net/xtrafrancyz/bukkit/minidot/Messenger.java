package net.xtrafrancyz.bukkit.minidot;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import net.minecraft.server.v1_6_R3.Packet250CustomPayload;
import net.xtrafrancyz.Core.network.packet.Packet7MiniDot;
import net.xtrafrancyz.Core.network.packet.Packet7MiniDot.Action;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.bukkit.minidot.database.MiniDotItem;
import net.xtrafrancyz.bukkit.minidot.database.MiniDotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Messenger {
   static MiniDot plugin;
   public static final String CHANNEL = "MiniDotMod";
   private static final int SET_PLAYER_ITEMS = 10;
   private static final int REMOVE_PLAYER = 20;
   private static final int OPEN_GUI = 100;
   private static final int SET_AVAILABLE_ITEMS = 200;
   private static final int SET_DISCOUNT_ITEMS = 201;
   private static final int REC_PREBUY_ITEM = 500;
   private static final int ASW_PREBUY_ITEM = 501;
   private static final int REC_BUY_ITEM = 502;
   private static final int ASW_BUY_ITEM = 503;
   private static final int REC_WEAR_ITEMS = 504;

   public static void sendAllInfoToPlayer(Player player) {
      for(MiniDotPlayer otherPlayer : plugin.database.players.values()) {
         if (!otherPlayer.dressed.isEmpty()) {
            send(player, packetSetPlayerItems(otherPlayer));
         }
      }

      MiniDot.debug("Sended all info to player " + player.getName());
   }

   public static void sendPlayerInfoToAll(MiniDotPlayer player) {
      Packet250CustomPayload packet = packetSetPlayerItems(player);

      for(Player otherPlayer : Bukkit.getOnlinePlayers()) {
         send(otherPlayer, packet);
      }

      MiniDot.debug("Sended info about player '" + player.username + "' to all");
   }

   public static void sendPlayerInfoToSelf(MiniDotPlayer player) {
      if (!player.available.isEmpty()) {
         send(player.player, packetSetAvailableItems(player));
         if (!player.dressed.isEmpty()) {
            send(player.player, packetSetPlayerItems(player));
         }

         MiniDot.debug("Sended info about himself to player " + player.username);
      } else {
         MiniDot.debug("Player " + player.username + " has no available items");
      }

   }

   public static void removePlayer(MiniDotPlayer player) {
      Packet250CustomPayload packet = write((Writer)((dos) -> {
         dos.writeInt(20);
         dos.writeUTF(player.username);
      }));

      for(Player other : Bukkit.getOnlinePlayers()) {
         send(other, packet);
      }

      MiniDot.debug("Player " + player.username + " has been removed");
   }

   public static void openGuiForPlayer(MiniDotPlayer player) {
      send(player.player, 100);
      MiniDot.debug("Opened gui for player " + player.username);
   }

   public static void sendDiscountsToAll() {
      if (!plugin.database.getDiscountedItems().isEmpty()) {
         Packet250CustomPayload packet = packetDiscountList();

         for(Player player : Bukkit.getOnlinePlayers()) {
            send(player, packet);
         }

      }
   }

   public static void sendDiscountsForPlayer(Player player) {
      if (!plugin.database.getDiscountedItems().isEmpty()) {
         send(player, packetDiscountList());
      }
   }

   public static void recievePacket(MiniDotPlayer pi, byte[] data) {
      try {
         DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
         if (is.available() < 1) {
            plugin.getLogger().info("Recieved empty packet from player");
         } else {
            int operation = is.readInt();
            switch (operation) {
               case 500:
                  int item = is.readInt();
                  MiniDot.debug("Player " + pi.username + " requested info for item " + item);
                  MiniDotItem mdi = plugin.database.getItem(item);
                  if (mdi == null) {
                     plugin.getLogger().info("Player " + pi.username + " requested item that not exists " + item);
                     send(pi.player, 501, -1);
                     sendPlayerInfoToSelf(pi);
                  } else if (pi.available.contains(mdi.id)) {
                     plugin.getLogger().info("Player " + pi.username + " already have item " + item);
                     send(pi.player, 501, -1);
                     sendPlayerInfoToSelf(pi);
                  } else if (mdi.isFree()) {
                     pi.player.sendMessage("Вы не можете купить этот предмет");
                  } else if (mdi.hasDiscount()) {
                     send(pi.player, 501, item, 2, mdi.discount, pi.getCoins(), mdi.price);
                  } else {
                     send(pi.player, 501, item, 1, pi.getCoins(), mdi.price);
                  }
                  break;
               case 501:
               case 503:
               default:
                  MiniDot.debug("Undefined operation: " + operation);
                  break;
               case 502:
                  int item = is.readInt();
                  MiniDot.debug("Player " + pi.username + " wants to buy item " + item);
                  MiniDotItem mdi = plugin.database.getItem(item);
                  if (mdi == null) {
                     plugin.getLogger().info("Player " + pi.username + " requested item that not exists " + item);
                     send(pi.player, 503, -1);
                  } else if (pi.available.contains(mdi.id)) {
                     plugin.getLogger().info("Player " + pi.username + " already have item " + item);
                     send(pi.player, 503, -1);
                     sendPlayerInfoToSelf(pi);
                  } else if (!mdi.isFree()) {
                     int price = mdi.hasDiscount() ? mdi.price * (100 - mdi.discount) / 100 : mdi.price;
                     int state = pi.getCoins() >= price ? 1 : 2;
                     if (state == 1) {
                        pi.takeCoins(price);
                        VimeNetwork.metrics().add("minidot.buy." + mdi.type.name().toLowerCase(), price);
                        plugin.database.unlockItem(pi.player, mdi.id);
                     }

                     send(pi.player, 503, item, state);
                  }
                  break;
               case 504:
                  pi.dressed.clear();
                  byte size = is.readByte();

                  for(byte i = 0; i < size; ++i) {
                     String slot = is.readUTF();
                     int id = is.readInt();
                     if (pi.available.contains(id)) {
                        pi.dressed.put(slot, id);
                     } else {
                        MiniDot.debug("Player " + pi.username + " tried to wear unavailable item " + id);
                     }
                  }

                  if (pi.dressed.size() == MiniDotItem.Slot.values().length) {
                     VimeNetwork.getPlayer(pi.player).getAchievements().complete(Achievement.GLOBAL_TRENDY);
                  }

                  MiniDot.debug("Player " + pi.username + " changed items to: " + pi.dressed);
                  if (VimeNetwork.core().isEnabled()) {
                     VimeNetwork.core().sendPacket(new Packet7MiniDot(pi.getId(), pi.dressed, Action.DRESS));
                  } else {
                     plugin.playerSaver.save(pi);
                  }

                  sendPlayerInfoToAll(pi);
            }
         }
      } catch (Exception ex) {
         plugin.getLogger().log(Level.SEVERE, "Could not parse packet data from player " + pi.username, ex);
      }

   }

   private static Packet250CustomPayload packetDiscountList() {
      return write((Writer)((dos) -> {
         dos.writeInt(201);
         TIntIntMap discounted = plugin.database.getDiscountedItems();
         dos.writeInt(discounted.size());
         TIntIntIterator it = discounted.iterator();

         while(it.hasNext()) {
            it.advance();
            dos.writeInt(it.key());
            dos.writeInt(it.value());
         }

      }));
   }

   private static Packet250CustomPayload packetSetPlayerItems(MiniDotPlayer player) {
      return write((Writer)((dos) -> {
         dos.writeInt(10);
         dos.writeUTF(player.username);
         dos.writeByte(player.dressed.size());

         for(Map.Entry entry : player.dressed.entrySet()) {
            dos.writeUTF((String)entry.getKey());
            dos.writeInt((Integer)entry.getValue());
         }

      }));
   }

   private static Packet250CustomPayload packetSetAvailableItems(MiniDotPlayer player) {
      return write((Writer)((dos) -> {
         dos.writeInt(200);
         dos.writeInt(player.available.size());

         for(int id : player.available.toArray()) {
            dos.writeInt(id);
         }

      }));
   }

   private static Packet250CustomPayload write(int... nums) {
      return write((Writer)((dos) -> {
         for(int i : nums) {
            dos.writeInt(i);
         }

      }));
   }

   private static Packet250CustomPayload write(Writer writer) {
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(baos);
         writer.write(dos);
         dos.flush();
         return new Packet250CustomPayload("MiniDotMod", baos.toByteArray());
      } catch (IOException ex) {
         plugin.getLogger().log(Level.SEVERE, (String)null, ex);
         return new Packet250CustomPayload("MiniDotMod", new byte[0]);
      }
   }

   private static void send(Player player, int... nums) {
      send(player, write(nums));
   }

   private static void send(Player player, Writer writer) {
      send(player, write(writer));
   }

   private static void send(Player player, Packet250CustomPayload packet) {
      U.sendPacket(player, packet);
   }

   private interface Writer {
      void write(DataOutputStream var1) throws IOException;
   }
}
