/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.packet;

import java.util.function.Consumer;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent;
import org.bukkit.plugin.Plugin;

class OutgoingListenerInfo {
    public final Plugin plugin;
    public final Consumer<OutgoingPacketEvent> listener;

    public OutgoingListenerInfo(Plugin plugin, Consumer<OutgoingPacketEvent> listener) {
        this.plugin = plugin;
        this.listener = listener;
    }
}

