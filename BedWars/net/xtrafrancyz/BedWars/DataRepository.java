package net.xtrafrancyz.BedWars;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;

public class DataRepository {
   public void getLeaderboard(Consumer callback) {
      VimeNetwork.mysql().select("SELECT username, userrank, kills, wins, games, bedBreaked FROM lobby_stats.bw_stats ORDER BY wins DESC LIMIT 50", (rs) -> {
         List<String[]> list = new LinkedList();
         int index = 1;

         while(rs.next()) {
            String[] row = new String[]{Integer.toString(index++), Rank.getRank(rs.getString("userrank")).getColor() + rs.getString("username"), Integer.toString(rs.getInt("wins")), Integer.toString(rs.getInt("games")), Integer.toString(rs.getInt("kills")), Integer.toString(rs.getInt("bedBreaked"))};
            list.add(row);
         }

         callback.accept(list);
      });
   }

   public void loadPlayer(PlayerInfo player) {
      player.stats.reset();
      player.isLoaded = false;
      int id = VimeNetwork.getPlayer(player.username).getId();
      VimeNetwork.mysql().select("SELECT * FROM bw_stats WHERE userid = " + id, (rs) -> {
         if (rs.next()) {
            player.stats.kills = rs.getInt("kills");
            player.stats.deaths = rs.getInt("deaths");
            player.stats.wins = rs.getInt("wins");
            player.stats.games = rs.getInt("games");
            player.stats.bedBreaked = rs.getInt("bedBreaked");
            TrailMenu.getPlayer(player.username).setActive(rs.getString("trail"));
         } else {
            VimeNetwork.mysql().query("INSERT INTO bw_stats (userid) VALUES (" + id + ")");
         }

         player.isLoaded = true;
      });
   }

   public void savePlayer(PlayerInfo player) {
      if (player.isLoaded) {
         VimeNetwork.mysql().query("UPDATE bw_stats SET trail = " + TrailMenu.getPlayer(player.username).getActiveMysqlString() + ", kills = " + player.stats.kills + ", deaths = " + player.stats.deaths + ", wins = " + player.stats.wins + ", games = " + player.stats.games + ", bedBreaked = " + player.stats.bedBreaked + " WHERE userid = " + VimeNetwork.getPlayer(player.username).getId());
      }
   }
}
