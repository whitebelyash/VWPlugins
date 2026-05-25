package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Core.network.packet.Packet1PlayerInfo;
import net.xtrafrancyz.Core.network.packet.Packet202GetPlayerInfo;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FindCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.WARDEN, true)) {
         return true;
      } else if (args.length == 0) {
         U.msg(sender, "&cИспользование:", "&e/find &7<игрок>&f: Найти сервер игрока");
         return true;
      } else {
         VimeNetwork.core().sendPacket(new Packet202GetPlayerInfo(args[0], 24), (packet0) -> {
            if (packet0.getId() == 1) {
               Packet1PlayerInfo packet = (Packet1PlayerInfo)packet0;
               if (packet.bukkit == null) {
                  U.msg(sender, "&cИгрок &f" + args[0] + "&c не найден");
               } else {
                  sender.sendMessage(U.colored("&eИгрок &f" + packet.username + "&e находится на сервере &f" + packet.bukkit + "&e, прокси &f" + packet.bungee));
               }
            } else {
               U.msg(sender, "&cИгрок &f" + args[0] + "&c не найден");
            }

         }, 300L, () -> U.msg(sender, "&cОшибка связи с главным сервером"));
         return true;
      }
   }
}
