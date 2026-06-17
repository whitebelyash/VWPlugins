package net.xtrafrancyz.VimeNetwork.packet;

import net.minecraft.server.v1_6_R3.Connection;
import net.minecraft.server.v1_6_R3.INetworkManager;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.PlayerConnection;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import org.spigotmc.netty.PacketListener;

public class SpigotPacketInjector extends PacketInjector {
   public SpigotPacketInjector() {
      PacketListener.register(new SpigotPacketListener(), VNPlugin.instance());
   }

   private class SpigotPacketListener extends PacketListener {
      private SpigotPacketListener() {
      }

      public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
         if (packet != null && connection instanceof PlayerConnection) {
            boolean cancelled = SpigotPacketInjector.this.handleIngoingPacket(packet, ((PlayerConnection)connection).getPlayer());
            if (cancelled) {
               return null;
            }
         }

         return packet;
      }

      public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
         if (packet != null && connection instanceof PlayerConnection) {
            boolean cancelled = SpigotPacketInjector.this.handleOutgoingPacket(packet, ((PlayerConnection)connection).getPlayer());
            if (cancelled) {
               return null;
            }
         }

         return packet;
      }
   }
}
