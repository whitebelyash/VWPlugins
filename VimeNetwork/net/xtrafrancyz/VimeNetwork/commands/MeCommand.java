package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.VimeNetwork.impl.player.VPlayerMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MeCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      (new VPlayerMenu((Player)sender)).show((Player)sender);
      return true;
   }
}
