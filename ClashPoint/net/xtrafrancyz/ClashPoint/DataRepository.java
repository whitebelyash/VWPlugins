/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.Rank
 */
package net.xtrafrancyz.ClashPoint;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.xtrafrancyz.ClashPoint.object.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;

public class DataRepository {
    public void getLeaderboard(Consumer<List<String[]>> callback) {
        VimeNetwork.mysql().select("SELECT username, userrank, kills, wins, games, resourcePointsBreaked FROM lobby_stats.cp_stats ORDER BY wins DESC LIMIT 50", rs -> {
            LinkedList<String[]> list = new LinkedList<String[]>();
            int index = 1;
            while (rs.next()) {
                String[] row = new String[]{Integer.toString(index++), Rank.getRank((String)rs.getString("userrank")).getColor() + rs.getString("username"), Integer.toString(rs.getInt("wins")), Integer.toString(rs.getInt("games")), Integer.toString(rs.getInt("kills")), Integer.toString(rs.getInt("resourcePointsBreaked"))};
                list.add(row);
            }
            callback.accept(list);
        });
    }

    public void loadPlayer(PlayerInfo player) {
        player.stats.reset();
        player.isLoaded = false;
        int id = VimeNetwork.getPlayer((String)player.username).getId();
        VimeNetwork.mysql().select("SELECT * FROM cp_stats WHERE userid = " + id, rs -> {
            if (rs.next()) {
                player.stats.kills = rs.getInt("kills");
                player.stats.deaths = rs.getInt("deaths");
                player.stats.wins = rs.getInt("wins");
                player.stats.games = rs.getInt("games");
                player.stats.resourcePointsBreaked = rs.getInt("resourcePointsBreaked");
                TrailMenu.getPlayer((String)player.username).setActive(rs.getString("trail"));
            } else {
                VimeNetwork.mysql().query("INSERT INTO cp_stats (userid) VALUES (" + id + ")");
            }
            player.isLoaded = true;
        });
    }

    public void savePlayer(PlayerInfo player) {
        if (!player.isLoaded) {
            return;
        }
        VimeNetwork.mysql().query("UPDATE cp_stats SET trail = " + TrailMenu.getPlayer((String)player.username).getActiveMysqlString() + ", kills = " + player.stats.kills + ", deaths = " + player.stats.deaths + ", wins = " + player.stats.wins + ", games = " + player.stats.games + ", resourcePointsBreaked = " + player.stats.resourcePointsBreaked + " WHERE userid = " + VimeNetwork.getPlayer((String)player.username).getId());
    }
}

