package net.xtrafrancyz.VimeNetwork.commands;

import java.util.regex.Pattern;
import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.Core.network.packet.Packet55PrivateIgnore;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import net.xtrafrancyz.VimeNetwork.impl.player.VPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CoreIgnoreCommand implements CommandExecutor {
   private static final Pattern NICK_PATTERN = Pattern.compile("^[a-z0-9_]{3,16}$", 2);

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      switch (command.getName().toLowerCase()) {
         case "ignore":
            if (args.length != 1) {
               U.msg(sender, "&cИспользование: &f/" + label + " <игрок>");
            } else if (args[0].equals("@all")) {
               U.msg(sender, T.system("VimeWorld", "Вы &cвыключили &fприватные сообщения"));
               VPlayer.get(sender.getName()).settings.set(1, false);
            } else if (!NICK_PATTERN.matcher(args[0]).find()) {
               U.msg(sender, T.error("VimeWorld", "Вы ввели неверный ник"));
            } else if (args[0].equalsIgnoreCase(sender.getName())) {
               U.msg(sender, T.error("VimeWorld", "Вы не можете игнорировать себя"));
            } else {
               U.msg(sender, T.success("VimeWorld", "Игрок &e" + args[0] + " &aдобавлен в черный список"));
               Packet55PrivateIgnore packet = new Packet55PrivateIgnore(sender.getName(), (Packet55PrivateIgnore.Action)null);
               packet.action = Packet55PrivateIgnore.Action.IGNORE;
               packet.target = args[0];
               VimeNetwork.core().sendPacket(packet);
            }
            break;
         case "unignore":
            if (args.length != 1) {
               U.msg(sender, "&cИспользование: &f/" + label + " <игрок>");
               return true;
            }

            if (args[0].equals("@all")) {
               U.msg(sender, T.system("VimeWorld", "Вы &aвключили &fприватные сообщения"));
               VPlayer.get(sender.getName()).settings.set(1, true);
            } else if (!NICK_PATTERN.matcher(args[0]).find()) {
               U.msg(sender, T.error("VimeWorld", "Вы ввели неверный ник"));
            } else {
               U.msg(sender, T.success("VimeWorld", "Игрок &e" + args[0] + " &aудалён из черного списка"));
               Packet55PrivateIgnore packet = new Packet55PrivateIgnore(sender.getName(), (Packet55PrivateIgnore.Action)null);
               packet.action = Packet55PrivateIgnore.Action.UNIGNORE;
               packet.target = args[0];
               VimeNetwork.core().sendPacket(packet);
            }
      }

      return true;
   }
}
