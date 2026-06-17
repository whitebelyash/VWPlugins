/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.Packet
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.packet;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Packet;
import net.xtrafrancyz.VimeNetwork.packet.IngoingListenerInfo;
import net.xtrafrancyz.VimeNetwork.packet.IngoingPacketEvent;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingListenerInfo;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public abstract class PacketInjector
implements Listener {
    private List<OutgoingListenerInfo> outgoingListeners = new LinkedList<OutgoingListenerInfo>();
    private List<IngoingListenerInfo> ingoingListeners = new LinkedList<IngoingListenerInfo>();

    public void addOutgoingListener(Plugin plugin, Consumer<OutgoingPacketEvent> listener) {
        this.removeOutgoingListener(listener);
        this.outgoingListeners.add(new OutgoingListenerInfo(plugin, listener));
    }

    public void addIngoingListener(Plugin plugin, Consumer<IngoingPacketEvent> listener) {
        this.removeIngoingListener(listener);
        this.ingoingListeners.add(new IngoingListenerInfo(plugin, listener));
    }

    public void removeOutgoingListener(Consumer<OutgoingPacketEvent> listener) {
        Iterator<OutgoingListenerInfo> it = this.outgoingListeners.iterator();
        while (it.hasNext()) {
            if (it.next().listener != listener) continue;
            it.remove();
        }
    }

    public void removeIngoingListener(Consumer<IngoingPacketEvent> listener) {
        Iterator<IngoingListenerInfo> it = this.ingoingListeners.iterator();
        while (it.hasNext()) {
            if (it.next().listener != listener) continue;
            it.remove();
        }
    }

    boolean handleOutgoingPacket(Packet obj, Player destination) {
        OutgoingPacketEvent event = new OutgoingPacketEvent(destination, obj);
        for (OutgoingListenerInfo listener : this.outgoingListeners) {
            try {
                listener.listener.accept(event);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event.isCancelled();
    }

    boolean handleIngoingPacket(Packet packet, Player sender) {
        IngoingPacketEvent event = new IngoingPacketEvent(sender, packet);
        for (IngoingListenerInfo listener : this.ingoingListeners) {
            try {
                listener.listener.accept(event);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return event.isCancelled();
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        this.outgoingListeners.removeIf(info -> info.plugin.equals(event.getPlugin()));
    }
}

