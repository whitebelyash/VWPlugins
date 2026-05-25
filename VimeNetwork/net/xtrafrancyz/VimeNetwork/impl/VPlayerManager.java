package net.xtrafrancyz.VimeNetwork.impl;

import net.xtrafrancyz.Commons.F;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.apache.commons.lang.StringEscapeUtils;

public class VPlayerManager {
   public static void ban(String username, int minutes, String reason, String banner) {
      if (minutes >= 0) {
         String kickreason = T.kickBanMessage(username, reason, minutes * 60 - 1, banner);
         String bantimemsg = minutes == 0 ? "навсегда" : "на " + F.formatSecondsShort(minutes * 60);
         NetworkPlayer player = (NetworkPlayer)VPlayer.PLAYERS.get(username.toLowerCase());
         if (player != null) {
            username = player.getName();
            player.getBukkitPlayer().kickPlayer(U.colored(kickreason));
         } else {
            VimeNetwork.core().sendPacket((new Packet52CustomMessage("kick", Packet52CustomMessage.Scope.BUNGEE_OF_PLAYER, username)).put("reason", kickreason).put("username", username));
         }

         U.bcast(T.error(banner, "Игрок &e" + username + "&c был забанен &e" + bantimemsg + "&c Причина: &e" + reason));
         VimeNetwork.mysql().query("INSERT INTO bans (username, banto, banreason, banfrom, status, admin) VALUES('" + StringEscapeUtils.escapeSql(username) + "', " + (minutes == 0 ? 0L : System.currentTimeMillis() + (long)('\uea60' * minutes)) + ", '" + StringEscapeUtils.escapeSql(reason) + "', " + System.currentTimeMillis() + ", 1, '" + StringEscapeUtils.escapeSql(banner) + "')");
         VimeNetwork.logAction(banner, "mod.ban", username, reason);
      }
   }

   public static void mute(String username, int minutes, String reason, String muter) {
      Packet52CustomMessage packet = (new Packet52CustomMessage("mute", Packet52CustomMessage.Scope.BUNGEE_OF_PLAYER, username)).put("username", username).put("who", muter).put("reason", reason).put("duration", minutes * 60);
      NetworkPlayer muterPlayer = (NetworkPlayer)VPlayer.PLAYERS.get(muter);
      if (muterPlayer != null) {
         VimeNetwork.core().sendPacket(packet, (packet0) -> U.msg(muterPlayer.getBukkitPlayer(), (String[])("&cИгрок &f" + username + "&c не найден")), 600L, () -> VimeNetwork.logAction(muter, "mod.mute", username, reason));
      } else {
         VimeNetwork.core().sendPacket(packet);
      }

   }

   public static void unmute(String username, String unmuter) {
      U.bcast(T.success(unmuter, "С игрока &e" + username + "&a снят мут"));
      VimeNetwork.core().sendPacket((new Packet52CustomMessage("unmute", Packet52CustomMessage.Scope.BUNGEE_OF_PLAYER, username)).put("username", username).put("who", unmuter));
   }

   public static void logAction(String username, String action, String target, String comment) {
      if (action != null && username != null) {
         if (!username.equals("АнтиЧит")) {
            target = target == null ? "NULL" : "'" + StringEscapeUtils.escapeSql(target) + "'";
            comment = comment == null ? "NULL" : "'" + StringEscapeUtils.escapeSql(comment) + "'";
            VimeNetwork.mysql().query("INSERT INTO `user_log_actions` (`username`, `time`, `action`, `data`, `comment`) VALUES ('" + username + "', " + System.currentTimeMillis() / 1000L + ", '" + action + "', " + target + ", " + comment + ")");
         }
      }
   }
}
