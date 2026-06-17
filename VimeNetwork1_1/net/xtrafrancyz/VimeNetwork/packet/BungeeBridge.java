package net.xtrafrancyz.VimeNetwork.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.v1_6_R3.Packet250CustomPayload;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeBridge implements PluginMessageListener {
   public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
      if (channel.equals("VimeBungee")) {
         String msg = new String(bytes, StandardCharsets.UTF_8);
         if (msg.startsWith("bcast┋")) {
            Bukkit.broadcastMessage(U.colored(msg.substring(6)));
         }
      } else if (channel.equals("Vime")) {
         try {
            String message = new String(bytes);
            if (message.equals("detected:speedhack")) {
               VimeNetwork.ban(player.getName(), 60, "Обнаружен спидхак", "АнтиЧит");
            }
         } catch (Exception var5) {
         }
      }

   }

   public static void toLobby(Player player) {
      U.sendPacket(player, new Packet250CustomPayload("VimeBungee", "toLobby".getBytes(StandardCharsets.UTF_8)));
   }

   public static void toServer(Player player, String server) {
      ByteArrayOutputStream b = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(b);

      try {
         out.writeUTF("Connect");
         out.writeUTF(server);
         out.flush();
      } catch (IOException var5) {
      }

      U.sendPacket(player, new Packet250CustomPayload("BungeeCord", b.toByteArray()));
   }
}
