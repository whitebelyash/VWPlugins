package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.collect.Sets;
import java.util.Set;
import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet200GetServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet201ServersInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.Lobby;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StpCommand implements CommandExecutor {
   static final Set ALLOWED_SERVER_TYPES = Sets.newHashSet(new String[]{"SW", "BW", "MW", "GG", "ANN", "BWH", "SWT", "BWQ", "HG", "HGL", "KPVP", "BB", "LOBBY", "DR", "CP"});

   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.WARDEN, true)) {
         return true;
      } else if (args.length == 0) {
         U.msg(sender, "&cИспользование:", "&e/stp &7<игрок>&f: Телепортация на сервер игрока", "&e/stp &7@<сервер>&f: Телепортация на определенный сервер");
         return true;
      } else {
         String target = args[0];
         if (target.charAt(0) == '@') {
            tpToServer(sender, target.substring(1).toUpperCase());
            return true;
         } else {
            VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(target, 8), (packet0) -> {
               if (packet0.getId() == 1) {
                  Packet1PlayerInfo packet = (Packet1PlayerInfo)packet0;
                  if (packet.bukkit != null) {
                     tpToServer(sender, packet.bukkit);
                  } else {
                     U.msg(sender, "&cИгрок &f" + target + "&c не найден");
                  }
               } else {
                  U.msg(sender, "&cИгрок &f" + target + "&c не найден");
               }

            }, 300L, () -> U.msg(sender, "&cОшибка связи с главным сервером"));
            return true;
         }
      }
   }

   private static void tpToServer(CommandSender sender, String server) {
      if (server.equals(VimeNetwork.lobby().getServerId())) {
         U.msg(sender, "&aВы уже находитесь на нужном сервере");
      } else if (!ALLOWED_SERVER_TYPES.contains(server.split("_")[0])) {
         U.msg(sender, "&cВы не можете телепортироваться на сервер &f" + server + "&c. В доступе отказано");
      } else {
         VimeNetwork.core().sendPacket(new Packet200GetServersInfo((byte)2, new String[]{server}), (packet0) -> {
            Packet201ServersInfo packet = (Packet201ServersInfo)packet0;
            if (packet.servers.isEmpty()) {
               U.msg(sender, "&cСервер &f" + server + "&c не найден");
            } else {
               switch (Lobby.State.byId(((Packet201ServersInfo.Data)packet.servers.get(0)).state)) {
                  case DENY_ALL:
                  case OFFLINE:
                     U.msg(sender, "&cСервер &f" + server + "&c в данный момент закрыт для входа");
                     return;
                  default:
                     U.msg(sender, "&aТелепортация на сервер &f" + server);
                     VimeNetwork.toServer(server, (Player)sender);
               }
            }

         }, 300L, () -> U.msg(sender, "&cСервер отвечает слишком долго. Какие-то проблемы..."));
      }
   }
}
