package net.xtrafrancyz.VimeNetwork.tasks;

import java.util.Collection;
import java.util.Map;
import net.xtrafrancyz.VimeNetwork.VNPlugin;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.apache.commons.lang.StringEscapeUtils;

public class PlayerMetaSaver implements Runnable {
   private final VNPlugin plugin;

   public PlayerMetaSaver(VNPlugin plugin) {
      this.plugin = plugin;
   }

   public void run() {
      long time = System.currentTimeMillis();

      for(MysqlPlayer player : this.getAllPlayers()) {
         for(Map.Entry entry : player.meta.entrySet()) {
            if (!((MysqlPlayer.MetaValue)entry.getValue()).saved && time - ((MysqlPlayer.MetaValue)entry.getValue()).changed > 5000L) {
               this.save(player, entry);
            }
         }
      }

   }

   private Collection getAllPlayers() {
      return VPlayer.PLAYERS.values();
   }

   public void saveNow(MysqlPlayer player) {
      for(Map.Entry entry : player.meta.entrySet()) {
         if (!((MysqlPlayer.MetaValue)entry.getValue()).saved) {
            this.save(player, entry);
         }
      }

   }

   public void finish() {
      this.getAllPlayers().forEach(this::saveNow);
   }

   private void save(MysqlPlayer player, Map.Entry entry) {
      MysqlPlayer.MetaValue value = (MysqlPlayer.MetaValue)entry.getValue();
      if (value.value == null) {
         if (value.prev != null) {
            this.plugin.mysql.query("DELETE FROM `users_meta` WHERE `userid` = " + player.id + " AND `key` = '" + (String)entry.getKey() + "'");
         }

         player.meta.remove(entry.getKey());
      } else {
         String escaped = StringEscapeUtils.escapeSql(value.value);
         if (value.prev == null) {
            this.plugin.mysql.query("INSERT INTO `users_meta` (`userid`, `key`, `value`) VALUES (" + player.id + ", '" + (String)entry.getKey() + "', '" + escaped + "')");
         } else if (!value.value.equals(value.prev)) {
            this.plugin.mysql.query("UPDATE `users_meta` SET `value` = '" + escaped + "' WHERE userid = " + player.id + " AND `key` = '" + (String)entry.getKey() + "'");
         }

         value.prev = value.value;
         value.saved = true;
      }

   }
}
