/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package net.xtrafrancyz.VimeNetwork.impl.lobby;

import net.xtrafrancyz.Core.network.packet.Packet101BukkitUpdateInfo;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CoreLobby
implements Lobby,
Runnable {
    private static int task = -1;
    private final VNPlugin plugin;
    private int maxPlayers = -1;
    private String[] menuInfo = new String[0];
    private Lobby.State state = Lobby.State.ALLOW_ALL;
    private String typeId;
    private ServerType type;
    private int number;
    private boolean needUpdate = true;

    public CoreLobby(VNPlugin plugin) {
        this.plugin = plugin;
        if (task != -1) {
            Bukkit.getScheduler().cancelTask(task);
            task = -1;
        }
        if (plugin.config.lobbyEnabled) {
            task = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin)plugin, (Runnable)this, 5L, 20L);
        }
        String[] split = this.getServerId().split("_", 2);
        this.typeId = split[0];
        this.type = ServerType.byId(split[0]);
        this.number = Integer.parseInt(split[1]);
    }

    @Override
    public void run() {
        if (this.needUpdate && this.plugin.core.isConnected()) {
            this.forceSend();
        }
    }

    @Override
    public int getMaxPlayers() {
        if (this.maxPlayers != -1) {
            return this.maxPlayers;
        }
        return Bukkit.getMaxPlayers();
    }

    @Override
    public void shutdown() {
        Bukkit.getScheduler().cancelTask(task);
        task = -1;
        this.state = Lobby.State.OFFLINE;
        this.forceSend();
    }

    @Override
    public void forceSend() {
        this.plugin.core.sendPacket(new Packet101BukkitUpdateInfo(this.menuInfo, this.getMaxPlayers(), this.state.getId()));
        this.needUpdate = false;
    }

    @Override
    public void setMenuText(String ... lines) {
        this.menuInfo = U.colored(lines);
        this.needUpdate = true;
    }

    @Override
    public void setConnectableState(Lobby.State state) {
        if (this.state != state) {
            this.state = state;
            this.needUpdate = true;
        }
    }

    @Override
    public void setMaxPlayers(int max) {
        if (this.maxPlayers != max) {
            this.maxPlayers = max;
            this.needUpdate = true;
        }
    }

    @Override
    public String getServerId() {
        return this.plugin.config.lobbyServerId;
    }

    @Override
    public ServerType getServerType() {
        return this.type;
    }

    @Override
    public String getServerTypeId() {
        return this.typeId;
    }

    @Override
    public int getServerNumber() {
        return this.number;
    }

    @Override
    public String getHost() {
        return this.plugin.config.lobbyServerHost;
    }

    @Override
    public int getPort() {
        return Bukkit.getPort();
    }
}

