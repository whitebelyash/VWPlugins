package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet53Answer;
import net.xtrafrancyz.Core.network.packet.Packet54PrivateMessage;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreMessageCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      switch (cmd.getName().toLowerCase()) {
         case "msg":
            if (args.length < 2) {
               U.msg(sender, "&cИспользование: &f/" + label + " <кому> <сообщение>");
               return true;
            }

            if (args[0].equalsIgnoreCase(sender.getName())) {
               U.msg(sender, "&6Одиночество...");
               return true;
            }

            String message = args[1];

            for(int i = 2; i < args.length; ++i) {
               message = message + " " + args[i];
            }

            this.trySendPrivateMessage(sender, args[0], message);
            break;
         case "reply":
            if (args.length < 1) {
               U.msg(sender, "&cИспользование: &f/" + label + " <ответ>");
               return true;
            }

            String message = args[0];

            for(int i = 1; i < args.length; ++i) {
               message = message + " " + args[i];
            }

            this.trySendPrivateMessage(sender, "@last", message);
      }

      return true;
   }

   private void trySendPrivateMessage(CommandSender sender, String receiver, String message) {
      VimeNetwork.core().sendPacket(new Packet54PrivateMessage(receiver, sender.getName(), message), (packet0) -> {
         Packet53Answer packet = (Packet53Answer)packet0;
         switch (packet.status) {
            case "NoLastWriter":
               U.msg(sender, T.error("VimeWorld", "У вас нет никого, кому бы вы могли ответить"));
               break;
            case "YouIgnoreAll":
               U.msg(sender, T.error("VimeWorld", "Вы отключили приватные сообщения. Включить их можно в настройках &f/me"));
               break;
            case "YouIgnorePlayer":
               U.msg(sender, T.error("VimeWorld", "Игрок &f" + receiver + "&c у вас в черном списке. Для разблокировки используйте &f/unignore " + receiver));
               break;
            case "RecIgnoreAll":
               U.msg(sender, T.error("VimeWorld", "Игрок отключил приватные сообщения."));
               break;
            case "RecIgnoreYou":
               U.msg(sender, T.error("VimeWorld", "Вы находитесь в черном списке у этого игрока."));
               break;
            default:
               if (packet.status.startsWith("-")) {
                  U.msg(sender, T.error("VimeWorld", "Игрок &f" + packet.status.substring(1) + "&c не найден"));
               } else if (packet.status.startsWith("+")) {
                  sender.sendMessage(U.colored("&e[&fВы&e -> &f" + packet.status.substring(1) + "&e] ") + message);
               } else {
                  U.msg(sender, "&cПроизошла ошибка. Попробуйте позже.");
               }
         }

      }, 200L);
   }
}
