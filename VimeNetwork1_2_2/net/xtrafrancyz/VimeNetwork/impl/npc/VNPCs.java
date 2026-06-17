/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  net.minecraft.server.v1_6_R3.Packet7UseEntity
 *  org.bukkit.Bukkit
 *  org.bukkit.Location
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Event
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.server.PluginDisableEvent
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.stream.IntStream;
import net.minecraft.server.v1_6_R3.Packet7UseEntity;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.event.NPCInteractEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.npc.NPC;
import net.xtrafrancyz.VimeNetwork.api.npc.NPCs;
import net.xtrafrancyz.VimeNetwork.impl.npc.VNPC;
import net.xtrafrancyz.VimeNetwork.packet.IngoingPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

public class VNPCs
implements NPCs,
Listener {
    private final TIntObjectMap<VNPC> map = new TIntObjectHashMap();
    private boolean enabled = false;

    public VNPCs() {
        VNPlugin.instance().packets.addIngoingListener((Plugin)VNPlugin.instance(), this::onIngoingPacket);
        Bukkit.getPluginManager().registerEvents((Listener)this, (Plugin)VNPlugin.instance());
        Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)VNPlugin.instance(), this::trackNpcs, 20L, 20L);
    }

    @EventHandler
    private void onPluginDisabled(PluginDisableEvent event) {
        if (event.getPlugin().equals((Object)VNPlugin.instance())) {
            for (VNPC npc : (VNPC[])this.map.values((Object[])new VNPC[this.map.size()])) {
                for (Player player : npc.visibleTo) {
                    npc.sendRemovePacket(player);
                }
            }
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerLeaveEvent event) {
        if (!this.enabled) {
            return;
        }
        for (VNPC npc : (VNPC[])this.map.values((Object[])new VNPC[this.map.size()])) {
            npc.visibleTo.remove(event.getPlayer());
        }
    }

    private void onIngoingPacket(IngoingPacketEvent event) {
        if (!this.enabled) {
            return;
        }
        if (event.getPacket().n() == 7) {
            Packet7UseEntity packet = (Packet7UseEntity)event.getPacket();
            VNPC npc = (VNPC)this.map.get(packet.target);
            if (npc != null) {
                Bukkit.getPluginManager().callEvent((Event)new NPCInteractEvent(event.getSender(), npc, NPCInteractEvent.Action.values()[packet.action]));
                event.setCancelled(true);
            }
        }
    }

    private void trackNpcs() {
        if (!this.enabled) {
            return;
        }
        VNPC[] npcs = (VNPC[])this.map.values((Object[])new VNPC[this.map.size()]);
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLoc = player.getLocation();
            for (VNPC npc : npcs) {
                if (npc.distanceSquared(playerLoc) <= (double)(npc.viewDistance * npc.viewDistance)) {
                    if (npc.visibleTo.contains(player)) continue;
                    npc.sendSpawnPacket(player);
                    npc.visibleTo.add(player);
                    continue;
                }
                if (!npc.visibleTo.contains(player)) continue;
                npc.sendRemovePacket(player);
                npc.visibleTo.remove(player);
            }
        }
    }

    @Override
    public NPC create(String name, Location loc) {
        VNPC npc = new VNPC(name, loc);
        this.map.put(npc.getEntityId(), (Object)npc);
        this.enabled = true;
        return npc;
    }

    @Override
    public void remove(int id) {
        VNPC npc = (VNPC)this.map.remove(id);
        if (npc != null) {
            for (Player player : npc.visibleTo) {
                npc.sendRemovePacket(player);
            }
            npc.visibleTo.clear();
            if (this.map.isEmpty()) {
                this.enabled = false;
            }
        }
    }

    @Override
    public NPC get(int id) {
        return (NPC)this.map.get(id);
    }

    @Override
    public void reset() {
        IntStream.of(this.map.keys()).forEach(this::remove);
    }
}

