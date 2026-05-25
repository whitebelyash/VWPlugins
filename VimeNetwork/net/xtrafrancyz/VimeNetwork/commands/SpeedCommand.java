package net.xtrafrancyz.VimeNetwork.commands;

import net.xtrafrancyz.Commons.T;
import net.xtrafrancyz.VimeNetwork.api.ServerType;
import net.xtrafrancyz.VimeNetwork.api.VimeNetwork;
import net.xtrafrancyz.VimeNetwork.api.player.NetworkPlayer;
import net.xtrafrancyz.VimeNetwork.api.player.Permission;
import net.xtrafrancyz.VimeNetwork.api.player.Rank;
import net.xtrafrancyz.VimeNetwork.api.util.Spectators;
import net.xtrafrancyz.VimeNetwork.api.util.U;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {
   public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
      NetworkPlayer networkPlayer = VimeNetwork.getPlayer(sender.getName());
      if (VimeNetwork.lobby().getServerType() == ServerType.BUILD) {
         if (!networkPlayer.getRank().has(sender, Permission.BUILDER)) {
            return true;
         }
      } else {
         if (!networkPlayer.getRank().has(sender, Permission.VANISH)) {
            return true;
         }

         if (!networkPlayer.getRank().has(Rank.CHIEF) && !Spectators.instance().contains((Player)sender)) {
            U.msg(sender, T.error("VimeWorld", "Вы можете использовать телепорт только в режиме наблюдателя (/vanish)"));
            return true;
         }
      }

      if (args.length == 0) {
         this.help(sender);
         return true;
      } else {
         int speed;
         try {
            speed = Integer.parseInt(args[0]);
         } catch (Exception var8) {
            this.help(sender);
            return true;
         }

         if (speed < 1) {
            speed = 1;
         }

         if (speed > 10) {
            speed = 10;
         }

         Player player = networkPlayer.getBukkitPlayer();
         if (player.isFlying()) {
            player.setFlySpeed(0.1F + 0.05F * (float)(speed - 1));
            U.msg(sender, T.system("VimeWorld", "Скорость &aполёта&f установлена на &a" + speed));
         } else {
            player.setWalkSpeed(0.2F + 0.08F * (float)(speed - 1));
            U.msg(sender, T.system("VimeWorld", "Скорость &aходьбы&f установлена на &a" + speed));
         }

         return true;
      }
   }

   private void help(CommandSender sender) {
      U.msg(sender, "&cИспользование: /speed <скорость ходьбы/полёта>");
   }
}
