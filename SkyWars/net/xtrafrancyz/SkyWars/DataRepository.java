/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.xtrafrancyz.Commons.player.Rank
 *  net.xtrafrancyz.VimeNetwork.api.VimeNetwork
 *  net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu
 *  net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer
 *  net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement
 */
package net.xtrafrancyz.SkyWars;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.xtrafrancyz.Commons.player.Rank;
import net.xtrafrancyz.SkyWars.PlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.menu.TrailMenu;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.achievement.Achievement;

public class DataRepository {
    public void getLeaderboard(Consumer<List<String[]>> callback) {
        VimeNetwork.mysql().select("SELECT username, userrank, wins, games, kills FROM lobby_stats.sw_stats ORDER BY wins DESC LIMIT 50", rs -> {
            LinkedList<String[]> list = new LinkedList<String[]>();
            int index = 1;
            while (rs.next()) {
                String[] row = new String[]{Integer.toString(index++), Rank.getRank((String)rs.getString("userrank")).getColor() + rs.getString("username"), Integer.toString(rs.getInt("wins")), Integer.toString(rs.getInt("games")), Integer.toString(rs.getInt("kills"))};
                list.add(row);
            }
            callback.accept(list);
        });
    }

    public void query(String query) {
        VimeNetwork.mysql().query(query);
    }

    public void loadPlayer(PlayerInfo player) {
        NetworkPlayer networkPlayer = VimeNetwork.getPlayer((String)player.username);
        VimeNetwork.mysql().select("SELECT * FROM sw_stats WHERE userid = " + networkPlayer.getId(), rs -> {
            if (rs.next()) {
                player.kit = rs.getString("kit");
                player.stats.wins = rs.getInt("wins");
                player.stats.games = rs.getInt("games");
                player.stats.kills = rs.getInt("kills");
                player.stats.deaths = rs.getInt("deaths");
                player.stats.arrowsFired = rs.getInt("arrowsFired");
                player.stats.blocksBroken = rs.getInt("blocksBroken");
                player.stats.blocksPlaced = rs.getInt("blocksPlaced");
                player.stats.winStreak = rs.getInt("currentWinStreak");
                player.stats.highestWinStreak = rs.getInt("winStreak");
                player.upgrades.arrow = rs.getInt("u_arrow");
                player.upgrades.blazeArrow = rs.getInt("u_blazeArrow");
                player.upgrades.juggernaut = rs.getInt("u_juggernaut");
                player.upgrades.speedBoost = rs.getInt("u_speedBoost");
                player.upgrades.resistance = rs.getInt("u_resistance");
                player.upgrades.redstoneHeart = rs.getInt("u_redstoneHeart");
                player.upgrades.enderman = rs.getInt("u_enderman");
                player.upgrades.builder = rs.getInt("u_builder");
                player.upgrades.zombie = rs.getInt("u_zombie");
                player.upgrades.enchanter = rs.getInt("u_enchanter");
                player.upgrades.goldenApple = rs.getInt("u_goldenApple");
                if (VimeNetwork.isPlayerOnline((int)networkPlayer.getId())) {
                    TrailMenu.getPlayer((String)player.username).setActive(rs.getString("trail"));
                }
            } else {
                VimeNetwork.mysql().query("INSERT INTO sw_stats (userid) VALUES (" + networkPlayer.getId() + ")");
            }
        });
        VimeNetwork.mysql().select("SELECT kit FROM sw_kits WHERE userid = " + networkPlayer.getId(), rs -> {
            while (rs.next()) {
                player.kits.add(rs.getString("kit"));
            }
            if (player.kits.size() != 0) {
                networkPlayer.getAchievements().complete(Achievement.SW_FIRST_KIT);
            }
            player.isLoaded = true;
        });
    }

    public void savePlayer(PlayerInfo player) {
        if (!player.isLoaded) {
            return;
        }
        VimeNetwork.mysql().query("UPDATE sw_stats SET trail = " + TrailMenu.getPlayer((String)player.username).getActiveMysqlString() + ", kit = " + (player.kit == null ? "NULL" : "'" + player.kit + "'") + ", wins = " + player.stats.wins + ", games = " + player.stats.games + ", kills = " + player.stats.kills + ", deaths = " + player.stats.deaths + ", arrowsFired = " + player.stats.arrowsFired + ", blocksBroken = " + player.stats.blocksBroken + ", blocksPlaced = " + player.stats.blocksPlaced + ", currentWinStreak = " + player.stats.winStreak + ", winStreak = " + player.stats.highestWinStreak + ", u_arrow = " + player.upgrades.arrow + ", u_blazeArrow = " + player.upgrades.blazeArrow + ", u_juggernaut = " + player.upgrades.juggernaut + ", u_speedBoost = " + player.upgrades.speedBoost + ", u_resistance = " + player.upgrades.resistance + ", u_redstoneHeart = " + player.upgrades.redstoneHeart + ", u_enderman = " + player.upgrades.enderman + ", u_builder = " + player.upgrades.builder + ", u_zombie = " + player.upgrades.zombie + ", u_enchanter = " + player.upgrades.enchanter + ", u_goldenApple = " + player.upgrades.goldenApple + " WHERE userid = " + VimeNetwork.getPlayer((String)player.username).getId());
    }
}

