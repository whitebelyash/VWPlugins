package net.xtrafrancyz.VimeNetwork.commands;

import java.util.ArrayList;
import java.util.List;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand implements CommandExecutor {
   private ArrayList commands = new ArrayList(20);

   public void addCommand(String command, String help) {
      this.addCommand(command, help, Rank.PLAYER);
   }

   public void addCommand(String command, String help, Rank rank) {
      for(Desc desc : this.commands) {
         if (desc.command.equals(command)) {
            return;
         }
      }

      this.commands.add(new RankDesc(command, help, rank));
   }

   public void addCommand(String command, String help, Permission permission) {
      for(Desc desc : this.commands) {
         if (desc.command.equals(command)) {
            return;
         }
      }

      this.commands.add(new PermissionDesc(command, help, permission));
   }

   public boolean onCommand(CommandSender sender, Command argcmd, String label, String[] args) {
      if (!(sender instanceof Player)) {
         U.msg(sender, "&cКоманда разрешена только игрокам");
         return true;
      } else {
         List<Desc> cmds = this.getAllowedCommands(VPlayer.get(sender.getName()));
         int page = 0;
         int pages = (cmds.size() - 1) / 9;
         if (args.length == 1) {
            try {
               page = Integer.parseInt(args[0]) - 1;
               if (page > pages) {
                  page = pages;
               }

               if (page < 0) {
                  page = 0;
               }
            } catch (NumberFormatException var11) {
            }
         }

         U.msg(sender, "&e---------- &fVimeWorld Помощь [&e" + (page + 1) + "&f/&e" + (pages + 1) + "&f]&e ---------------");
         int index = page * 9;

         for(int i = index; i < cmds.size() && i < index + 9; ++i) {
            Desc cmd = (Desc)cmds.get(i);
            sender.sendMessage(ChatColor.YELLOW + "/" + cmd.command + ChatColor.WHITE + ": " + cmd.help);
         }

         return true;
      }
   }

   private List getAllowedCommands(NetworkPlayer player) {
      List<Desc> cmds = new ArrayList(this.commands.size());
      this.commands.stream().filter((cmd) -> cmd.canUse(player)).forEachOrdered(cmds::add);
      return cmds;
   }

   private abstract static class Desc {
      String command;
      String help;

      Desc(String command, String help) {
         this.command = U.colored(command);
         this.help = U.colored(help);
      }

      public abstract boolean canUse(NetworkPlayer var1);

      public String toString() {
         return this.command;
      }
   }

   private static class RankDesc extends Desc {
      Rank rank;

      RankDesc(String command, String help, Rank rank) {
         super(command, help);
         this.rank = rank;
      }

      public boolean canUse(NetworkPlayer player) {
         return player.getRank().has(this.rank);
      }
   }

   private static class PermissionDesc extends Desc {
      Permission permission;

      PermissionDesc(String command, String help, Permission permission) {
         super(command, help);
         this.permission = permission;
      }

      public boolean canUse(NetworkPlayer player) {
         return player.getRank().has(this.permission);
      }
   }
}
