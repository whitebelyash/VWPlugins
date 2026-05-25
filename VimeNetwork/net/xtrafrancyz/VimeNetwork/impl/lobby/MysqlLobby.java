package net.xtrafrancyz.VimeNetwork.impl.lobby;

import com.google.common.base.Joiner;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.util.Reflect;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;

public class MysqlLobby implements Lobby, Runnable {
   private static int task = -1;
   private final VNPlugin plugin;
   private final Class vimeGuardConfigClazz = Reflect.findClass("vimeworld.VimeGuard.Config");
   private int maxPlayers = -1;
   private String menuInfo = "NULL";
   private Lobby.State state;
   private String typeId;
   private ServerType type;
   private int number;

   public MysqlLobby(VNPlugin plugin) {
      this.state = Lobby.State.ALLOW_ALL;
      this.plugin = plugin;
      if (task != -1) {
         Bukkit.getScheduler().cancelTask(task);
         task = -1;
      }

      if (plugin.config.lobbyEnabled) {
         task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 5L, 30L);
         plugin.mysql.query("INSERT IGNORE INTO servers (id, port) VALUES ('" + plugin.config.lobbyServerId + "', " + Bukkit.getPort() + ")");
      }

      String[] split = this.getServerId().split("_", 2);
      this.typeId = split[0];
      this.type = ServerType.byId(split[0]);
      this.number = Integer.parseInt(split[1]);
   }

   public void run() {
      this.send(System.currentTimeMillis() / 1000L);
   }

   public int getMaxPlayers() {
      if (this.maxPlayers != -1) {
         return this.maxPlayers;
      } else {
         return this.vimeGuardConfigClazz != null ? (Integer)Reflect.get(this.vimeGuardConfigClazz, "maxPlayers") : Bukkit.getMaxPlayers();
      }
   }

   private int getOnlinePlayers() {
      return Bukkit.getOnlinePlayers().length;
   }

   private void send(long updateTime) {
      this.plugin.mysql.query("UPDATE servers SET updated = " + updateTime + ",max = " + this.getMaxPlayers() + ",online = " + this.getOnlinePlayers() + ",menu_status = " + this.menuInfo + ",connectable = " + this.state.getId() + " WHERE id = '" + this.plugin.config.lobbyServerId + "'");
   }

   public void shutdown() {
      Bukkit.getScheduler().cancelTask(task);
      task = -1;
      this.send(0L);
   }

   public void forceSend() {
      this.send(System.currentTimeMillis());
   }

   public void setMenuText(String... lines) {
      if (lines != null && lines.length != 0) {
         this.menuInfo = "'" + StringEscapeUtils.escapeSql(Joiner.on("^").join(lines)) + "'";
      } else {
         this.menuInfo = "NULL";
      }

   }

   public void setConnectableState(Lobby.State state) {
      this.state = state;
   }

   public void setMaxPlayers(int max) {
      this.maxPlayers = max;
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
