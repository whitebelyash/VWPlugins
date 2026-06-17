package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
      if (command.getName().equals("ignore")) {
         if (args.length != 1) {
            return false;
         }

         MysqlPlayer vplayer = (MysqlPlayer)VPlayer.get(sender.getName());
         if (args[0].equals("@all")) {
            U.msg(sender, T.success("VimeWorld", "Вы отключили приватные сообщения"));
            vplayer.ignoreAll = true;
            return true;
         }

         Player player = Bukkit.getPlayerExact(args[0]);
         if (player == null) {
            U.msg(sender, T.error("VimeWorld", "Игрок &e" + args[0] + "&c не найден"));
         } else if (vplayer != null) {
            if (vplayer.ignored.contains(player.getName())) {
               U.msg(sender, T.warning("VimeWorld", "Игрок &e" + player.getName() + " &6уже находится в черном списке"));
            } else {
               U.msg(sender, T.success("VimeWorld", "Игрок &e" + player.getName() + " &aуспешно добавлен в черный список"));
               vplayer.ignored.add(player.getName());
            }
         }
      }

      if (command.getName().equals("unignore")) {
         if (args.length != 1) {
            return false;
         }

         MysqlPlayer vplayer = (MysqlPlayer)VPlayer.get(sender.getName());
         if (args[0].equals("@all")) {
            U.msg(sender, T.success("VimeWorld", "Вы снова включили приватные сообщения"));
            vplayer.ignoreAll = false;
            return true;
         }

         Player player = Bukkit.getPlayerExact(args[0]);
         if (player == null) {
            U.msg(sender, T.error("VimeWorld", "Игрок &e" + args[0] + "&c не найден"));
         } else if (vplayer != null) {
            if (!vplayer.ignored.contains(player.getName())) {
               U.msg(sender, T.warning("VimeWorld", "Игрока &e" + player.getName() + " &6нет в черном списке"));
            } else {
               U.msg(sender, T.success("VimeWorld", "Игрок &e" + player.getName() + " &aуспешно удалён из черного списка"));
               vplayer.ignored.remove(player.getName());
            }
         }
      }

      return true;
   }
}
