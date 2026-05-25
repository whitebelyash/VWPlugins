package net.xtrafrancyz.VimeNetwork;

import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Commons.Leveling;
import net.xtrafrancyz.Core.CoreByteMap;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.event.PlayerLoadedEvent;
import net.xtrafrancyz.VimeNetwork.api.mysql.MysqlThread;
import net.xtrafrancyz.VimeNetwork.api.mysql.SelectCallback;
import net.xtrafrancyz.VimeNetwork.api.mysql.UpdateCallback;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MysqlWorker extends MysqlThread {
   private final VNPlugin plugin;
   private int queryCounter = 0;
   private long queryStartTime = 0L;

   MysqlWorker(VNPlugin plugin) {
      super(plugin, new MysqlThread.MysqlConfigSupplier(() -> plugin.config.mysqlUrl, () -> plugin.config.mysqlUsername, () -> plugin.config.mysqlPassword));
      this.useUnicode();
      this.plugin = plugin;
      SelectCallback.class.getName();
      UpdateCallback.class.getName();
   }

   protected void onConnect() {
      if (!this.plugin.core.isEnabled()) {
         for(Player player : Bukkit.getOnlinePlayers()) {
            this.addLoadPlayer(VPlayer.get(player));
         }

      }
   }

   protected void onDisconnect() {
      if (!this.plugin.core.isEnabled()) {
         for(VPlayer player : VPlayer.PLAYERS.values()) {
            player.coins = 0;
            VTexteria.showCoins(player);
         }

      }
   }

   protected String onPreQuery(String query) {
      if (Debug.MYSQL.isEnabled()) {
         this.queryStartTime = System.nanoTime();
      }

      return query;
   }

   protected void onPostQuery(String query, boolean success) {
      if (success) {
         ++this.queryCounter;
      }

      if (Debug.MYSQL.isEnabled()) {
         Debug.MYSQL.info("- [" + F.formatFloat((float)(System.nanoTime() - this.queryStartTime) / 1000000.0F, 1) + " ms.] " + query);
      }

   }

   public int getExecutedQueries() {
      return this.queryCounter;
   }

   void addLoadPlayer(VPlayer player) {
      if (this.plugin.config.banCheck) {
         this.select("SELECT banto, banreason FROM bans WHERE status = 1 AND username = '" + player.getName() + "'", (rs) -> {
            if (rs.next()) {
               long currtime = System.currentTimeMillis();
               long banto = rs.getLong("banto");
               if (banto <= 0L || banto >= currtime) {
                  String bantime = banto == 0L ? "навсегда" : "на " + (1 + (int)Math.ceil((double)((banto - currtime) / 60000L))) + " мин";
                  String banreason = rs.getString("banreason");
                  this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, () -> player.player.kickPlayer(U.colored("&cВы забанены &e" + bantime + "&c. Причина: &e" + banreason + "&r \nСнять бан можно в Личном кабинете\nhttp://cp.vimeworld.ru")));
                  return;
               }

               this.query("UPDATE bans SET status = 0 WHERE username = '" + player.getName() + "'");
            }

            this.loadPlayer((MysqlPlayer)player);
         });
      } else {
         this.loadPlayer((MysqlPlayer)player);
      }

   }

   private void loadPlayer(MysqlPlayer player) {
      if (player.isOnline()) {
         this.select("SELECT id, coins, exp, status FROM users WHERE username = '" + player.getName() + "'", (rs) -> {
            if (rs.next()) {
               player.id = rs.getInt("id");
               player.rank = Rank.getRank(rs.getString("status"));
               player.coins += rs.getInt("coins");
               player.exp = rs.getInt("exp");
               player.level = Leveling.getLevel(player.exp);
               this.select("SELECT `key`, `value` FROM `users_meta` WHERE `userid` = " + player.getId(), (rs1) -> {
                  while(rs1.next()) {
                     String key = rs1.getString("key");
                     MysqlPlayer.MetaValue value = new MysqlPlayer.MetaValue(rs1.getString("value"));
                     value.saved = true;
                     value.prev = value.value;
                     player.meta.put(key, value);
                  }

                  Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, new LoadFinishRunnable(player));
                  Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                     if (player.isOnline()) {
                        VTexteria.showCoins(player);
                        Bukkit.getPluginManager().callEvent(new PlayerLoadedEvent(player, (CoreByteMap)null));
                     }
                  }, 1L);
               });
            } else {
               this.query("INSERT INTO users (username, coins) VALUES ('" + player.getName() + "', 8000)");
               this.select("SELECT `id` FROM users WHERE username = '" + player.getName() + "'", (rs1) -> {
                  rs1.next();
                  player.id = rs1.getInt("id");
               });
               player.coins = 8000;
            }

         });
      }
   }

   private class LoadFinishRunnable implements Runnable {
      private MysqlPlayer player;

      public LoadFinishRunnable(VPlayer player) {
         this.player = (MysqlPlayer)player;
      }

      public void run() {
         if (this.player.isOnline()) {
            this.player.onMetaLoaded();
            if (this.player.rank != Rank.PLAYER) {
               if (VimeNetwork.features().CHANGE_TAGS.isEnabled() && this.player.rank.has(Rank.VIP)) {
                  this.player.setTag(this.player.rank.getColor() + this.player.username);
               }

               if (VimeNetwork.features().CHANGE_PLAYER_LIST_NAMES.isEnabled()) {
                  String name = this.player.rank.getColor() + this.player.username;
                  if (name.length() > 16) {
                     name = name.substring(0, 15);
                  }

                  this.player.player.setPlayerListName(name);
               }

               this.player.player.setDisplayName(this.player.getPrefixedName());
               this.player.player.addAttachment(MysqlWorker.this.plugin).setPermission("vimeworld." + this.player.rank.name().toLowerCase(), true);
               if (this.player.getMeta("pm-ignore") != null) {
                  this.player.ignoreAll = true;
               }

            }
         }
      }
   }
}
