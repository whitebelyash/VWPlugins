package net.xtrafrancyz.VimeNetwork.impl.lobby;

import net.xtrafrancyz.Core.network.packet.Packet101BukkitUpdateInfo;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;

public class CoreLobby implements Lobby, Runnable {
   private static int task = -1;
   private final VNPlugin plugin;
   private int maxPlayers = -1;
   private String[] menuInfo = new String[0];
   private Lobby.State state;
   private String typeId;
   private ServerType type;
   private int number;
   private boolean needUpdate;

   public CoreLobby(VNPlugin plugin) {
      this.state = Lobby.State.ALLOW_ALL;
      this.needUpdate = true;
      this.plugin = plugin;
      if (task != -1) {
         Bukkit.getScheduler().cancelTask(task);
         task = -1;
      }

      if (plugin.config.lobbyEnabled) {
         task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 5L, 20L);
      }

      String[] split = this.getServerId().split("_", 2);
      this.typeId = split[0];
      this.type = ServerType.byId(split[0]);
      this.number = Integer.parseInt(split[1]);
   }

   public void run() {
      if (this.needUpdate && this.plugin.core.isConnected()) {
         this.forceSend();
      }

   }

   public int getMaxPlayers() {
      return this.maxPlayers != -1 ? this.maxPlayers : Bukkit.getMaxPlayers();
   }

   public void shutdown() {
      Bukkit.getScheduler().cancelTask(task);
      task = -1;
      this.state = Lobby.State.OFFLINE;
      this.forceSend();
   }

   public void forceSend() {
      this.plugin.core.sendPacket(new Packet101BukkitUpdateInfo(this.menuInfo, this.getMaxPlayers(), this.state.getId()));
      this.needUpdate = false;
   }

   public void setMenuText(String... lines) {
      this.menuInfo = U.colored(lines);
      this.needUpdate = true;
   }

   public void setConnectableState(Lobby.State state) {
      if (this.state != state) {
         this.state = state;
         this.needUpdate = true;
      }

   }

   public void setMaxPlayers(int max) {
      if (this.maxPlayers != max) {
         this.maxPlayers = max;
         this.needUpdate = true;
      }

   }

   public String getServerId() {
      return this.plugin.config.lobbyServerId;
   }

   public ServerType getServerType() {
      return this.type;
   }

   public String getServerTypeId() {
      return this.typeId;
   }

   public int getServerNumber() {
      return this.number;
   }

   public String getHost() {
      return this.plugin.config.lobbyServerHost;
   }

   public int getPort() {
      return Bukkit.getPort();
   }
}
