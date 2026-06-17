/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.Packet250CustomPayload
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.messaging.PluginMessageListener
 */
package net.xtrafrancyz.VimeNetwork.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.Packet250CustomPayload;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeBridge
implements PluginMessageListener {
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (channel.equals("VimeBungee")) {
            String msg = new String(bytes, StandardCharsets.UTF_8);
            if (msg.startsWith("bcast\u250b")) {
                Bukkit.broadcastMessage((String)U.colored(msg.substring(6)));
            }
        } else if (channel.equals("Vime")) {
            try {
                String message = new String(bytes);
                if (message.equals("detected:speedhack")) {
                    VimeNetwork.ban(player.getName(), 60, "\u041e\u0431\u043d\u0430\u0440\u0443\u0436\u0435\u043d \u0441\u043f\u0438\u0434\u0445\u0430\u043a", "\u0410\u043d\u0442\u0438\u0427\u0438\u0442");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public static void toLobby(Player player) {
        U.sendPacket(player, (Packet)new Packet250CustomPayload("VimeBungee", "toLobby".getBytes(StandardCharsets.UTF_8)));
    }

    public static void toServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
            out.flush();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        U.sendPacket(player, (Packet)new Packet250CustomPayload("BungeeCord", b.toByteArray()));
    }
}

