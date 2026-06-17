/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.packet;

import java.util.function.Consumer;
import net.xtrafrancyz.VimeNetwork.packet.IngoingPacketEvent;
import org.bukkit.plugin.Plugin;

class IngoingListenerInfo {
    public final Plugin plugin;
    public final Consumer<IngoingPacketEvent> listener;

    public IngoingListenerInfo(Plugin plugin, Consumer<IngoingPacketEvent> listener) {
        this.plugin = plugin;
        this.listener = listener;
    }
}

