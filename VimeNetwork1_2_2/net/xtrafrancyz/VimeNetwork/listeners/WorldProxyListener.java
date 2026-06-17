/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.v1_6_R3.WorldServer
 *  org.bukkit.Chunk
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.world.ChunkUnloadEvent
 *  org.bukkit.event.world.WorldLoadEvent
 *  org.bukkit.event.world.WorldUnloadEvent
 */
package net.xtrafrancyz.VimeNetwork.listeners;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.server.v1_6_R3.WorldServer;
import net.xtrafrancyz.VimeNetwork.api.entity.NMSEntityUtils;
import net.xtrafrancyz.VimeNetwork.impl.world.proxy.NmsWorldProxy;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldProxyListener
implements Listener {
    private Map<String, Map<String, NmsWorldProxy>> proxied;
    private Map<String, NmsWorldProxy> proxies = new HashMap<String, NmsWorldProxy>();

    public WorldProxyListener() {
        this.proxied = new HashMap<String, Map<String, NmsWorldProxy>>();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        WorldServer nmsWorld = NMSEntityUtils.getNMSWorld(event.getWorld());
        if (nmsWorld instanceof NmsWorldProxy) {
            NmsWorldProxy proxy = (NmsWorldProxy)nmsWorld;
            this.proxies.put(event.getWorld().getName(), proxy);
            this.proxied.computeIfAbsent(proxy.proxied.getWorld().getName(), n -> new LinkedHashMap()).put(event.getWorld().getName(), proxy);
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        Map<String, NmsWorldProxy> map;
        NmsWorldProxy removed = this.proxies.remove(event.getWorld().getName());
        if (removed != null && (map = this.proxied.get(removed.proxied.getWorld().getName())) != null && map.remove(event.getWorld().getName()) != null && map.isEmpty()) {
            this.proxied.remove(removed.proxied.getWorld().getName());
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        Map<String, NmsWorldProxy> map = this.proxied.get(event.getWorld().getName());
        if (map != null) {
            Chunk chunk = event.getChunk();
            for (NmsWorldProxy proxy : map.values()) {
                if (!proxy.isChunkLoaded(chunk.getX(), chunk.getZ())) continue;
                event.setCancelled(true);
                return;
            }
        }
    }
}

