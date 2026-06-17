/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.bukkit.texteria.Texteria2D
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.core;

import net.xtrafrancyz.Core.network.connector.CoreCallback;
import net.xtrafrancyz.Core.network.connector.CoreConnector;
import net.xtrafrancyz.Core.network.packet.Packet;
import net.xtrafrancyz.Core.network.packet.Packet100BukkitConnect;
import net.xtrafrancyz.Core.network.packet.Packet4PlayerChangeServer;
import net.xtrafrancyz.Core.network.packet.ResponsePacket;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.VTexteria;
import net.xtrafrancyz.VimeNetwork.api.CoreBukkit;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.core.BukkitPacketHandler;
import net.xtrafrancyz.VimeNetwork.core.ConsoleLogHandler;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import net.xtrafrancyz.bukkit.texteria.Texteria2D;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class CoreBukkitImpl
extends CoreConnector
implements CoreBukkit {
    private final VNPlugin plugin;
    ConsoleLogHandler logHandler;

    public CoreBukkitImpl(VNPlugin plugin) {
        super(plugin.getLogger(), plugin.config.coreHost, plugin.config.corePort);
        this.plugin = plugin;
        this.setMainHandler(new BukkitPacketHandler(plugin));
        if (this.isEnabled()) {
            this.logHandler = new ConsoleLogHandler(this);
            Bukkit.getLogger().addHandler(this.logHandler);
        }
        CoreCallback.class.getName();
        this.addConnectListener(() -> {
            Packet100BukkitConnect connect = new Packet100BukkitConnect(VimeNetwork.lobby().getServerId(), VimeNetwork.lobby().getHost(), VimeNetwork.lobby().getPort(), VimeNetwork.lobby().getMaxPlayers());
            this.sendPacket(connect, p -> {}, 2000L, () -> Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)plugin, () -> {
                this.disconnect();
                this.connect();
            }));
            for (Player player : Bukkit.getOnlinePlayers()) {
                VimeNetwork.getPlayer(player);
                this.sendPacket(new Packet4PlayerChangeServer(player.getName()));
            }
            VimeNetwork.lobby().forceSend();
        });
        this.addDisconnectListener(() -> {
            if (!plugin.isEnabled()) {
                return;
            }
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)plugin, () -> {
                for (VPlayer player : VPlayer.PLAYERS.values()) {
                    player.coins = 0;
                    VTexteria.showCoins(player);
                    if (!player.hasParty()) continue;
                    player.party = null;
                    Texteria2D.remove((String)"vn.p", (Player[])new Player[]{player.getBukkitPlayer()});
                }
                plugin.streamMenu.clear();
            });
        });
    }

    public void onDisable() {
        if (this.isEnabled()) {
            this.dispose();
            Bukkit.getLogger().removeHandler(this.logHandler);
        }
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.config.coreEnabled;
    }

    @Override
    public void sendPacket(Packet packet) {
        if (this.isEnabled()) {
            super.sendPacket(packet);
        }
    }

    @Override
    public void sendPacket(ResponsePacket packet, CoreCallback callback, long timeout, Runnable onTimeout) {
        if (this.isEnabled()) {
            super.sendPacket(packet, callback, timeout, onTimeout);
        }
    }
}

