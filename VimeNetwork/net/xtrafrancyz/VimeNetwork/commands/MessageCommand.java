package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.MysqlPlayer;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      switch (cmd.getName().toLowerCase()) {
         case "msg":
            if (args.length < 2) {
               U.msg(sender, "&cИспользование: &f/" + label + " <кому> <сообщение>");
               return true;
            }

            Player player = Bukkit.getPlayerExact(args[0]);
            if (player == null) {
               U.msg(sender, T.error("VimeWorld", "Игрок &e" + args[0] + "&c не найден"));
            } else {
               MysqlPlayer senderInfo = (MysqlPlayer)VPlayer.get(sender.getName());
               MysqlPlayer recieverInfo = (MysqlPlayer)VPlayer.get(player);
               String message = args[1];

               for(int i = 2; i < args.length; ++i) {
                  message = message + " " + args[i];
               }

               this.trySendPrivateMessage(senderInfo, recieverInfo, message);
            }
            break;
         case "reply":
            if (args.length < 1) {
               U.msg(sender, "&cИспользование: &f/" + label + " <ответ>");
               return true;
            }

            MysqlPlayer senderInfo = (MysqlPlayer)VPlayer.get(sender.getName());
            MysqlPlayer lastWriter;
            if (senderInfo.lastWriter == null || (lastWriter = (MysqlPlayer)VPlayer.PLAYERS.get(senderInfo.lastWriter)) == null) {
               U.msg(sender, T.error("VimeWorld", "У вас нет никого, кому бы вы могли ответить"));
               return true;
            }

            String message = args[0];

            for(int i = 1; i < args.length; ++i) {
               message = message + " " + args[i];
            }

            this.trySendPrivateMessage(senderInfo, lastWriter, message);
      }

      return true;
   }

   private void trySendPrivateMessage(MysqlPlayer sender, MysqlPlayer receiver, String message) {
      if (sender.ignoreAll) {
         U.msg(sender.player, (String[])(T.error("VimeWorld", "Вы отключили приватные сообщения. Включить: &e/unignore @all")));
      } else if (!sender.rank.has(Rank.CHIEF) && receiver.ignoreAll) {
         U.msg(sender.player, (String[])(T.error(receiver.getName(), "Отключил приватные сообщения")));
      } else if (sender.ignored.contains(receiver.getName())) {
         U.msg(sender.player, (String[])(T.error("VimeWorld", "Игрок &e" + receiver.getName() + "&c у вас в черном списке. Для разблокировки используйте &e/unignore " + receiver.getName())));
      } else if (!sender.rank.has(Rank.CHIEF) && receiver.ignored.contains(sender.getName())) {
         U.msg(sender.player, (String[])(T.error(receiver.getName(), "Вы находитесь в черном списке у этого игрока")));
      } else {
         sender.player.sendMessage(U.colored("&e[&fВы&e -> &f" + receiver.player.getDisplayName() + "&e] ") + message);
         receiver.player.sendMessage(U.colored("&e[&f" + sender.player.getDisplayName() + "&e -> &fВы&e] ") + message);
         receiver.lastWriter = sender.getName();
         sender.lastWriter = sender.getName();
      }
   }
}
