package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      if (VimeNetwork.lobby().getServerType() == ServerType.BUILD) {
         if (!VimeNetwork.hasPermission(sender, Permission.BUILDER, true)) {
            return true;
         }
      } else if (!VimeNetwork.hasRank(sender, Rank.CHIEF, true)) {
         return true;
      }

      Player player = (Player)sender;
      switch (cmd.getName()) {
         case "gamemode":
            if (args.length == 0) {
               U.msg(player, (String[])("&cИспользование: /" + label + " <режим>"));
            } else {
               switch (args[0].toLowerCase()) {
                  case "0":
                  case "s":
                  case "survival":
                     this.changeGamemode(player, GameMode.SURVIVAL);
                     return true;
                  case "1":
                  case "c":
                  case "creative":
                     this.changeGamemode(player, GameMode.CREATIVE);
                     return true;
                  case "2":
                  case "a":
                  case "adventure":
                     this.changeGamemode(player, GameMode.ADVENTURE);
               }
            }
            break;
         case "gms":
            this.changeGamemode(player, GameMode.SURVIVAL);
            break;
         case "gmc":
            this.changeGamemode(player, GameMode.CREATIVE);
            break;
         case "gma":
            this.changeGamemode(player, GameMode.ADVENTURE);
      }

      return true;
   }

   private void changeGamemode(Player player, GameMode mode) {
      if (player.getGameMode() == mode) {
         U.msg(player, (String[])(T.warning("VimeWorld", "Игровой режим &e" + mode.name() + " &6уже установлен")));
      } else {
         player.setGameMode(mode);
         U.msg(player, (String[])(T.success("VimeWorld", "Игровой режим изменён на &e" + mode.name())));
      }
   }
}
