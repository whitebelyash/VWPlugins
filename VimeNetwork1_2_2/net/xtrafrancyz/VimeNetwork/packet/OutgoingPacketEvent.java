/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet
 *  org.bukkit.entity.Player
 */
package net.xtrafrancyz.VimeNetwork.packet;

import net.minecraft.server.v1_6_R3.Packet;
import org.bukkit.entity.Player;

public class OutgoingPacketEvent {
    private boolean cancelled = false;
    private Player sender;
    private Packet packet;

    public OutgoingPacketEvent(Player sender, Packet packet) {
        this.sender = sender;
        this.packet = packet;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Player getReceiver() {
        return this.sender;
    }

    public Packet getPacket() {
        return this.packet;
    }
}

