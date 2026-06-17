/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  gnu.trove.map.TIntObjectMap
 *  gnu.trove.map.hash.TIntObjectHashMap
 *  net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.function.Consumer;
import net.minecraft.server.v1_6_R3.Packet20NamedEntitySpawn;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLeaveEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerTagChangeEvent;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.packet.OutgoingPacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class TagManager
implements Listener,
Consumer<OutgoingPacketEvent> {
    public TIntObjectMap<NetworkPlayer> idToPlayer = new TIntObjectHashMap();

    public TagManager(VNPlugin plugin) {
        plugin.packets.addOutgoingListener((Plugin)plugin, this);
    }

    @Override
    public void accept(OutgoingPacketEvent event) {
        if (event.getPacket() instanceof Packet20NamedEntitySpawn) {
            Packet20NamedEntitySpawn packet = (Packet20NamedEntitySpawn)event.getPacket();
            NetworkPlayer player = (NetworkPlayer)this.idToPlayer.get(packet.a);
            if (player != null) {
                packet.b = player.getTag().getVisibleName();
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerLoaded(PlayerLoadedEvent event) {
        this.idToPlayer.put(event.getPlayer().getEntityId(), (Object)event.getNetworkPlayer());
        event.getNetworkPlayer().getTag().refresh();
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        this.idToPlayer.remove(event.getPlayer().getEntityId());
    }

    @EventHandler
    public void onPlayerTagChanged(PlayerTagChangeEvent event) {
        Player pl = event.getPlayer();
        for (Player seer : Bukkit.getOnlinePlayers()) {
            if (!seer.canSee(pl)) continue;
            seer.hidePlayer(pl);
            seer.showPlayer(pl);
        }
    }
}

