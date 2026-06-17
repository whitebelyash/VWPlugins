package net.xtrafrancyz.VimeNetwork.commands;

import com.google.common.base.Joiner;
import net.xtrafrancyz.Core.network.packet.Packet52CustomMessage;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AlertCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (!VimeNetwork.hasRank(sender, Rank.ADMIN, true)) {
         return true;
      } else if (args.length == 0) {
         sender.sendMessage(ChatColor.RED + "Использование: " + ChatColor.WHITE + " /alert <message>");
         return true;
      } else {
         VimeNetwork.core().sendPacket((new Packet52CustomMessage("bcast", Packet52CustomMessage.Scope.ALL_BUNGEE)).put("message", Joiner.on(" ").join(args)));
         return true;
      }
   }
}
