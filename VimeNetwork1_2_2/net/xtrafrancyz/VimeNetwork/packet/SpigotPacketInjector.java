/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Connection
 *  net.minecraft.server.v1_6_R3.INetworkManager
 *  net.minecraft.server.v1_6_R3.Packet
 *  net.minecraft.server.v1_6_R3.PlayerConnection
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 *  org.spigotmc.netty.PacketListener
 */
package net.xtrafrancyz.VimeNetwork.packet;

import net.minecraft.server.v1_6_R3.Connection;
import net.minecraft.server.v1_6_R3.INetworkManager;
import net.minecraft.server.v1_6_R3.Packet;
import net.minecraft.server.v1_6_R3.PlayerConnection;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.packet.PacketInjector;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.spigotmc.netty.PacketListener;

public class SpigotPacketInjector
extends PacketInjector {
    public SpigotPacketInjector() {
        PacketListener.register((PacketListener)new SpigotPacketListener(), (Plugin)VNPlugin.instance());
    }

    private class SpigotPacketListener
    extends PacketListener {
        private SpigotPacketListener() {
        }

        public Packet packetReceived(INetworkManager networkManager, Connection connection, Packet packet) {
            boolean cancelled;
            if (packet != null && connection instanceof PlayerConnection && (cancelled = SpigotPacketInjector.this.handleIngoingPacket(packet, (Player)((PlayerConnection)connection).getPlayer()))) {
                return null;
            }
            return packet;
        }

        public Packet packetQueued(INetworkManager networkManager, Connection connection, Packet packet) {
            boolean cancelled;
            if (packet != null && connection instanceof PlayerConnection && (cancelled = SpigotPacketInjector.this.handleOutgoingPacket(packet, (Player)((PlayerConnection)connection).getPlayer()))) {
                return null;
            }
            return packet;
        }
    }
}

