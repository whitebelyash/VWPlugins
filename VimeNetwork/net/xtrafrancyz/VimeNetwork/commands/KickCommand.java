package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.MODER, true)) {
         return true;
      } else {
         if (cmd.getName().equals("kick")) {
            if (args.length == 0) {
               sender.sendMessage(ChatColor.RED + "Использование: /kick <ник> [причина]");
            } else {
               String kickmessage;
               if (args.length <= 1) {
                  kickmessage = "Не указана";
               } else {
                  StringBuilder sb = new StringBuilder();

                  for(int i = 1; i < args.length; ++i) {
                     sb.append(args[i]).append(' ');
                  }

                  kickmessage = sb.substring(0, sb.length() - 1);
               }

               String formattedKickMessage = T.kickMessage(args[0], kickmessage, sender.getName());
               Player player = Bukkit.getPlayerExact(args[0]);
               if (player != null) {
                  player.kickPlayer(U.colored(formattedKickMessage));
                  sender.sendMessage(ChatColor.GREEN + "Игрок " + player.getName() + " был кикнут");
                  VimeNetwork.logAction(sender.getName(), "mod.kick", args[0], kickmessage);
               } else if (VimeNetwork.core().isEnabled()) {
                  VimeNetwork.core().sendPacket((new Packet52CustomMessage("kick", Packet52CustomMessage.Scope.BUNGEE_OF_PLAYER, args[0])).put("username", args[0]).put("reason", formattedKickMessage), (answer) -> {
                     if (answer instanceof Packet53Answer && ((Packet53Answer)answer).status.equals("notfound")) {
                        sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден");
                     }

                  }, 500L, () -> {
                     sender.sendMessage(ChatColor.GREEN + "Игрок " + args[0] + " был кикнут");
                     VimeNetwork.logAction(sender.getName(), "mod.kick", args[0], kickmessage);
                  });
               } else {
                  sender.sendMessage(ChatColor.RED + "Игрок " + args[0] + " не найден");
               }
            }
         }

         return true;
      }
   }
}
